package com.custard.ehr.identity.infrastructure.config;

import com.custard.ehr.identity.application.ports.UserRepository;
import com.custard.ehr.identity.domain.User;
import com.custard.ehr.identity.domain.UserRole;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        var authorities = user.getRoles()
                .stream()
                .map(UserRole::getRole)
                .map(role -> "ROLE_" + role.getName().name())
                .map(org.springframework.security.core.authority.SimpleGrantedAuthority::new);

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPasswordHash())
                .authorities(authorities.toList())
                .disabled(!user.isActive())
                .build();
    }
}
