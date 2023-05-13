package teleder.core.services.User;

import com.google.zxing.WriterException;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import teleder.core.config.JwtTokenUtil;
import teleder.core.dtos.ContactInfoDto;
import teleder.core.dtos.PagedResultDto;
import teleder.core.dtos.Pagination;
import teleder.core.dtos.SocketPayload;
import teleder.core.exceptions.BadRequestException;
import teleder.core.exceptions.NotFoundException;
import teleder.core.models.Conservation.Conservation;
import teleder.core.models.File.File;
import teleder.core.models.Message.Message;
import teleder.core.models.User.Block;
import teleder.core.models.User.Contact;
import teleder.core.models.User.User;
import teleder.core.repositories.IConservationRepository;
import teleder.core.repositories.IFileRepository;
import teleder.core.repositories.IMessageRepository;
import teleder.core.repositories.IUserRepository;
import teleder.core.services.File.IFileService;
import teleder.core.services.User.dtos.*;
import teleder.core.utils.CONSTS;
import teleder.core.utils.NullAwareBeanUtilsBean;
import teleder.core.utils.QRCodeGenerator;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class UserService implements IUserService, UserDetailsService {
    final SimpMessagingTemplate simpMessagingTemplate;
    final IUserRepository userRepository;
    final IFileService fileService;
    final IMessageRepository messageRepository;
    private final MongoTemplate mongoTemplate;
    final
    IConservationRepository conservationRepository;
    private final ModelMapper toDto;
    private final IFileRepository iFileRepository;

    public UserService(SimpMessagingTemplate simpMessagingTemplate, IUserRepository userRepository, IFileService fileService, IMessageRepository messageRepository, MongoTemplate mongoTemplate, IConservationRepository conservationRepository, ModelMapper toDto,
                       IFileRepository iFileRepository) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.userRepository = userRepository;
        this.fileService = fileService;
        this.messageRepository = messageRepository;
        this.mongoTemplate = mongoTemplate;
        this.conservationRepository = conservationRepository;
        this.toDto = toDto;
        this.iFileRepository = iFileRepository;
    }

    @Override
    @Async
    public CompletableFuture<UserDto> create(CreateUserDto input) throws WriterException, IOException, ExecutionException, InterruptedException {
        toDto.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        User user = toDto.map(input, User.class);
        int width = 300;
        int height = 300;
        MultipartFile qrCodeImage = QRCodeGenerator.generateQRCodeImage(input.getEmail(), width, height);
        File file = fileService.uploadFileLocal(qrCodeImage, user.getEmail()).get();
        user.setQr(file);
        user.setDisplayName(user.getFirstName() + " " + user.getLastName());
        user.setPassword(JwtTokenUtil.hashPassword(user.getPassword()));
        try {
            return CompletableFuture.completedFuture(toDto.map(userRepository.insert(user), UserDto.class));
        } catch (Exception e) {
            fileService.deleteFileLocal(file.getName());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Async
    public CompletableFuture<UserProfileDto> getProfile(String id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null)
            throw new NotFoundException("Not found user!");

        return CompletableFuture.completedFuture(toDto.map(user, UserProfileDto.class));
    }

    @Override
    @Async
    public CompletableFuture<UserProfileDto> removeRequestFriend(String userId, String contactId) {
        User user = userRepository.findById(contactId).orElseThrow(() -> new NotFoundException("Not found contact!"));
        User contact = userRepository.findById(contactId).orElseThrow(() -> new NotFoundException("Not found user!"));
        boolean flag = false;
        for (Contact c : user.getList_contact()) {
            if (c.getUser().getId().equals(contactId)) {
                if (c.getStatus().equals(Contact.Status.WAITING)) {
                    user.getList_contact().remove(c);
                    userRepository.save(user);
                    userRepository.save(contact);
                    flag = true;
                    break;
                } else {
                    throw new BadRequestException("Cannot remove request friend!");
                }
            }
        }
        if (!flag)
            throw new BadRequestException("Cannot remove request friend!");
        for (Contact c : contact.getList_contact()) {
            if (c.getUser().getId().equals(userId)) {
                contact.getList_contact().remove(c);
                break;
            }
        }
        return CompletableFuture.completedFuture(toDto.map(user, UserProfileDto.class));
    }

    @Override
    @Async
    public CompletableFuture<Boolean> addContact(String contactId) {
        String userId = ((UserDetails) (((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getAttribute("user"))).getUsername();
        if (userId == contactId)
            throw new BadRequestException("Cannot add friend self");
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Not found user!"));
        User contact = userRepository.findById(contactId).orElseThrow(() -> new NotFoundException("Not found contact!"));
        for (Contact c : user.getList_contact()) {
            if (c.getUser().getId().equals(contactId))
                throw new BadRequestException("Cannot perform this action");
        }
        user.getList_contact().add(new Contact(contact, Contact.Status.WAITING));
        contact.getList_contact().add(new Contact(user, Contact.Status.REQUEST));
        userRepository.save(user);
        userRepository.save(contact);
        simpMessagingTemplate.convertAndSend("/messages/user." + contactId, SocketPayload.create(new ContactInfoDto(contact), CONSTS.MESSAGE_GROUP));
        return CompletableFuture.completedFuture(true);
    }

    @Override
    @Async
    public CompletableFuture<Boolean> blockContact(String contact_id, String reason) {
        String userId = ((UserDetails) (((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getAttribute("user"))).getUsername();
        Optional<User> userOptional = userRepository.findById(userId);
        Optional<User> contactOptional = userRepository.findById(contact_id);
        if (userOptional.isPresent() && contactOptional.isPresent()) {
            User user = userOptional.get();
            User contact = contactOptional.get();
            // them vao danh sach chan
            user.getBlocks().add(new Block(contact, reason));
            for (Conservation x : user.getConservations()) {
                if (x.getUser_2().getId().contains(contact.getId()) || x.getUser_1().getId().contains(contact.getId())) {
                    x.setStatus(false);
                    conservationRepository.save(x);
                    break;
                }
            }
            //Huy ket ban 2 ben
            unContact(user, contact);
            simpMessagingTemplate.convertAndSend("/messages/user." + contact_id, SocketPayload.create(new ContactInfoDto(contact), CONSTS.BLOCK_CONTACT));
            return CompletableFuture.completedFuture(true);
        }
        throw new NotFoundException("Not Found Contact!");
    }

    @Override
    @Async
    public CompletableFuture<Boolean> removeContact(String contactId) {
        String userId = ((UserDetails) (((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getAttribute("user"))).getUsername();
        Optional<User> userOptional = userRepository.findById(userId);
        Optional<User> contactOptional = userRepository.findById(contactId);

        if (userOptional.isPresent() && contactOptional.isPresent()) {
            //Huy ket ban 2 ben
            unContact(userOptional.get(), contactOptional.get());
            simpMessagingTemplate.convertAndSend("/messages/user." + contactId, SocketPayload.create(new ContactInfoDto(contactOptional.get()), CONSTS.REMOVE_CONTACT));
            return CompletableFuture.completedFuture(true);
        }
        throw new NotFoundException("Not Found Contact!");
    }

    @Override
    @Async
    public CompletableFuture<Boolean> removeBlock(String contactId) {
        String userId = ((UserDetails) (((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getAttribute("user"))).getUsername();
        Optional<User> userOptional = userRepository.findById(userId);
        Optional<User> contactOptional = userRepository.findById(contactId);

        if (userOptional.isPresent() && contactOptional.isPresent()) {
            User user = userOptional.get();
            User contact = contactOptional.get();

            Block blockToRemove = null;
            for (Block block : user.getBlocks()) {
                if (block.getUser().getId().contains(contact.getId())) {
                    blockToRemove = block;
                    break;
                }
            }
            if (blockToRemove != null) {
                user.getBlocks().remove(blockToRemove);
                userRepository.save(user);
            }
            // Kiểm tra xem bên kia có chặn không nếu có thì vẫn để status = false nếu 2 bên không chặn nhau thì set lại status
            blockToRemove = null;
            for (Block block : contact.getBlocks()) {
                if (block.getUser().getId().contains(user.getId())) {
                    blockToRemove = block;
                    break;
                }
            }
            if (blockToRemove != null) {
                Conservation conservation = user.getConservations().stream()
                        .filter(x -> x.getUser_1().getId().contains(contact.getId()) || x.getUser_2().getId().contains(contact.getId()))
                        .findFirst().orElse(null);
                if (conservation == null)
                    throw new NotFoundException("Not found Conservation");
                conservation.setStatus(true);
                conservationRepository.save(conservation);
            }
            simpMessagingTemplate.convertAndSend("/messages/user." + contactId, SocketPayload.create(new ContactInfoDto(contact), CONSTS.REMOVE_BLOCK_CONTACT));
            return CompletableFuture.completedFuture(true);
        }
        throw new NotFoundException("Not Found Contact!");
    }

    @Override
    @Async
    public CompletableFuture<PagedResultDto<UserSearchDto>> getListContact(String displayName, long skip, int limit) {
        String userId = ((UserDetails) (((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getAttribute("user"))).getUsername();
//        MatchOperation matchOperation = Aggregation.match(
//                Criteria.where("list_contact.user.displayName").regex(displayName, "i").and("_id").is(userId)
//        );
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Not found user!"));
        List<String> listContact = user.getList_contact()
                .stream()
                .filter(x -> x.getStatus().equals(Contact.Status.ACCEPT))
                .map(x -> x.getUser().getId()).toList();
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("displayName").regex(displayName, "i").and("_id").in(listContact)),
                Aggregation.sort(Sort.Direction.ASC, "displayName"),
                Aggregation.skip(skip),
                Aggregation.limit(limit)
        );
        List<User> contacts = mongoTemplate.aggregate(aggregation, "User", User.class).getMappedResults();
        long totalCount = user.getList_contact().stream().filter(x -> x.getStatus().equals(Contact.Status.ACCEPT)).toList().size();
        return CompletableFuture.completedFuture(PagedResultDto.create(Pagination.create(totalCount, skip, limit), contacts.stream().map(x -> toDto.map(x, UserSearchDto.class)).toList()));
    }

    @Override
    public CompletableFuture<PagedResultDto<Contact>> getListContactWaitingAccept(long skip, int limit) {
        String userId = ((UserDetails) (((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getAttribute("user"))).getUsername();

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("_id").is(userId)),
                Aggregation.match(Criteria.where("list_contact.status").is(Contact.Status.REQUEST)),
                Aggregation.unwind("list_contact"),
                Aggregation.sort(Sort.Direction.ASC, "list_contact.user.displayName"),
                Aggregation.skip(skip),
                Aggregation.limit(limit),
                Aggregation.project()
                        .and("_id").as("id")
                        .and("list_contact.user").as("user")
                        .and("list_contact.status").as("status")
        );

        List<Contact> contacts = mongoTemplate.aggregate(aggregation, "User", Contact.class).getMappedResults();
        long totalCount = userRepository.findById(userId).get().getList_contact().stream().filter(x -> x.getStatus() == Contact.Status.REQUEST).count();
        return CompletableFuture.completedFuture(PagedResultDto.create(Pagination.create(totalCount, skip, limit), contacts));
    }

    @Override
    public CompletableFuture<Boolean> responseToRequestForContacts(String contact_id, Boolean accept) {
        String userId = ((UserDetails) (((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getAttribute("user"))).getUsername();
        User user = userRepository.findById(userId).orElse(null);
        User contact = userRepository.findById(contact_id).orElse(null);
        Contact friend = null;
        if (user == null || contact == null)
            throw new NotFoundException("Not found user");
        if (!accept) {
            for (Contact f : user.getList_contact()) {
                if (f.getUser().getId().contains(contact.getId())) {
                    friend = f;
                    break;
                }
            }
            if (friend != null) {
                user.getList_contact().remove(friend);
                userRepository.save(user);
            }
            for (Contact f : contact.getList_contact()) {
                if (f.getUser().getId().contains(user.getId())) {
                    friend = f;
                    break;
                }
            }
            if (friend != null) {
                contact.getList_contact().remove(friend);
                userRepository.save(contact);
            }
            simpMessagingTemplate.convertAndSend("/messages/user." + contact_id, SocketPayload.create(new ContactInfoDto(contact), CONSTS.DENY_CONTACT));
            return CompletableFuture.completedFuture(false);
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            Conservation conservation = new Conservation(user, contact, null);
            conservation.setCode(UUID.randomUUID().toString());
            Message mess = new Message("Friend from " + LocalDate.now().format(formatter), conservation.getCode(), CONSTS.ACCEPT_CONTACT);
            mess = messageRepository.save(mess);
            conservation.setLastMessage(mess);
            conservation = conservationRepository.save(conservation);
            for (Contact f : user.getList_contact()) {
                if (f.getUser().getId().contains(contact.getId())) {
                    f.setStatus(Contact.Status.ACCEPT);
                    user.getConservations().add(conservation);
                    userRepository.save(user);
                    break;
                }
            }
            for (Contact f : contact.getList_contact()) {
                if (f.getUser().getId().contains(user.getId())) {
                    f.setStatus(Contact.Status.ACCEPT);
                    contact.getConservations().add(conservation);
                    userRepository.save(contact);
                    break;
                }
            }
            simpMessagingTemplate.convertAndSend("/messages/user." + contact_id, SocketPayload.create(new ContactInfoDto(contact), CONSTS.ACCEPT_CONTACT));
            return CompletableFuture.completedFuture(true);
        }
    }

    @Async
    @Override
    public CompletableFuture<List<UserSearchDto>> searchUser(String searchText) {
        Criteria criteria = new Criteria();
        criteria.orOperator(
                Criteria.where("phone").regex(searchText, "i"),
                Criteria.where("bio").regex(searchText, "i")
        );
        Query query = new Query(criteria);
        List<User> users = mongoTemplate.find(query, User.class);
        toDto.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return CompletableFuture.completedFuture(users.stream().map(x -> toDto.map(x, UserSearchDto.class)).toList());
    }

    @Override
    public CompletableFuture<List<Contact>> getListContactRequestSend() {
        String userId = ((UserDetails) (((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getAttribute("user"))).getUsername();
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("_id").is(userId)),
                Aggregation.match(Criteria.where("list_contact.status").is(Contact.Status.WAITING)),
                Aggregation.unwind("list_contact"),
                Aggregation.sort(Sort.Direction.ASC, "list_contact.user.displayName"),
                Aggregation.project()
                        .and("_id").as("id")
                        .and("list_contact.user").as("user")
                        .and("list_contact.status").as("status")
        );
        List<Contact> contacts = mongoTemplate.aggregate(aggregation, "User", Contact.class).getMappedResults();
        return CompletableFuture.completedFuture(contacts);
    }

    // Basic CRUD
    @Override
    @Async
    public CompletableFuture<UserDto> getOne(String id) {
        return CompletableFuture.completedFuture(toDto.map(userRepository.findById(id), UserDto.class));
    }

    @Override
    @Async
    public CompletableFuture<List<UserDto>> getAll() {
        return CompletableFuture.completedFuture(userRepository.findAll().stream().map(x -> toDto.map(x, UserDto.class)).toList());
    }

    @Override
    @Async
    public CompletableFuture<UserDto> update(String id, UpdateUserDto User) throws InvocationTargetException, IllegalAccessException {
        User existingUserLevel = userRepository.findById(id).orElse(null);
        if (existingUserLevel == null)
            throw new NotFoundException("Unable to find user level!");
        BeanUtilsBean nullAwareBeanUtilsBean = NullAwareBeanUtilsBean.getInstance();
        nullAwareBeanUtilsBean.copyProperties(existingUserLevel, User);
        existingUserLevel.setDisplayName(existingUserLevel.getFirstName() + " " + existingUserLevel.getLastName());
        return CompletableFuture.completedFuture(toDto.map(userRepository.save(existingUserLevel), UserDto.class));
    }

    @Override
    @Async
    public CompletableFuture<Void> delete(String id) {
        User existingUserLevel = userRepository.findById(id).orElse(null);
        if (existingUserLevel == null)
            throw new NotFoundException("Unable to find user!");
        existingUserLevel.setDeleted(true);
        userRepository.save(existingUserLevel);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public UserDetails loadUserByUsername(String input) throws UsernameNotFoundException {
        User user = userRepository.findById(input).orElse(null);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email or phone: " + input);
        } else {
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(user.getRole()));
            return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
        }
    }

    private void unContact(User user, User contact) {
        Contact contactToRemove = null;
        for (Contact cont : user.getList_contact()) {
            if (cont.getUser().getId().contains(contact.getId())) {
                contactToRemove = cont;
                break;
            }
        }
        if (contactToRemove != null) {
            user.getList_contact().remove(contactToRemove);
            userRepository.save(user);
        }

        for (Contact cont : contact.getList_contact()) {
            if (cont.getUser().getId().contains(user.getId())) {
                contactToRemove = cont;
                break;
            }
        }
        if (contactToRemove != null) {
            contact.getList_contact().remove(contactToRemove);
            userRepository.save(contact);
        }
    }


}
