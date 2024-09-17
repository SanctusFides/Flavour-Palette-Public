package io.sanctus.flavourpalette.user_favorite_recipe;

import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

@Repository
public interface UserFavoriteRecipeRepository extends JpaRepository<UserFavoriteRecipe, String> {
    Optional<List<UserFavoriteRecipe>> findAllByUserId(String userId);
    Optional<List<UserFavoriteRecipe>> findAllByRecipeId(String recipeId);
    Optional<UserFavoriteRecipe> findByUserIdAndRecipeId(String userId, String recipeId);

    @Transactional
    void deleteByUserIdAndRecipeId(String userId, String recipeId);

    @Modifying
    @Transactional
    void deleteAllByUserId(String userId);
}
