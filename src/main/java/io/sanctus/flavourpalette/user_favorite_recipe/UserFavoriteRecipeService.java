package io.sanctus.flavourpalette.user_favorite_recipe;

import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public interface UserFavoriteRecipeService {
    List<UserFavoriteRecipeDTO> getAllByUserId(String userId) ;
    UserFavoriteRecipeDTO getByUserIdAndRecipeId(String userId, String recipeId);
    void saveUserFavoriteRecipe(UserFavoriteRecipeDTO userFavoriteRecipeDTO);
    List<UserFavoriteRecipe> getAllEntitiesByRecipeId(String recipeId);
    void deleteByUserIdAndRecipeId(String userId, String recipeId);
    void deleteAllByUserId(String userId);
    void handleDeleteAllByRecipeId(String recipeId);
    boolean favoritePreCheck(String userId, String recipeId);
    void handleFavoriteCheckBox(MultiValueMap<String, String> formData, String userId, String recipeId);

    default UserFavoriteRecipe mapToFavoriteEntity(UserFavoriteRecipeDTO userFavoriteRecipeDTO) {
        return new UserFavoriteRecipe(
                userFavoriteRecipeDTO.getId(),
                userFavoriteRecipeDTO.getUserId(),
                userFavoriteRecipeDTO.getRecipeId());
    }
    default UserFavoriteRecipeDTO mapToFavoriteDTO(UserFavoriteRecipe userFavoriteRecipe) {
        return new UserFavoriteRecipeDTO(
                userFavoriteRecipe.getId(),
                userFavoriteRecipe.getUserId(),
                userFavoriteRecipe.getRecipeId());
    }

    default List<UserFavoriteRecipeDTO> mapToFavoriteDTOList(List<UserFavoriteRecipe> userFavoriteRecipeList) {
        List<UserFavoriteRecipeDTO> userFavoriteRecipeDTOList = new ArrayList<>();
        userFavoriteRecipeList.forEach(userFavoriteRecipe -> userFavoriteRecipeDTOList.add( mapToFavoriteDTO(userFavoriteRecipe)));
        return userFavoriteRecipeDTOList;
    }
}
