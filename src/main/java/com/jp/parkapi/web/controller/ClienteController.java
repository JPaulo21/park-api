package com.jp.parkapi.web.controller;

import com.jp.parkapi.entity.Cliente;
import com.jp.parkapi.jwt.JwtUserDetails;
import com.jp.parkapi.repository.projection.ClienteProjection;
import com.jp.parkapi.service.ClienteService;
import com.jp.parkapi.service.UsuarioService;
import com.jp.parkapi.web.dto.ClienteCreateDto;
import com.jp.parkapi.web.dto.ClienteResponseDto;
import com.jp.parkapi.web.dto.PageableDto;
import com.jp.parkapi.web.dto.mapper.ClienteMapper;
import com.jp.parkapi.web.dto.mapper.PageableMapper;
import com.jp.parkapi.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY;

@Tag(name = "Clientes", description = "Contém todas as operações relativas ao recurso de um cliente")
@RestController
@RequestMapping("api/v1/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;
    private final UsuarioService usuarioService;

    @Operation(summary = "Criar um novo cliente", description = "Recurso para criar um novo cliente vinculado a um usuário cadastrado. " +
            "Requisição exige uso de um bearer token. Acesso restrito a Role='CLIENTE",
            security = @SecurityRequirement(name = "security"),
            responses = {
                @ApiResponse(responseCode = "201", description = "Recurso criado com sucesso",
                    content = @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ClienteResponseDto.class))),
                @ApiResponse(responseCode = "403", description = "Recurso não permitido ao perfil 'ADMIN'",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                @ApiResponse(responseCode = "409", description = "Cliente CPF já possui cadastro no sistema",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                @ApiResponse(responseCode = "422", description = "Recurso não processado por falta de dados ou dados inválidos",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            })
    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ClienteResponseDto> create(@RequestBody @Valid ClienteCreateDto dto,
                                                     @AuthenticationPrincipal JwtUserDetails userDetails){
        Cliente cliente = ClienteMapper.toCliente(dto);
        cliente.setUsuario(usuarioService.buscarPorId(userDetails.getId()));
        clienteService.salvar(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(ClienteMapper.toDto(cliente));
    }

    @Operation(summary = "Localizar um cliente", description = "Recurso para localizar um cliente pelo ID. " +
            "Requisição exige uso de um bearer token. Acesso restrito a Role='ADMIN'",
            security = @SecurityRequirement(name = "security"),
            responses = {
                @ApiResponse(responseCode = "200", description = "Recurso localizado com sucesso",
                        content = @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ClienteResponseDto.class))),
                @ApiResponse(responseCode = "404", description = "Cliente não encontrado",
                        content = @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ClienteResponseDto.class))),
                @ApiResponse(responseCode = "403", description = "Recurso não permitido ao perfil de CLIENTE",
                        content = @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ClienteResponseDto.class)))
            }
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClienteResponseDto> getById(@PathVariable Long id){
        Cliente cliente = clienteService.buscarPorId(id);
        return ResponseEntity.ok(ClienteMapper.toDto(cliente));
    }

    @Operation(summary = "Recuperar lista de clientes",
            description = "Requisição exige uso de um bearer token. Acesso restrito a Role='ADMIN'",
            security = @SecurityRequirement(name = "security"),
            parameters = {
                    @Parameter(in = QUERY, name = "page",
                    content = @Content(schema = @Schema(type = "integer", defaultValue = "0")),
                    description = "Representa a página retornada"
                    ),
                    @Parameter(in = QUERY, name = "size",
                            content = @Content(schema = @Schema(type = "integer", defaultValue = "20")),
                            description = "Representa o total de elementos por página"
                    ),
                    @Parameter(in = QUERY, name = "sort", hidden = true,
                            content = @Content(schema = @Schema(type = "integer", defaultValue = "id,asc")),
                            description = "Representa a ordenação dos resultados. Aceita multiplos critérios de ordenação são suportados"
                    ),
            },
            responses = {
                @ApiResponse(responseCode = "200", description = "Recurso recuperado com sucesso",
                    content = @Content(mediaType = "application/json;charset=UTF-8",
                            schema = @Schema(implementation = ClienteResponseDto.class))
                ),
                @ApiResponse(responseCode = "403", description = "Recurso não permitido ao perfil de CLIENTE",
                    content = @Content(mediaType = "application/json;charset=UTF-8",
                            schema = @Schema(implementation = ErrorMessage.class))
                )
            })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageableDto> getAll(@Parameter(hidden = true) @PageableDefault(size = 5, sort = {"nome"}) Pageable pageable){
        Page<ClienteProjection> clientes = clienteService.buscarTodos(pageable);
        return ResponseEntity.ok(PageableMapper.toDto(clientes));
    }

    @Operation(summary = "Recuperar dados do cliente autenticado",
            description = "Requisição exige uso de um bearer token. Acesso restrito a Role='CLIENTE'",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso recuperado com sucesso",
                            content = @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ClienteResponseDto.class))),
                    @ApiResponse(responseCode = "403", description = "Recurso não permitido ao perfil de ADMIN",
                            content = @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ClienteResponseDto.class)))
            }
    )
    @GetMapping("/detalhes")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ClienteResponseDto> getDetalhes(@AuthenticationPrincipal JwtUserDetails userDetails){
        Cliente cliente = clienteService.buscarPorUsuarioId(userDetails.getId());
        return ResponseEntity.ok(ClienteMapper.toDto(cliente));
    }
}