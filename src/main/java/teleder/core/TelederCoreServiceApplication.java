package teleder.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.io.File;
//@EnableMongoRepositories(basePackages = "teleder.core.repositories")
@SpringBootApplication
public class TelederCoreServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(TelederCoreServiceApplication.class, args);
        System.out.println("""
                --------------------------------------------------------------------------------------------------------------------------------------------------------
                """);
        System.out.println("""
                🚀 Server ready at http://localhost:8080
                """);
        System.out.println("""
                🚀 Api doc ready at http://localhost:8080/swagger-ui/index.html
                """);
    }
}
