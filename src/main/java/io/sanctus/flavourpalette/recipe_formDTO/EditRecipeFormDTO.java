package io.sanctus.flavourpalette.recipe_formDTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@SuppressWarnings("unused")
public class EditRecipeFormDTO {

    private String recipeName;
    private int prepTime;
    private int cookTime;
    private String description;
    private String imageId;
}
