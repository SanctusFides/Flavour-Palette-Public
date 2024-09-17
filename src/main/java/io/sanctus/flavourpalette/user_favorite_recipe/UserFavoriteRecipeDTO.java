package io.sanctus.flavourpalette.user_favorite_recipe;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserFavoriteRecipeDTO {

    private Long id;
    private String userId;
    private String recipeId;
}
