package io.sanctus.flavourpalette.instructions;

import io.sanctus.flavourpalette.recipe.RecipeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InstructionsServiceImpl implements InstructionsService{

    private final InstructionsRepository instructionsRepository;

    @Autowired
    @SuppressWarnings("unused")
    public InstructionsServiceImpl(InstructionsRepository instructionsRepository) {
        this.instructionsRepository = instructionsRepository;
    }

    @Override
    public List<InstructionsDTO> getInstrDTOListByRecipeDTO(RecipeDTO recipeDTO) {
        Optional<List<Instructions>> instructionsList = instructionsRepository.findAllByRecipeRecipeId(recipeDTO.getRecipeId());
        return instructionsList.map(this::mapToInstrDTOList).orElse(null);
    }

    @Override
    public void deleteInstrList(List<InstructionsDTO> instructions) {
        instructionsRepository.deleteAll(mapToInstrEntityList(instructions));
    }

    @Override
    public List<InstructionsDTO> saveInstrList(List<InstructionsDTO> instructionsList) {
        List<Instructions> savedInstructions = instructionsRepository.saveAll(mapToInstrEntityList(instructionsList));
        return mapToInstrDTOList(savedInstructions);
    }
}
