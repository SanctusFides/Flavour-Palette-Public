package io.sanctus.flavourpalette.ingredient;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Integer> {

    Optional<List<Ingredient>> findAllByRecipeRecipeId(String recipeId);

    Optional<List<Ingredient>> findAllBydbNameContaining(String ingredientName);

    Optional<Integer> countBydbNameContaining(String ingrName);
}
