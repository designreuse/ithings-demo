package ch.ithings.demo.repository.mongo;

import ch.ithings.demo.domain.OAuth2AuthenticationRefreshToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * 
 *
 * @author tph
 */
@Repository
public interface OAuth2RefreshTokenRepository extends MongoRepository<OAuth2AuthenticationRefreshToken, String>{
    
    public OAuth2AuthenticationRefreshToken findByTokenId(String tokenId);
    
}
