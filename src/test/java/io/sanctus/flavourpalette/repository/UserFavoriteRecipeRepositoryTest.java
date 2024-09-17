package io.sanctus.flavourpalette.repository;

import io.sanctus.flavourpalette.user_favorite_recipe.UserFavoriteRecipe;
import io.sanctus.flavourpalette.user_favorite_recipe.UserFavoriteRecipeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class UserFavoriteRecipeRepositoryTest {

    private final UserFavoriteRecipeRepository userFavoriteRecipeRepository;
    private final UserFavoriteRecipe favorite1;
    private final UserFavoriteRecipe favorite2;
    private final UserFavoriteRecipe favorite3;

    @Autowired
    UserFavoriteRecipeRepositoryTest(UserFavoriteRecipeRepository userFavoriteRecipeRepository) {
        this.userFavoriteRecipeRepository = userFavoriteRecipeRepository;
        this.favorite1 = UserFavoriteRecipe.builder().recipeId("ABC").userId("test1").build();
        this.favorite2 = UserFavoriteRecipe.builder().recipeId("ABC").userId("test2").build();
        this.favorite3 = UserFavoriteRecipe.builder().recipeId("DEF").userId("test1").build();
    }
    @BeforeEach
    void init(){
        userFavoriteRecipeRepository.save(favorite1);
        userFavoriteRecipeRepository.save(favorite2);
        userFavoriteRecipeRepository.save(favorite3);
    }
    @AfterEach
    void tearDown(){
        userFavoriteRecipeRepository.deleteAll();
    }

    @Test
    void Save_Find_ReturnsSavedFavorite() {
        Optional<UserFavoriteRecipe> resultOpt = userFavoriteRecipeRepository.findByUserIdAndRecipeId("test1", "ABC");
        UserFavoriteRecipe resultActual = null;
        if (resultOpt.isPresent()) {
            resultActual = resultOpt.get();
        }
        assertThat(resultActual).isNotNull();
        assertThat(resultActual.getRecipeId()).isEqualTo("ABC");
    }

    @Test
    void FindByUserId_AndRecipeId_ReturnsSpecifiedFavorite() {
        Optional<UserFavoriteRecipe> resultOpt = userFavoriteRecipeRepository.findByUserIdAndRecipeId("test1", "ABC");
        UserFavoriteRecipe resultActual = null;
        if (resultOpt.isPresent()) {
            resultActual = resultOpt.get();
        }
        assertThat(resultActual).isNotNull();
        assertThat(resultActual.getRecipeId()).isEqualTo("ABC");
        assertThat(resultActual.getUserId()).isEqualTo("test1");
    }

    @Test
    void FindAllByRecipeId_ReturnFavoriteListByRecipeId() {
        Optional<List<UserFavoriteRecipe>> resultOpt = userFavoriteRecipeRepository.findAllByRecipeId("ABC");
        List<UserFavoriteRecipe> resultActual = null;
        if (resultOpt.isPresent()) {
            resultActual = resultOpt.get();
        }
        assertThat(resultActual).isNotNull().isNotEmpty().hasSize(2);
        assertThat(resultActual.get(0).getUserId()).isEqualTo("test1");
        assertThat(resultActual.get(1).getUserId()).isEqualTo("test2");
        assertThat(resultActual.get(1).getRecipeId()).isEqualTo("ABC");
    }

    @Test
    void FindAllByUserId_ReturnsFavoriteListByUserId() {
        Optional<List<UserFavoriteRecipe>> resultOpt = userFavoriteRecipeRepository.findAllByUserId("test1");
        List<UserFavoriteRecipe> resultActual = null;
        if (resultOpt.isPresent()) {
            resultActual = resultOpt.get();
        }
        assertThat(resultActual).isNotNull().isNotEmpty().hasSize(2);
        assertThat(resultActual.get(0).getUserId()).isEqualTo("test1");
        assertThat(resultActual.get(0).getRecipeId()).isEqualTo("ABC");
        assertThat(resultActual.get(1).getRecipeId()).isEqualTo("DEF");
    }

    @Test
    void DeleteByUserId_AndRecipeId_FindAll_ReturnsEmptyList() {
        userFavoriteRecipeRepository.deleteByUserIdAndRecipeId("test1", "ABC");
        List<UserFavoriteRecipe> result = userFavoriteRecipeRepository.findAll();
        assertThat(result).isNotNull().isNotEmpty().hasSize(2);
        assertThat(result.get(0).getUserId()).isEqualTo("test2");
        assertThat(result.get(0).getRecipeId()).isEqualTo("ABC");
        assertThat(result.get(1).getUserId()).isEqualTo("test1");
        assertThat(result.get(1).getRecipeId()).isEqualTo("DEF");
    }
}
