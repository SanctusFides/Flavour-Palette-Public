package io.sanctus.flavourpalette.security.controller;

import io.sanctus.flavourpalette.error.ErrorDTO;
import io.sanctus.flavourpalette.exception.UserAlreadyExistsException;
import io.sanctus.flavourpalette.security.dto.RegisterDTO;
import io.sanctus.flavourpalette.security.service.RegistrationService;
import io.sanctus.flavourpalette.user.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("SameReturnValue")
@Controller
public class RegistrationController {

    private final RegistrationService registrationService;

    @Autowired
    @SuppressWarnings("unused")
    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping("/signup")
    public String registerForm(Model model,
                               Principal principal,
                               HttpSession session,
                               HttpServletResponse response,
                               HttpServletRequest request,
                               @ModelAttribute("Flash") String string,
                               @ModelAttribute("UserExistsError") ErrorDTO errorDTO) {
        if ((principal != null && principal.getName() != null)) {
            String userId = principal.getName();
            model.addAttribute("loginId", userId);
        }
        if (session != null) {
            response.addCookie(new Cookie("prevURL", request.getHeader("referer")));
        }
        if (errorDTO != null && errorDTO.getMessage() != null){
                model.addAttribute("UserError", true);
        }
        model.addAttribute("regDetail", new RegisterDTO());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Validated @ModelAttribute("regDetail") RegisterDTO registerDTO,
                                     Principal principal,
                                     HttpServletRequest request,
                                     HttpServletResponse response) {
        try {
            User createdUser = registrationService.registerUser(registerDTO);
            if (createdUser != null) {
                registrationService.loginNewUser(createdUser, principal, request);
            }
            List<Cookie> cookieList = Arrays.stream(request.getCookies()).filter(cookie -> cookie.getName().equals("prevURL")).toList();
            if (createdUser != null
                    && !cookieList.isEmpty()
                    && !cookieList.getFirst().getValue().equals("http://localhost:8080/signup")
                    && !cookieList.getFirst().getValue().equals("http://localhost:8080/login")) {
                return "redirect:"+cookieList.getFirst().getValue();
            }
            return "redirect:/";
        } catch (UserAlreadyExistsException e) {
            throw new UserAlreadyExistsException(e.getMessage());
        }
    }

//  ExceptionHandler for existing user email will route here so the error can be attached to the form
    @GetMapping("/signup/error")
    public String redirect(RedirectAttributes redirectAttributes) {
        ErrorDTO error = new ErrorDTO();
        error.setMessage("UserExists");
        redirectAttributes.addFlashAttribute("UserExistsError", error);
        return "redirect:/signup";
    }
}
