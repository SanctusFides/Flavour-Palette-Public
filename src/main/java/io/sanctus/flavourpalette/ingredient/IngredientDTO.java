package io.sanctus.flavourpalette.ingredient;

import io.sanctus.flavourpalette.recipe.Recipe;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class IngredientDTO {
    private Long id;
    private Recipe recipe;
    private String name;
    private String dbName;
    private String quantity;
    private String size;
    private String prepType;
}
