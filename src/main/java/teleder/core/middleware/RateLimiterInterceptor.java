package teleder.core.middleware;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

@SuppressWarnings("NullableProblems")
public class RateLimiterInterceptor implements HandlerInterceptor {

    private final RateLimiter rateLimiter;

    public RateLimiterInterceptor(RateLimiterRegistry rateLimiterRegistry) {
        this.rateLimiter = rateLimiterRegistry.rateLimiter("default");
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        boolean hasPermission = rateLimiter.acquirePermission();
        if (!hasPermission) {
            response.setStatus(HttpStatus.SC_TOO_MANY_REQUESTS);
            return false;
        }

        return true;
    }

}
