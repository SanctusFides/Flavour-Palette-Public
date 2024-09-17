package io.sanctus.flavourpalette.exception;

import io.sanctus.flavourpalette.error.ErrorDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.security.Principal;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String loginId = "loginId";
    private static final String errorRedirectURL = "redirect:/error";

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ModelAndView handleUserAlreadyExistsException() {
        return new ModelAndView("redirect:/signup/error");
    }

    @ExceptionHandler(RecipeNotFoundException.class)
    public ModelAndView handleRecipeNotFoundException(Principal principal,
                                                      RedirectAttributes redirectAttributes) {
        ErrorDTO error = new ErrorDTO("RecipeNotFound");
        redirectAttributes.addFlashAttribute("RecipeNotFound", error);
        if (principal != null) {
            redirectAttributes.addFlashAttribute(loginId, principal.getName());
        }
        return new ModelAndView(errorRedirectURL);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ModelAndView handleNoResourceFoundException(Principal principal,
                                                       RedirectAttributes redirectAttributes) {
        ErrorDTO error = new ErrorDTO("PageNotFound");
        redirectAttributes.addFlashAttribute("PageNotFound", error);
        if (!principal.getName().isBlank()) {
            redirectAttributes.addFlashAttribute(loginId, principal.getName());
        }
        return new ModelAndView(errorRedirectURL);
    }

    @ExceptionHandler(UserReviewSaveException.class)
    public ModelAndView handleUserReviewSaveException(Principal principal,
                                                       RedirectAttributes redirectAttributes) {
        ErrorDTO error = new ErrorDTO("SaveReviewError");
        redirectAttributes.addFlashAttribute("SaveReviewError", error);
        if (!principal.getName().isBlank()) {
            redirectAttributes.addFlashAttribute(loginId, principal.getName());
        }
        return new ModelAndView(errorRedirectURL);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ModelAndView handlerUserNotFoundException(HttpServletRequest request,
                                                     RedirectAttributes redirectAttributes) throws ServletException {
        request.logout();
        ErrorDTO error = new ErrorDTO("UserNotFound");
        redirectAttributes.addFlashAttribute("LoadUserFailed", error);
        return new ModelAndView("redirect:/login");
    }

    @ExceptionHandler(FormNotValidException.class)
    public ModelAndView handleFormNotValidException(FormNotValidException formNotValidException,
                                                    RedirectAttributes redirectAttributes) {
        ErrorDTO error = new ErrorDTO(formNotValidException.getMissingField());
        error.setMessage(formNotValidException.getMissingField());

        redirectAttributes.addFlashAttribute("MissingValueError", error);
        return new ModelAndView("redirect:/create-recipe");
    }

    @ExceptionHandler(CloudinaryException.class)
    public ModelAndView handleCloudinaryException(RedirectAttributes redirectAttributes) {
        ErrorDTO error = new ErrorDTO("CloudinaryError");
        redirectAttributes.addFlashAttribute("CloudinaryError", error);
        return new ModelAndView(errorRedirectURL);
    }

    @ExceptionHandler(UserFavoriteException.class)
    public ModelAndView handleUserFavoriteException(RedirectAttributes redirectAttributes) {
        ErrorDTO error = new ErrorDTO("UserFavorite");
        redirectAttributes.addFlashAttribute("UserFavorite", error);
        return new ModelAndView(errorRedirectURL);
    }
}

