package com.jp.parkapi;

import com.jp.parkapi.web.dto.UsuarioCreateDto;
import com.jp.parkapi.web.dto.UsuarioLoginDto;
import com.jp.parkapi.web.dto.UsuarioResponseDto;
import com.jp.parkapi.web.dto.UsuarioSenhaDto;
import com.jp.parkapi.web.exception.ErrorMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) //Rodar a aplicação em ambiente de teste em porta randomica
@Sql(scripts = "/sql/usuarios/usuarios-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD) // Executando o script antes do método
@Sql(scripts = "/sql/usuarios/usuarios-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD) // Executando o script depois do método
public class UsuarioIT { // Referente a tudo que é do usuário será testado nessa classe

    @Autowired
    WebTestClient testClient;

    @Test
    public void createUsuario_ComUsernamePasswordValidos_RetornarUsuarioCriadoComStatus201(){
        UsuarioResponseDto usuarioResponseDto = testClient
                .post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioCreateDto("julia@email.com","123456"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(UsuarioResponseDto.class)
                .returnResult().getResponseBody();

        assertThat(usuarioResponseDto).isNotNull();
        assertThat(usuarioResponseDto.getId()).isNotNull();
        assertThat(usuarioResponseDto.getUsername()).isEqualTo("julia@email.com");
        assertThat(usuarioResponseDto.getRole()).isEqualTo("CLIENTE");

    }

    @Test
    public void createUsuario_ComUsernameInvalido_RetornarErrorMessageStatus422(){
        ErrorMessage responseBody = testClient
                .post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioCreateDto("tody@email","123456"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = testClient
                .post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioCreateDto("","123456"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = testClient
                .post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioCreateDto("tody@","123456"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(422);
    }

    @Test
    public void createUsuario_ComUsernameRepetido_RetornarErrorMessageStatus409(){
        ErrorMessage errorMessage = testClient
                .post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioCreateDto("tulio@email.com","124578"))
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(errorMessage).isNotNull();
        assertThat(errorMessage.getMessage()).isEqualTo("Username tulio@email.com já cadastrado!");
    }

    @Test
    public void buscarUsuario_ComIdExistente_RetornarUsuarioComStatus200(){
        UsuarioResponseDto responseBody = testClient
                .get()
                .uri("/api/v1/usuarios/100")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ygor@email.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(UsuarioResponseDto.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getId()).isEqualTo(100);
        assertThat(responseBody.getRole()).isEqualTo("ADMIN");

        responseBody = testClient
                .get()
                .uri("/api/v1/usuarios/101")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ygor@email.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(UsuarioResponseDto.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getId()).isEqualTo(101);
        assertThat(responseBody.getRole()).isEqualTo("CLIENTE");

        responseBody = testClient
                .get()
                .uri("/api/v1/usuarios/101")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "tulio@email.com", "235689"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(UsuarioResponseDto.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getId()).isEqualTo(101);
        assertThat(responseBody.getRole()).isEqualTo("CLIENTE");
    }

    @Test
    public void buscarUsuario_ComIdInexistente_RetornarUsuarioComStatus200(){
        ErrorMessage responseBody = testClient
                .get()
                .uri("/api/v1/usuarios/0")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ygor@email.com", "123456"))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(404);
        assertThat(responseBody.getMessage()).isEqualTo("Usuário id=0 não encontrado.");

    }

    @Test
    public void buscarUsuario_ComUsuarioClienteBuscandoOutroCliente_RetornarErrorMessageComStatus404(){
        ErrorMessage responseBody = testClient
                .get()
                .uri("/api/v1/usuarios/103")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "tulio@email.com", "235689"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(403);

    }

    @Test
    public void updateUsuario_ComDadosValidos_RetornarStatus204(){
        testClient
                .patch()
                .uri("/api/v1/usuarios/102")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "dilma@email.com", "124578"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioSenhaDto("124578","120795","120795"))
                .exchange()
                .expectStatus().isNoContent();

        testClient
                .patch()
                .uri("/api/v1/usuarios/103")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "maria@email.com", "124578"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioSenhaDto("124578","121793","121793"))
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    public void listarUsuarios_ComUsuarioSemPermissao_RetornarErrorMessageComStatus403(){
        ErrorMessage responseBody = testClient
                .get()
                .uri("/api/v1/usuarios")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "maria@email.com", "124578"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(403);
    }

    @Test
    public void editarSenha_ComUsuariosDiferentes_RetornarErrorMessageStatus403(){
        ErrorMessage errorMessage = testClient
                .patch()
                .uri("/api/v1/usuarios/0")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ygor@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioSenhaDto("123456","120794","120794"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(errorMessage).isNotNull();
        assertThat(errorMessage.getStatus()).isEqualTo(403);

        errorMessage = testClient
                .patch()
                .uri("/api/v1/usuarios/0")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "maria@email.com", "124578"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioSenhaDto("124578","120794","120794"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(errorMessage).isNotNull();
        assertThat(errorMessage.getStatus()).isEqualTo(403);
    }

    @ParameterizedTest
    @MethodSource("providerUserLoginAndPasswordsNotFormat")
    public void updateUsuario_ComDadosInvalidos_RetornarStatus422(String idUsuario, UsuarioLoginDto user, String password){
        ErrorMessage errorMessage = testClient
                .patch()
                .uri("/api/v1/usuarios/103"+idUsuario)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, user.getUsername(), user.getPassword()))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioSenhaDto(password, password, password))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(errorMessage).isNotNull();
        assertThat(errorMessage.getStatus()).isEqualTo(422);
    }

    private static Stream<Arguments> providerUserLoginAndPasswordsNotFormat(){
        return Stream.of(
                Arguments.of("103", new UsuarioLoginDto("maria@email.com","124578")
                    , ""),
                Arguments.of("103", new UsuarioLoginDto("maria@email.com","124578")
                        , "12345"),
                Arguments.of("103", new UsuarioLoginDto("maria@email.com","124578")
                        , "12345678")
        );
    }

    @Test
    public void updateUsuario_ComSenhaInvalida_RetornarStatus400(){
        ErrorMessage errorMessage = testClient
                .patch()
                .uri("/api/v1/usuarios/101")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "tulio@email.com", "235689"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioSenhaDto("000000","456789","456789"))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(errorMessage.getMessage()).isEqualTo("Senha atual inválida.");

        errorMessage = testClient
                .patch()
                .uri("/api/v1/usuarios/101")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "tulio@email.com", "235689"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioSenhaDto("235689","000000", "111111"))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(errorMessage.getMessage()).isEqualTo("Nova senha não confere com a confirmação de senha.");
    }

    @Test
    public void findListUsuarios_RetornarStatus200(){
        List<UsuarioResponseDto> usuarios = testClient
                .get()
                .uri("/api/v1/usuarios")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UsuarioResponseDto.class)
                .returnResult().getResponseBody();

        assertThat(usuarios.size()).isEqualTo(3);
    }

}
