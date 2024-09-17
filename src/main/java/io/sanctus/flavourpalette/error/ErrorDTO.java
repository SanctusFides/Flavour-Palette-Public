package io.sanctus.flavourpalette.error;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ErrorDTO {

    private String message;

    public ErrorDTO() {
    }

    public ErrorDTO(String message) {
        this.message = message;
    }

}
