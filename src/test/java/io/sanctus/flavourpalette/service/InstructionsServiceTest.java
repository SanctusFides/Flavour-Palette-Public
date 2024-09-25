package io.sanctus.flavourpalette.service;

import io.sanctus.flavourpalette.cloudinary_impl.CloudinaryImpl;
import io.sanctus.flavourpalette.instructions.Instructions;
import io.sanctus.flavourpalette.instructions.InstructionsDTO;
import io.sanctus.flavourpalette.instructions.InstructionsRepository;
import io.sanctus.flavourpalette.instructions.InstructionsServiceImpl;
import io.sanctus.flavourpalette.recipe.Recipe;
import io.sanctus.flavourpalette.recipe.RecipeDTO;
import io.sanctus.flavourpalette.recipe.RecipeRepository;
import jakarta.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class InstructionsServiceTest {
    @MockBean
    CloudinaryImpl cloudinary;

    private final InstructionsRepository instructionsRepository;
    private final InstructionsServiceImpl instructionsService;
    private final List<InstructionsDTO> instrDTOList;

    @Autowired
    InstructionsServiceTest(RecipeRepository recipeRepository,
                            InstructionsServiceImpl instructionsService,
                            InstructionsRepository instructionsRepository) {
        this.instructionsRepository = instructionsRepository;
        this.instructionsService = instructionsService;

        Recipe recipe = Recipe.builder().recipeId("ABC").recipeName("Sandwich").build();
        recipeRepository.save(recipe);

        instrDTOList = new ArrayList<>();
        instrDTOList.add(new InstructionsDTO(1, recipe,"first"));
        instrDTOList.add(new InstructionsDTO(2, recipe,"second"));
        Instructions instr1 = Instructions.builder().id(1).stepText("first").recipe(recipe).build();
        Instructions instr2 = Instructions.builder().id(2).stepText("second").recipe(recipe).build();
        instructionsRepository.save(instr1);
        instructionsRepository.save(instr2);
    }

    @Test
    @Transactional
    void DeleteInstrList_FindAll_ReturnsEmpty() {
        List<Instructions> preDelete = instructionsRepository.findAll();
        assertThat(preDelete).isNotNull().isNotEmpty().hasSize(2);
        instructionsService.deleteInstrList(instrDTOList);
        List<Instructions> postDelete = instructionsRepository.findAll();
        assertThat(postDelete).isEmpty();
    }

    @Test
    @Transactional
    void GetInstrDTOListByRecipeDTO_ReturnsInstrDTOList() {
        RecipeDTO recipeDTO = RecipeDTO.builder().recipeId("ABC").recipeName("Sandwich").build();
        List<InstructionsDTO> instrList = instructionsService.getInstrDTOListByRecipeDTO(recipeDTO);
        assertThat(instrList).isNotNull().hasSize(2);
        assertThat(instrList.getLast().getId()).isEqualTo(2);
        assertThat(instrList.getLast().getStepText()).isEqualTo("second");
    }

    @Test
    @Transactional
    void SaveInstrList_ReturnsInstrDTOList() {
        instructionsService.saveInstrList(instrDTOList);
        Optional<List<Instructions>> repoListOpt = instructionsRepository.findAllByRecipeRecipeId("ABC");
        List<Instructions> repoActual = null;
        if (repoListOpt.isPresent()) {
            repoActual = repoListOpt.get();
        }
        assertThat(repoActual).isNotNull().hasSize(2);
        assertThat(repoActual.getLast().getId()).isEqualTo(2);
        assertThat(repoActual.getLast().getStepText()).isEqualTo("second");
    }
}
