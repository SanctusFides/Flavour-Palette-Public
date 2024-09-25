package io.sanctus.flavourpalette.service;

import io.sanctus.flavourpalette.cloudinary_impl.CloudinaryImpl;
import io.sanctus.flavourpalette.ingredient.Ingredient;
import io.sanctus.flavourpalette.ingredient.IngredientDTO;
import io.sanctus.flavourpalette.ingredient.IngredientRepository;
import io.sanctus.flavourpalette.ingredient.IngredientServiceImpl;
import io.sanctus.flavourpalette.recipe.Recipe;
import io.sanctus.flavourpalette.recipe.RecipeDTO;
import io.sanctus.flavourpalette.recipe.RecipeRepository;
import static org.assertj.core.api.Assertions.assertThat;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class IngredientServiceTest {

    @MockBean
    CloudinaryImpl cloudinary;
    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final IngredientServiceImpl ingredientService;
    private final RecipeDTO recipeDTO;
    private List<IngredientDTO> ingrDTOList;
    private final Recipe recipe;

    @Autowired
    IngredientServiceTest(RecipeRepository recipeRepository,
                          IngredientRepository ingredientRepository,
                          IngredientServiceImpl ingredientService) {
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
        this.ingredientService = ingredientService;
        this.recipeDTO = RecipeDTO.builder().recipeId("ABC").recipeName("Sandwich").build();
        this.recipe = Recipe.builder().recipeId("ABC").recipeName("Sandwich").build();
    }

    @BeforeEach
    void init() {
        recipeRepository.save(recipe);

        Ingredient ingr1 = new Ingredient();
        ingr1.setName("First");
        ingr1.setDbName("first");
        ingr1.setPrepType("diced");
        ingr1.setRecipe(recipe);
        ingredientRepository.save(ingr1);

        Ingredient ingr2 = new Ingredient();
        ingr2.setName("Second");
        ingr2.setDbName("second");
        ingr2.setPrepType("sliced");
        ingr2.setRecipe(recipe);
        ingredientRepository.save(ingr2);

        ingrDTOList = new ArrayList<>();
        ingrDTOList.add(IngredientDTO.builder().id(ingr1.getId()).name(ingr1.getName()).dbName(ingr1.getDbName())
                .prepType(ingr1.getPrepType()).recipe(recipe).build());
        ingrDTOList.add(IngredientDTO.builder().id(ingr2.getId()).name(ingr2.getName()).dbName(ingr2.getDbName())
                .prepType(ingr2.getPrepType()).recipe(recipe).build());
    }

    @AfterEach
    void tearDown() {
        ingredientRepository.flush();
        ingredientRepository.deleteAll();
        recipeRepository.flush();
        recipeRepository.deleteAllInBatch();
        ingrDTOList.clear();
    }

    @Test
    @Transactional
    void GetIngrDTOListByRecipeDTO_ReturnsIngredientsDTOList() {
        List<IngredientDTO> result = ingredientService.getIngrDTOListByRecipeDTO(recipeDTO);
        assertThat(result.getLast().getName()).isEqualTo("Second");
    }

    @Test
    @Transactional
    void DeleteIngrList_GetIngrList_ReturnsEmptyList() {
        List<IngredientDTO> preDelete = ingredientService.getIngrDTOListByRecipeDTO(recipeDTO);
        assertThat(preDelete).isNotNull().hasSize(2);
        assertThat(preDelete.getLast().getName()).isEqualTo("Second");

        ingredientService.deleteIngrList(ingrDTOList);
        List<IngredientDTO> postDelete = ingredientService.getIngrDTOListByRecipeDTO(recipeDTO);
        assertThat(postDelete).isEmpty();
    }

    @Test
    void SaveIngrList_ReturnSavedIngredientList() {
        List<IngredientDTO> savedResults = ingredientService.getIngrDTOListByRecipeDTO(recipeDTO);
        assertThat(savedResults).isNotNull().hasSize(2);
        assertThat(savedResults.getLast().getName()).isEqualTo("Second");
        assertThat(savedResults.getLast().getPrepType()).isEqualTo("sliced");
    }

    @Test
    @Transactional
    void GetRecipeIdsByIngrName_ReturnsRecipeList() {
        List<Recipe> recipeList = ingredientService.getRecipeIdsByIngrName(ingrDTOList.getFirst().getDbName());
        assertThat(recipeList).isNotNull().isNotEmpty();
        assertThat(recipeList.getFirst().getRecipeName()).isEqualTo(recipe.getRecipeName());
        assertThat(recipeList.getFirst().getRecipeId()).isEqualTo(recipe.getRecipeId());
    }

    @Test
    void GetCountByDBNameContaining_ReturnsCount() {
        int count = ingredientService.getCountBydbNameContaining(ingrDTOList.getFirst().getDbName());
        assertThat(count).isPositive().isEqualTo(1);
    }
}
