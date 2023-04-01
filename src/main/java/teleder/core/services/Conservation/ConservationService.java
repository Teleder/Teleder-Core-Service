package teleder.core.services.Conservation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import teleder.core.exceptions.NotFoundException;
import teleder.core.models.Conservation.Conservation;
import teleder.core.models.Message.Message;
import teleder.core.models.User.User;
import teleder.core.repositories.IConservationRepository;
import teleder.core.repositories.IMessageRepository;
import teleder.core.services.Conservation.dtos.ConservationDto;
import teleder.core.services.Conservation.dtos.CreateConservationDto;
import teleder.core.services.Conservation.dtos.UpdateConservationDto;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class ConservationService implements IConservationService {
    final
    IConservationRepository conservationRepository;
    final
    IMessageRepository messageRepository;

    public ConservationService(IConservationRepository conservationRepository, IMessageRepository messageRepository) {
        this.conservationRepository = conservationRepository;
        this.messageRepository = messageRepository;
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


}
