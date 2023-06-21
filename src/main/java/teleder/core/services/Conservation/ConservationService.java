package teleder.core.services.Conservation;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import teleder.core.dtos.PagedResultDto;
import teleder.core.dtos.Pagination;
import teleder.core.exceptions.BadRequestException;
import teleder.core.models.Conservation.Conservation;
import teleder.core.models.User.User;
import teleder.core.repositories.IConservationRepository;
import teleder.core.repositories.IMessageRepository;
import teleder.core.repositories.IUserRepository;
import teleder.core.services.Conservation.dtos.*;
import teleder.core.utils.CONSTS;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static teleder.core.utils.PopulateDocument.populateConservation;

@Service
public class ConservationService implements IConservationService {
    final
    IConservationRepository conservationRepository;
    final
    IMessageRepository messageRepository;
    final IUserRepository userRepository;
    private final MongoTemplate mongoTemplate;
    private final ModelMapper toDto;

    public ConservationService(IConservationRepository conservationRepository, IMessageRepository messageRepository, IUserRepository userRepository, MongoTemplate mongoTemplate, ModelMapper toDto) {
        this.conservationRepository = conservationRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.mongoTemplate = mongoTemplate;
        this.toDto = toDto;
    }

    @Override
    public CompletableFuture<ConservationDto> create(CreateConservationDto input) {
        return null;
    }

    @Override
    public CompletableFuture<ConservationDto> getOne(String id) {
        return null;
    }

    @Override
    public CompletableFuture<List<ConservationDto>> getAll() {
        return null;
    }

    @Override
    public CompletableFuture<ConservationDto> update(String id, UpdateConservationDto Conservation) {
        return null;
    }

    @Override
    public CompletableFuture<Void> delete(String id) {
        return null;
    }

    @Override
    @Async
    public CompletableFuture<PagedResultDto<Conservation>> getMyConversations(String userId, long skip, int limit) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            List<String> conservationIds = user.getConservations();
            List<Conservation> conservations = conservationRepository.findByIds(conservationIds.stream().limit(limit).skip(skip).toList());
//            conservations = conservations.stream()
//                    .sorted(Comparator.comparing(Conservation::getCreateAt))
//                    .collect(Collectors.toList());
            for (Conservation conservation : conservations) {
                populateConservation(mongoTemplate, conservation, toDto);
            }
            conservations.sort((c1, c2) ->
                    c2.getLastMessage().getUpdateAt().compareTo(c1.getLastMessage().getCreateAt()));

            return CompletableFuture.completedFuture(PagedResultDto.create(new Pagination(conservationIds.size(), skip, limit), conservations));
        }
        return CompletableFuture.completedFuture(PagedResultDto.create(new Pagination(0, skip, limit), new ArrayList<>()));
    }


    @Override
    @Async
    public CompletableFuture<List<String>> getAllIdConservationGroup(String userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            List<String> conservationIds = user.getConservations();
            List<Conservation> conservations = conservationRepository.getConservationGroup(conservationIds);

            return CompletableFuture.completedFuture(conservations.stream().map(Conservation::getGroupId).toList());
        }
        return CompletableFuture.completedFuture(new ArrayList<>());
    }

    @Override
    @Async
    public CompletableFuture<Boolean> deleteConservation(String userId, String code) {
        Conservation conservation = conservationRepository.findByCode(code);
        if (conservation.getGroupId() != null) {
            throw new BadRequestException("Conservation is not group");
        }
        userRepository.findById(userId).orElseThrow(() -> new BadRequestException("User not found"));
        userRepository.findById(conservation.getUserId_1()).ifPresent(user -> {
            user.getConservations().remove(conservation.getCode());
            userRepository.save(user);
        });
        userRepository.findById(conservation.getUserId_2()).ifPresent(user -> {
            user.getConservations().remove(conservation.getCode());
            userRepository.save(user);
        });
        conservationRepository.delete(conservation);
        return CompletableFuture.completedFuture(true);
    }

    @Override
    @Async
    public CompletableFuture<Conservation> createPrivateConservation(String userId, ConservationPrivateDto input) {
        Conservation conservation = new Conservation();
        conservation.setUserId_1(userId);
        conservation.setUserId_2(input.getUser());
        conservation.setGroupId(null);
        conservation.setType(CONSTS.MESSAGE_PRIVATE);
        toDto.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return CompletableFuture.completedFuture(toDto.map(conservationRepository.save(conservation), Conservation.class));
    }

    @Override
    @Async
    public CompletableFuture<Conservation> createGroupConservation(ConservationGroupDto input) {
        Conservation conservation = new Conservation();
        conservation.setGroupId(input.getGroupId());
        conservation.setType(CONSTS.MESSAGE_GROUP);
        toDto.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        return CompletableFuture.completedFuture(toDto.map(conservationRepository.save(conservation), Conservation.class));
    }
}
