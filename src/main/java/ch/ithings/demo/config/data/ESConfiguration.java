package ch.ithings.demo.config.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.elasticsearch.client.Client;

/**
 *
 * @author tph
 */
@Configuration
@EnableElasticsearchRepositories("ch.ithings.demo.repository.es")
public class ESConfiguration implements EnvironmentAware {

    private final Logger log = LoggerFactory.getLogger(ESConfiguration.class);

    private RelaxedPropertyResolver propertyResolver;

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
        this.propertyResolver = new RelaxedPropertyResolver(environment, "spring.data.elasticsearch.");
    }

    @Bean
    public ElasticsearchTemplate elasticsearchTemplate(Client client) {
        //final NodeBuilder nodeBuilder = new NodeBuilder();
        //return new ElasticsearchTemplate(nodeBuilder.local(true).clusterName(propertyResolver.getProperty("clusterName", "elasticsearch")).node().client());
        return new ElasticsearchTemplate(client);
    }

}
