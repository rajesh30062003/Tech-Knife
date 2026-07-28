package com.techknife.backend.audit;

import com.techknife.backend.constant.AppConstants;
import com.techknife.security.UserPrincipal;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || 
            "anonymousUser".equals(authentication.getPrincipal())) {
            return Optional.of(AppConstants.SYSTEM_USER);
        }

        if (authentication.getPrincipal() instanceof UserPrincipal principal) {
            return Optional.ofNullable(principal.getId());
        }

        return Optional.of(authentication.getName());
    }
}
