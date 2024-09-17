package io.sanctus.flavourpalette.user_recipes;

import io.sanctus.flavourpalette.bundle_recipe_ratings.BundledRecipeRating;
import io.sanctus.flavourpalette.recipe.Recipe;
import io.sanctus.flavourpalette.recipe.RecipeServiceImpl;
import io.sanctus.flavourpalette.user_recipe_review.UserRecipeReviewDTO;
import io.sanctus.flavourpalette.user_recipe_review.UserRecipeReviewServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserRecipeServiceImpl implements UserRecipeService {

    private final RecipeServiceImpl recipeServiceImpl;
    private final UserRecipeReviewServiceImpl userRecipeReviewServiceImpl;

    @Autowired
    @SuppressWarnings("unused")
    public UserRecipeServiceImpl(RecipeServiceImpl recipeServiceImpl,
                                       UserRecipeReviewServiceImpl userRecipeReviewServiceImpl) {
        this. recipeServiceImpl = recipeServiceImpl;
        this.userRecipeReviewServiceImpl = userRecipeReviewServiceImpl;
    }

/*  Builds out a list of a users recipes while also looking up any ratings they may have applied to the recipe to be
    displayed on the recipe card with the star rating */
    @Override
    public List<BundledRecipeRating> buildUserRecipeList(String userId) {
        List<Integer> userRatings = new ArrayList<>();

        List<Recipe> userRecipeList = recipeServiceImpl.getRecipesByAuthorId(userId);
        userRecipeList.forEach( recipe -> {
            UserRecipeReviewDTO userRating = userRecipeReviewServiceImpl.getDTOByRecipeIdAndUserId(recipe.getRecipeId()
                    , userId);
            if (userRating != null) {
                userRatings.add(userRating.getRating());
            } else {
                userRatings.add(0);
            }
        });
        List<BundledRecipeRating> bundledData = new ArrayList<>();
        for (int i = 0; i < userRecipeList.size(); i++) {
            bundledData.add(new BundledRecipeRating(recipeServiceImpl.mapToDTO(userRecipeList.get(i)), userRatings.get(i)));
        }
        return bundledData;
    }
}
