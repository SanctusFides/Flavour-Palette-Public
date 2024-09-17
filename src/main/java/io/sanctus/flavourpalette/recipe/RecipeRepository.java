package io.sanctus.flavourpalette.recipe;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, String> {
    Optional<Recipe> findByRecipeId(String recipeId);
    Page<Recipe> findBydbNameContaining(String recipeName, Pageable pageable);
    Optional<Integer> countBydbNameContaining(String recipeName);
    Optional<List<Recipe>> findAllByAuthorId(String authorId);

    @Modifying
    @Transactional
    void deleteByRecipeId(String recipeId);

}
