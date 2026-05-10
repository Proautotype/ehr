package com.custard.ehr.identity;

import com.custard.ehr.identity.application.ports.UserRepository;
import com.custard.ehr.identity.domain.AppUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

public interface IdentityLookupService {
    Optional<AppUser> getUserByEmail(String email);
    Optional<AppUser> getByUsername(String username);
}

@Service
@Slf4j
class IdentityLookupServiceImpl implements IdentityLookupService{

    private final UserRepository userRepository;

    IdentityLookupServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<AppUser> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<AppUser> getByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}