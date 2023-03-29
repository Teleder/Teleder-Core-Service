package teleder.core.config;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import teleder.core.config.RequiresAuthorization;
import teleder.core.exceptions.BadRequestException;
import teleder.core.exceptions.ForbiddenException;

@Aspect
@Component
@Order(2)
public class AuthorizationAspect {
    @After("@annotation(requiresAuthorization)")
    public void checkAuthorization(JoinPoint joinPoint, RequiresAuthorization requiresAuthorization) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        UserDetails user = (UserDetails) request.getAttribute("user");
        if (user == null)
            throw new BadRequestException("User is not authorized");
        if (!user.getAuthorities().stream().anyMatch(x -> x.getAuthority().equals(requiresAuthorization.value()[0]))) {
            throw new ForbiddenException("Don't have permission to do this!");
        }
    }
}