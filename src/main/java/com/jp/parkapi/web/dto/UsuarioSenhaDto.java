package com.jp.parkapi.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UsuarioSenhaDto {

    @NotBlank @Size(min = 6, max = 6)
    @Schema(description = "Senha Atual", example = "654321")
    private String senhaAtual;

    @NotBlank @Size(min = 6, max = 6)
    @Schema(description = "Nova Senha", example = "123556")
    private String novaSenha;

    @NotBlank @Size(min = 6, max = 6)
    @Schema(description = "Confirmar nova senha", example = "123556")
    private String confirmaSenha;

}
