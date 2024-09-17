package io.sanctus.flavourpalette.error;

import io.sanctus.flavourpalette.recipe.RecipeDTO;
import io.sanctus.flavourpalette.recipe_formDTO.IngredientFormDTO;
import io.sanctus.flavourpalette.recipe_formDTO.InstructionsFormDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("unused")
public class FormErrorDTO {

    private String missingField;
    private RecipeDTO recipeDTO;
    private InstructionsFormDTO instrFormDTO;
    private IngredientFormDTO ingrFormDTO;

    public FormErrorDTO(String missingField,
                                 RecipeDTO recipeDTO,
                                 InstructionsFormDTO instrFormDTO,
                                 IngredientFormDTO ingrFormDTO) {
        this.missingField = missingField;
        this.recipeDTO = recipeDTO;
        this.instrFormDTO = instrFormDTO;
        this.ingrFormDTO = ingrFormDTO;
    }

}
