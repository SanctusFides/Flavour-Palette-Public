package io.sanctus.flavourpalette.repository;

import io.sanctus.flavourpalette.ingredient.Ingredient;
import io.sanctus.flavourpalette.ingredient.IngredientRepository;
import io.sanctus.flavourpalette.recipe.Recipe;
import io.sanctus.flavourpalette.recipe.RecipeRepository;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class IngredientRepositoryTest {

    private final IngredientRepository ingredientRepository;
    private final RecipeRepository recipeRepository;
    private final Ingredient ingredient1;
    private final Ingredient ingredient2;
    private final Ingredient ingredient3;
    private final Recipe recipe1;
    private final Recipe recipe2;

    @Autowired
    IngredientRepositoryTest(IngredientRepository ingredientRepository, RecipeRepository recipeRepository) {
        this.ingredientRepository = ingredientRepository;
        this.recipeRepository = recipeRepository;
        this.recipe1 = Recipe.builder().recipeId("ABC").recipeName("Sandwich").build();
        this.recipe2 = Recipe.builder().recipeId("DEF").recipeName("Sandwich").build();
        this.ingredient1 = Ingredient.builder().name("Tomato").size("1").size("ounce").prepType("diced").recipe(recipe1)
                .build();
        this.ingredient2 = Ingredient.builder().name("Onion").size("2").size("ounce").prepType("diced").recipe(recipe1)
                .build();
        this.ingredient3 = Ingredient.builder().name("Onion").size("2").size("ounce").prepType("diced").recipe(recipe2)
                .build();
    }
    @BeforeEach
    void init() {
        recipeRepository.save(recipe1);
    }

    @AfterEach
    void tearDown() {
        recipeRepository.deleteAll();
    }

    @Test
    void Save_SavesIngredient_FindAll_ReturnsSavedIngredient() {
        ingredientRepository.save(ingredient1);
        List<Ingredient> resultList = ingredientRepository.findAll();
        assertThat(resultList).isNotEmpty().hasSize(1);
        assertThat(resultList.getFirst()).isNotNull();
        assertThat(resultList.getFirst().getName()).isEqualTo("Tomato");
    }

    @Test
    void SaveAll_FindAll_ReturnsSavedIngredients() {
        ingredientRepository.deleteAll();

        List<Ingredient> ingredientList = new ArrayList<>();
        ingredientList.add(ingredient1);
        ingredientList.add(ingredient2);
        ingredientRepository.saveAll(ingredientList);
        List<Ingredient> resultList = ingredientRepository.findAll();
        assertThat(resultList).isNotNull()
                .isNotEmpty()
                .hasSize(2);
        assertThat(resultList.get(1).getName()).isEqualTo("Onion");
    }

    @Test
    void DeleteAll_FindAll_ReturnsEmptyList() {
        List<Ingredient> ingredientList = new ArrayList<>();
        ingredientList.add(ingredient1);

        ingredientRepository.deleteAll(ingredientList);
        List<Ingredient> empptyList = ingredientRepository.findAll();
        assertThat(empptyList).isEmpty();
    }

    @Test
    void FindAllByRecipeRecipeId_ReturnsListFoundByRecipeId() {
        recipeRepository.save(recipe2);
        List<Ingredient> ingredientList = new ArrayList<>();
        ingredientList.add(ingredient1);
        ingredientList.add(ingredient2);
        ingredientList.add(ingredient3);

        ingredientRepository.saveAll(ingredientList);
        Optional<List<Ingredient>> resultListOpt = ingredientRepository.findAllByRecipeRecipeId("ABC");
        List<Ingredient> resultListActual = null;
        if (resultListOpt.isPresent()) {
            resultListActual = resultListOpt.get();
        }
        assertThat(resultListActual).isNotNull()
                .isNotEmpty()
                .hasSize(2);
        assertThat(resultListActual.get(1).getRecipe().getRecipeId()).isEqualTo("ABC");
    }
}
