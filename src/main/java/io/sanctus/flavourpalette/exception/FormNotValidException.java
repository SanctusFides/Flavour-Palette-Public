package io.sanctus.flavourpalette.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FormNotValidException extends RuntimeException {

    private final String missingField;

    public FormNotValidException(String missingField) {
        this.missingField = missingField;
    }
}
