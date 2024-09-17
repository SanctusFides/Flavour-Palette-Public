package io.sanctus.flavourpalette.home;

import java.security.Principal;
import java.util.List;

import io.sanctus.flavourpalette.favorites.FavoritesServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import io.sanctus.flavourpalette.bundle_recipe_ratings.BundledRecipeRating;

@Controller
class HomeController {

    private final FavoritesServiceImpl favoritesService;
    @Autowired
    @SuppressWarnings("unused")
    public HomeController(FavoritesServiceImpl favoritesService) {
        this.favoritesService = favoritesService;
    }

    @GetMapping(value = "/")
    public String index(Principal principalUser, Model model)  {
        /* Checks if the principalUser is logged in, can only access homepage if logged in. Sends
           guests back to index page */
        if (principalUser == null || principalUser.getName() == null) {
            return "index";
        }
        // Obtaining the user email for repo look up of all their recipes
        String userId = principalUser.getName();
        model.addAttribute("loginId", userId);

        List<BundledRecipeRating> bundledData = favoritesService.getBundledData(userId);
        model.addAttribute("favoriteList", bundledData);
        return "home";
    }
}
