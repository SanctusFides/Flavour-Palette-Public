package io.sanctus.flavourpalette.user_recipe_review;

import org.springframework.util.MultiValueMap;
import java.util.List;

@SuppressWarnings("unused")
public interface UserRecipeReviewService {
    UserRecipeReviewDTO getDTOByRecipeIdAndUserId(String recipeId, String userId);
    List<UserRecipeReview> getAllByRecipeId(String recipeId) ;
    void handleRecipeReview(MultiValueMap<String, String> formData, String userId, String recipeId);
    void deleteAllByRecipeId(String recipeId) ;
    void deleteAllReviewsByUserId(String userId);

    default UserRecipeReviewDTO mapToReviewDTO(UserRecipeReview userRecipeReview) {
        return new UserRecipeReviewDTO(
                userRecipeReview.getId(),
                userRecipeReview.getUserId(),
                userRecipeReview.getRecipeId(),
                userRecipeReview.getRating()
        );
    }
}
