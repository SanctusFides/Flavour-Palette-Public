package io.sanctus.flavourpalette.service;

import io.sanctus.flavourpalette.cloudinary_impl.CloudinaryImpl;
import io.sanctus.flavourpalette.create.CreateServiceImpl;
import io.sanctus.flavourpalette.ingredient.Ingredient;
import io.sanctus.flavourpalette.ingredient.IngredientDTO;
import io.sanctus.flavourpalette.ingredient.IngredientRepository;
import io.sanctus.flavourpalette.instructions.Instructions;
import io.sanctus.flavourpalette.instructions.InstructionsDTO;
import io.sanctus.flavourpalette.instructions.InstructionsRepository;
import io.sanctus.flavourpalette.recipe.Recipe;
import io.sanctus.flavourpalette.recipe.RecipeDTO;
import io.sanctus.flavourpalette.recipe.RecipeRepository;
import io.sanctus.flavourpalette.recipe_formDTO.IngredientFormDTO;
import io.sanctus.flavourpalette.recipe_formDTO.InstructionsFormDTO;
import lombok.NonNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class CreateServiceTest {

    @MockBean
    CloudinaryImpl cloudinary;
    private final CreateServiceImpl createService;
    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final InstructionsRepository instructionsRepository;
    private final RecipeDTO recipeDTO1;

    @Autowired
    public CreateServiceTest(CreateServiceImpl createService,
                             RecipeRepository recipeRepository,
                             IngredientRepository ingredientRepository,
                             InstructionsRepository instructionsRepository
                             ){
        this.createService = createService;
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
        this.instructionsRepository = instructionsRepository;
        this.recipeDTO1 = RecipeDTO.builder().recipeName("Sandwich").prepTime(5).cookTime(5).description("yummy").build();
    }

    @Test
    void BuildIngredientDTOListViaForm_BuildIngrList_ReturnsMappedList() {
        IngredientDTO ingr1 = IngredientDTO.builder().id(1L).name("Mustard").size("cup").quantity("1").prepType("diced").build();
        IngredientDTO ingr2 = IngredientDTO.builder().id(2L).name("Ketchup").size("ounce").quantity("2").prepType("chopped").build();
        List<IngredientDTO> ingrList = new ArrayList<>();
        ingrList.add(ingr1);
        ingrList.add(ingr2);
        IngredientFormDTO igrDTO = new IngredientFormDTO(ingrList);

        List<IngredientDTO> result = createService.buildIngredientDTOListViaForm(igrDTO,recipeDTO1);

        assertThat(result).isNotNull().hasSize(2);
        assertThat(result.getFirst().getId()).isEqualTo(1L);
        assertThat(result.getFirst().getDbName()).isNotNull().isEqualTo("mustard");
        assertThat(result.getLast().getId()).isNotNull().isEqualTo(2L);
        assertThat(result.getLast().getDbName()).isNotNull().isEqualTo("ketchup");
    }

    @Test
    void BuildInstructionDTOListViaForm_BuildInstrList_ReturnsMappedList() {
        InstructionsDTO instr1 = InstructionsDTO.builder().id(1).stepText("first").build();
        InstructionsDTO instr2 = InstructionsDTO.builder().id(2).stepText("second").build();

        List<InstructionsDTO> instrList = new ArrayList<>();
        instrList.add(instr1);
        instrList.add(instr2);
        InstructionsFormDTO instrDTO = new InstructionsFormDTO(instrList);

        List<InstructionsDTO> result = createService.buildInstructionDTOListViaForm(instrDTO,recipeDTO1);

        assertThat(result).isNotNull().hasSize(2);
        assertThat(result.getFirst().getId()).isEqualTo(1);
        assertThat(result.getFirst().getStepText()).isEqualTo("first");
        assertThat(result.getLast().getId()).isEqualTo(2);
        assertThat(result.getLast().getStepText()).isEqualTo("second");
    }

    @Test
    void BuildRecipeDTOViaForm_BuildRecipe_ReturnsRecipeDTO() {
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
            public @NonNull InputStream getInputStream() {
                return new InputStream() {
                    @Override
                    public int read() {
                        return 0;
                    }
                };
            }

            @Override
            public void transferTo(@NonNull File dest) {

            }
        };
        Authentication userAuth = new UsernamePasswordAuthenticationToken("test@test.com","test" ,new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(userAuth);
        Principal principal = SecurityContextHolder.getContext().getAuthentication();
        RecipeDTO recipeDTO = createService.buildRecipeDTOViaForm(recipeDTO1,multipartFile,principal);

        assertThat(recipeDTO).isNotNull();
        assertThat(recipeDTO.getAuthorId()).isEqualTo("test@test.com");
        assertThat(recipeDTO.getRecipeName()).isEqualTo("Sandwich");
        assertThat(recipeDTO.getDbName()).isEqualTo("sandwich");
        assertThat(recipeDTO.getTotalTime()).isEqualTo(10);
    }

    @Test
    void CreatesRecipeAndIngrAndInstr_ReturnRecipeDTO() {
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
            public @NonNull InputStream getInputStream() {
                return new InputStream() {
                    @Override
                    public int read() {
                        return 0;
                    }
                };
            }

            @Override
            public void transferTo(@NonNull File dest) {

            }
        };

        Authentication userAuth = new UsernamePasswordAuthenticationToken("test@test.com","test" ,new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(userAuth);
        Principal principal = SecurityContextHolder.getContext().getAuthentication();

        InstructionsDTO instr1 = InstructionsDTO.builder().id(1).stepText("first").build();
        InstructionsDTO instr2 = InstructionsDTO.builder().id(2).stepText("second").build();
        List<InstructionsDTO> instrList = new ArrayList<>();
        instrList.add(instr1);
        instrList.add(instr2);
        InstructionsFormDTO instrDTO = new InstructionsFormDTO(instrList);

        IngredientDTO ingr1 = IngredientDTO.builder().id(1L).name("Mustard").size("cup").quantity("1").prepType("diced").build();
        IngredientDTO ingr2 = IngredientDTO.builder().id(2L).name("Ketchup").size("ounce").quantity("2").prepType("chopped").build();

        List<IngredientDTO> ingrList = new ArrayList<>();
        ingrList.add(ingr1);
        ingrList.add(ingr2);
        IngredientFormDTO igrDTO = new IngredientFormDTO(ingrList);

        RecipeDTO savedRecipe = createService.handleSavingRecipe(recipeDTO1,instrDTO,igrDTO,multipartFile,principal);

        Optional<Recipe> recipeOptional = recipeRepository.findByRecipeId(savedRecipe.getRecipeId());
        Recipe recipeActual = null;
        if (recipeOptional.isPresent()) {
            recipeActual = recipeOptional.get();
        }

        Optional<List<Ingredient>> ingrOptional = ingredientRepository.findAllByRecipeRecipeId(savedRecipe.getRecipeId());
        List<Ingredient> ingrActual = null;
        if (ingrOptional.isPresent()) {
            ingrActual = ingrOptional.get();
        }

        Optional<List<Instructions>> instrOptional = instructionsRepository.findAllByRecipeRecipeId(savedRecipe.getRecipeId());
        List<Instructions> instrActual = null;
        if (instrOptional.isPresent()) {
            instrActual = instrOptional.get();
        }

        assertThat(recipeActual).isNotNull();
        assertThat(recipeActual.getAuthorId()).isEqualTo("test@test.com");
        assertThat(recipeActual.getRecipeName()).isEqualTo("Sandwich");

        assertThat(ingrActual).isNotNull();
        assertThat(ingrActual.getLast().getName()).isEqualTo("Ketchup");

        assertThat(instrActual).isNotNull();
        assertThat(instrActual.getLast().getStepText()).isEqualTo("second");
    }
}
