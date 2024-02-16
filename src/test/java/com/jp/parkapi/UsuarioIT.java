package com.jp.parkapi;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.jp.parkapi.entity.Usuario;
import com.jp.parkapi.web.dto.UsuarioCreateDto;
import com.jp.parkapi.web.dto.UsuarioResponseDto;
import com.jp.parkapi.web.dto.UsuarioSenhaDto;
import com.jp.parkapi.web.exception.ErrorMessage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

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

        Assertions.assertThat(usuarioResponseDto).isNotNull();
        Assertions.assertThat(usuarioResponseDto.getId()).isNotNull();
        Assertions.assertThat(usuarioResponseDto.getUsername()).isEqualTo("julia@email.com");
        Assertions.assertThat(usuarioResponseDto.getRole()).isEqualTo("CLIENTE");

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

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = testClient
                .post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioCreateDto("","123456"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = testClient
                .post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioCreateDto("tody@","123456"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);
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

        Assertions.assertThat(errorMessage).isNotNull();
        Assertions.assertThat(errorMessage.getMessage()).isEqualTo("Username tulio@email.com já cadastrado!");
    }

    @Test
    public void findUsuario_ComIdExistente_RetornarUsuarioComStatus200(){
        UsuarioResponseDto responseBody = testClient
                .get()
                .uri("/api/v1/usuarios/100")
                .exchange()
                .expectStatus().isOk()
                .expectBody(UsuarioResponseDto.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getId()).isEqualTo(100);
        Assertions.assertThat(responseBody.getRole()).isEqualTo("ADMIN");

    }

    @Test
    public void findUsuario_ComIdInexistente_RetornarUsuarioComStatus200(){
        ErrorMessage responseBody = testClient
                .get()
                .uri("/api/v1/usuarios/0")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(404);
        Assertions.assertThat(responseBody.getMessage()).isEqualTo("Usuário id=0 não encontrado.");

    }

    @Test
    public void updateUsuario_ComDadosValidos_RetornarStatus204(){
        testClient
                .patch()
                .uri("/api/v1/usuarios/102")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioSenhaDto("124578","120794","120794"))
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    public void updateUsuario_ComIdInexistente_RetornarStatus404(){
        ErrorMessage errorMessage = testClient
                .patch()
                .uri("/api/v1/usuarios/0")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioSenhaDto("124578","120794","120794"))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(errorMessage).isNotNull();
        Assertions.assertThat(errorMessage.getStatus()).isEqualTo(404);
    }

    @Test
    public void updateUsuario_ComDadosInvalidos_RetornarStatus422(){
        ErrorMessage errorMessage = testClient
                .patch()
                .uri("/api/v1/usuarios/102")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioSenhaDto("1245","120794","120794"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(errorMessage).isNotNull();
        Assertions.assertThat(errorMessage.getStatus()).isEqualTo(422);

        errorMessage = testClient
                .patch()
                .uri("/api/v1/usuarios/102")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioSenhaDto("","",""))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(errorMessage).isNotNull();
        Assertions.assertThat(errorMessage.getStatus()).isEqualTo(422);
    }

    @Test
    public void updateUsuario_ComSenhaInvalida_RetornarStatus400(){
        ErrorMessage errorMessage = testClient
                .patch()
                .uri("/api/v1/usuarios/101")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioSenhaDto("123456","456789","456789"))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(errorMessage.getMessage()).isEqualTo("Senha atual inválida.");

        errorMessage = testClient
                .patch()
                .uri("/api/v1/usuarios/101")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioSenhaDto("235689","123456","123450"))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(errorMessage.getMessage()).isEqualTo("Nova senha não confere com a confirmação de senha.");

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

        Assertions.assertThat(usuarios.size()).isEqualTo(3);
    }

}
