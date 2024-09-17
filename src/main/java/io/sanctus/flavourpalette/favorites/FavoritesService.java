package io.sanctus.flavourpalette.favorites;

import io.sanctus.flavourpalette.bundle_recipe_ratings.BundledRecipeRating;
import io.sanctus.flavourpalette.recipe.RecipeDTO;
import io.sanctus.flavourpalette.user_favorite_recipe.UserFavoriteRecipeDTO;

import java.util.List;

@SuppressWarnings("unused")
public interface FavoritesService {
    List<BundledRecipeRating> getBundledData (String userId) ;
    List<RecipeDTO> getFavoriteRecipes(List<UserFavoriteRecipeDTO> favoriteRecipeDTOList);
    List<Integer> getFavoriteRatings(List<UserFavoriteRecipeDTO> favoriteRecipeDTOList);
    List<BundledRecipeRating> buildBundledData(List<RecipeDTO> favoriteRecipes, List<Integer> favoriteRatings);
}
