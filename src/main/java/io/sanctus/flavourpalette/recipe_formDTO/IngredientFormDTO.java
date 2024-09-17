package io.sanctus.flavourpalette.recipe_formDTO;

import io.sanctus.flavourpalette.ingredient.IngredientDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngredientFormDTO {

    private List<IngredientDTO> ingredientList;
}
