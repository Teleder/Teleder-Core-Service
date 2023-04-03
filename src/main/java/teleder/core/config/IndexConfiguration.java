package teleder.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.IndexDefinition;
import org.springframework.stereotype.Component;

@Component
public class IndexConfiguration implements CommandLineRunner{

    @Value("${app.skip-command-line-runners:false}")
    private boolean skipCommandLineRunners;
    private final MongoTemplate mongoTemplate;
    public IndexConfiguration(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
    @Override
    public void run(String... args) {
        if (skipCommandLineRunners) {
            return;
        }
//        createUniqueIndexIfNotExists(mongoTemplate, "User", "bio");
//        createUniqueIndexIfNotExists(mongoTemplate, "User", "phone");
//        createUniqueIndexIfNotExists(mongoTemplate, "User", "email");
    }


    private void createUniqueIndexIfNotExists(MongoTemplate mongoTemplate, String collectionName, String fieldName) {
        IndexOperations indexOperations = mongoTemplate.indexOps(collectionName);
        IndexDefinition indexDefinition = new Index().on(fieldName, Sort.Direction.ASC).unique();
        indexOperations.ensureIndex(indexDefinition);
    }
}
