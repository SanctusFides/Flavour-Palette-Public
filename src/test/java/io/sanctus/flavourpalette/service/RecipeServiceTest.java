package io.sanctus.flavourpalette.service;

import io.sanctus.flavourpalette.cloudinary_impl.CloudinaryImpl;
import io.sanctus.flavourpalette.recipe.RecipeDTO;
import io.sanctus.flavourpalette.recipe.RecipeServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Principal;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class RecipeServiceTest {

    @MockBean
    CloudinaryImpl cloudinary;

    private final RecipeServiceImpl recipeService;

    @Autowired
    public RecipeServiceTest (RecipeServiceImpl recipeService) {
        this.recipeService = recipeService;
    }

    @Test
    void CheckAuthorRights_ReturnTrue() {
        Authentication userAuth = new UsernamePasswordAuthenticationToken("Test","test" ,new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(userAuth);
        Principal principal = SecurityContextHolder.getContext().getAuthentication();

        RecipeDTO recipeDTO = new RecipeDTO();
        recipeDTO.setAuthorId("Test");
        recipeDTO.setImageId("test!");

        boolean result = recipeService.checkAuthorRights(principal,recipeDTO);
        assertThat(result).isTrue();
    }
}