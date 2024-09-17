package io.sanctus.flavourpalette.user_favorite_recipe;

import io.sanctus.flavourpalette.exception.UserFavoriteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Optional;

@Service
public class UserFavoriteRecipeServiceImpl implements UserFavoriteRecipeService{

    private final UserFavoriteRecipeRepository userFavoriteRecipeRepository;

    @Autowired
    @SuppressWarnings("unused")
    public UserFavoriteRecipeServiceImpl(UserFavoriteRecipeRepository userFavoriteRecipeRepository) {
        this.userFavoriteRecipeRepository = userFavoriteRecipeRepository;
    }

    @Override
    public void saveUserFavoriteRecipe(UserFavoriteRecipeDTO userFavoriteRecipeDTO) throws UserFavoriteException {
        try{
            UserFavoriteRecipe userFavoriteRecipe = mapToFavoriteEntity(userFavoriteRecipeDTO);
            userFavoriteRecipeRepository.save(userFavoriteRecipe);
        } catch (UserFavoriteException e) {
            throw new UserFavoriteException(e.getMessage());
        }
    }

    @Override
    public List<UserFavoriteRecipeDTO> getAllByUserId(String userId)  {
        Optional<List<UserFavoriteRecipe>> result = userFavoriteRecipeRepository.findAllByUserId(userId);
        return result.map(this::mapToFavoriteDTOList).orElse(null);
    }

    @Override
    public UserFavoriteRecipeDTO getByUserIdAndRecipeId(String userId, String recipeId) {
        Optional<UserFavoriteRecipe> result = userFavoriteRecipeRepository.findByUserIdAndRecipeId(userId, recipeId);
        return result.map(this::mapToFavoriteDTO).orElse(null);
    }

    @Override
    public List<UserFavoriteRecipe> getAllEntitiesByRecipeId(String recipeId) {
        Optional<List<UserFavoriteRecipe>> result = userFavoriteRecipeRepository.findAllByRecipeId(recipeId);
        return result.orElse(null);
    }

    @Override
    public void deleteByUserIdAndRecipeId(String userId, String recipeId) {
        userFavoriteRecipeRepository.deleteByUserIdAndRecipeId(userId, recipeId);
    }

    @Override
    public void handleDeleteAllByRecipeId(String recipeId) {
//      Look up a list of all the instances of this recipe being favorited and then delete them using User and Recipe Ids
        List<UserFavoriteRecipe> favoriteRecipeList =  getAllEntitiesByRecipeId(recipeId);
        favoriteRecipeList.forEach(favoriteResult ->
                deleteByUserIdAndRecipeId(favoriteResult.getUserId(),favoriteResult.getRecipeId()));
    }

    @Override
    public void deleteAllByUserId(String userId) {
        userFavoriteRecipeRepository.deleteAllByUserId(userId);
    }

    @Override
    public boolean favoritePreCheck(String userId, String recipeId) {
        UserFavoriteRecipeDTO checkResult = getByUserIdAndRecipeId(userId, recipeId);
        return checkResult != null;
    }

    @Override
    public void handleFavoriteCheckBox(MultiValueMap<String, String>  formData,
                                       String userId,
                                       String recipeId) {
        UserFavoriteRecipeDTO checkResult = getByUserIdAndRecipeId(userId, recipeId);
        if (checkResult != null) {
            if (formData.getFirst("favorited") == null) {
                deleteByUserIdAndRecipeId(userId, recipeId);
            }
        } else {
            if (formData != null) {
                UserFavoriteRecipeDTO favoriteRecipeDTO = new UserFavoriteRecipeDTO();
                favoriteRecipeDTO.setUserId(userId);
                favoriteRecipeDTO.setRecipeId(recipeId);
                saveUserFavoriteRecipe(favoriteRecipeDTO);
            }
        }
    }
}
