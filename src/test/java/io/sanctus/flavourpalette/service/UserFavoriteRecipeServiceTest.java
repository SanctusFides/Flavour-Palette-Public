package io.sanctus.flavourpalette.service;

import io.sanctus.flavourpalette.cloudinary_impl.CloudinaryImpl;
import io.sanctus.flavourpalette.user_favorite_recipe.UserFavoriteRecipe;
import io.sanctus.flavourpalette.user_favorite_recipe.UserFavoriteRecipeDTO;
import io.sanctus.flavourpalette.user_favorite_recipe.UserFavoriteRecipeRepository;
import io.sanctus.flavourpalette.user_favorite_recipe.UserFavoriteRecipeServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.lang.NonNull;
import org.springframework.util.MultiValueMap;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class UserFavoriteRecipeServiceTest {

    @MockBean
    CloudinaryImpl cloudinary;

    private final UserFavoriteRecipeServiceImpl userFavoriteRecipeService;
    private final UserFavoriteRecipeRepository userFavoriteRecipeRepository;

    @Autowired
    UserFavoriteRecipeServiceTest(UserFavoriteRecipeServiceImpl userFavoriteRecipeService,
                                  UserFavoriteRecipeRepository userFavoriteRecipeRepository) {
        this.userFavoriteRecipeService = userFavoriteRecipeService;
        this.userFavoriteRecipeRepository = userFavoriteRecipeRepository;
    }
    @BeforeEach
    void init() {
        userFavoriteRecipeRepository.save(new UserFavoriteRecipe(1L,"test@test.com","ABC"));
        userFavoriteRecipeRepository.save(new UserFavoriteRecipe(2L,"test@test.com","DEF"));
        userFavoriteRecipeRepository.save(new UserFavoriteRecipe(3L,"com@com.test","ABC"));
    }
    @AfterEach
    void tearDown() {
        userFavoriteRecipeRepository.deleteAll();
    }

    @Test
    void GetAllByUserId_ReturnsListOfUserFavoriteRecipeDTOS() {
        List<UserFavoriteRecipeDTO> result = userFavoriteRecipeService.getAllByUserId("test@test.com");
        assertThat(result).isNotNull().isNotEmpty().hasSize(2);
        assertThat(result.getFirst().getRecipeId()).isEqualTo("ABC");
        assertThat(result.getLast().getRecipeId()).isEqualTo("DEF");
    }

    @Test
    void GetByUserIdAndRecipeId_ReturnsUserFavoriteRecipeDTO() {
        UserFavoriteRecipeDTO result = userFavoriteRecipeService.getByUserIdAndRecipeId("test@test.com","ABC");
        assertThat(result.getUserId()).isEqualTo("test@test.com");
    }

    @Test
    void SaveUserFavoriteRecipe_GetAllEntities_ReturnsSavedFavorite() {
        userFavoriteRecipeRepository.deleteAll();
        UserFavoriteRecipeDTO newDTO = new UserFavoriteRecipeDTO(4L,"com@com.test","XYZ");
        userFavoriteRecipeService.saveUserFavoriteRecipe(newDTO);
        List<UserFavoriteRecipe> list = userFavoriteRecipeService.getAllEntitiesByRecipeId("XYZ");
        assertThat(list.getFirst().getUserId()).isEqualTo("com@com.test");
    }

    @Test
    void GetAllEntitiesByRecipeId_ReturnsListOfUserFavoriteRecipes() {
        List<UserFavoriteRecipe> result = userFavoriteRecipeService.getAllEntitiesByRecipeId("ABC");
        assertThat(result).isNotNull().isNotEmpty().hasSize(2);
        assertThat(result.getFirst().getUserId()).isEqualTo("test@test.com");
        assertThat(result.getLast().getUserId()).isEqualTo("com@com.test");
    }

    @Test
    void DeleteByUserIdAndRecipeId_GetAll_ReturnsListSansDeleted() {
        userFavoriteRecipeService.deleteByUserIdAndRecipeId("test@test.com","ABC");
        List<UserFavoriteRecipeDTO> list = userFavoriteRecipeService.getAllByUserId("test@test.com");
        assertThat(list).hasSize(1);
        assertThat(list.getFirst().getRecipeId()).isEqualTo("DEF");
    }

    @Test
    void handleDeleteAllByRecipeId_GetAll_ReturnsEmptyList() {
        userFavoriteRecipeService.handleDeleteAllByRecipeId("ABC");
        List<UserFavoriteRecipe> list = userFavoriteRecipeService.getAllEntitiesByRecipeId("ABC");
        assertThat(list).isEmpty();
    }

    @Test
    void deleteAllByUserId_DeletesAllByUser_FindAll_ReturnsEmptyList() {
        userFavoriteRecipeService.deleteAllByUserId("test@test.com");
        List<UserFavoriteRecipe> list = userFavoriteRecipeRepository.findAll();
        assertThat(list).isNotEmpty().isNotNull().hasSize(1);
        assertThat(list.getFirst().getRecipeId()).isEqualTo("ABC");
        assertThat(list.getFirst().getUserId()).isEqualTo("com@com.test");
    }

    @Test
    void HandleFavoriteCheckbox_SaveReviewAndDeleteReview() {
        MultiValueMap<String, String> favoriteValid = new MultiValueMap<>() {
            @Override
            public String getFirst(@NonNull String key) {
                return "Yes";
            }

            @Override
            public void add(@NonNull String key, String value) {

            }

            @Override
            public void addAll(@NonNull String key,@NonNull List<? extends String> values) {

            }

            @Override
            public void addAll(@NonNull MultiValueMap<String, String> values) {

            }

            @Override
            public void set(@NonNull String key, String value) {

            }

            @Override
            public void setAll(@NonNull Map<String,String> values) {

            }

            @Override
            public @NonNull Map<String, String> toSingleValueMap() {
                return Map.of();
            }

            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean containsKey(Object key) {
                return false;
            }

            @Override
            public boolean containsValue(Object value) {
                return false;
            }

            @Override
            public List<String> get(Object key) {
                return List.of();
            }

            @Override
            public List<String> put(String key, List<String> value) {
                return List.of();
            }

            @Override
            public List<String> remove(Object key) {
                return List.of();
            }

            @Override
            public void putAll(@NonNull Map<? extends String, ? extends List<String>> m) {

            }

            @Override
            public void clear() {

            }

            @Override
            public @NonNull Set<String> keySet() {
                return Set.of();
            }

            @Override
            public @NonNull Collection<List<String>> values() {
                return List.of();
            }

            @Override
            public @NonNull Set<Entry<String, List<String>>> entrySet() {
                return Set.of();
            }
        };
        MultiValueMap<String, String> favoriteNull = new MultiValueMap<>() {
            @Override
            public String getFirst(@NonNull String key) {
                return null;
            }

            @Override
            public void add(@NonNull String key, String value) {

            }

            @Override
            public void addAll(@NonNull String key,@NonNull List<? extends String> values) {

            }

            @Override
            public void addAll(@NonNull MultiValueMap<String, String> values) {

            }

            @Override
            public void set(@NonNull String key, String value) {

            }

            @Override
            public void setAll(@NonNull Map<String, String> values) {

            }

            @Override
            public @NonNull Map<String, String> toSingleValueMap() {
                return Map.of();
            }

            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean containsKey(Object key) {
                return false;
            }

            @Override
            public boolean containsValue(Object value) {
                return false;
            }

            @Override
            public List<String> get(Object key) {
                return List.of();
            }

            @Override
            public List<String> put(String key, List<String> value) {
                return List.of();
            }

            @Override
            public List<String> remove(Object key) {
                return List.of();
            }

            @Override
            public void putAll(@NonNull Map<? extends String, ? extends List<String>> m) {

            }

            @Override
            public void clear() {

            }

            @Override
            public @NonNull Set<String> keySet() {
                return Set.of();
            }

            @Override
            public @NonNull Collection<List<String>> values() {
                return List.of();
            }

            @Override
            public @NonNull Set<Entry<String, List<String>>> entrySet() {
                return Set.of();
            }
        };

        userFavoriteRecipeService.handleFavoriteCheckBox(favoriteValid,"new@test.com","XYZ");
        Optional<List<UserFavoriteRecipe>> preDeleteOpt = userFavoriteRecipeRepository.findAllByUserId("new@test.com");
        List<UserFavoriteRecipe> preDeleteActual = new ArrayList<>();
        if (preDeleteOpt.isPresent()) {
            preDeleteActual = preDeleteOpt.get();
        }
        assertThat(preDeleteActual.getFirst().getUserId()).isEqualTo("new@test.com");

        userFavoriteRecipeService.handleFavoriteCheckBox(favoriteNull,"new@test.com","XYZ");
        Optional<List<UserFavoriteRecipe>>  postDeleteOpt = userFavoriteRecipeRepository.findAllByUserId("new@test.com");
        List<UserFavoriteRecipe> postDeleteActual = new ArrayList<>();
        if (postDeleteOpt.isPresent()) {
            postDeleteActual = postDeleteOpt.get();
        }
        assertThat(postDeleteActual).isEmpty();
    }
}
