package io.sanctus.flavourpalette.bundle_recipe_ratings;

import io.sanctus.flavourpalette.recipe.RecipeDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BundledRecipeRating {

  private RecipeDTO recipeDTO;
  private int rating;

}
