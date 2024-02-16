package com.jp.parkapi.web.dto;

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
    private String senhaAtual;

    @NotBlank @Size(min = 6, max = 6)
    private String novaSenha;

    @NotBlank @Size(min = 6, max = 6)
    private String confirmaSenha;

}
