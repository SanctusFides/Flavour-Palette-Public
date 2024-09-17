package io.sanctus.flavourpalette.service;

import io.sanctus.flavourpalette.bundle_recipe_ratings.BundledRecipeRating;
import io.sanctus.flavourpalette.cloudinary_impl.CloudinaryImpl;
import io.sanctus.flavourpalette.recipe.Recipe;
import io.sanctus.flavourpalette.recipe.RecipeRepository;
import io.sanctus.flavourpalette.user_recipe_review.UserRecipeReview;
import io.sanctus.flavourpalette.user_recipe_review.UserRecipeReviewRepository;
import io.sanctus.flavourpalette.user_recipes.UserRecipeServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;


import java.util.List;

@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class UserRecipeServiceTest {
    @MockBean
    CloudinaryImpl cloudinary;

    private final UserRecipeServiceImpl userRecipeService;
    private final RecipeRepository recipeRepository;
    private final UserRecipeReviewRepository userRecipeReviewRepository;

    @Autowired
    UserRecipeServiceTest(UserRecipeServiceImpl userRecipeService,
                          RecipeRepository recipeRepository,
                          UserRecipeReviewRepository userRecipeReviewRepository) {
        this.userRecipeService = userRecipeService;
        this.recipeRepository = recipeRepository;
        this.userRecipeReviewRepository = userRecipeReviewRepository;
    }

    @Test
    void BuildUserRecipeList_ReturnsBundleOfRecipesAndRatings(){
        Recipe recipe1 = Recipe.builder().recipeId("ABC").authorId("test@test.com").recipeName("Sandwich")
                .dbName("sandwich").build();
        recipeRepository.save(recipe1);
        UserRecipeReview review1 = UserRecipeReview.builder().userId("test@test.com").rating(5).recipeId("ABC").build();
        userRecipeReviewRepository.save(review1);
        Recipe recipe2 = Recipe.builder().recipeId("DEF").authorId("test@test.com").recipeName("Sandwich")
                .dbName("sandwich").build();
        recipeRepository.save(recipe2);
        UserRecipeReview review2 = UserRecipeReview.builder().userId("test@test.com").rating(1).recipeId("DEF").build();
        userRecipeReviewRepository.save(review2);

        List<BundledRecipeRating> result = userRecipeService.buildUserRecipeList("test@test.com");
        assertThat(result).isNotNull().isNotEmpty().hasSize(2);
        assertThat(result.getFirst().getRecipeDTO().getRecipeId()).isEqualTo("ABC");
        assertThat(result.getFirst().getRating()).isEqualTo(5);
        assertThat(result.getLast().getRecipeDTO().getRecipeId()).isEqualTo("DEF");
        assertThat(result.getLast().getRating()).isEqualTo(1);
    }
}
