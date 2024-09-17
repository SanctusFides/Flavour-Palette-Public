package io.sanctus.flavourpalette.repository;

import io.sanctus.flavourpalette.instructions.Instructions;
import io.sanctus.flavourpalette.instructions.InstructionsRepository;
import io.sanctus.flavourpalette.recipe.Recipe;
import io.sanctus.flavourpalette.recipe.RecipeRepository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class InstructionRepositoryTest {

    private final InstructionsRepository instructionsRepository;
    private final RecipeRepository recipeRepository;
    private final Instructions instruction1;
    private final Instructions instruction2;
    private final Instructions instruction3;
    private final Recipe recipe;

    @Autowired
    InstructionRepositoryTest(InstructionsRepository instructionsRepository, RecipeRepository recipeRepository) {
        this.instructionsRepository = instructionsRepository;
        this.recipeRepository = recipeRepository;
        this.recipe = Recipe.builder().recipeId("ABC").recipeName("Sandwich").build();
        this.instruction1 = Instructions.builder().stepText("Step 1").recipe(recipe).build();
        this.instruction2 = Instructions.builder().stepText("Step 2").recipe(recipe).build();
        this.instruction3 = Instructions.builder().stepText("Step 3").recipe(recipe).build();
    }
    @BeforeEach
    void init() {
        recipeRepository.save(recipe);
    } 
    @AfterEach
    void tearDown(){
        recipeRepository.deleteAll();
    }
     
    @Test
    void Save_FindAll_ReturnSavedInstruction() {
        instructionsRepository.save(instruction1);
        List<Instructions> savedInstruction1 = instructionsRepository.findAll();

        assertThat(savedInstruction1).isNotEmpty();
        assertThat(savedInstruction1.getFirst()).isNotNull();
        assertThat(savedInstruction1.getFirst().getStepText()).isEqualTo("Step 1");
        assertThat(savedInstruction1.getFirst().getRecipe()).isEqualTo(recipe);
    }

    @Test
    void SaveAll_FindAll_ReturnsListSavedInstruction() {
        List<Instructions> instructionsList = new ArrayList<>();
        instructionsList.add(instruction1);
        instructionsList.add(instruction2);
        instructionsList.add(instruction3);

        instructionsRepository.saveAll(instructionsList);
        List<Instructions> savedInstruction1 = instructionsRepository.findAll();

        assertThat(savedInstruction1).isNotEmpty();
        assertThat(savedInstruction1.getFirst()).isNotNull();
        assertThat(savedInstruction1.getFirst().getStepText()).isEqualTo("Step 1");
        assertThat(savedInstruction1.getFirst().getRecipe()).isEqualTo(recipe);
    }


    @Test
    void FindAllByRecipeRecipeId_ReturnsInstructionMatchingRecipeId() {
        instructionsRepository.save(instruction1);
        instructionsRepository.save(instruction2);

        Optional<List<Instructions>> instructionsListOpt = instructionsRepository.findAllByRecipeRecipeId("ABC");
        List<Instructions> instructionsListActual = null;
        if (instructionsListOpt.isPresent()) {
            instructionsListActual = instructionsListOpt.get();
        }

        assertThat(instructionsListActual).isNotNull()
                .isNotEmpty()
                .hasSize(2);
        assertThat(instructionsListActual.get(1).getStepText()).isEqualTo("Step 2");
    }

    @Test
    void DeleteAll_ReturnsEmptyList() {
        instructionsRepository.save(instruction1);
        instructionsRepository.save(instruction2);

        instructionsRepository.deleteAll();
        List<Instructions> instructionsList = instructionsRepository.findAll();

        assertThat(instructionsList).isEmpty();
    }

}
