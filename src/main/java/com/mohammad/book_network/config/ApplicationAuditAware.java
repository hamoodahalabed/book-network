package com.mohammad.book_network.config;

import com.mohammad.book_network.user.User;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;


//Spring need to have bean of type AuditorAware in order to use it for fetching current auditor
public class ApplicationAuditAware implements AuditorAware<Integer> {

    public Optional<Integer> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }

         User userPrincipal = (User) authentication.getPrincipal();

        return Optional.ofNullable(userPrincipal.getId());
    }
}
