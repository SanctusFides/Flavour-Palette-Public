package io.sanctus.flavourpalette.recipe;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.security.Principal;
import java.util.List;

@SuppressWarnings("unused")
public interface RecipeService {
    RecipeDTO saveRecipeToDB(RecipeDTO recipeDTO);
    Recipe getRecipeById(String recipeId);
    RecipeDTO getRecipeDTOById(String recipeId);
    void deleteRecipeById(String recipeId);
    List<Recipe> getRecipesByAuthorId(String authorId) ;
    boolean checkAuthorRights(Principal principal, RecipeDTO recipeDTO);
    String getFullImageURL(RecipeDTO recipeDTO);
    String getThumbImageURL(RecipeDTO recipeDTO);
    Page<Recipe> findBydbNameContaining(String normalizedRecipeName, Pageable pageable);
    int getCountBydbNameContaining(String normalizedRecipeName);

    default Recipe mapToEntity(RecipeDTO recipeDTO) {
        Recipe recipe = new Recipe();
        recipe.setRecipeId(recipeDTO.getRecipeId());
        recipe.setAuthorId(recipeDTO.getAuthorId());
        recipe.setRecipeName(recipeDTO.getRecipeName());
        recipe.setDbName(recipeDTO.getDbName());
        recipe.setPrepTime(recipeDTO.getPrepTime());
        recipe.setCookTime(recipeDTO.getCookTime());
        recipe.setTotalTime(recipeDTO.getPrepTime() + recipeDTO.getCookTime());
        recipe.setDescription(recipeDTO.getDescription());
        if (recipeDTO.getImageId() != null) {
            recipe.setImageId(recipeDTO.getImageId());
        }
        return recipe;
    }
    default RecipeDTO mapToDTO(Recipe recipe) {
        RecipeDTO recipeDTO = new RecipeDTO();
        recipeDTO.setRecipeId(recipe.getRecipeId());
        recipeDTO.setAuthorId(recipe.getAuthorId());
        recipeDTO.setRecipeName(recipe.getRecipeName());
        recipeDTO.setDbName(recipe.getDbName());
        recipeDTO.setPrepTime(recipe.getPrepTime());
        recipeDTO.setCookTime(recipe.getCookTime());
        recipeDTO.setTotalTime(recipe.getPrepTime() + recipe.getCookTime());
        recipeDTO.setDescription(recipe.getDescription());
        if (recipe.getImageId() != null) {
            recipeDTO.setImageId(recipe.getImageId());
        }
        return recipeDTO;
    }
}
