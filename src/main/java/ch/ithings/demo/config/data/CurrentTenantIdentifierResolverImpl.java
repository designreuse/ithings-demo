package ch.ithings.demo.config.data;

import ch.ithings.demo.security.CustomUserDetails;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 *
 * @author tph
 *
 * TODO: Implement service to identify tenant like:
 * userService.getCurrentlyAuthUser().getTenantId();
 */
@Component
public class CurrentTenantIdentifierResolverImpl implements CurrentTenantIdentifierResolver {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    @Override
    public String resolveCurrentTenantIdentifier() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String tenantId = "public";
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
            tenantId = user.getTenantId();
        }
        log.debug("Set current tenantId: {}", tenantId);
        return tenantId;
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }

}
