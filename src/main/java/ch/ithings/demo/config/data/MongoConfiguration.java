package ch.ithings.demo.config.data;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import org.aspectj.lang.Aspects;
import org.mongeez.Mongeez;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoExceptionTranslator;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoTypeMapper;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.mapping.event.ValidatingMongoEventListener;
import org.springframework.data.mongodb.crossstore.MongoChangeSetPersister;
import org.springframework.data.mongodb.crossstore.MongoDocumentBacking;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
@EnableMongoRepositories("ch.ithngs.demo.repository.mongo")
@EnableMongoAuditing(auditorAwareRef = "springSecurityAuditorAware")
public class MongoConfiguration implements EnvironmentAware {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());
    
    @Inject
    private EntityManagerFactory entityManagerFactory;

    private RelaxedPropertyResolver propertyResolver;

    private Environment environment;
    
    @Inject
    private Mongo mongo;
    
    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
        this.propertyResolver = new RelaxedPropertyResolver(environment, "spring.data.mongodb.");
        java.util.logging.Logger mongoLogger = java.util.logging.Logger.getLogger( "com.mongodb" );
        mongoLogger.setLevel(Level.INFO);
    }

    @Bean
    public ValidatingMongoEventListener validatingMongoEventListener() {
        return new ValidatingMongoEventListener(validator());
    }

    @Bean
    public LocalValidatorFactoryBean validator() {
        return new LocalValidatorFactoryBean();
    }
    
    @Bean
    public MongoDbFactory mongoDbFactory() throws Exception {
        MongoCredential credential = MongoCredential.createMongoCRCredential("user1", "test", "password1".toCharArray());
        MongoClient mongoClient;
        if (propertyResolver.getProperty("mode").equalsIgnoreCase("cluster")){
            List<ServerAddress> servers = mongo.getServerAddressList();    
            mongoClient = new MongoClient(servers, Arrays.asList(credential));
            mongoClient.setReadPreference(ReadPreference.nearest());
            mongoClient.getReplicaSetStatus();
            
            return new SimpleMongoDbFactory(mongoClient, propertyResolver.getProperty("databaseName"));
        } else {
            return new SimpleMongoDbFactory(mongo, propertyResolver.getProperty("databaseName"));
        }    
    }

    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongoDbFactory(), mappingMongoConverter());
    }

    @Bean
    public MongoTypeMapper mongoTypeMapper() {
        return new DefaultMongoTypeMapper(null);
    }

    @Bean
    public MongoMappingContext mongoMappingContext() {
        return new MongoMappingContext();
    }
    
    @Bean
    public MongoExceptionTranslator mongoExceptionTranslator(){
        return new MongoExceptionTranslator();
    }

    @Bean
    public MappingMongoConverter mappingMongoConverter() throws Exception {
        MappingMongoConverter converter = new MappingMongoConverter(mongoDbFactory(), mongoMappingContext());
        converter.setTypeMapper(mongoTypeMapper());
        return converter;
    }

    @Bean
    public MongoChangeSetPersister mongoChangeSetPersister() throws Exception {
        MongoChangeSetPersister mcsp = new MongoChangeSetPersister();
        mcsp.setMongoTemplate(mongoTemplate()); 
        mcsp.setEntityManagerFactory(entityManagerFactory);
        log.debug("Configure MongoChangeSetPersister");
        return mcsp;
    }

    @Bean
    public MongoDocumentBacking mongoDocumentBacking(MongoChangeSetPersister mongoChangeSetPersister) throws Exception {
        MongoDocumentBacking mdb = Aspects.aspectOf(MongoDocumentBacking.class);
        mdb.setChangeSetPersister(mongoChangeSetPersister);
        return mdb;
    }

    @Bean
    public Mongeez mongeez() throws Exception {
        log.debug("Configuring Mongeez");
        Mongeez mongeez = new Mongeez();

        mongeez.setFile(new ClassPathResource("/config/mongeez/master.xml"));
        mongeez.setMongo(mongo);
        mongeez.setDbName(propertyResolver.getProperty("databaseName"));
        mongeez.process();
        
        return mongeez;
    }

}
