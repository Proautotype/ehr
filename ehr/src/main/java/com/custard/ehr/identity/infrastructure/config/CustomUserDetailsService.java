package com.custard.ehr.identity.infrastructure.config;

import com.custard.ehr.identity.application.ports.UserRepository;
import com.custard.ehr.identity.domain.User;
import com.custard.ehr.identity.domain.UserRole;
import com.custard.ehr.shared.security.Permission;
import com.custard.ehr.shared.security.Role;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        log.debug("Loading user security details for username: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Authentication failed. User {} not found", username);
                    return new UsernameNotFoundException("User not found");
                });

        Set<SimpleGrantedAuthority> authorities = new HashSet<>();

        for (UserRole userRole : user.getRoles()) {
            Role role = userRole.getRole().getName();

            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()));

            for (Permission permission : role.permissions()) {
                authorities.add(new SimpleGrantedAuthority(permission.name()));
            }
        }

        log.debug(
                "Loaded {} authorities for user {}",
                authorities.size(),
                username
        );

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPasswordHash())
                .authorities(authorities)
                .disabled(!user.isActive())
                .build();
    }
}