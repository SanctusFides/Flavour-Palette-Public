package io.sanctus.flavourpalette.edit;

import io.sanctus.flavourpalette.ingredient.IngredientDTO;
import io.sanctus.flavourpalette.instructions.InstructionsDTO;
import io.sanctus.flavourpalette.recipe.RecipeDTO;
import io.sanctus.flavourpalette.recipe_formDTO.IngredientFormDTO;
import io.sanctus.flavourpalette.recipe_formDTO.InstructionsFormDTO;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@SuppressWarnings("unused")
public interface EditRecipeService {
    RecipeDTO generateUpdatedRecipe(RecipeDTO recipeDTO, MultiValueMap<String, String> formData, MultipartFile multipartFile, String recipeId, Principal principal);
    void handleUpdateRecipe(RecipeDTO recipeDTO, InstructionsFormDTO instrDTOList, IngredientFormDTO ingrList, MultiValueMap<String, String> formData, MultipartFile multipartFile, String recipeId, Principal principal);
    List<IngredientDTO> handleUpdateIngredients(IngredientFormDTO ingredientFormDTO, RecipeDTO recipeDTO);
    List<IngredientDTO> generateUpdatedIngredientsDTO(IngredientFormDTO ingredientFormDTO, RecipeDTO recipeDTO);
    List<InstructionsDTO> handleUpdateInstructions(InstructionsFormDTO instrList, RecipeDTO recipeDTO);
    List<InstructionsDTO> generateUpdatedInstructions(InstructionsFormDTO instructionsFormDTO, RecipeDTO recipeDTO);
}


