package teleder.core.middleware;

import org.springframework.context.ApplicationListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertCallback;
import org.springframework.data.mongodb.core.mapping.event.MongoMappingEvent;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

@Component
public class MongoRepositoryMiddleware implements ApplicationListener<MongoMappingEvent<?>> {

    @Override
    public void onApplicationEvent(MongoMappingEvent<?> event) {
        Object source = event.getSource();
        if (source instanceof Query) {
            Query query = (Query) source;

            // Check if the query contains a 'find' method
            if (query.toString().matches(".*find.*")) {
                // Add 'isDeleted = false' condition to the query
                query.addCriteria(Criteria.where("isDeleted").is(false));
            }
        }
    }
}
