package io.sanctus.flavourpalette.user_recipe_review;

import io.sanctus.flavourpalette.user_favorite_recipe.UserFavoriteRecipeServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;

@Controller
public class UserRecipeReviewController {

    private final UserRecipeReviewServiceImpl userRecipeReviewService;
    private final UserFavoriteRecipeServiceImpl userFavoriteRecipeService;

    @Autowired
    @SuppressWarnings("unused")
    public UserRecipeReviewController(UserRecipeReviewServiceImpl userRecipeReviewService,
                                      UserFavoriteRecipeServiceImpl userFavoriteRecipeService) {
        this.userRecipeReviewService = userRecipeReviewService;
        this.userFavoriteRecipeService = userFavoriteRecipeService;
    }

    @PostMapping("/saveUserRecipe")
    public ModelAndView saveUserRecipeReview(
            @RequestBody (required=false) MultiValueMap<String, String> formData,
            HttpServletRequest request,
            Principal principal) {

        if (principal == null || principal.getName() == null) {
            return new ModelAndView("index");
        }
/*      Pull the recipe's id from the recipe page url - this ensures that they can't submit an invalid url unless
        the recipe has been deleted while page was loaded - which will throw a recipe not found error and redirect
        user to the error page */
        String path = request.getHeader("referer");
        String recipeId = path.substring(path.lastIndexOf("/")+1);
        String userId = principal.getName();
        boolean favoriteStatus = userFavoriteRecipeService.favoritePreCheck(userId, recipeId);
/*      formData will only NOT be null if there is a value on the page for the favorite checkbox or the star int ratings
        so this means if they have NOT rated, but previously FAVORITED and now want to UNFAVORITE - the formData will
        be NULL. So if it is null, and they have previously favorited, then they would like to unfavorite this recipe.
        So the first if block is to remove this favorite from the db */
        if (favoriteStatus && formData == null) {
            userFavoriteRecipeService.deleteByUserIdAndRecipeId(userId, recipeId);
        }
        if (formData != null) {
            userRecipeReviewService.handleRecipeReview(formData, userId, recipeId);
            userFavoriteRecipeService.handleFavoriteCheckBox(formData, userId, recipeId);
        }
        return new ModelAndView("redirect:/recipe/" + recipeId);
    }
}
