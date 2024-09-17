package io.sanctus.flavourpalette.repository;


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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class RecipeRepositoryTest {

    private final RecipeRepository recipeRepository;
    private final Recipe recipe1;
    private final Recipe recipe2;
    private final Recipe recipe3;

    @Autowired
    public RecipeRepositoryTest(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
        this.recipe1 = Recipe.builder().recipeId("ABC").recipeName("Sandwich").dbName("sandwich").prepTime(5)
                .cookTime(5).totalTime(10).authorId("me@me.com").description("Food").build();
        this.recipe2 = Recipe.builder().recipeId("DEF").recipeName("Salad").dbName("salad").prepTime(5)
                .cookTime(5).totalTime(10).authorId("test@test.com").description("Food").build();
        this.recipe3 = Recipe.builder().recipeId("GHI").recipeName("Sandwich!").dbName("sandwich!").prepTime(5)
                .cookTime(5).totalTime(10).authorId("me@me.com").description("Food").build();
    }
    @BeforeEach
    void init() {
        recipeRepository.save(recipe1);
        recipeRepository.save(recipe2);
        recipeRepository.save(recipe3);
    }
    @AfterEach
    void tearDown() {
        recipeRepository.deleteAll();
    }
    
    @Test
    void Save_ReturnsSavedRecipe() {
        recipeRepository.deleteAll();
        recipeRepository.save(recipe1);
        Optional<Recipe> savedRecipeOpt = recipeRepository.findById("ABC");
        Recipe savedRecipeActual = null;
        if (savedRecipeOpt.isPresent()) {
            savedRecipeActual = savedRecipeOpt.get();
        }
        assertThat(savedRecipeActual).isNotNull();
        assertThat(savedRecipeActual.getRecipeId()).isEqualTo("ABC");
    }

    @Test
    void SaveAll_FindAll_ReturnsAllRecipes() {
        recipeRepository.deleteAll();
        List<Recipe> recipeList = new ArrayList<>();
        recipeList.add(recipe1);
        recipeList.add(recipe2);
        recipeRepository.saveAll(recipeList);
        List<Recipe> searchResult = recipeRepository.findAll();
        assertThat(searchResult).isNotNull()
                .isNotEmpty()
                .hasSize(2);
        assertThat(searchResult.get(1).getRecipeId()).isEqualTo("DEF");
    }

    @Test
    void FindById_ReturnsRecipeById() {
        Optional<Recipe> searchResultOpt = recipeRepository.findById("ABC");
        Recipe searchResultActual = null;
        if (searchResultOpt.isPresent()) {
            searchResultActual = searchResultOpt.get();
        }
        assertThat(searchResultActual).isNotNull();
        assertThat(searchResultActual.getRecipeId()).isEqualTo("ABC");
    }

    @Test
    void FindBydbNameContaining_ReturnsRecipesByDBName() {
        Pageable pageable = PageRequest.of(0,100);
        Page<Recipe> result = recipeRepository.findBydbNameContaining("sandwich", pageable);
        List<Recipe> listOfRecipes = result.getContent();
        assertThat(listOfRecipes).isNotNull()
                .isNotEmpty()
                .hasSize(2);
        assertThat(listOfRecipes.get(1).getRecipeName()).isEqualTo("Sandwich!");
    }

    @Test
    void FindAllByAuthorId_ReturnMoreThanOneRecipe() {
        Optional<List<Recipe>> recipeListOpt = recipeRepository.findAllByAuthorId("me@me.com");
        List<Recipe> recipeListActual = new ArrayList<>();
        if (recipeListOpt.isPresent()) {
            recipeListActual = recipeListOpt.get();
        }
        assertThat(recipeListActual).isNotNull()
                .isNotEmpty()
                .hasSize(2);
        assertThat(recipeListActual.get(1).getAuthorId()).isEqualTo("me@me.com");
    }

    @Test
    void RecipeRepositoryRepository_CountByDBNameContaining_ReturnCountByRecipe() {
        Optional<Integer> recipeListCountOpt = recipeRepository.countBydbNameContaining("sandwich");
        Integer recipeListCountActual = null;
        if (recipeListCountOpt.isPresent()) {
            recipeListCountActual = recipeListCountOpt.get();
        }
        assertThat(recipeListCountActual).isNotNull()
                .isEqualTo(2);
    }

    @Test
    void DeleteByRecipeId_ReturnRecipeListWithoutDeletedRecipe() {
        recipeRepository.deleteByRecipeId("DEF");
        List<Recipe> searchList = recipeRepository.findAll();
        assertThat(searchList).isNotNull()
                .isNotEmpty()
                .hasSize(2)
                .doesNotContain(recipe2);
    }
}
