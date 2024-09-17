package io.sanctus.flavourpalette.search;

import io.sanctus.flavourpalette.ingredient.IngredientServiceImpl;
import io.sanctus.flavourpalette.recipe.Recipe;
import io.sanctus.flavourpalette.recipe.RecipeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {

    private final RecipeServiceImpl recipeService;
    private final IngredientServiceImpl ingredientService;
    @Autowired
    @SuppressWarnings("unused")
    public SearchServiceImpl(RecipeServiceImpl recipeService, IngredientServiceImpl ingredientService) {
        this.recipeService = recipeService;
        this.ingredientService = ingredientService;
    }

    @Override
    public List<Recipe> getRecipesByName(String recipeName, int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum,pageSize);
        String lowerRecipeName = recipeName.replace(" ", "-").toLowerCase();
        Page<Recipe> result = recipeService.findBydbNameContaining(lowerRecipeName, pageable);
        return result.getContent();
    }

    @Override
    public int getRecipeCountByName(String recipeName) {
        String lowerRecipeName = recipeName.replace(" ", "-").toLowerCase();
        return recipeService.getCountBydbNameContaining(lowerRecipeName);
    }

    public List<Recipe> getRecipesByIngredient(String ingredientName) {
        String lowerIngrName = ingredientName.replace(" ", "-").toLowerCase();
        return ingredientService.getRecipeIdsByIngrName(lowerIngrName);
    }

    public int getIngredientCountByName(String ingrName) {
        String lowerIngrName = ingrName.replace(" ", "-").toLowerCase();
        return ingredientService.getCountBydbNameContaining(lowerIngrName);
    }
}
