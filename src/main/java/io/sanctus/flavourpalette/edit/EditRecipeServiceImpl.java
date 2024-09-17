package io.sanctus.flavourpalette.edit;

import io.sanctus.flavourpalette.cloudinary_impl.CloudinaryImpl;
import io.sanctus.flavourpalette.exception.CloudinaryException;
import io.sanctus.flavourpalette.ingredient.Ingredient;
import io.sanctus.flavourpalette.ingredient.IngredientDTO;
import io.sanctus.flavourpalette.ingredient.IngredientServiceImpl;
import io.sanctus.flavourpalette.instructions.Instructions;
import io.sanctus.flavourpalette.instructions.InstructionsDTO;
import io.sanctus.flavourpalette.instructions.InstructionsServiceImpl;
import io.sanctus.flavourpalette.recipe.Recipe;
import io.sanctus.flavourpalette.recipe.RecipeDTO;
import io.sanctus.flavourpalette.recipe.RecipeServiceImpl;
import io.sanctus.flavourpalette.recipe_formDTO.IngredientFormDTO;
import io.sanctus.flavourpalette.recipe_formDTO.InstructionsFormDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class EditRecipeServiceImpl implements EditRecipeService {

    private final RecipeServiceImpl recipeServiceImpl;
    private final InstructionsServiceImpl instructionsServiceImpl;
    private final IngredientServiceImpl ingredientServiceImpl;

    @SuppressWarnings("unused")
    @Autowired
    public EditRecipeServiceImpl(RecipeServiceImpl recipeServiceImpl,
                                 InstructionsServiceImpl instructionsServiceImpl,
                                 IngredientServiceImpl ingredientServiceImpl) {
        this.recipeServiceImpl = recipeServiceImpl;
        this.instructionsServiceImpl = instructionsServiceImpl;
        this.ingredientServiceImpl = ingredientServiceImpl;
    }

    @Override
    public void handleUpdateRecipe(RecipeDTO recipeDTO,
                                   InstructionsFormDTO instrDTOList,
                                   IngredientFormDTO ingrList,
                                   MultiValueMap<String, String> formData,
                                   MultipartFile multipartFile,
                                   String recipeId,
                                   Principal principal) {
        RecipeDTO updatedRecipeDTO = generateUpdatedRecipe(recipeDTO,formData,multipartFile,recipeId,principal);
        recipeServiceImpl.saveRecipeToDB(updatedRecipeDTO);
        handleUpdateInstructions(instrDTOList, updatedRecipeDTO);
        handleUpdateIngredients(ingrList, updatedRecipeDTO);
    }

/*    Ingredients/Instructions will be handled by first deleting the old batch, generating a new batch and saving those.
      The logic here is that it's hard to update, delete, create etc. and isn't necessary to preserve them
      - so make it disposable */
    @Override
    public List<IngredientDTO> handleUpdateIngredients(IngredientFormDTO ingrList, RecipeDTO recipeDTO) {
/*      This block catches if the ingredient boxes are somehow missing or empty - such as if a user manually
        deleted the boxes from the source and hit submit. If so, make blank ones - Alternative idea is to catch
        it in an exception and reload them back to the page but this is super edge-case only caused by tampering and I
        would need to find a way to reload the values after directing back since this happens in the post function.
        This may be the way to go long term though, so I can catch all the values that may be missing and pass through
        which is missing to be highlighted on the page - currently only checking on ingredients and instructions
        but not the name/description/time fields*/
        if (ingrList.getIngredientList() == null || ingrList.getIngredientList().isEmpty()) {
            Ingredient blankIngredient = new Ingredient();
            blankIngredient.setRecipe(recipeServiceImpl.getRecipeById(recipeDTO.getRecipeId()));
            blankIngredient.setName("Enter Ingredient Here");
            blankIngredient.setDbName(null); /*Making this null prevents it from appearing in search*/
            List<Ingredient> ingredientList = new ArrayList<>();
            return ingredientServiceImpl.saveIngrList(ingredientServiceImpl.mapToIngrDTOList(ingredientList));
        }
//      First look up the old set, so it can be passed in as a group to be deleted
        List<IngredientDTO> oldIngredients = ingredientServiceImpl.getIngrDTOListByRecipeDTO(recipeDTO);
        ingredientServiceImpl.deleteIngrList(oldIngredients);
        List<IngredientDTO> newIngredients = generateUpdatedIngredientsDTO(ingrList, recipeDTO);
        return ingredientServiceImpl.saveIngrList(newIngredients);
    }

//  Compares the new list and old list. If they are the same, don't bother saving. Otherwise, delete and save new list
    @Override
    public List<InstructionsDTO> handleUpdateInstructions(InstructionsFormDTO instrDTOList, RecipeDTO recipeDTO) {
/*      This block is functionally the same as the If block for Ingredients above - catches users who have somehow
        submitted the form with an empty value */
        if (instrDTOList.getInstructionList() == null || instrDTOList.getInstructionList().isEmpty()) {
            Instructions blankInstruction = new Instructions();
            blankInstruction.setRecipe(recipeServiceImpl.getRecipeById(recipeDTO.getRecipeId()));
            blankInstruction.setStepText("Enter Instructions Here");
            List<Instructions> instructionList = new ArrayList<>();
            instructionList.add(blankInstruction);
            return instructionsServiceImpl.saveInstrList(instructionsServiceImpl.mapToInstrDTOList(instructionList));
        }
        List<InstructionsDTO> oldInstructions = instructionsServiceImpl.getInstrDTOListByRecipeDTO(recipeDTO);
        List<InstructionsDTO> newInstructions = generateUpdatedInstructions(instrDTOList, recipeDTO);
        if (!instructionEqualityCheck(oldInstructions, newInstructions)) {
            instructionsServiceImpl.deleteInstrList(oldInstructions);
            return instructionsServiceImpl.saveInstrList(newInstructions);
        }
        return oldInstructions;
    }

    @Override
    public RecipeDTO generateUpdatedRecipe(RecipeDTO recipeDTO,
                                           @RequestParam MultiValueMap<String, String> formData,
                                            MultipartFile multipartFile,
                                            String recipeId,
                                           Principal principal){
        recipeDTO.setAuthorId(principal.getName());
        recipeDTO.setDbName(recipeDTO.getRecipeName().toLowerCase().trim().replace(" ", "-"));

        String imageId = handleImageFileUpdate(multipartFile, formData, recipeId);
        recipeDTO.setTotalTime(recipeDTO.getPrepTime() + recipeDTO.getCookTime());
        if (imageId != null) {
            recipeDTO.setImageId(imageId);
        }
        return recipeDTO;
    }

    @Override
    public List<InstructionsDTO> generateUpdatedInstructions(InstructionsFormDTO instructionsFormDTO, RecipeDTO recipeDTO) {
        instructionsFormDTO.getInstructionList().forEach(step -> {
            Recipe recipe = recipeServiceImpl.mapToEntity(recipeDTO);
            if (!step.getStepText().isBlank() && !step.getStepText().isEmpty()) {
                step.setRecipe(recipe);
            } else {
                step.setStepText("EMPTY STEP");
                step.setRecipe(recipe);
            }
        });
        return instructionsFormDTO.getInstructionList();
    }

/*  Ingredients that have a blank in the name or the quantity are skipped in the backend. Enforcement is on the front
    to make sure they don't start Name or Quantity */
    @Override
    public List<IngredientDTO> generateUpdatedIngredientsDTO(IngredientFormDTO ingredientFormDTO, RecipeDTO recipeDTO) {
        List<IngredientDTO> parsedList = new ArrayList<>();
        ingredientFormDTO.getIngredientList().forEach(ingredientDTO -> {
            if (!ingredientDTO.getName().isBlank() && !ingredientDTO.getQuantity().isBlank()) {
                parsedList.add(ingredientDTO);
            }
        });
        parsedList.forEach(ingredientDTO -> {
            ingredientDTO.setDbName(ingredientDTO.getName().toLowerCase().trim().replace(" ", "-"));
            Recipe recipe = recipeServiceImpl.mapToEntity(recipeDTO);
            ingredientDTO.setRecipe(recipe);
        });
        return parsedList;
    }

    boolean instructionEqualityCheck(List<InstructionsDTO> oldList, List<InstructionsDTO> newList) {
//      If the list size is different and step text don't match - then return false - otherwise everything matches
        for (int i = 0; i < newList.size(); i++) {
            if (oldList.size() != newList.size() || !newList.get(i).getStepText().equals(oldList.get(i).getStepText())) {
                return false;
            }
        } return  true;
    }

    String handleImageFileUpdate(MultipartFile multipartFile, MultiValueMap<String, String> formData, String recipeId) {
        /* If the image is able to upload, it will return a string of the Id to be attached to the recipe object to be saved to DB
         If there is no image to upload, it will not return anything and the imageId will be null and won't be attached

         Section for uploading the recipe image to Cloudinary using their API and my implementation object
         Checks if the multipart file is NOT empty - meaning they attached an image file then run the code to upload and save Id in DB
         Normally I avoid nested if-else blocks, but this type of structure is required for my 1 button approach to the images */
        try {
            // Check for null must be done - request will return null if no file attached, so the .isEmpty method causes error
            // This is the way to get it to work with the current params - works fine though cause of the deleting of old recipe before saving the new one
            Recipe recipe = recipeServiceImpl.getRecipeById(recipeId);
            if (multipartFile != null && !multipartFile.isEmpty()) {
                // If there is an image attached to the input element with the "image" name attribute - these paths are followed
                if (recipe.getImageId() == null) {
                    // If there is an image upload and there is no previous image - upload image & return Id to save in DB
                    Map result = CloudinaryImpl.uploadFile(multipartFile);
                    return result.get("public_id").toString();
                } else {
                    // If there is an image upload and there is a previous image - delete old image then upload new image &  and return Id to save in DB
                    CloudinaryImpl.deleteImage(recipe.getImageId());
                    Map uploadResult = CloudinaryImpl.uploadFile(multipartFile);
                    return uploadResult.get("public_id").toString();
                }
            } else {
                // If there is no file attached to the input element with the "image" name attribute - meaning no image was uploaded by user - follow these
                if (formData.getFirst("deleted") != null  && recipe.getImageId() != null) {
                    // If the user has clicked the "Remove Image" button - a hidden input named "deleted" is added to the form
                    // couple the deleted element along with there being no new image uploaded - this indicates the user wants to remove the current image with no replacement
                    CloudinaryImpl.deleteImage(recipe.getImageId());
                } else {
                    // If this path is reached, it means no file was uploaded and the "Remove Image" button was NOT pressed by user
                    if (recipe.getImageId() != null) {
                        // If there is no new image and no image deleted - but an img Id exists on the recipe, then continue using that Id
                        return recipe.getImageId();
                    } else {
                        // If NO image upload is being done and there is NO previous image attached then return null
                        return null;
                    }
                }
            }
        } catch (IOException e) {
            throw new CloudinaryException(e.getMessage());
        }
        return null;
    }
}
