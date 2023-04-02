package teleder.core.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.*;
import teleder.core.annotations.ApiPrefixController;
import teleder.core.middleware.GlobalApiLoggerInterceptor;
import teleder.core.middleware.RateLimiterInterceptor;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.concurrent.Executor;

@Configuration
public class AppConfig implements WebMvcConfigurer {
    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    @Autowired
    private GlobalApiLoggerInterceptor globalApiLoggerInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(globalApiLoggerInterceptor);
        registry.addInterceptor(rateLimiterInterceptor(rateLimiterRegistry()));
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix("api/v1", HandlerTypePredicate.forAnnotation(ApiPrefixController.class));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ModelMapper getModelMapper() {
        return new ModelMapper();
    }

    //AsyncConfig
    @Bean(name = "asyncTaskExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(10);
        executor.setThreadNamePrefix("MyAsyncThread-");
        executor.initialize();
        return executor;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        String uploadsLocation = "file:" +  Paths.get("").toAbsolutePath().getParent().toString() + "/uploads/";
        registry.addResourceHandler("/uploads/**")
//                .addResourceLocations("file:" +  Paths.get("").toAbsolutePath().getParent().toString() + "/uploads/");
                .addResourceLocations("file:./uploads/");
    }


    @Bean
    public Cloudinary cloudinaryConfig() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
    }
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")
                .allowedHeaders("*");
    }

    @Bean
    public RateLimiterRegistry rateLimiterRegistry() {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitForPeriod(1000)
                .limitRefreshPeriod(Duration.ofHours(1))
                .timeoutDuration(Duration.ofMillis(25))
                .build();
        return RateLimiterRegistry.of(config);
    }

    @Bean
    public RateLimiterInterceptor rateLimiterInterceptor(RateLimiterRegistry rateLimiterRegistry) {
        return new RateLimiterInterceptor(rateLimiterRegistry);
    }
}