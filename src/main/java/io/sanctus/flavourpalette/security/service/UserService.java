package io.sanctus.flavourpalette.security.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

//  This UserService is related Security login - do not use this for regular user actions on the app - make a new one
    UserDetails loadUserByUsername(String username);

}
