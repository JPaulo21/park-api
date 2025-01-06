package com.jp.parkapi.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.br.CPF;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstacionamentoCreateDto {
    @NotBlank
    @Size(min = 8, max = 8)
    @Pattern(regexp = "[A-Z]{3}-[0-9]{4}", message = "A placa do veículo deve seguir o padrão 'XXX-00000'")
    private String placa;
    @NotBlank
    @Schema(example = "HONDA", type = "string")
    private String marca;
    @NotBlank
    @Schema(example = "CIVIC", type = "string")
    private String modelo;
    @NotBlank
    @Schema(example = "PRETO", type = "string")
    private String cor;
    @NotBlank
    @Size(min = 11, max = 11)
    @CPF
    @Schema(example = "32165498700")
    private String clienteCpf;
}
