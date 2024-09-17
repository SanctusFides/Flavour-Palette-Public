package io.sanctus.flavourpalette.security.manager;

import io.sanctus.flavourpalette.security.service.UserServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@AllArgsConstructor
@SuppressWarnings("unused")
public class CustomAuthenticationManager implements AuthenticationManager {

    private final UserServiceImpl userServiceImpl;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UserDetails user = userServiceImpl.loadUserByUsername(authentication.getName());
/*      Requires checking if user already exists - critically important for throwing the errors that get caught in the
        unsuccessfulAuthentication function in the Auth Filter. */
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        if (!bCryptPasswordEncoder.matches(authentication.getCredentials().toString(), user.getPassword())) {
            throw new BadCredentialsException("Incorrect Password");
        }
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        return new UsernamePasswordAuthenticationToken(authentication.getName(), user.getPassword(), authorities);
    }
}
