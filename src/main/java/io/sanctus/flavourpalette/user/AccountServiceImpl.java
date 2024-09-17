package io.sanctus.flavourpalette.user;

import io.sanctus.flavourpalette.cloudinary_impl.CloudinaryImpl;
import io.sanctus.flavourpalette.exception.CloudinaryException;
import io.sanctus.flavourpalette.exception.UserNotFoundException;
import io.sanctus.flavourpalette.recipe.Recipe;
import io.sanctus.flavourpalette.recipe.RecipeServiceImpl;
import io.sanctus.flavourpalette.user_favorite_recipe.UserFavoriteRecipeServiceImpl;
import io.sanctus.flavourpalette.user_recipe_review.UserRecipeReviewServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {

    private final UserRepository userRepository;
    private final RecipeServiceImpl recipeService;
    private final UserFavoriteRecipeServiceImpl userFavoriteRecipeService;
    private final UserRecipeReviewServiceImpl userRecipeReviewService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    @SuppressWarnings("unused")
    public AccountServiceImpl(UserRepository userRepository,
                              RecipeServiceImpl recipeService,
                              UserFavoriteRecipeServiceImpl userFavoriteRecipeService,
                              UserRecipeReviewServiceImpl userRecipeReviewService,
                              BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.recipeService = recipeService;
        this.userFavoriteRecipeService = userFavoriteRecipeService;
        this.userRecipeReviewService = userRecipeReviewService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public User loadUserByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new UserNotFoundException("User not found");
        }
    }

    @Override
    public void handlePasswordUpdate(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            user.get().setPassword(bCryptPasswordEncoder.encode(password));
            userRepository.save(user.get());
        } else {
            throw new UserNotFoundException("User not found");
        }
    }

    @Override
    public void handleDeleteUserByUsername(String username) {
/*      First we gather a list of all the recipes this user has created, looping through and deleting them
        Also checking if there is any image associated with the recipes and deleting it from Cloudinary
        Lastly, we need to loop through any ratings and reviews created by the user and delete those
        Once all their data is removed from the tables, we can finally remove their user from the user table */
        List<Recipe> userRecipes = recipeService.getRecipesByAuthorId(username);
        if (!userRecipes.isEmpty()) {
            userRecipes.forEach(recipe -> {
                recipeService.deleteRecipeById(recipe.getRecipeId());
                if (recipe.getImageId() != null) {
                    try {
                        CloudinaryImpl.deleteImage(recipe.getImageId());
                    } catch (IOException e) {
                        throw new CloudinaryException(e.getMessage());
                    }
                }
            });
        }
        if (!userFavoriteRecipeService.getAllByUserId(username).isEmpty()){
            userFavoriteRecipeService.deleteAllByUserId(username);
        }
        if (!userRecipeReviewService.getAllByUserId(username).isEmpty()) {
            userRecipeReviewService.deleteAllReviewsByUserId(username);
        }
        userRepository.deleteByUsername(username);
    }
}
