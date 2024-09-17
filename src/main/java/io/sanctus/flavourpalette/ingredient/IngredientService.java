package io.sanctus.flavourpalette.ingredient;

import io.sanctus.flavourpalette.recipe.RecipeDTO;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public interface IngredientService {

    List<IngredientDTO> getIngrDTOListByRecipeDTO(RecipeDTO recipeDTO);
    void deleteIngrList(List<IngredientDTO> ingredientList);
    List<IngredientDTO> saveIngrList(List<IngredientDTO> ingredientList);

    default IngredientDTO mapToIngrDTO(Ingredient ingredient) {
        return new IngredientDTO(
                ingredient.getId(),
                ingredient.getRecipe(),
                ingredient.getName(),
                ingredient.getDbName(),
                ingredient.getQuantity(),
                ingredient.getSize(),
                ingredient.getPrepType());
    }
    default Ingredient mapToIngrEntity(IngredientDTO ingredientDTO) {
        return new Ingredient(
                ingredientDTO.getId(),
                ingredientDTO.getRecipe(),
                ingredientDTO.getName(),
                ingredientDTO.getDbName(),
                ingredientDTO.getQuantity(),
                ingredientDTO.getSize(),
                ingredientDTO.getPrepType());
    }

    default List<Ingredient> mapToIngrEntityList(List<IngredientDTO> ingredientDTOList) {
        List<Ingredient> ingredientEntityList = new ArrayList<>();
        ingredientDTOList.forEach( ingredientDTO -> ingredientEntityList.add(mapToIngrEntity(ingredientDTO)));
        return ingredientEntityList;
    }
    default List<IngredientDTO> mapToIngrDTOList(List<Ingredient> ingredientEntityList) {
        List<IngredientDTO> ingredientDTOList = new ArrayList<>();
        ingredientEntityList.forEach( ingredient -> ingredientDTOList.add(mapToIngrDTO(ingredient)));
        return ingredientDTOList;
    }
}
