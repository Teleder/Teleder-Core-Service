package teleder.core.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.IndexDefinition;

@Configuration
public class IndexConfiguration {

    @Bean
    public CommandLineRunner ensureUniqueIndexes(MongoTemplate mongoTemplate) {
        return args -> {
            createUniqueIndexIfNotExists(mongoTemplate, "User", "bio");
            createUniqueIndexIfNotExists(mongoTemplate, "User", "phone");
            createUniqueIndexIfNotExists(mongoTemplate, "User", "email");

        };
    }

    private void createUniqueIndexIfNotExists(MongoTemplate mongoTemplate, String collectionName, String fieldName) {
        IndexOperations indexOperations = mongoTemplate.indexOps(collectionName);
        IndexDefinition indexDefinition = new Index().on(fieldName, Sort.Direction.ASC).unique();
        indexOperations.ensureIndex(indexDefinition);
    }
}
