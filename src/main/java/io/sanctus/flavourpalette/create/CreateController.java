package io.sanctus.flavourpalette.create;

import io.sanctus.flavourpalette.error.ErrorDTO;
import io.sanctus.flavourpalette.exception.FormNotValidException;
import io.sanctus.flavourpalette.recipe.RecipeDTO;
import io.sanctus.flavourpalette.recipe_formDTO.IngredientFormDTO;
import io.sanctus.flavourpalette.recipe_formDTO.InstructionsFormDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import java.security.Principal;

@Controller
class CreateController {

    private final CreateServiceImpl createServiceImpl;

    @Autowired
    public CreateController(CreateServiceImpl createServiceImpl) {
        this.createServiceImpl = createServiceImpl;
    }

    @GetMapping("/create-recipe")
    public String createRecipeForm(Principal principal,
                                   Model model,
                                   @ModelAttribute("MissingValueError") ErrorDTO errorDTO) {
        if (principal != null) {
            if (errorDTO != null && errorDTO.getMessage() != null) {
                model.addAttribute("error", errorDTO.getMessage());
            }
            model.addAttribute("loginId", principal.getName());
            model.addAttribute("recipe", new RecipeDTO());
            model.addAttribute("instrList", new InstructionsFormDTO());
            model.addAttribute("ingrList", new IngredientFormDTO());
            return "create-recipe";
        }
        return "index";
    }

    // The Request Param for the multipart file is what is passed in for the recipe image that is uploaded to Cloudinary
    @PostMapping("/createRecipe")
    public String createRecipe(@ModelAttribute("recipe") RecipeDTO recipeFormDTO,
                               @ModelAttribute("ingrList") IngredientFormDTO ingrList,
                               @ModelAttribute("instrList") InstructionsFormDTO instrList,
                               @RequestParam("image") MultipartFile multipartFile,
                               Principal principal) {
        if (recipeFormDTO.nullCheck()) {
            throw new FormNotValidException(recipeFormDTO.nullTypeCheck());
        } else {
            RecipeDTO recipeDTO = createServiceImpl.handleSavingRecipe(recipeFormDTO,instrList,ingrList,multipartFile,principal);
            return ("redirect:/recipe/" + recipeDTO.getRecipeId());
        }
    }
}
