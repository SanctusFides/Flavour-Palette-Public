package io.sanctus.flavourpalette.repository;

import io.sanctus.flavourpalette.user_recipe_review.UserRecipeReview;
import io.sanctus.flavourpalette.user_recipe_review.UserRecipeReviewRepository;
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

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class UserRecipeReviewRepositoryTest {

    private final UserRecipeReviewRepository userRecipeReviewRepository;
    private final UserRecipeReview review1;
    private final UserRecipeReview review1B;
    private final UserRecipeReview review2;
    private final UserRecipeReview review3;

    @Autowired
    UserRecipeReviewRepositoryTest(UserRecipeReviewRepository userRecipeReviewRepository) {
        this.userRecipeReviewRepository = userRecipeReviewRepository;
        this.review1 = UserRecipeReview.builder().recipeId("ABC").userId("test1").rating(1).build();
        this.review1B = UserRecipeReview.builder().recipeId("ABC").userId("test2").rating(5).build();
        this.review2 = UserRecipeReview.builder().recipeId("DEF").userId("test2").rating(2).build();
        this.review3 = UserRecipeReview.builder().recipeId("GHI").userId("test3").rating(3).build();
    }
    @BeforeEach
    void init() {
        userRecipeReviewRepository.save(review1);
        userRecipeReviewRepository.save(review1B);
        userRecipeReviewRepository.save(review2);
        userRecipeReviewRepository.save(review3);
    }
    @AfterEach
    void tearDown() {
        userRecipeReviewRepository.deleteAll();
    }


    @Test
    void Save_FindAll_ReturnsSavedReview() {

        List<UserRecipeReview> result = userRecipeReviewRepository.findAll();
        assertThat(result).isNotNull();
        assertThat(result.getFirst().getRecipeId()).isEqualTo("ABC");
        assertThat(result.getFirst().getUserId()).isEqualTo("test1");
        assertThat(result.getFirst().getRating()).isEqualTo(1);
    }

    @Test
    void SaveAll_FindAll_ReturnsAllSavedReview() {
        userRecipeReviewRepository.deleteAll();

        List<UserRecipeReview> reviewList = new ArrayList<>();
        reviewList.add(UserRecipeReview.builder().recipeId("ABC").userId("test1").rating(1).build());
        reviewList.add(UserRecipeReview.builder().recipeId("DEF").userId("test2").rating(2).build());

        userRecipeReviewRepository.saveAll(reviewList);
        List<UserRecipeReview> result = userRecipeReviewRepository.findAll();
        assertThat(result).isNotNull()
                .isNotEmpty()
                .hasSize(2);
        assertThat(result.getFirst().getRecipeId()).isEqualTo("ABC");
        assertThat(result.getFirst().getUserId()).isEqualTo("test1");
        assertThat(result.getFirst().getRating()).isEqualTo(1);
        assertThat(result.get(1).getRecipeId()).isEqualTo("DEF");
        assertThat(result.get(1).getUserId()).isEqualTo("test2");
        assertThat(result.get(1).getRating()).isEqualTo(2);
    }

//    This function is related to a recipe being deleted and removing all rows related to it from Review DB
    @Test
    void FindAllByRecipeId_ReturnReviewContaining() {
        Optional<List<UserRecipeReview>> resultOpt = userRecipeReviewRepository.findAllByRecipeId("ABC");
        List<UserRecipeReview> resultActual = null;
        if (resultOpt.isPresent()) {
            resultActual = resultOpt.get();
        }
        assertThat(resultActual).isNotNull()
                .isNotEmpty()
                .hasSize(2);
        assertThat(resultActual.getFirst().getRecipeId()).isEqualTo("ABC");
        assertThat(resultActual.getFirst().getUserId()).isEqualTo("test1");
        assertThat(resultActual.getFirst().getRating()).isEqualTo(1);
        assertThat(resultActual.get(1).getRecipeId()).isEqualTo("ABC");
        assertThat(resultActual.get(1).getUserId()).isEqualTo("test2");
        assertThat(resultActual.get(1).getRating()).isEqualTo(5);
    }

    @Test
    void FindAllByRecipeIdAndUserId_ReturnReviewMatchingUserAndRecipeIds() {
        Optional<UserRecipeReview> resultOpt = userRecipeReviewRepository.findByRecipeIdAndUserId("ABC","test1");
        UserRecipeReview resultActual = null;
        if (resultOpt.isPresent()) {
            resultActual = resultOpt.get();
        }
        assertThat(resultActual).isNotNull();
        assertThat(resultActual.getRecipeId()).isEqualTo("ABC");
        assertThat(resultActual.getUserId()).isEqualTo("test1");
        assertThat(resultActual.getRating()).isEqualTo(1);
    }

    @Test
    void UpdateRecipeRating_ReturnUpdatedReview() {
        Optional<UserRecipeReview> resultOpt = userRecipeReviewRepository.findByRecipeIdAndUserId("ABC","test1");
        UserRecipeReview resultActual = null;
        if (resultOpt.isPresent()) {
            resultActual = resultOpt.get();
        }
        assert resultActual != null;
        resultActual.setRating(5);
        userRecipeReviewRepository.save(resultActual);
        Optional<UserRecipeReview> updatedResultOpt = userRecipeReviewRepository.findByRecipeIdAndUserId("ABC","test1");
        UserRecipeReview updatedResultActual = null;
        if (updatedResultOpt.isPresent()) {
            updatedResultActual = updatedResultOpt.get();
        }

        assertThat(updatedResultActual).isNotNull();
        assertThat(updatedResultActual.getRating()).isEqualTo(5);
    }
}
