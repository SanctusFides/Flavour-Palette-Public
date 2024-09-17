package io.sanctus.flavourpalette.service;

import io.sanctus.flavourpalette.cloudinary_impl.CloudinaryImpl;
import io.sanctus.flavourpalette.user_recipe_review.UserRecipeReview;
import io.sanctus.flavourpalette.user_recipe_review.UserRecipeReviewDTO;
import io.sanctus.flavourpalette.user_recipe_review.UserRecipeReviewRepository;
import io.sanctus.flavourpalette.user_recipe_review.UserRecipeReviewServiceImpl;
import lombok.NonNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.util.MultiValueMap;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class UserRecipeReviewServiceTest {
    @MockBean
    CloudinaryImpl cloudinary;

    private final UserRecipeReviewServiceImpl userRecipeReviewService;
    private final UserRecipeReviewRepository userRecipeReviewRepository;

    @Autowired
    UserRecipeReviewServiceTest(UserRecipeReviewServiceImpl userRecipeReviewService,
                                UserRecipeReviewRepository userRecipeReviewRepository) {
        this.userRecipeReviewService = userRecipeReviewService;
        this.userRecipeReviewRepository = userRecipeReviewRepository;

    }
    @BeforeEach
    void init() {
        userRecipeReviewRepository.save(new UserRecipeReview(1L,"test@test.com","ABC",5));
        userRecipeReviewRepository.save(new UserRecipeReview(2L,"com@com.test","ABC",3));
        userRecipeReviewRepository.save(new UserRecipeReview(3L,"test@test.com","DEF",1));
    }
    @AfterEach
    void tearDown() {
        userRecipeReviewRepository.deleteAll();
    }

    @Test
    void GetDTOByRecipeIdAndUserId_ReturnsUserRecipeReviewDTOAndUserId() {
        UserRecipeReviewDTO result = userRecipeReviewService.getDTOByRecipeIdAndUserId("ABC","test@test.com");
        assertThat(result).isNotNull();
        assertThat(result.getRating()).isEqualTo(5);
    }

    @Test
    void GetAllByRecipeId_ReturnsListOfUsersReviews() {
        List<UserRecipeReview> result = userRecipeReviewService.getAllByUserId("test@test.com");
        assertThat(result).isNotNull().isNotEmpty().hasSize(2);
        assertThat(result.getFirst().getRating()).isEqualTo(5);
        assertThat(result.getLast().getRating()).isEqualTo(1);
    }

    @Test
    void GetAllByUserId_ReturnsListOfUserReviews() {
        List<UserRecipeReview> result = userRecipeReviewService.getAllByRecipeId("ABC");
        assertThat(result).isNotNull().isNotEmpty().hasSize(2);
        assertThat(result.getFirst().getRating()).isEqualTo(5);
        assertThat(result.getFirst().getUserId()).isEqualTo("test@test.com");
        assertThat(result.getLast().getRating()).isEqualTo(3);
        assertThat(result.getLast().getUserId()).isEqualTo("com@com.test");
    }

    @Test
    void DeleteAllReviewsById_GetAllByRecId_ReturnsEmptyList() {
        List<UserRecipeReview> preDelete = userRecipeReviewService.getAllByRecipeId("ABC");
        assertThat(preDelete).isNotNull().isNotEmpty().hasSize(2);

        userRecipeReviewService.deleteAllByRecipeId("ABC");
        List<UserRecipeReview> postDelete = userRecipeReviewService.getAllByRecipeId("ABC");
        assertThat(postDelete).isEmpty();
    }

    @Test
    void DeleteAllReviewsByUserId_GetAllByUserId_ReturnsEmptyList() {
        List<UserRecipeReview> preDelete = userRecipeReviewService.getAllByUserId("test@test.com");
        assertThat(preDelete).isNotNull().isNotEmpty().hasSize(2);

        userRecipeReviewService.deleteAllReviewsByUserId("test@test.com");
        List<UserRecipeReview> postDelete = userRecipeReviewService.getAllByUserId("test@test.com");
        assertThat(postDelete).isEmpty();
    }

    @Test
    void HandleRecipeReview_SavesUserReview() {
        MultiValueMap<String, String> rating4 = new MultiValueMap<>() {
            @Override
            public String getFirst(@NonNull String key) {
                return "4";
            }
            @Override
            public void add(@NonNull String key, String value) {
            }
            @Override
            public void addAll(@NonNull String key, @NonNull List<? extends String> values) {
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
        MultiValueMap<String, String> rating1 = new MultiValueMap<>() {
            @Override
            public String getFirst(@NonNull String key) {
                return "1";
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

        UserRecipeReviewDTO startingRating = userRecipeReviewService.getDTOByRecipeIdAndUserId("ABC","test@test.com");
        assertThat(startingRating.getRating()).isEqualTo(5);

        userRecipeReviewService.handleRecipeReview(rating4,"test@test.com","ABC");
        UserRecipeReviewDTO firstUpdate = userRecipeReviewService.getDTOByRecipeIdAndUserId("ABC","test@test.com");
        assertThat(firstUpdate.getRating()).isEqualTo(4);

        userRecipeReviewService.handleRecipeReview(rating1,"test@test.com","ABC");
        UserRecipeReviewDTO secondUpdate = userRecipeReviewService.getDTOByRecipeIdAndUserId("ABC","test@test.com");
        assertThat(secondUpdate.getRating()).isEqualTo(1);
    }
}
