package ch.ithings.demo.domain;

/**
 *
 * @author tph
 */
@org.springframework.data.elasticsearch.annotations.Document(indexName = "product", type = "product" , shards = 1, replicas = 0, indexStoreType = "memory", refreshInterval = "-1")
public class Product extends BaseDocument {
    
    private String name;
    private String description;
    private boolean enabled;

    public Product(String name, String description, boolean enabled) {
        super();
        this.name = name;
        this.description = description;
        this.enabled = enabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}