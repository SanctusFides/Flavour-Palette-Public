package io.sanctus.flavourpalette.instructions;

import io.sanctus.flavourpalette.recipe.RecipeDTO;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public interface InstructionsService {
    List<InstructionsDTO> getInstrDTOListByRecipeDTO(RecipeDTO recipeDTO);
    List<InstructionsDTO> saveInstrList(List<InstructionsDTO> instructionsDTOSList);

    void deleteInstrList(List<InstructionsDTO> instructions);

    default List<Instructions> mapToInstrEntityList(List<InstructionsDTO> instructionsDTOSList){
        List<Instructions> instructionsList = new ArrayList<>();
        instructionsDTOSList.forEach(instruction -> {
            Instructions instructionEntity = new Instructions();
            instructionEntity.setId(instruction.getId());
            instructionEntity.setRecipe(instruction.getRecipe());
            instructionEntity.setStepText(instruction.getStepText());
            instructionsList.add(instructionEntity);
        });
        return instructionsList;
    }

    default List<InstructionsDTO> mapToInstrDTOList(List<Instructions> instructionsList){
        List<InstructionsDTO> instructionsDTOSList = new ArrayList<>();
        instructionsList.forEach(instruction -> {
            InstructionsDTO instructionsDTO = new InstructionsDTO();
            instructionsDTO.setId(instruction.getId());
            instructionsDTO.setRecipe(instruction.getRecipe());
            instructionsDTO.setStepText(instruction.getStepText());
            instructionsDTOSList.add(instructionsDTO);
        });
        return instructionsDTOSList;
    }

}
