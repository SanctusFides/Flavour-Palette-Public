package io.sanctus.flavourpalette.user_recipe_review;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserRecipeReviewDTO {

    private Long id;
    private String userId;
    private String recipeId;
    private int rating;

}
