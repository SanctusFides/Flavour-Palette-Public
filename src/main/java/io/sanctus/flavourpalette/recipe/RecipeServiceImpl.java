package io.sanctus.flavourpalette.recipe;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import io.sanctus.flavourpalette.cloudinary_impl.CloudinaryImpl;
import io.sanctus.flavourpalette.exception.RecipeNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service()
public class RecipeServiceImpl implements RecipeService {

    private final RecipeRepository recipeRepository;

    @Autowired
    @SuppressWarnings("unused")
    public RecipeServiceImpl(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    @Override
    public RecipeDTO saveRecipeToDB(RecipeDTO recipeDTO) {
//      First use the DTO to build out the recipe object that is then saved to the DB and then return the DTO that was sent in.
        Recipe recipe = mapToEntity(recipeDTO);
        Recipe savedResponse = recipeRepository.save(recipe);
/*      The reason I am reconstructing this for now and returning it, is if anything happens in the building process
        then I can take a look at the recipe that was built instead of relying on the DTO argument being correct */
        return mapToDTO(savedResponse);
    }

    @Override
    public Recipe getRecipeById(String recipeId) {
        Optional<Recipe> result = Optional.of(recipeRepository.findById(recipeId).orElseThrow());
        return result.orElse(null);
    }

    public RecipeDTO getRecipeDTOById(String recipeId) throws RecipeNotFoundException {
        Optional<Recipe> result = Optional.ofNullable(recipeRepository.findById(recipeId).orElseThrow(() -> new RecipeNotFoundException("Recipe not found")));
        return result.map(this::mapToDTO).orElse(null);
    }

    @Override
    public List<Recipe> getRecipesByAuthorId(String authorId) {
        Optional<List<Recipe>> results = recipeRepository.findAllByAuthorId(authorId);
        return results.orElse(null);
    }

    @Override
    public void deleteRecipeById(String recipeId) {
        try {
            recipeRepository.deleteByRecipeId(recipeId);
        } catch(RecipeNotFoundException e) {
            throw new RecipeNotFoundException(e.getMessage());
        }
    }

    @Override
    public Page<Recipe> findBydbNameContaining(String normalizedRecipeName, Pageable pageable){
        return recipeRepository.findBydbNameContaining(normalizedRecipeName, pageable);
    }

    @Override
    public int getCountBydbNameContaining(String normalizedRecipeName) {
        Optional<Integer> count = recipeRepository.countBydbNameContaining(normalizedRecipeName);
        return count.orElse(0);
    }

    @Override
    public boolean checkAuthorRights(Principal principal, RecipeDTO recipeDTO) {
        return principal != null && recipeDTO.getAuthorId().equals(principal.getName());
    }

    @Override
    public String getFullImageURL(RecipeDTO recipeDTO) {
        if (recipeDTO.getImageId() != null) {
            return CloudinaryImpl.getFullImageURL(recipeDTO.getImageId());
        } else {
            return "../images/no-image.png";
        }
    }

    @Override
    public String getThumbImageURL(RecipeDTO recipeDTO) {
        if (recipeDTO.getImageId() != null) {
            return CloudinaryImpl.getThumbImageURL(recipeDTO.getImageId());
        } else {
            return "../images/no-image.png";
        }
    }
}
