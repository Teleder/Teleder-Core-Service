package teleder.core.controllers;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import teleder.core.annotations.ApiPrefixController;
import teleder.core.annotations.Authenticate;
import teleder.core.dtos.PagedResultDto;
import teleder.core.models.Conservation.Conservation;
import teleder.core.services.Conservation.IConservationService;
import teleder.core.services.Conservation.dtos.ConservationDto;
import teleder.core.services.Conservation.dtos.ConservationGroupDto;
import teleder.core.services.Conservation.dtos.ConservationPrivateDto;
import teleder.core.services.Group.dtos.GroupDto;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@ApiPrefixController("conservations")
public class ConservationController {
    final
    IConservationService conservationService;

    public ConservationController(IConservationService conservationService) {
        this.conservationService = conservationService;
    }

    @Authenticate
    @GetMapping("/get-my-conversations")
    public CompletableFuture<PagedResultDto<Conservation>> getMyConversations(@RequestParam(name = "page", defaultValue = "0") long page,
                                                                              @RequestParam(name = "size", defaultValue = "10") int size) {
        String userId = ((UserDetails) (((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getAttribute("user"))).getUsername();
        return conservationService.getMyConversations(userId, page * size, size);
    }

    @Authenticate
    @GetMapping("/get-my-conversations-group")
    public CompletableFuture<List<String>> getAllIdConservationGroup(){
        String userId = ((UserDetails) (((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getAttribute("user"))).getUsername();
        return conservationService.getAllIdConservationGroup(userId);
    }

    @Authenticate
    @DeleteMapping("/delete/{code}")
    public CompletableFuture<Boolean> deleteConservation(@PathVariable String code){
        String userId = ((UserDetails) (((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getAttribute("user"))).getUsername();
        return conservationService.deleteConservation(userId, code);
    }


    @Authenticate
    @PostMapping("/create-private")
    public CompletableFuture<ConservationDto> createPrivateConservation(@RequestBody ConservationPrivateDto input){
        String userId = ((UserDetails) (((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getAttribute("user"))).getUsername();
        return conservationService.createPrivateConservation(input);
    }


    @Authenticate
    @PostMapping("/create-group")
    public CompletableFuture<ConservationDto> createGroupConservation(@RequestBody ConservationGroupDto input){
        String userId = ((UserDetails) (((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getAttribute("user"))).getUsername();
        return conservationService.createGroupConservation(input);
    }
}
