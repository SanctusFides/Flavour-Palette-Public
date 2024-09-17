package io.sanctus.flavourpalette.recipe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeDTO {

    private String recipeId;
    private String authorId;
    private String recipeName;
    private String dbName;
    private Integer prepTime;
    private Integer cookTime;
    private int totalTime;
    private String description;
    private String imageId;

    public boolean nullCheck() {
        if (this.recipeName == null) {
            return true;
        }
        return this.description == null;
    }
    public String nullTypeCheck() {
        if (this.recipeName == null) {
            return "recipeName";
        }
        if (this.description == null) {
            return "description";
        }
        return "";
    }
}
