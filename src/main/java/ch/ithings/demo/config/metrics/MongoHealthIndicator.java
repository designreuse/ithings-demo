package ch.ithings.demo.config.metrics;

import com.mongodb.CommandResult;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 *
 * @author tph
 */
public class MongoHealthIndicator extends AbstractHealthIndicator {

    private MongoTemplate mongoTemplate;

    public MongoHealthIndicator(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        try {
            CommandResult result = mongoTemplate.executeCommand("{ serverStatus: 1 }");
            if (result.ok()) {
                builder.up().withDetail("mongodb", "ok")
                        .withDetail("version", result.getString("version"));
            } else {
                builder.down().withDetail("mongodb", "error");
            }
        } catch (Exception e) {
            builder.down(e);
        }
    }

}
