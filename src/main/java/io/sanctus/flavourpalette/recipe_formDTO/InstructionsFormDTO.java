package io.sanctus.flavourpalette.recipe_formDTO;

import io.sanctus.flavourpalette.instructions.InstructionsDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstructionsFormDTO {
    private List<InstructionsDTO> instructionList;
}
