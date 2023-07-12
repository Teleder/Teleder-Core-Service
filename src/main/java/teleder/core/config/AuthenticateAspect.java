package teleder.core.config;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import teleder.core.exceptions.UnauthorizedException;
import teleder.core.services.User.UserService;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@Aspect
@Component
@Order(1)
public class AuthenticateAspect {
    private final JwtTokenUtil jwtTokenUtil;
    private final UserService userDetailsService;

    public AuthenticateAspect(JwtTokenUtil jwtTokenUtil, UserService userDetailsService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
    }

    @Before("@annotation(teleder.core.annotations.Authenticate)")
    public void authenticate(JoinPoint joinPoint) throws UnauthorizedException, NoSuchAlgorithmException, InvalidKeySpecException {
        String token = getTokenFromRequest();
        if (token == null || jwtTokenUtil.isTokenExpired(token)) {
            throw new UnauthorizedException("Unauthorized");
        }
        String username = jwtTokenUtil.getUsernameFromToken(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (token != null && jwtTokenUtil.validateToken(token, userDetails)) {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            request.setAttribute("user", userDetails);
        } else
            throw new UnauthorizedException("Unauthorized");
    }

    private String getTokenFromRequest() {
        // Get token from request headers
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        final String requestTokenHeader = request.getHeader("Authorization");
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            return requestTokenHeader.substring(7);
        }
        return null;
    }
}
