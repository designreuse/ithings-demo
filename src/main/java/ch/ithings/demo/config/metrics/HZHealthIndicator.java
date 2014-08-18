package ch.ithings.demo.config.metrics;

import ch.ithings.demo.config.CacheConfiguration;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;

/**
 *
 * @author tph
 */
public class HZHealthIndicator extends AbstractHealthIndicator{

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        if (CacheConfiguration.getHazelcastInstance().getLifecycleService().isRunning()) {
            builder.up();
        } else {
            builder.down();
        }
    }
    
}
