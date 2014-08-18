package ch.ithings.demo.config.metrics;

import javax.inject.Inject;
import javax.sql.DataSource;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.elasticsearch.client.Client;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class HealthIndicatorConfiguration {

    @Inject
    private JavaMailSenderImpl javaMailSender;

    @Inject
    private DataSource dataSource;
    
    @Inject
    private MongoTemplate mongoTemplate;
    
    @Inject
    private Client esClient;

    @Bean
    public HealthIndicator dbHealthIndicator() {
        return new DBHealthIndicator(dataSource);
    }
    
    @Bean
    public HealthIndicator mongoHealthIndicator() {
        return new MongoHealthIndicator(mongoTemplate);
    }

    @Bean
    public HealthIndicator mailHealthIndicator() {
        return new JavaMailHealthIndicator(javaMailSender);
    }
    
    @Bean
    public HealthIndicator esHealthIndicator() {
        return new ESHealthIndicator(esClient);
    }
    
    @Bean
    public HealthIndicator hzHealthIndicator() {
        return new HZHealthIndicator();
    }
    
}
