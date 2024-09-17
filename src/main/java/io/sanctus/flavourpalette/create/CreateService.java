package io.sanctus.flavourpalette.create;

import io.sanctus.flavourpalette.ingredient.IngredientDTO;
import io.sanctus.flavourpalette.instructions.InstructionsDTO;

import io.sanctus.flavourpalette.recipe.RecipeDTO;
import io.sanctus.flavourpalette.recipe_formDTO.IngredientFormDTO;
import io.sanctus.flavourpalette.recipe_formDTO.InstructionsFormDTO;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.*;

@SuppressWarnings("unused")
public interface CreateService  {
    RecipeDTO handleSavingRecipe(RecipeDTO recipeDTO,InstructionsFormDTO instrDTOList,IngredientFormDTO ingrList, MultipartFile multipartFile, Principal principal);
    RecipeDTO buildRecipeDTOViaForm(RecipeDTO recipeFormDTO, MultipartFile multipartFile, Principal principal);
    List<InstructionsDTO> buildInstructionDTOListViaForm(InstructionsFormDTO instrList, RecipeDTO recipeDTO);
    List<IngredientDTO> buildIngredientDTOListViaForm(IngredientFormDTO ingredientFormDTO, RecipeDTO recipeDTO);
}
