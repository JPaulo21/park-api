package com.jp.parkapi.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UsuarioLoginDto {

    @NotBlank
    @Email(message = "Formato do e-mail inválido!", regexp = "^[a-z0-9.+-]+@[a-z0-9.-]+\\.[a-z]{2,}$")
    @Schema(description = "Email do usuário de Login", example = "name@email.com")
    private String username;

    @NotBlank @Size(min = 6, max = 6)
    @Schema(example = "123456")
    private String password;
}
