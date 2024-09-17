package io.sanctus.flavourpalette.user;

import io.sanctus.flavourpalette.error.ErrorDTO;
import io.sanctus.flavourpalette.exception.UserNotFoundException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@RestController
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountServiceImpl accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/account")
    public ModelAndView loadUserAccountPage(Principal principal, Model model) {
        try {
/*          Need to check if the user actually exists for the exception handling, but pass only the DTO, so we don't
            ever pass on the object with the password field to be sniffed out */
            User user = accountService.loadUserByUsername(principal.getName());
            AccountDTO accountDTO = new AccountDTO();
            accountDTO.setUsername(user.getUsername());
            accountDTO.setAuthType(user.getAuthType());
            PasswordDTO pass = new PasswordDTO();
            model.addAttribute("userData", accountDTO);
            model.addAttribute("loginId", user.getUsername());
            model.addAttribute("passDTO", pass);
            return new ModelAndView("account");
        } catch (UserNotFoundException ex) {
            throw new UserNotFoundException("User not found");
        }
    }

    @PostMapping("/update-user")
    public ModelAndView updatePassword(Principal principal,
                                       RedirectAttributes redirectAttributes,
                                       @ModelAttribute("passDTO") PasswordDTO passwordDTO) {
//      Update user is only used for updating password - so check if their 2 passwords match or else send error to page
        if (passwordDTO.getPassword().equals(passwordDTO.getConfirmPassword())) {
            accountService.handlePasswordUpdate(principal.getName(), passwordDTO.getPassword());
        } else {
            ErrorDTO error = new ErrorDTO("passMismatch");
            redirectAttributes.addFlashAttribute("passMismatch", error);
        }
        return new ModelAndView("redirect:/account");
    }

    @PostMapping("/delete-user")
    public ModelAndView deleteUserAccount(Principal principal,
                                  HttpServletRequest request) throws ServletException {
/*      Send their username to the account service to have their data deleted and then finally log them out
        Once data is removed and user is logged out, they are redirected back to the home page */
        accountService.handleDeleteUserByUsername(principal.getName());
        request.logout();
        return new ModelAndView("redirect:/");
    }

}
