package io.sanctus.flavourpalette.service;

import io.sanctus.flavourpalette.cloudinary_impl.CloudinaryImpl;
import io.sanctus.flavourpalette.ingredient.IngredientDTO;
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
    private final IngredientServiceImpl ingredientService;
    private final RecipeDTO recipeDTO;
    private final Recipe recipe;
    private final List<IngredientDTO> ingrDTOList;

    @Autowired
    IngredientServiceTest(RecipeRepository recipeRepository,
                          IngredientServiceImpl ingredientService) {
        this.recipeRepository = recipeRepository;
        this.ingredientService = ingredientService;
        recipeDTO = RecipeDTO.builder().recipeId("ABC").recipeName("Sandwich").build();
        recipe = Recipe.builder().recipeId("ABC").recipeName("Sandwich").build();
        ingrDTOList = new ArrayList<>();
    }
    @BeforeEach
    void init() {
        recipeRepository.save(recipe);
        ingrDTOList.add(IngredientDTO.builder().id(1L).name("first").dbName("first").prepType("diced").recipe(recipe).build());
        ingrDTOList.add(IngredientDTO.builder().id(2L).name("second").prepType("sliced").recipe(recipe).build());
        ingredientService.saveIngrList(ingrDTOList);
    }
    @AfterEach
    void tearDown() {
        recipeRepository.deleteAll();
        ingrDTOList.clear();
        ingredientService.deleteIngrList(ingrDTOList);
    }
    
/*  THIS TEST CLASS NEEDS TO HAVE THE TESTS RUN INDIVIDUALLY BECAUSE FOR SOME REASON THE TEST WILL ALTER THE 
    ID VALUES AND THE ASSERTIONS FOR THOSE IDS WILL RETURN FALSE. IF RUN INDIVIDUALLY THEN THE IDS WILL RETURN CORRECT
    IT IS NOT CLEAR WHY THESE VALUES ARE CHANGING EVEN THOUGH THE DATA SHOULD BE TORN DOWN AND REMADE BETWEEN THE TESTS*/
    @Test
    void GetIngrDTOListByRecipeDTO_ReturnsIngredientsDTOList() {
        List<IngredientDTO> result = ingredientService.getIngrDTOListByRecipeDTO(recipeDTO);
        assertThat(result).isNotNull().hasSize(2);
        assertThat(result.getLast().getId()).isEqualTo(2L);
        assertThat(result.getLast().getName()).isEqualTo("second");
    }

    @Test
    void DeleteIngrList_GetIngrList_ReturnsEmptyList() {
        List<IngredientDTO> preDelete = ingredientService.getIngrDTOListByRecipeDTO(recipeDTO);
        assertThat(preDelete).isNotNull().hasSize(2);
        assertThat(preDelete.getLast().getId()).isEqualTo(2L);
        assertThat(preDelete.getLast().getName()).isEqualTo("second");
        
        ingredientService.deleteIngrList(ingrDTOList);
        List<IngredientDTO> postDelete = ingredientService.getIngrDTOListByRecipeDTO(recipeDTO);
        assertThat(postDelete).isEmpty();
    }

    @Test
    void SaveIngrList_ReturnSavedIngredientList() {
        List<IngredientDTO> savedResults = ingredientService.getIngrDTOListByRecipeDTO(recipeDTO);
        assertThat(savedResults).isNotNull().hasSize(2);
        assertThat(savedResults.getLast().getId()).isEqualTo(2L);
        assertThat(savedResults.getLast().getName()).isEqualTo("second");
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
