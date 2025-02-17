package com.jp.parkapi;

import com.jp.parkapi.web.dto.EstacionamentoCreateDto;
import com.jp.parkapi.web.dto.PageableDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) //Rodar a aplicação em ambiente de teste em porta randomica
@Sql(scripts = "/sql/estacionamentos/estacionamentos-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD) // Executando o script antes do método
@Sql(scripts = "/sql/estacionamentos/estacionamentos-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD) // Executando o script depois do método
public class EstacionamentoIT {

    @Autowired
    WebTestClient webTestClient;

    @Test
    public void criarCheckin_ComDadosValidos_RetornarCreatedAndLocation(){
        EstacionamentoCreateDto createDto = EstacionamentoCreateDto.builder()
                .placa("WER-1111")
                .marca("FIAT")
                .modelo("UNO")
                .cor("AZUL")
                .clienteCpf("87150818005")
                .build();

        webTestClient
                .post()
                .uri("/api/v1/estacionamentos/check-in")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "ygor@email.com", "123456"))
                .bodyValue(createDto)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists(HttpHeaders.LOCATION)
                .expectBody()
                .jsonPath("placa").isEqualTo("WER-1111")
                .jsonPath("marca").isEqualTo("FIAT")
                .jsonPath("modelo").isEqualTo("UNO")
                .jsonPath("cor").isEqualTo("AZUL")
                .jsonPath("clienteCpf").isEqualTo("87150818005")
                .jsonPath("recibo").exists()
                .jsonPath("dataEntrada").exists()
                .jsonPath("vagaCodigo").exists();
    }

    @Test
    public void criarCheckin_ComRoleCliente_RetornarErrorMessageStatus403(){
        EstacionamentoCreateDto createDto = EstacionamentoCreateDto.builder()
                .placa("WER-1111")
                .marca("FIAT")
                .modelo("UNO")
                .cor("AZUL")
                .clienteCpf("87150818005")
                .build();

        webTestClient
                .post()
                .uri("/api/v1/estacionamentos/check-in")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "maria@email.com", "124578"))
                .bodyValue(createDto)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("status").isEqualTo("403")
                .jsonPath("path").isEqualTo("/api/v1/estacionamentos/check-in")
                .jsonPath("method").isEqualTo("POST");
    }

    @Test
    public void criarCheckin_ComDadosInvalidos_RetornarErrorMessageStatus422(){
        EstacionamentoCreateDto createDto = EstacionamentoCreateDto.builder()
                .placa("")
                .marca("")
                .modelo("")
                .cor("")
                .clienteCpf("")
                .build();

        webTestClient
                .post()
                .uri("/api/v1/estacionamentos/check-in")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "ygor@email.com", "123456"))
                .bodyValue(createDto)
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody()
                .jsonPath("status").isEqualTo("422")
                .jsonPath("path").isEqualTo("/api/v1/estacionamentos/check-in")
                .jsonPath("method").isEqualTo("POST");
    }

    @Test
    public void criarCheckin_ComCpfInexistente_RetornarErrorMessageStatus404(){
        EstacionamentoCreateDto createDto = EstacionamentoCreateDto.builder()
                .placa("WER-1111")
                .marca("FIAT")
                .modelo("UNO")
                .cor("AZUL")
                .clienteCpf("43919081072")
                .build();

        webTestClient
                .post()
                .uri("/api/v1/estacionamentos/check-in")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "ygor@email.com", "123456"))
                .bodyValue(createDto)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("status").isEqualTo("404")
                .jsonPath("path").isEqualTo("/api/v1/estacionamentos/check-in")
                .jsonPath("method").isEqualTo("POST");
    }

    @Sql(scripts = "/sql/estacionamentos/estacionamento-insert-vagas-ocupadas.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD) // Executando o script antes do método
    @Sql(scripts = "/sql/estacionamentos/estacionamento-delete-vagas-ocupadas.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    public void criarCheckin_ComVagasOcupadas_RetornarErrorMessageStatus404(){
        EstacionamentoCreateDto createDto = EstacionamentoCreateDto.builder()
                .placa("WER-1111")
                .marca("FIAT")
                .modelo("UNO")
                .cor("AZUL")
                .clienteCpf("87150818005")
                .build();

        webTestClient
                .post()
                .uri("/api/v1/estacionamentos/check-in")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "ygor@email.com", "123456"))
                .bodyValue(createDto)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("status").isEqualTo("404")
                .jsonPath("path").isEqualTo("/api/v1/estacionamentos/check-in")
                .jsonPath("method").isEqualTo("POST");
    }

    @Test
    public void buscarCheckin_ComPerfilAdmin_RetornarDadosStatus200(){
        webTestClient
                .get()
                .uri("/api/v1/estacionamentos/check-in/{recibo}", "20230313-101300")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "ygor@email.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("placa").isEqualTo("FIT-1020")
                .jsonPath("marca").isEqualTo("FIAT")
                .jsonPath("modelo").isEqualTo("PALIO")
                .jsonPath("cor").isEqualTo("VERDE")
                .jsonPath("clienteCpf").isEqualTo("90287456021")
                .jsonPath("recibo").isEqualTo("20230313-101300")
                .jsonPath("dataEntrada").isEqualTo("13/03/2023 10:15:00")
                .jsonPath("vagaCodigo").isEqualTo("A-01");
    }

    @Test
    public void buscarCheckin_ComPerfilCliente_RetornarDadosStatus200(){
        webTestClient
                .get()
                .uri("/api/v1/estacionamentos/check-in/{recibo}", "20230313-101300")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "tupac@email.com", "124578"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("placa").isEqualTo("FIT-1020")
                .jsonPath("marca").isEqualTo("FIAT")
                .jsonPath("modelo").isEqualTo("PALIO")
                .jsonPath("cor").isEqualTo("VERDE")
                .jsonPath("clienteCpf").isEqualTo("90287456021")
                .jsonPath("recibo").isEqualTo("20230313-101300")
                .jsonPath("dataEntrada").isEqualTo("13/03/2023 10:15:00")
                .jsonPath("vagaCodigo").isEqualTo("A-01");
    }

    @Test
    public void buscarCheckin_ComReciboInexistente_RetornarErrorStatus404(){
        webTestClient
                .get()
                .uri("/api/v1/estacionamentos/check-in/{recibo}", "20230313-999999")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "tupac@email.com", "124578"))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("status").isEqualTo("404")
                .jsonPath("path").isEqualTo("/api/v1/estacionamentos/check-in/20230313-999999")
                .jsonPath("method").isEqualTo("GET");
    }

    @Test
    public void criarCheckout_ComReciboExistente_RetornarSucesso(){
        webTestClient
                .put()
                .uri("/api/v1/estacionamentos/check-out/{recibo}", "20240107-101600")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "ygor@email.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("placa").isEqualTo("FIT-1020")
                .jsonPath("marca").isEqualTo("FIAT")
                .jsonPath("modelo").isEqualTo("PALIO")
                .jsonPath("cor").isEqualTo("VERDE")
                .jsonPath("clienteCpf").isEqualTo("90287456021")
                .jsonPath("recibo").exists()
                .jsonPath("dataEntrada").exists()
                .jsonPath("vagaCodigo").exists();
    }

    @Test
    public void criarCheckout_ComReciboInexistente_RetornarErrorStatus404(){
        webTestClient
                .put()
                .uri("/api/v1/estacionamentos/check-out/{recibo}", "20230313-999999")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "ygor@email.com", "123456"))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("status").isEqualTo("404")
                .jsonPath("path").isEqualTo("/api/v1/estacionamentos/check-out/20230313-999999")
                .jsonPath("method").isEqualTo("PUT");
    }

    @Test
    public void criarCheckout_ComRoleCliente_RetornarErrorStatus403(){
        webTestClient
                .put()
                .uri("/api/v1/estacionamentos/check-out/{recibo}", "20230313-999999")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "tupac@email.com", "124578"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("status").isEqualTo("403")
                .jsonPath("path").isEqualTo("/api/v1/estacionamentos/check-out/20230313-999999")
                .jsonPath("method").isEqualTo("PUT");
    }

    @Test
    public void buscarEstacionamentos_PorClienteCpf_RetornarSucesso(){
        PageableDto responseBody = webTestClient
                .get()
                .uri("/api/v1/estacionamentos/cpf/{cpf}?size=1&page=0", "90287456021")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "ygor@email.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(PageableDto.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getContent().size()).isEqualTo(1);
        assertThat(responseBody.getTotalPages()).isEqualTo(3);
        assertThat(responseBody.getNumber()).isEqualTo(0);
        assertThat(responseBody.getSize()).isEqualTo(1);

        responseBody = webTestClient
                .get()
                .uri("/api/v1/estacionamentos/cpf/{cpf}?size=1&page=1", "90287456021")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "ygor@email.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(PageableDto.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getContent().size()).isEqualTo(1);
        assertThat(responseBody.getTotalPages()).isEqualTo(3);
        assertThat(responseBody.getNumber()).isEqualTo(1);
        assertThat(responseBody.getSize()).isEqualTo(1);
    }

    @Test
    public void buscarEstacionamentos_PorClienteCpf_RetornarErrorStatus403(){
        webTestClient
                .get()
                .uri("/api/v1/estacionamentos/cpf/{cpf}", "90287456021")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "tupac@email.com", "124578"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("status").isEqualTo("403")
                .jsonPath("path").isEqualTo("/api/v1/estacionamentos/cpf/90287456021")
                .jsonPath("method").isEqualTo("GET");
    }

    @Test
    public void buscarEstacionamentos_DoClienteLogado_RetornarSucesso(){
        PageableDto responseBody = webTestClient
                .get()
                .uri("/api/v1/estacionamentos?size=1&page=0")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "tupac@email.com", "124578"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(PageableDto.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getContent().size()).isEqualTo(1);
        assertThat(responseBody.getTotalPages()).isEqualTo(3);
        assertThat(responseBody.getNumber()).isEqualTo(0);
        assertThat(responseBody.getSize()).isEqualTo(1);

        responseBody = webTestClient
                .get()
                .uri("/api/v1/estacionamentos?size=1&page=0")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "tupac@email.com", "124578"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(PageableDto.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getContent().size()).isEqualTo(1);
        assertThat(responseBody.getTotalPages()).isEqualTo(3);
        assertThat(responseBody.getNumber()).isEqualTo(0);
        assertThat(responseBody.getSize()).isEqualTo(1);
    }

    @Test
    public void buscarEstacionamentos_DoClienteLogadoPerfilAdmin_RetornarErrorStatus403(){
        webTestClient
                .get()
                .uri("/api/v1/estacionamentos")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "ygor@email.com", "123456"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("status").isEqualTo("403")
                .jsonPath("path").isEqualTo("/api/v1/estacionamentos")
                .jsonPath("method").isEqualTo("GET");
    }
}
