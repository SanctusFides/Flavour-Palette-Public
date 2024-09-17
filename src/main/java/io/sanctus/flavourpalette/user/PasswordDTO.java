package io.sanctus.flavourpalette.user;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
public class PasswordDTO {
    String password;
    String confirmPassword;
}
