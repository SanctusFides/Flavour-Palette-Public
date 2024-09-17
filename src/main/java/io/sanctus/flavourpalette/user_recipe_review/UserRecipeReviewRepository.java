package io.sanctus.flavourpalette.user_recipe_review;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRecipeReviewRepository extends JpaRepository<UserRecipeReview, Long> {
    Optional<List<UserRecipeReview>> findAllByRecipeId(String recipeId);
    Optional<List<UserRecipeReview>> findAllByUserId(String userId);
    Optional<UserRecipeReview> findByRecipeIdAndUserId(String recipeId, String userId);

    @Transactional
    @Modifying
    void deleteAllByUserId(String userId);

}
