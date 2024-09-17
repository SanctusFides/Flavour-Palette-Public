package io.sanctus.flavourpalette.user_recipes;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import io.sanctus.flavourpalette.bundle_recipe_ratings.BundledRecipeRating;

// This is for the recipes that a user has created, this is for the page that will group all of theirs together in a list
@Controller
public class UserRecipesController {

  private final UserRecipeServiceImpl userRecipeServiceImpl;
  @Autowired
  @SuppressWarnings("unused")
  public UserRecipesController(UserRecipeServiceImpl userRecipeServiceImpl) {
    this.userRecipeServiceImpl = userRecipeServiceImpl;
  }

  @GetMapping("/user-recipes")
  public String getUserRecipes(Principal principal, Model model)  {
    if (principal == null || principal.getName() == null) {
      return "index";
    }
    String userId = principal.getName();
    model.addAttribute("loginId", userId);
    // Builds a list of bundled data - the bundle being their own recipes and the star ratings they may have
    List<BundledRecipeRating> bundledData = userRecipeServiceImpl.buildUserRecipeList(userId);
    model.addAttribute("userRecipeList",bundledData);
    return "user-recipes";
  }
}
