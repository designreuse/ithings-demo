package ch.ithings.demo.repository.es;

import ch.ithings.demo.domain.Product;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 *
 * @author tph
 */
public interface ProductRepository extends ElasticsearchRepository<Product,String>{
    List<Product> findByName(String name);
    List<Product> findByName(String name, Pageable pageable);
    List<Product> findByNameAndId(String name, String id);
}
