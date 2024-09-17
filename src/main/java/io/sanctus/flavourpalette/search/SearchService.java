package io.sanctus.flavourpalette.search;

import io.sanctus.flavourpalette.recipe.Recipe;

import java.util.List;

@SuppressWarnings("unused")
public interface SearchService {
    List<Recipe> getRecipesByName(String recipeName, int pageNum, int pageSize);
    int getRecipeCountByName(String recipeName);
}
