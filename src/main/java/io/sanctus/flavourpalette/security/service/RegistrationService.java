package io.sanctus.flavourpalette.security.service;

import io.sanctus.flavourpalette.exception.UserAlreadyExistsException;
import io.sanctus.flavourpalette.security.dto.RegisterDTO;
import io.sanctus.flavourpalette.user.Role;
import io.sanctus.flavourpalette.user.RoleRepository;
import io.sanctus.flavourpalette.user.User;
import io.sanctus.flavourpalette.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.*;

@Service
@SuppressWarnings("unused")
public class RegistrationService {

    private final UserServiceImpl userService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public RegistrationService(UserServiceImpl userService,UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public User registerUser(RegisterDTO request) {
//      Looks up user and if it doesn't already exist, add it. If it does exist, throw error to redirect back to form
        UserDetails existingLookUp = userService.loadUserByUsername(request.getUsername().toLowerCase().replace(" ",""));
        if (existingLookUp == null ) {
            Optional<Role> userRole = roleRepository.findByAuthority("USER");

            Set<Role> authorities = new HashSet<>();
            userRole.ifPresent(authorities::add);

            User newUser = new User();
            newUser.setUsername(request.getUsername().toLowerCase().replace(" ",""));
            newUser.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));
            newUser.setAuthorities(authorities);
            newUser.setAuthType("FORM");

            return userRepository.save(newUser);
        }
            throw new UserAlreadyExistsException("Email already in use");
    }

    public void loginNewUser(User newUser,
                             Principal principal,
                             HttpServletRequest request) {
        if (principal != null) {
            SecurityContextHolder.clearContext();
        }
        UserDetails userDetails = userService.loadUserByUsername(newUser.getUsername());
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        Authentication authentication = new UsernamePasswordAuthenticationToken(newUser.getUsername(), null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);

        HttpSession session = request.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
    }
}
