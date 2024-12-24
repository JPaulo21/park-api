package com.jp.parkapi;

import com.jp.parkapi.web.dto.ClienteCreateDto;
import com.jp.parkapi.web.dto.ClienteResponseDto;
import com.jp.parkapi.web.dto.PageableDto;
import com.jp.parkapi.web.exception.ErrorMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) //Rodar a aplicação em ambiente de teste em porta randomica
@Sql(scripts = "/sql/clientes/clientes-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD) // Executando o script antes do método
@Sql(scripts = "/sql/clientes/clientes-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD) // Executando o script depois do método
public class ClienteIT {

    @Autowired
    WebTestClient webTestClient;

    @Test
    public void criarCliente_ComDadosValidos_RetornarClienteStatus201(){
        ClienteResponseDto responseBody = webTestClient
                .post()
                .uri("/api/v1/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "tupac@email.com", "124578"))
                .bodyValue(new ClienteCreateDto("Tupac Amaru Shakur", "36302502039"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ClienteResponseDto.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getId()).isNotNull();
        assertThat(responseBody.getCpf()).isEqualTo("36302502039");
        assertThat(responseBody.getNome()).isEqualTo("Tupac Amaru Shakur");
    }

    @Test
    public void criarCliente_ComCpfJaCadastrado_RetornarErrorMessageStatus409(){
        ErrorMessage responseBody = webTestClient
                .post()
                .uri("/api/v1/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "tupac@email.com", "124578"))
                .bodyValue(new ClienteCreateDto("Tupac Amaru Shakur", "87150818005"))
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(409);
    }

    @Test
    public void criarCliente_ComDadosInvalidos_RetornarErrorMessageStatus422(){
        ErrorMessage responseBody = webTestClient
                .post()
                .uri("/api/v1/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "tupac@email.com", "124578"))
                .bodyValue(new ClienteCreateDto("", ""))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = webTestClient
                .post()
                .uri("/api/v1/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "tupac@email.com", "124578"))
                .bodyValue(new ClienteCreateDto("Leo", "00000000000"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = webTestClient
                .post()
                .uri("/api/v1/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "tupac@email.com", "124578"))
                .bodyValue(new ClienteCreateDto("Leonardo", "871.508.180-05"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(422);
    }

    @Test
    public void criarCliente_ComUsuarioNaoPermitido_RetornarErrorMessageStatus409(){
        ErrorMessage responseBody = webTestClient
                .post()
                .uri("/api/v1/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "dilma@email.com", "124578"))
                .bodyValue(new ClienteCreateDto("Dilminha", "87150818005"))
                .exchange()
                .expectStatus().isEqualTo(403)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(403);
    }

    @Test
    public void buscarCliente_ComIdExistentePeloAdmin_RetornarClienteComStatus200(){
        ClienteResponseDto responseBody = webTestClient
                .get()
                .uri("/api/v1/clientes/101")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "ygor@email.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ClienteResponseDto.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getId()).isEqualTo(101);
    }

    @Test
    public void buscarCliente_ComIdInexistentePeloAdmin_RetornarErrorMessageComStatus404(){
        ErrorMessage responseBody = webTestClient
                .get()
                .uri("/api/v1/clientes/0")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "ygor@email.com", "123456"))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(404);
    }

    @Test
    public void buscarCliente_ComIdExistentePeloCliente_RetornarErrorMessageComStatus403(){
        ErrorMessage responseBody = webTestClient
                .get()
                .uri("/api/v1/clientes/104")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "maria@email.com", "124578"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(403);
    }

    @Test
    public void buscarCliente_ComPaginacaoPeloAdmin_RetornarClientesComStatus200(){
        PageableDto responseBody = webTestClient
                .get()
                .uri("/api/v1/clientes")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "ygor@email.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(PageableDto.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getContent().size()).isEqualTo(2);
        assertThat(responseBody.getNumber()).isEqualTo(0);
        assertThat(responseBody.getTotalPages()).isEqualTo(1);

        responseBody = webTestClient
                .get()
                .uri("/api/v1/clientes?size=1&page=1")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "ygor@email.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(PageableDto.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getContent().size()).isEqualTo(1);
        assertThat(responseBody.getNumber()).isEqualTo(1);
        assertThat(responseBody.getTotalPages()).isEqualTo(2);
    }

    @Test
    public void buscarCliente_ComPaginacaoPeloCliente_RetornarErrorMessageComStatus403(){
        ErrorMessage responseBody = webTestClient
                .get()
                .uri("/api/v1/clientes")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "tupac@email.com", "124578"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(403);
    }
}
