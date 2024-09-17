package io.sanctus.flavourpalette.security.filter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class ExceptionHandlerFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (JWTVerificationException e) {
            Cookie cookie = new Cookie("auth_by_cookie", "");
            response.addCookie(cookie);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("JWT NOT VALID");
            response.sendRedirect("/");
            response.getWriter().flush();
        }
    }
}
