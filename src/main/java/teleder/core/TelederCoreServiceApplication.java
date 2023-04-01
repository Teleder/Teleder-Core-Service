package teleder.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

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
        File currentDir = new File(".");
        String absolutePath = currentDir.getAbsolutePath();
        System.out.println(absolutePath);
    }
}
