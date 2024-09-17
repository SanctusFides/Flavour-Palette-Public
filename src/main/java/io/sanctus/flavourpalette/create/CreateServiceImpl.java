package io.sanctus.flavourpalette.create;

import io.sanctus.flavourpalette.cloudinary_impl.CloudinaryImpl;
import io.sanctus.flavourpalette.exception.CloudinaryException;
import io.sanctus.flavourpalette.ingredient.IngredientDTO;
import io.sanctus.flavourpalette.ingredient.IngredientServiceImpl;
import io.sanctus.flavourpalette.instructions.InstructionsDTO;
import io.sanctus.flavourpalette.instructions.InstructionsServiceImpl;
import io.sanctus.flavourpalette.recipe.Recipe;
import io.sanctus.flavourpalette.recipe.RecipeDTO;
import io.sanctus.flavourpalette.recipe.RecipeServiceImpl;
import io.sanctus.flavourpalette.recipe_formDTO.IngredientFormDTO;
import io.sanctus.flavourpalette.recipe_formDTO.InstructionsFormDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.*;

@Service
public class CreateServiceImpl implements CreateService {

    private final RecipeServiceImpl recipeServiceImpl;
    private final IngredientServiceImpl ingredientServiceImpl;
    private final InstructionsServiceImpl instructionsServiceImpl;

    @Autowired
    @SuppressWarnings("unused")
    public CreateServiceImpl( RecipeServiceImpl recipeServiceImpl,
                              IngredientServiceImpl ingredientServiceImpl,
                              InstructionsServiceImpl instructionsServiceImpl){
        this.recipeServiceImpl = recipeServiceImpl;
        this.ingredientServiceImpl = ingredientServiceImpl;
        this.instructionsServiceImpl = instructionsServiceImpl;
    }

    @Override
    public RecipeDTO handleSavingRecipe(RecipeDTO recipeDTO,
                                        InstructionsFormDTO instrDTOList,
                                        IngredientFormDTO ingrList,
                                        MultipartFile multipartFile,
                                        Principal principal) {
        RecipeDTO builtRecipe = buildRecipeDTOViaForm(recipeDTO,multipartFile,principal);
        RecipeDTO savedRecipe = recipeServiceImpl.saveRecipeToDB(builtRecipe);

        List<InstructionsDTO> builtInstructions = buildInstructionDTOListViaForm(instrDTOList, builtRecipe);
        instructionsServiceImpl.saveInstrList(builtInstructions);

        List<IngredientDTO> builtIngredients = buildIngredientDTOListViaForm(ingrList, builtRecipe);
        ingredientServiceImpl.saveIngrList(builtIngredients);
        return savedRecipe;
    }

    @Override
    public RecipeDTO buildRecipeDTOViaForm(RecipeDTO recipeDTO,
                                           MultipartFile multipartFile,
                                           Principal principal) {
        recipeDTO.setRecipeId( UUID.randomUUID().toString());
        recipeDTO.setAuthorId(principal.getName());
        recipeDTO.setDbName(recipeDTO.getRecipeName().toLowerCase().trim().replace(" ", "-"));
        if(recipeDTO.getPrepTime() == null) {
            recipeDTO.setPrepTime(0);
        }
        if(recipeDTO.getCookTime() == null) {
            recipeDTO.setCookTime(0);
        }
        recipeDTO.setTotalTime(recipeDTO.getPrepTime() + recipeDTO.getCookTime());
        if (!multipartFile.isEmpty()) {
            String imageId = handleImageFileUpload(multipartFile);
            if (imageId != null) {
                recipeDTO.setImageId(imageId);
            }
        }
        return recipeDTO;
    }

    @Override
    public List<InstructionsDTO> buildInstructionDTOListViaForm(InstructionsFormDTO instructionsFormDTO, RecipeDTO recipeDTO) {
        instructionsFormDTO.getInstructionList().forEach(instructionsDTO -> {
            if (!instructionsDTO.getStepText().isBlank()) {
                Recipe recipe = recipeServiceImpl.mapToEntity(recipeDTO);
                instructionsDTO.setRecipe(recipe);
            } else {
                instructionsDTO.setStepText("EMPTY INSTRUCTION");
            }
        });
        return instructionsFormDTO.getInstructionList();
    }

    @Override
    public List<IngredientDTO> buildIngredientDTOListViaForm(IngredientFormDTO ingredientFormDTO, RecipeDTO recipeDTO) {
        ingredientFormDTO.getIngredientList().forEach(ingredientDTO -> {
            ingredientDTO.setDbName(ingredientDTO.getName().toLowerCase().trim().replace(" ", "-"));
            Recipe recipe = recipeServiceImpl.mapToEntity(recipeDTO);
            ingredientDTO.setRecipe(recipe);
        });
        return ingredientFormDTO.getIngredientList();
    }

    String handleImageFileUpload(MultipartFile multipartFile) {
        try {
            Map result = CloudinaryImpl.uploadFile(multipartFile);
            return result.get("public_id").toString();
        } catch(IOException exception) {
            throw new CloudinaryException(exception.getMessage());
        }
    }
}
