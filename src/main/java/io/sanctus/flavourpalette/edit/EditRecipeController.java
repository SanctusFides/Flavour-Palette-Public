package io.sanctus.flavourpalette.edit;

import io.sanctus.flavourpalette.exception.RecipeNotFoundException;
import io.sanctus.flavourpalette.ingredient.IngredientServiceImpl;
import io.sanctus.flavourpalette.instructions.InstructionsServiceImpl;
import io.sanctus.flavourpalette.recipe.RecipeDTO;
import io.sanctus.flavourpalette.recipe.RecipeServiceImpl;
import io.sanctus.flavourpalette.recipe_formDTO.IngredientFormDTO;
import io.sanctus.flavourpalette.recipe_formDTO.InstructionsFormDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.sanctus.flavourpalette.cloudinary_impl.CloudinaryImpl;

import java.security.Principal;

@Controller
class EditRecipeController {

    private final RecipeServiceImpl recipeServiceImpl;
    private final EditRecipeServiceImpl editRecipeServiceImpl;
    private final IngredientServiceImpl ingredientServiceImpl;
    private final InstructionsServiceImpl instructionsServiceImpl;

    @Autowired
    @SuppressWarnings("unused")
    public EditRecipeController(RecipeServiceImpl recipeServiceImpl,
                                EditRecipeServiceImpl editRecipeServiceImpl,
                                IngredientServiceImpl ingredientServiceImpl,
                                InstructionsServiceImpl instructionsServiceImpl) {
        this.recipeServiceImpl = recipeServiceImpl;
        this.editRecipeServiceImpl = editRecipeServiceImpl;
        this.ingredientServiceImpl = ingredientServiceImpl;
        this.instructionsServiceImpl = instructionsServiceImpl;
    }

    @GetMapping(value = "/edit/{recipeId}")
    public String editRecipeForm(@PathVariable String recipeId, Model model,
                                 Principal principal) {
        /* Since the Get request for the edit page functions the same as the create page form, just using the same
        service to call the recipe. The logic in EditRecipeService is related to the submission of the edit page form */
        try {
            RecipeDTO recipeDTO = recipeServiceImpl.getRecipeDTOById(recipeId);
//          First checks if the user is signed in - then it checks if their email matches the one on the recipe, if not then they are sent home
            if (principal == null || !principal.getName().equals(recipeDTO.getAuthorId())){
                return "redirect:/";
            }
            if (recipeDTO.getImageId() != null) {
                String previewImage = CloudinaryImpl.getSmallImageURL(recipeDTO.getImageId());
                recipeDTO.setImageId(previewImage);
            }
            model.addAttribute("formDTO", recipeDTO);
            model.addAttribute("ingrList", new IngredientFormDTO(ingredientServiceImpl.getIngrDTOListByRecipeDTO(recipeDTO)));
            model.addAttribute("instrList", new InstructionsFormDTO(instructionsServiceImpl.getInstrDTOListByRecipeDTO(recipeDTO)));

            String userId = principal.getName();
            model.addAttribute("loginId", userId);
            return "edit-recipe";
        } catch (RecipeNotFoundException e) {
            throw new RecipeNotFoundException(e.getMessage());
        }
    }

    @PostMapping("/editRecipe/{recipeId}")
    public String editRecipeForm(
            @ModelAttribute("formDTO") RecipeDTO recipeFormDTO,
            @ModelAttribute("instructionList") InstructionsFormDTO instrFormDTO,
            @ModelAttribute("ingredientList") IngredientFormDTO ingrFormDTO,
            @PathVariable String recipeId,
            @RequestParam MultiValueMap<String, String> formData,
            @RequestPart(value = "image", required = false) MultipartFile multipartFile,
            Principal principal) {
//      Passes form data, image file, recipeId and the user's account info for the handler function to generate and save the new values
        try {
//          First checks if the user is signed in - then it checks if their email matches the one on the recipe, if not then they are sent home
            RecipeDTO recipeDTO = recipeServiceImpl.getRecipeDTOById(recipeId);
            if (principal == null || !principal.getName().equals(recipeDTO.getAuthorId())){
                return "redirect:/";
            }
            editRecipeServiceImpl.handleUpdateRecipe(recipeFormDTO, instrFormDTO, ingrFormDTO, formData, multipartFile, recipeId, principal);
        } catch (Exception e) {
            return "redirect:/error";
        }
        return "redirect:/recipe/" + recipeId;
    }

}