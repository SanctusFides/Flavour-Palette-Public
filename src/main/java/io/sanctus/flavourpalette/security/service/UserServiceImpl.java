package io.sanctus.flavourpalette.security.service;

import io.sanctus.flavourpalette.user.User;
import io.sanctus.flavourpalette.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

//    Normally I would keep this in the User feature folder - but this service class relates to security
    private final UserRepository userRepository;

    @SuppressWarnings("unused")
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.orElse(null);
    }
}
