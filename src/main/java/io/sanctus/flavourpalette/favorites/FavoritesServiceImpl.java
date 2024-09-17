package io.sanctus.flavourpalette.favorites;

import io.sanctus.flavourpalette.bundle_recipe_ratings.BundledRecipeRating;
import io.sanctus.flavourpalette.recipe.RecipeDTO;
import io.sanctus.flavourpalette.recipe.RecipeServiceImpl;
import io.sanctus.flavourpalette.user_favorite_recipe.UserFavoriteRecipeDTO;
import io.sanctus.flavourpalette.user_favorite_recipe.UserFavoriteRecipeServiceImpl;
import io.sanctus.flavourpalette.user_recipe_review.UserRecipeReviewDTO;
import io.sanctus.flavourpalette.user_recipe_review.UserRecipeReviewServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FavoritesServiceImpl implements FavoritesService {

    private final UserFavoriteRecipeServiceImpl userFavoriteRecipeService;
    private final RecipeServiceImpl recipeService;
    private final UserRecipeReviewServiceImpl userRecipeReviewService;

    @Autowired
    @SuppressWarnings("unused")
    public FavoritesServiceImpl(UserFavoriteRecipeServiceImpl userFavoriteRecipeService,
                                RecipeServiceImpl recipeService,
                                UserRecipeReviewServiceImpl userRecipeReviewService) {
        this.userRecipeReviewService = userRecipeReviewService;
        this.recipeService = recipeService;
        this.userFavoriteRecipeService = userFavoriteRecipeService;
    }

    @Override
    public List<BundledRecipeRating> getBundledData (String userId)  {
        List<UserFavoriteRecipeDTO> userFavoriteDTOList = userFavoriteRecipeService.getAllByUserId(userId);
        /* Creating 2 lists to hold the recipes and ratings for each of the favorites - this will be combined into 1 below
           First creating the recipe object and adding it to the list of retrieved recipes */
        List<RecipeDTO> favoriteRecipes = getFavoriteRecipes(userFavoriteDTOList);
        // Creating the user favorite rating - if none exist then fills in with 0 and adds to list
        List<Integer> favoriteRatings = getFavoriteRatings(userFavoriteDTOList);
        // Returning a list of the bundled data which stores the Recipe obj and the Rating int to pass through in 1 list to the page
        return buildBundledData(favoriteRecipes,favoriteRatings);
    }

    public List<RecipeDTO> getFavoriteRecipes(List<UserFavoriteRecipeDTO> favoriteRecipeDTOList) {
        List<RecipeDTO> favoriteRecipes = new ArrayList<>();
        favoriteRecipeDTOList.forEach(favorite ->
            // First retrieving the recipe object and adding it to the list of retrieved recipes
                favoriteRecipes.add(recipeService.getRecipeDTOById(favorite.getRecipeId()))
        ); return favoriteRecipes;
    }

    @Override
    public List<Integer> getFavoriteRatings(List<UserFavoriteRecipeDTO> favoriteRecipeDTOList) {
        List<Integer> favoriteRatings = new ArrayList<>();
        favoriteRecipeDTOList.forEach(favorite -> {
            UserRecipeReviewDTO userRatingDTO = userRecipeReviewService.getDTOByRecipeIdAndUserId(
                    favorite.getRecipeId(),favorite.getUserId());
            if (userRatingDTO != null) {
                favoriteRatings.add(userRatingDTO.getRating());
            } else {
                favoriteRatings.add(0);
            }});
        return favoriteRatings;
    }

    @Override
    public List<BundledRecipeRating> buildBundledData(List<RecipeDTO> favoriteRecipes, List<Integer> favoriteRatings) {
        List<BundledRecipeRating> bundledData = new ArrayList<>();
        for (int i = 0; i < favoriteRecipes.size(); i++) {
            bundledData.add(new BundledRecipeRating(favoriteRecipes.get(i), favoriteRatings.get(i)));
        }
        return bundledData;
    }
}
