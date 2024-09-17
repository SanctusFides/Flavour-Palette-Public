package io.sanctus.flavourpalette.security.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("ALL")
@Controller
public class LoginController {

    private final String prevURL;
    private final String redirectURL;

    @Autowired
    public LoginController() {
        this.prevURL = "prevURL";
        this.redirectURL = "redirect:/";
    }

    @GetMapping("/login")
    public String login(Model model,
                        Principal user,
                        HttpServletRequest request,
                        HttpServletResponse response,
                        HttpSession session,
                        @RequestParam(value = "error", required = false) String error){
        if (user != null ){
            String userId = user.getName();
            model.addAttribute("loginId", userId);
        }
        if (error != null) {
            if (error.equals("user")) {
                model.addAttribute("UserDetailsError", true);
            } else if (error.equals("password")){
                model.addAttribute("UserDetailsError", true);
            } else {
                model.addAttribute("GeneralError", true);
            }
        }
/*      If session is null, then they went straight to the login page without visiting any other page.
        So if the session is not null then we want to capture the previous pages url - extra crucial with Google oauth*/
        if (session != null) {
            response.addCookie(new Cookie(prevURL, request.getHeader("referer")));
        }
        return "login-form";
    }

    @GetMapping("/google-login")
    public String googleLogIn(HttpServletRequest request, HttpSession session) {
        return "redirect:/oauth2/authorization/google";
    }

    @GetMapping("/google-success")
    public ModelAndView googleLoginSuccess(HttpServletRequest request,
                                     HttpServletResponse response,
                                     Principal principal,
                                     Authentication authResult) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authResult);
        HttpSession session = request.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);

        List<Cookie> cookieList = Arrays.stream(request.getCookies()).filter(cookie -> cookie.getName().equals(prevURL)).toList();
        if (!cookieList.isEmpty()) {
            return new ModelAndView("redirect:"+cookieList.getFirst().getValue());
        }
        return new ModelAndView(redirectURL);
    }

    @GetMapping("/login-success")
    public ModelAndView loginSuccess(HttpServletRequest request) {
//      Checks for a cookie set containing the url for the page prior to the visiting the login page to redirect to
        List<Cookie> cookieList = Arrays.stream(request.getCookies()).filter(cookie -> cookie.getName().equals(prevURL)).toList();
        if (!cookieList.isEmpty()) {
            if (cookieList.getFirst().getValue().equals("http://localhost:8080/login") || cookieList.getFirst().getValue().equals("http://localhost:8080/signup")) {
                return new ModelAndView(redirectURL);
            }
            return new ModelAndView("redirect:"+cookieList.getFirst().getValue());
        }
        return new ModelAndView(redirectURL);
    }
}
