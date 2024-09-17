package io.sanctus.flavourpalette.security.handler;

import java.io.IOException;
import java.util.*;

import io.sanctus.flavourpalette.security.service.UserServiceImpl;
import io.sanctus.flavourpalette.user.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final UserServiceImpl userService;

  @Autowired
  public OAuth2LoginSuccessHandler(UserRepository userRepository, RoleRepository roleRepository, UserServiceImpl userService) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.userService = userService;
  }

  /* This is important and required for the redirection back to users previous page.
     Without this step, authorization will keep dumping users back to the index page.
     The session's variable "prevSite" is set in the LoginController file under user-login */
  @Override
  public void onAuthenticationSuccess(HttpServletRequest request,
                                      HttpServletResponse response,
                                      Authentication authentication) throws IOException {
//  This top portion checks if the user is already in the DB, if not then build a user object with no pass and add them to DB
    String userEmail = extractUserEmail(authentication);

    Optional<User> userResult = userRepository.findByUsername(userEmail);
    if (userResult.isEmpty()) {
      User newUser = new User();
      newUser.setUsername(userEmail);
      newUser.setAuthType("GOOGLE");

      Optional<Role> userRole = roleRepository.findByAuthority("USER");
      Set<Role> authorities = new HashSet<>();
      userRole.ifPresent(authorities::add);

      newUser.setAuthorities(authorities);
      userRepository.save(newUser);
    }
    UserDetails userDetails = userService.loadUserByUsername(userEmail);
    Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

    Authentication auth = new UsernamePasswordAuthenticationToken(userEmail, null, authorities);
    SecurityContext securityContext = SecurityContextHolder.getContext();
    securityContext.setAuthentication(auth);

    response.sendRedirect("/google-success");
  }

  private String extractUserEmail(Authentication auth) {
    String principalString = auth.getPrincipal().toString();
    int emailIndex = principalString.indexOf("email=");
    return auth.getPrincipal().toString().substring(emailIndex + 6, principalString.length()-2);
  }
}
