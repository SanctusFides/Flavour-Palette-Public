package io.sanctus.flavourpalette.favorites;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import io.sanctus.flavourpalette.bundle_recipe_ratings.BundledRecipeRating;

@Controller
public class FavoritesController {

    private final FavoritesServiceImpl favoritesServiceImpl;

    @Autowired
    @SuppressWarnings("unused")
    public FavoritesController(FavoritesServiceImpl favoritesServiceImpl) {
        this.favoritesServiceImpl = favoritesServiceImpl;
    }

    @GetMapping("/favorites")
    public String getFavorites(Principal principalUser, Model model)  {

        if (principalUser == null || principalUser.getName() == null) {
            return "index";
        }
        String userId = principalUser.getName();
        model.addAttribute("loginId", userId);

        List<BundledRecipeRating> bundledData = favoritesServiceImpl.getBundledData(userId);
        model.addAttribute("favoriteList", bundledData);

        return "favorites";
    }
    
}
