package io.sanctus.flavourpalette.security;

import io.sanctus.flavourpalette.security.filter.AuthenticationFilter;
import io.sanctus.flavourpalette.security.filter.ExceptionHandlerFilter;
import io.sanctus.flavourpalette.security.handler.CustomLogoutSuccessHandler;
import io.sanctus.flavourpalette.security.handler.OAuth2LoginSuccessHandler;
import io.sanctus.flavourpalette.security.manager.CustomAuthenticationManager;
import io.sanctus.flavourpalette.security.service.UserServiceImpl;
import io.sanctus.flavourpalette.user.RoleRepository;
import io.sanctus.flavourpalette.user.UserRepository;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@AllArgsConstructor
public class SecurityConfig {

	CustomAuthenticationManager customAuthenticationManager;
	RoleRepository roleRepository;
	UserRepository userRepository;
	UserServiceImpl userService;

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		AuthenticationFilter authenticationFilter = new AuthenticationFilter(customAuthenticationManager);
		authenticationFilter.setFilterProcessesUrl("/authenticate");
        http
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/","/css/**","/javascript/**", "/error", "/webjars/**","/images/**","/favicon.ico",
								"/auth/**","/search**","/recipe/**","/error/**",
								"/signup/**","/register","/login","/login-form", "/google-login","/oauth2/authorization/google", "/login-success").permitAll()
						.requestMatchers("/create-recipe","/edit/**","/account").hasAnyAuthority("ADMIN", "USER")
						.requestMatchers(HttpMethod.POST, SecurityConstants.REGISTER_PATH).permitAll()
						.anyRequest().authenticated())
				.addFilterBefore(new ExceptionHandlerFilter(), AuthenticationFilter.class)
				.addFilter(authenticationFilter)
				.cors(cors -> cors
						.configurationSource(corsConfigurationSource()))
				.csrf(AbstractHttpConfigurer::disable)
				.oauth2Login(login -> login
						.loginPage("/login")
						.successHandler(new OAuth2LoginSuccessHandler(userRepository, roleRepository, userService)))
				.logout(l -> l
						.clearAuthentication(true)
						.deleteCookies("prevURL", "JSESSIONID")
						.invalidateHttpSession(true)
						.logoutSuccessHandler(new CustomLogoutSuccessHandler())
						.logoutSuccessUrl("/").permitAll());
		return http.build();
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		final var configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(List.of("*"));
		configuration.setAllowedMethods(List.of("GET","POST"));
		configuration.setAllowedHeaders(List.of("*"));
		configuration.setExposedHeaders(List.of("*"));

		final var source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);

		return source;
	}

}
