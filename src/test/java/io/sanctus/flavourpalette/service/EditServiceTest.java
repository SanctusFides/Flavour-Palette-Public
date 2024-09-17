package io.sanctus.flavourpalette.service;

import io.sanctus.flavourpalette.cloudinary_impl.CloudinaryImpl;
import io.sanctus.flavourpalette.edit.EditRecipeServiceImpl;
import io.sanctus.flavourpalette.ingredient.Ingredient;
import io.sanctus.flavourpalette.ingredient.IngredientDTO;
import io.sanctus.flavourpalette.ingredient.IngredientRepository;
import io.sanctus.flavourpalette.ingredient.IngredientServiceImpl;
import io.sanctus.flavourpalette.instructions.Instructions;
import io.sanctus.flavourpalette.instructions.InstructionsDTO;
import io.sanctus.flavourpalette.instructions.InstructionsRepository;
import io.sanctus.flavourpalette.instructions.InstructionsServiceImpl;
import io.sanctus.flavourpalette.recipe.Recipe;
import io.sanctus.flavourpalette.recipe.RecipeDTO;
import io.sanctus.flavourpalette.recipe.RecipeRepository;
import io.sanctus.flavourpalette.recipe.RecipeServiceImpl;
import io.sanctus.flavourpalette.recipe_formDTO.IngredientFormDTO;
import io.sanctus.flavourpalette.recipe_formDTO.InstructionsFormDTO;
import lombok.NonNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class EditServiceTest {

    @MockBean
    CloudinaryImpl cloudinary;

    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final InstructionsRepository instructionsRepository;
    private final EditRecipeServiceImpl editRecipeService;
    private final RecipeServiceImpl recipeServiceImpl;
    private final InstructionsServiceImpl instructionsServiceImpl;
    private final IngredientServiceImpl ingredientServiceImpl;
    private final RecipeDTO recipeDTO;
    private final List<InstructionsDTO> instructionsDTOList;
    private final InstructionsFormDTO instructionsFormDTO;
    private final List<IngredientDTO> ingredientDTOList;
    private final IngredientFormDTO ingredientFormDTO;

    @Autowired
    public EditServiceTest(EditRecipeServiceImpl editRecipeService,
                           RecipeServiceImpl recipeServiceImpl,
                           InstructionsServiceImpl instructionsServiceImpl,
                           IngredientServiceImpl ingredientServiceImpl,
                           RecipeRepository recipeRepository,
                           IngredientRepository ingredientRepository,
                           InstructionsRepository instructionsRepository) {
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
        this.instructionsRepository = instructionsRepository;
        this.editRecipeService = editRecipeService;
        this.recipeServiceImpl = recipeServiceImpl;
        this.instructionsServiceImpl = instructionsServiceImpl;
        this.ingredientServiceImpl = ingredientServiceImpl;
        this.recipeDTO = RecipeDTO.builder().recipeId("ABC").recipeName("Sandwich").prepTime(5).cookTime(5).build();
        this.instructionsFormDTO = new InstructionsFormDTO();
        this.instructionsDTOList = new ArrayList<>();
        this.ingredientFormDTO = new IngredientFormDTO();
        this.ingredientDTOList = new ArrayList<>();
    }
    @BeforeEach
    void init() {
        recipeServiceImpl.saveRecipeToDB(recipeDTO);
        instructionsDTOList.add(InstructionsDTO.builder().id(1).stepText("Step 1").build());
        instructionsDTOList.add(InstructionsDTO.builder().id(2).stepText("Step 2").build());
        instructionsFormDTO.setInstructionList(instructionsDTOList);
        ingredientDTOList.add(IngredientDTO.builder().id(1L).name("Ingr 1").quantity("1").build());
        ingredientDTOList.add(IngredientDTO.builder().id(2L).name("Ingr 2").quantity("2").build());
        ingredientFormDTO.setIngredientList(ingredientDTOList);
    }
    @AfterEach
    void tearDown() {
        instructionsDTOList.clear();
        ingredientDTOList.clear();
        recipeRepository.deleteAll();
        ingredientRepository.deleteAll();
        instructionsRepository.deleteAll();
    }

    @Test
    void GenerateUpdatedInstructions_ReturnsListOfInstructionDTOs() {
        List<InstructionsDTO> result = editRecipeService.generateUpdatedInstructions(instructionsFormDTO,recipeDTO);
        assertThat(result).isNotEmpty().isNotNull().hasSize(2);
        assertThat(result.getFirst().getId()).isEqualTo(1);
        assertThat(result.getFirst().getRecipe().getRecipeName()).isEqualTo("Sandwich");
        assertThat(result.getFirst().getStepText()).isEqualTo("Step 1");
        assertThat(result.getLast().getId()).isEqualTo(2);
        assertThat(result.getLast().getStepText()).isEqualTo("Step 2");
    }

    @Test
    void GenerateUpdatedIngredients_ReturnsListOfIngredientDTOs() {
        List<IngredientDTO> result = editRecipeService.generateUpdatedIngredientsDTO(ingredientFormDTO,recipeDTO);
        assertThat(result).isNotEmpty().isNotNull().hasSize(2);
        assertThat(result.getFirst().getRecipe().getRecipeName()).isEqualTo("Sandwich");
        assertThat(result.getFirst().getId()).isEqualTo(1L);
        assertThat(result.getFirst().getName()).isEqualTo("Ingr 1");
        assertThat(result.getFirst().getDbName()).isEqualTo("ingr-1");
        assertThat(result.getFirst().getQuantity()).isEqualTo("1");
        assertThat(result.getLast().getId()).isEqualTo(2L);
        assertThat(result.getLast().getName()).isEqualTo("Ingr 2");
        assertThat(result.getLast().getDbName()).isEqualTo("ingr-2");
        assertThat(result.getLast().getQuantity()).isEqualTo("2");
    }

    @Test
//  This test requires a bunch of objects that come from the user in a form. Mocking this data that isn't important to
//  the goal of this function - will return object with base required fields
    void GenerateUpdatedRecipe_ReturnRecipeDTO() {
        Authentication userAuth = new UsernamePasswordAuthenticationToken("test@test.com","test" ,new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(userAuth);
        Principal principal = SecurityContextHolder.getContext().getAuthentication();
        MultipartFile multipartFile = new MultipartFile() {
            @Override
            public @NonNull String getName() {
                return "";
            }

            @Override
            public String getOriginalFilename() {
                return "";
            }

            @Override
            public String getContentType() {
                return "";
            }

            @Override
            public boolean isEmpty() {
                return true;
            }

            @Override
            public long getSize() {
                return 0;
            }

            @Override
            public byte @NonNull [] getBytes() {
                return new byte[0];
            }

            @Override
            public @NonNull InputStream getInputStream()  {
                return new InputStream() {
                    @Override
                    public int read(){
                        return 0;
                    }
                };
            }

            @Override
            public void transferTo(@NonNull File dest) {

            }
        };
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

        RecipeDTO result = editRecipeService.generateUpdatedRecipe(recipeDTO,formData, multipartFile,"ABC",principal);
        assertThat(result).isNotNull();
        assertThat(result.getAuthorId()).isEqualTo("test@test.com");
        assertThat(result.getRecipeName()).isEqualTo("Sandwich");
        assertThat(result.getDbName()).isEqualTo("sandwich");
        assertThat(result.getTotalTime()).isEqualTo(10);
    }

    @Test
    void HandleUpdateInstructions_FindAll_ReturnsListOfInstructionDTOs() {
        List<InstructionsDTO> firstSave = instructionsServiceImpl
                .saveInstrList(editRecipeService.generateUpdatedInstructions(instructionsFormDTO, recipeDTO));
        assertThat(firstSave.getFirst().getStepText()).isEqualTo("Step 1");
        assertThat(firstSave.getLast().getStepText()).isEqualTo("Step 2");

        List<InstructionsDTO> newList = new ArrayList<>();
        newList.add(InstructionsDTO.builder().id(1).stepText("Step 3").build());
        newList.add(InstructionsDTO.builder().id(2).stepText("Step 4").build());
        InstructionsFormDTO newFormDTO = new InstructionsFormDTO(newList);

        List<InstructionsDTO> result = editRecipeService.handleUpdateInstructions(newFormDTO,recipeDTO);
        List<Instructions> result2 = instructionsRepository.findAll();
        assertThat(result).isNotNull().isNotEmpty().hasSize(2);
        assertThat(result.getFirst().getStepText()).isEqualTo("Step 3");
        assertThat(result.getLast().getStepText()).isEqualTo("Step 4");
        assertThat(result2).hasSize(2);
    }

    @Test
    void HandleUpdateIngredients_FindAll_ReturnsListOfIngredientDTOs() {
        List<IngredientDTO> firstSave = ingredientServiceImpl
                .saveIngrList(editRecipeService.generateUpdatedIngredientsDTO(ingredientFormDTO,recipeDTO));
        assertThat(firstSave.getFirst().getName()).isEqualTo("Ingr 1");
        assertThat(firstSave.getLast().getName()).isEqualTo("Ingr 2");

        List<IngredientDTO> newList = new ArrayList<>();
        newList.add(IngredientDTO.builder().id(1L).name("Ingr 3").quantity("3").build());
        newList.add(IngredientDTO.builder().id(2L).name("Ingr 4").quantity("4").build());
        IngredientFormDTO newFormDTO = new IngredientFormDTO(newList);

        List<IngredientDTO> result = editRecipeService.handleUpdateIngredients(newFormDTO, recipeDTO);
        List<Ingredient> result2 = ingredientRepository.findAll();
        assertThat(result).isNotNull().isNotEmpty().hasSize(2);
        assertThat(result.getFirst().getName()).isEqualTo("Ingr 3");
        assertThat(result.getLast().getName()).isEqualTo("Ingr 4");
        assertThat(result2).hasSize(2);
    }

    @Test
    void HandleUpdatedRecipe_FindAll_ReturnsRecipeDTO() {
        List<Recipe> firstRecipe = recipeRepository.findAll();
        assertThat(firstRecipe.getFirst().getRecipeName()).isEqualTo("Sandwich");
        assertThat(firstRecipe.getFirst().getTotalTime()).isEqualTo(10);

        Authentication userAuth = new UsernamePasswordAuthenticationToken("test@test.com","test" ,new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(userAuth);
        Principal principal = SecurityContextHolder.getContext().getAuthentication();
        MultipartFile multipartFile = new MultipartFile() {
            @Override
            public @NonNull String getName() {
                return "";
            }@Override
            public String getOriginalFilename() {
                return "";
            }@Override
            public String getContentType() {
                return "";
            }@Override
            public boolean isEmpty() {
                return true;
            }@Override
            public long getSize() {
                return 0;
            }@Override
            public byte @NonNull [] getBytes() {
                return new byte[0];
            }@Override
            public @NonNull InputStream getInputStream() {
                return new InputStream() {
                    @Override
                    public int read() {
                        return 0;
                    }
                };
            }@Override
            public void transferTo(@NonNull File dest) {
            }
        };
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

        RecipeDTO newRecipeDTO = RecipeDTO.builder().recipeId("ABC").recipeName("Sandwich!").prepTime(10).cookTime(10).build();
        editRecipeService.handleUpdateRecipe(newRecipeDTO,instructionsFormDTO,ingredientFormDTO,
                formData,multipartFile,"ABC",principal);
        List<Recipe> savedList = recipeRepository.findAll();

        assertThat(savedList).isNotNull().isNotEmpty().hasSize(1);
        assertThat(savedList.getFirst().getRecipeId()).isEqualTo("ABC");
        assertThat(savedList.getFirst().getAuthorId()).isEqualTo("test@test.com");
        assertThat(savedList.getFirst().getRecipeName()).isEqualTo("Sandwich!");
        assertThat(savedList.getFirst().getTotalTime()).isEqualTo(20);
    }
}
