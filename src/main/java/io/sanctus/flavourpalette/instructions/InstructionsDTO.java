package io.sanctus.flavourpalette.instructions;

import io.sanctus.flavourpalette.recipe.Recipe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class InstructionsDTO {
    private int id;
    private Recipe recipe;
    private String stepText;
}
