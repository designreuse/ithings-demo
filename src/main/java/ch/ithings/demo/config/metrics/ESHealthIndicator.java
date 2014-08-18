package ch.ithings.demo.config.metrics;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthStatus;
import org.elasticsearch.client.Client;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;

/**
 *
 * @author tph
 */
public class ESHealthIndicator extends AbstractHealthIndicator{
    
    private Client client;

    public ESHealthIndicator(Client client) {
        this.client = client;
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        try{
            ClusterHealthResponse response = client.admin().cluster().prepareHealth().execute().actionGet();
            if (response.getStatus().equals(ClusterHealthStatus.GREEN)){
                builder.up().withDetail("color", response.getStatus().toString());
            } else {
                builder.down().withDetail("color", response.getStatus().toString());
            }
        } catch (Exception e){
            builder.down(e);
        }
    }
    
}
