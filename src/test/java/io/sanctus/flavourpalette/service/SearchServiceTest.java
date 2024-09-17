package io.sanctus.flavourpalette.service;

import io.sanctus.flavourpalette.cloudinary_impl.CloudinaryImpl;
import io.sanctus.flavourpalette.ingredient.Ingredient;
import io.sanctus.flavourpalette.ingredient.IngredientRepository;
import io.sanctus.flavourpalette.recipe.Recipe;
import io.sanctus.flavourpalette.recipe.RecipeRepository;
import io.sanctus.flavourpalette.search.SearchServiceImpl;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class SearchServiceTest {
    @MockBean
    CloudinaryImpl cloudinary;

    private final SearchServiceImpl searchService;
    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final Recipe recipe1;
    private final Recipe recipe2;

    @Autowired
    SearchServiceTest(SearchServiceImpl searchService,
                      RecipeRepository recipeRepository,
                      IngredientRepository ingredientRepository) {
        this.searchService = searchService;
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
        this.recipe1 = Recipe.builder().recipeId("ABC").recipeName("Beef Sandwich").dbName("beef-sandwich").build();
        this.recipe2 = Recipe.builder().recipeId("DEF").recipeName("Ham Sandwich").dbName("ham-sandwich").build();
    }
    @BeforeEach
    void init(){
        recipeRepository.save(recipe1);
        recipeRepository.save(recipe2);
    }
    @AfterEach
    void tearDown() {
        recipeRepository.deleteAll();
    }

    @Test
    void GetRecipesByName_ReturnsListOfRecipes() {
        List<Recipe> result = searchService.getRecipesByName("Sandwich", 0, 10);
        assertThat(result).isNotEmpty();
        assertThat(result.getFirst().getRecipeId()).isEqualTo("ABC");
        assertThat(result.getLast().getRecipeId()).isEqualTo("DEF");
    }

    @Test
    void GetRecipeCountByName_ReturnsCountOfRecipes() {
        int result = searchService.getRecipeCountByName("sandwich");
        assertThat(result).isEqualTo(2);
    }

    @Test
    @Transactional
    void GetRecipesByIngredient_ReturnsListOfRecipesContainingIngr() {
        Ingredient ingr1 = Ingredient.builder().id(1L).name("Salt").dbName("salt").recipe(recipe1).build();
        Ingredient ingr2 = Ingredient.builder().id(2L).name("Salt").dbName("salt").recipe(recipe2).build();
        ingredientRepository.save(ingr1);
        ingredientRepository.save(ingr2);

        List<Recipe> result = searchService.getRecipesByIngredient("Salt");
        assertThat(result).isNotNull().isNotEmpty().hasSize(2);
        assertThat(result.getFirst().getRecipeId()).isEqualTo("ABC");
        assertThat(result.getLast().getRecipeId()).isEqualTo("DEF");
    }

    @Test
    void GetIngredientCountByName_ReturnsCountOfIngredientsByName() {
        Ingredient ingr1 = Ingredient.builder().id(1L).name("Salt").dbName("salt").recipe(recipe1).build();
        Ingredient ingr2 = Ingredient.builder().id(2L).name("Salt").dbName("salt").recipe(recipe2).build();
        ingredientRepository.save(ingr1);
        ingredientRepository.save(ingr2);
        int result = searchService.getIngredientCountByName("Salt");
        assertThat(result).isEqualTo(2);
    }

}
