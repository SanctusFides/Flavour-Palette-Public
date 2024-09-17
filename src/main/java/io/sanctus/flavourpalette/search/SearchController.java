package io.sanctus.flavourpalette.search;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.sanctus.flavourpalette.recipe.Recipe;

@SuppressWarnings("SameReturnValue")
@Controller
public class SearchController {

    private final SearchServiceImpl searchServiceImpl;

    @Autowired
    @SuppressWarnings("unused")
    public SearchController(SearchServiceImpl searchServiceImpl) {
        this.searchServiceImpl = searchServiceImpl;
    }

    @GetMapping(value = "/search")
    public String getSearchResults(@RequestParam String c,
                                    @RequestParam String q,
                                    @RequestParam(value = "pageNum", defaultValue = "0", required = false) int pageNum,
                                    @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
                                    Model model,
                                    Principal principal) {
/*      Setting the start values to be 0 and blank for recipes so that the values can be set based on the search
        criteria chosen from the dropdown box for searching based on recipe name, ingredients */
        String searchCount = String.valueOf(0);
        List<Recipe> recipesFound = new ArrayList<>();
        if (!q.isBlank() ) {
            if (c.equals("R")) {
                 recipesFound = searchServiceImpl.getRecipesByName(q, pageNum, pageSize);
                 searchCount = String.valueOf(searchServiceImpl.getRecipeCountByName(q));
            } else if (c.equals("I")) {
                recipesFound = searchServiceImpl.getRecipesByIngredient(q);
                searchCount = String.valueOf(searchServiceImpl.getIngredientCountByName(q));
            }
            model.addAttribute("prevSearch", q);
        }
//      Checks if user is logged in and attach their Id to the model for it to display login button or user features
        if (principal != null && principal.getName() != null) {
            String userId = principal.getName();
            model.addAttribute("loginId", userId);
        }
        model.addAttribute("prevCategory", c);
        model.addAttribute("searchCount", searchCount);
        model.addAttribute("searchResult", recipesFound);
        return "search";
    }
}
