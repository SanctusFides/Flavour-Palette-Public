package io.sanctus.flavourpalette.user_recipe_review;

import io.sanctus.flavourpalette.exception.UserReviewException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserRecipeReviewServiceImpl implements UserRecipeReviewService {

    private final UserRecipeReviewRepository userRecipeReviewRepository;

    @Autowired
    @SuppressWarnings("unused")
    public UserRecipeReviewServiceImpl(UserRecipeReviewRepository userRecipeReviewRepository) {
        this.userRecipeReviewRepository = userRecipeReviewRepository;
    }

    @Override
    public UserRecipeReviewDTO getDTOByRecipeIdAndUserId(String recipeId, String userId) {
        Optional<UserRecipeReview> result = userRecipeReviewRepository.findByRecipeIdAndUserId(recipeId, userId);
        if (result.isPresent()) {
            return mapToReviewDTO(result.get());
        }
        return new UserRecipeReviewDTO();
    }

    @Override
    public List<UserRecipeReview> getAllByRecipeId(String recipeId) {
        Optional<List<UserRecipeReview>> result = userRecipeReviewRepository.findAllByRecipeId(recipeId);
        return result.orElseGet(ArrayList::new);
    }

    public List<UserRecipeReview> getAllByUserId(String userId) {
        Optional<List<UserRecipeReview>> result = userRecipeReviewRepository.findAllByUserId(userId);
        return result.orElseGet(ArrayList::new);
    }

    @Override
    public void deleteAllReviewsByUserId(String userId){
        userRecipeReviewRepository.deleteAllByUserId(userId);
    }

    @Override
    public void deleteAllByRecipeId(String recipeId) {
        List<UserRecipeReview> favoriteList = getAllByRecipeId(recipeId);
        for (UserRecipeReview recipe : favoriteList) {
            userRecipeReviewRepository.deleteById(recipe.getId());
        }
    }

    @Override
    public void handleRecipeReview(MultiValueMap<String, String> formData,
                                       String userId,
                                       String recipeId) throws UserReviewException {
        Optional<UserRecipeReview> result = userRecipeReviewRepository.findByRecipeIdAndUserId(recipeId, userId);
        String userRating = formData.getFirst("rating");

//      This initial if block activates the logic if a user has entered a rating, if null then we just ignore
        if (userRating != null && !userRating.isBlank()) {
            int userIntRating = Integer.parseInt(userRating);
//          These 2 if checks ensure the user can't edit the form to a value outside 0 through 5
            if (userIntRating > 5) {
                userIntRating = 5;
            } else if (userIntRating < 0) {
                userIntRating = 0;
            }
//          This if else block checks if a review already exists - if so then we need to update and if not then create
            if (result.isPresent()) {
                if (result.get().getRating() != userIntRating) {
                    UserRecipeReview review = result.get();
                    review.setRating(userIntRating);
                    userRecipeReviewRepository.save(review);
                }
            } else {
                UserRecipeReview recipeReview = new UserRecipeReview();
                recipeReview.setUserId(userId);
                recipeReview.setRecipeId(recipeId);
                recipeReview.setRating(userIntRating);
                userRecipeReviewRepository.save(recipeReview);
            }
        }
    }
}
