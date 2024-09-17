package io.sanctus.flavourpalette.recipe;

import io.sanctus.flavourpalette.exception.RecipeNotFoundException;
import io.sanctus.flavourpalette.ingredient.IngredientServiceImpl;
import io.sanctus.flavourpalette.instructions.InstructionsServiceImpl;
import io.sanctus.flavourpalette.recipe_formDTO.IngredientFormDTO;
import io.sanctus.flavourpalette.recipe_formDTO.InstructionsFormDTO;
import io.sanctus.flavourpalette.user_favorite_recipe.UserFavoriteRecipeServiceImpl;
import io.sanctus.flavourpalette.user_recipe_review.UserRecipeReviewServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import io.sanctus.flavourpalette.cloudinary_impl.CloudinaryImpl;

import java.security.Principal;

@SuppressWarnings("SameReturnValue")
@Controller
class RecipeController {

    private final RecipeServiceImpl recipeServiceImpl;
    private final InstructionsServiceImpl instructionsServiceImpl;
    private final IngredientServiceImpl ingredientServiceImpl;
    private final UserRecipeReviewServiceImpl userRecipeReviewServiceImpl;
    private final UserFavoriteRecipeServiceImpl userFavoriteRecipeServiceImpl;

    @Autowired
    @SuppressWarnings("unused")
    public RecipeController(RecipeServiceImpl recipeServiceImpl,
                            InstructionsServiceImpl instructionsServiceImpl,
                            IngredientServiceImpl ingredientServiceImpl,
                            UserRecipeReviewServiceImpl userRecipeReviewServiceImpl,
                            UserFavoriteRecipeServiceImpl userFavoriteRecipeServiceImpl) {
        this.recipeServiceImpl = recipeServiceImpl;
        this.instructionsServiceImpl = instructionsServiceImpl;
        this.ingredientServiceImpl = ingredientServiceImpl;
        this.userRecipeReviewServiceImpl = userRecipeReviewServiceImpl;
        this.userFavoriteRecipeServiceImpl = userFavoriteRecipeServiceImpl;
    }

    @GetMapping("/recipe/{recipeId}")
    public String getRecipe(@PathVariable String recipeId, Model model, Principal principal) {
        try {
            RecipeDTO recipeDTO = recipeServiceImpl.getRecipeDTOById(recipeId);
/*          Checks if the user logged in has the same username as the author - if so it
            will send the recipe page the permission to display edit and delete buttons */
            model.addAttribute("authorRights", recipeServiceImpl.checkAuthorRights(principal, recipeDTO));

            model.addAttribute("instrList", new InstructionsFormDTO(instructionsServiceImpl.getInstrDTOListByRecipeDTO(recipeDTO)));
            model.addAttribute("ingrList", new IngredientFormDTO(ingredientServiceImpl.getIngrDTOListByRecipeDTO(recipeDTO)));

//          This section checks if there is a value in the imageId on the DB - if so build the url and attach it to the model
            model.addAttribute("imageURL", recipeServiceImpl.getFullImageURL(recipeDTO));
            model.addAttribute("thumbURL", recipeServiceImpl.getThumbImageURL(recipeDTO));
//          RECIPE IS FULLY BUILT AT THIS POINT - attach it to the model now
            model.addAttribute("recipe", recipeDTO);

//          Checking to see if user is signed in to provide the info for their rating and favorites
            if ((principal != null && principal.getName() != null)) {
                String userId = principal.getName();
                model.addAttribute("loginId", userId);
                model.addAttribute("userRecipe", userRecipeReviewServiceImpl.getDTOByRecipeIdAndUserId(recipeId, userId));
/*              Do a lookup to see if this recipe is set as a favorite for the user
                If this recipe is not found for this user it will return a null object and set the checkbox to false */
                model.addAttribute("userFavorite", userFavoriteRecipeServiceImpl.getByUserIdAndRecipeId(userId, recipeId));
            }
            return "recipe";
        } catch (RecipeNotFoundException e) {
            throw new RecipeNotFoundException(e.getMessage());
        }
    }

    @PostMapping("/deleteRecipe/{recipeId}")
    public ModelAndView deleteUserRecipe(@PathVariable String recipeId, Principal principal) {
//      Deletions needed are the recipe from the recipe db and any favorites or ratings associated with it
        try {
//          Checks if the recipe has an image, if it does then delete it from the Cloudinary servers
            RecipeDTO recipeDTO = recipeServiceImpl.getRecipeDTOById(recipeId);
//          Important to first check if this recipe's author matches the signed-in user - if not kick them back to index
            if (principal == null || !principal.getName().equals(recipeDTO.getAuthorId())) {
                return new ModelAndView("index");
            }
            if (recipeDTO.getImageId() != null) {
                CloudinaryImpl.deleteImage(recipeDTO.getImageId());
            }
            recipeServiceImpl.deleteRecipeById(recipeId);
//          This handles deleting the ratings stored for all users for this recipe
            userRecipeReviewServiceImpl.deleteAllByRecipeId(recipeId);
            userFavoriteRecipeServiceImpl.handleDeleteAllByRecipeId(recipeId);
        } catch (Exception e) {
            return new ModelAndView("redirect:/error");
        }
        return new ModelAndView("redirect:/");
    }
}
