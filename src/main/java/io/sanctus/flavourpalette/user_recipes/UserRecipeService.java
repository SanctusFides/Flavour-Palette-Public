package io.sanctus.flavourpalette.user_recipes;

import io.sanctus.flavourpalette.bundle_recipe_ratings.BundledRecipeRating;
import java.util.List;

@SuppressWarnings("unused")
public interface UserRecipeService {
    List<BundledRecipeRating> buildUserRecipeList(String userId) ;
}
