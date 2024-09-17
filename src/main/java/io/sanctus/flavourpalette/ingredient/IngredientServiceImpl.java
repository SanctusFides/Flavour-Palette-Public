package io.sanctus.flavourpalette.ingredient;

import io.sanctus.flavourpalette.recipe.Recipe;
import io.sanctus.flavourpalette.recipe.RecipeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class IngredientServiceImpl implements IngredientService {

    private final IngredientRepository ingredientRepository;

    @Autowired
    @SuppressWarnings("unused")
    public IngredientServiceImpl(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    @Override
    public List<IngredientDTO> getIngrDTOListByRecipeDTO(RecipeDTO recipeDTO) {
        Optional<List<Ingredient>> entityResult = ingredientRepository.findAllByRecipeRecipeId(recipeDTO.getRecipeId());
        return entityResult.map(this::mapToIngrDTOList).orElse(null);
    }

    @Override
    public void deleteIngrList(List<IngredientDTO> ingredientList) {
        List<Ingredient> deleteList = mapToIngrEntityList(ingredientList);
        ingredientRepository.deleteAll(deleteList);
    }

    @Override
    public List<IngredientDTO> saveIngrList(List<IngredientDTO> ingredientList) {
        List<Ingredient> savedIngredients = ingredientRepository.saveAll(mapToIngrEntityList(ingredientList));
        return mapToIngrDTOList(savedIngredients);
    }

    public List<Recipe> getRecipeIdsByIngrName(String normalizedIngrName) {
        List<Recipe> recipes = new ArrayList<>();
        Optional<List<Ingredient>> foundIngredients = ingredientRepository.findAllBydbNameContaining(normalizedIngrName);
        foundIngredients.ifPresent(ingredients -> ingredients.forEach(ingredient -> recipes.add(ingredient.getRecipe())));
        return recipes;
    }

    public int getCountBydbNameContaining(String normalizedRecipeName) {
        Optional<Integer> result = ingredientRepository.countBydbNameContaining(normalizedRecipeName);
        return result.orElse(0);
    }
}
