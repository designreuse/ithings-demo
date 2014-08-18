package ch.ithings.demo.config.data;

import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import com.vladmihalcea.flexypool.FlexyPoolDataSource;
import com.vladmihalcea.flexypool.adaptor.HikariCPPoolAdapter;
import com.vladmihalcea.flexypool.connection.JdkConnectionProxyFactory;
import com.vladmihalcea.flexypool.metric.codahale.CodahaleMetrics;
import com.vladmihalcea.flexypool.strategy.IncrementPoolOnTimeoutConnectionAcquiringStrategy;
import com.vladmihalcea.flexypool.strategy.RetryConnectionAcquiringStrategy;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.sql.DataSource;
import liquibase.integration.spring.SpringLiquibase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.hibernate4.HibernateExceptionTranslator;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "ch.ithings.demo.repository.jpa")
@EnableTransactionManagement
@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware")
public class JpaConfiguration implements EnvironmentAware {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    private RelaxedPropertyResolver datasourcePropertyResolver;

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
        this.datasourcePropertyResolver = new RelaxedPropertyResolver(environment, "spring.datasource.");
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public DataSource dataSource() {
        com.vladmihalcea.flexypool.config.Configuration<HikariDataSource> configuration = new com.vladmihalcea.flexypool.config.Configuration.Builder<HikariDataSource>(
                datasourcePropertyResolver.getProperty("uniqueId"),
                poolingDataSource(),
                HikariCPPoolAdapter.FACTORY
        )
                .setMetricsFactory(CodahaleMetrics.UNIFORM_RESERVOIR_FACTORY)
                .setConnectionProxyFactory(JdkConnectionProxyFactory.INSTANCE)
                .setJmxEnabled(true)
                .setMetricLogReporterMillis(TimeUnit.SECONDS.toMillis(5))
                .build();
        log.debug("Configuring FlexyPool");
        return new FlexyPoolDataSource<HikariDataSource>(configuration,
                new IncrementPoolOnTimeoutConnectionAcquiringStrategy.Factory(5),
                new RetryConnectionAcquiringStrategy.Factory(2)
        );
    }

    private HikariDataSource poolingDataSource() {
        log.debug("Configuring HikariCP");
        HikariConfig config = new HikariConfig();
        config.setDataSourceClassName(datasourcePropertyResolver.getProperty("dataSourceClassName"));
        if (datasourcePropertyResolver.getProperty("url") == null || "".equals(datasourcePropertyResolver.getProperty("url"))) {
            config.addDataSourceProperty("databaseName", datasourcePropertyResolver.getProperty("databaseName"));
            config.addDataSourceProperty("serverName", datasourcePropertyResolver.getProperty("serverName"));
        } else {
            config.addDataSourceProperty("url", datasourcePropertyResolver.getProperty("url"));
        }
        config.addDataSourceProperty("user", datasourcePropertyResolver.getProperty("username"));
        config.addDataSourceProperty("password", datasourcePropertyResolver.getProperty("password"));
        config.setMaximumPoolSize(30);
        config.setConnectionTimeout(500);
        config.setDataSourceJNDI("jdbc/ithings");
        config.setConnectionTestQuery("SELECT 1;");
        HikariDataSource poolingDataSource = new HikariDataSource(config);
        return poolingDataSource;
    }

    @Bean
    public SpringLiquibase liquibase(DataSource datasource) {
        log.debug("Configuring Liquibase");
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(datasource);
        liquibase.setDefaultSchema("PUBLIC");
        liquibase.setChangeLog("classpath:config/liquibase/master.xml");
        liquibase.setContexts("development, production");
        return liquibase;
    }

    @Bean(name = {"org.springframework.boot.autoconfigure.AutoConfigurationUtils.basePackages"})
    public List<String> getBasePackages() {
        List<String> basePackages = new ArrayList<>();
        basePackages.add("ch.ithings.demo.domain");
        return basePackages;
    }

    @Bean
    public HibernateExceptionTranslator hibernateExceptionTranslator() {
        return new HibernateExceptionTranslator();
    }

    @Bean
    public Hibernate4Module hibernate4Module() {
        return new Hibernate4Module();
    }
    
}
