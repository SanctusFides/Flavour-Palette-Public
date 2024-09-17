package io.sanctus.flavourpalette.service;

import io.sanctus.flavourpalette.bundle_recipe_ratings.BundledRecipeRating;
import io.sanctus.flavourpalette.cloudinary_impl.CloudinaryImpl;
import io.sanctus.flavourpalette.favorites.FavoritesServiceImpl;
import io.sanctus.flavourpalette.recipe.Recipe;
import io.sanctus.flavourpalette.recipe.RecipeDTO;
import io.sanctus.flavourpalette.recipe.RecipeRepository;
import io.sanctus.flavourpalette.user_favorite_recipe.UserFavoriteRecipe;
import io.sanctus.flavourpalette.user_favorite_recipe.UserFavoriteRecipeDTO;
import io.sanctus.flavourpalette.user_favorite_recipe.UserFavoriteRecipeRepository;
import io.sanctus.flavourpalette.user_recipe_review.UserRecipeReview;
import io.sanctus.flavourpalette.user_recipe_review.UserRecipeReviewRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class FavoritesServiceTest {

    @MockBean
    CloudinaryImpl cloudinary;

    private final FavoritesServiceImpl favoritesService;
    private final UserRecipeReviewRepository userRecipeReviewRepository;
    private final RecipeRepository recipeRepository;
    private final Recipe recipe1;
    private final Recipe recipe2;
    private final List<UserFavoriteRecipeDTO> favoriteRecipeDTOList;
    private final UserFavoriteRecipeRepository userFavoriteRecipeRepository;

    @Autowired
    public FavoritesServiceTest(FavoritesServiceImpl favoritesService,
                           RecipeRepository recipeRepository,
                           UserRecipeReviewRepository userRecipeReviewRepository,
                           UserFavoriteRecipeRepository userFavoriteRecipeRepository) {
        this.favoritesService = favoritesService;
        this.recipeRepository = recipeRepository;
        this.recipe1 = Recipe.builder().recipeId("ABC").build();
        this.recipe2 = Recipe.builder().recipeId("DEF").build();
        this.favoriteRecipeDTOList = new ArrayList<>();
        this.userRecipeReviewRepository = userRecipeReviewRepository;
        this.userFavoriteRecipeRepository = userFavoriteRecipeRepository;
    }
    @BeforeEach
    void init() {
        recipeRepository.save(recipe1);
        recipeRepository.save(recipe2);
        favoriteRecipeDTOList.add(new UserFavoriteRecipeDTO(1L,"test@test.com","ABC"));
        favoriteRecipeDTOList.add(new UserFavoriteRecipeDTO(2L,"test@test.com","DEF"));
        userRecipeReviewRepository.save(UserRecipeReview.builder().userId("test@test.com").recipeId("ABC").rating(5).build());
        userRecipeReviewRepository.save(UserRecipeReview.builder().userId("test@test.com").recipeId("DEF").rating(1).build());
    }
    @AfterEach
    void tearDown() {
        recipeRepository.deleteAll();
        favoriteRecipeDTOList.clear();
        userRecipeReviewRepository.deleteAll();
    }


    @Test
    void GetFavoriteRecipes_ReturnsRecipeDTOList() {
        List<RecipeDTO> recipeDTOList = favoritesService.getFavoriteRecipes(favoriteRecipeDTOList);
        assertThat(recipeDTOList).isNotEmpty().isNotNull();
        assertThat(recipeDTOList.getFirst().getRecipeId()).isEqualTo("ABC");
        assertThat(recipeDTOList.getLast().getRecipeId()).isEqualTo("DEF");
    }

    @Test
    void GetFavoriteRatings_ReturnsListOfIntegers() {
        List<Integer> ratingList = favoritesService.getFavoriteRatings(favoriteRecipeDTOList);
        assertThat(ratingList).isNotNull().isNotEmpty().hasSize(2);
        assertThat(ratingList.getFirst()).isEqualTo(5);
        assertThat(ratingList.getLast()).isEqualTo(1);
    }

    @Test
    void GetFavoriteRatings_ReturnsRatingOfZero() {
        List<UserFavoriteRecipeDTO> emptyList = new ArrayList<>();
        emptyList.add(new UserFavoriteRecipeDTO());
        List<Integer> ratingList = favoritesService.getFavoriteRatings(emptyList);
        assertThat(ratingList).isNotNull().isNotEmpty().hasSize(1);
        assertThat(ratingList.getFirst()).isZero();
    }

    @Test
    void BuildBundledDate_ReturnsListOfBundledRecipeRatings() {
        List<Integer> ratingList = favoritesService.getFavoriteRatings(favoriteRecipeDTOList);
        List<RecipeDTO> recipeDTOList = favoritesService.getFavoriteRecipes(favoriteRecipeDTOList);
        List<BundledRecipeRating> bundledData = favoritesService.buildBundledData(recipeDTOList, ratingList);
        assertThat(bundledData).isNotNull().isNotEmpty().hasSize(2);
        assertThat(bundledData.getFirst().getRating()).isEqualTo(5);
        assertThat(bundledData.getFirst().getRecipeDTO().getRecipeId()).isEqualTo("ABC");
        assertThat(bundledData.getLast().getRating()).isEqualTo(1);
        assertThat(bundledData.getLast().getRecipeDTO().getRecipeId()).isEqualTo("DEF");
    }

    @Test
    void GetBundledData_ReturnsBundledRecipeRatingList() {
        UserFavoriteRecipe favorite1 = UserFavoriteRecipe.builder().recipeId("ABC").userId("test@test.com").build();
        UserFavoriteRecipe favorite2 = UserFavoriteRecipe.builder().recipeId("DEF").userId("test@test.com").build();
        userFavoriteRecipeRepository.save(favorite1);
        userFavoriteRecipeRepository.save(favorite2);
        List<BundledRecipeRating> bundledData = favoritesService.getBundledData("test@test.com");
        assertThat(bundledData).isNotNull().isNotEmpty().hasSize(2);
        assertThat(bundledData.getFirst().getRating()).isEqualTo(5);
        assertThat(bundledData.getFirst().getRecipeDTO().getRecipeId()).isEqualTo("ABC");
        assertThat(bundledData.getLast().getRating()).isEqualTo(1);
        assertThat(bundledData.getLast().getRecipeDTO().getRecipeId()).isEqualTo("DEF");
    }
}
