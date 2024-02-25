package com.jp.parkapi;

import com.jp.parkapi.web.dto.ClientCreateDTO;
import com.jp.parkapi.web.dto.ClientResponseDTO;
import com.jp.parkapi.web.dto.UserCreateDTO;
import com.jp.parkapi.web.dto.UserResponseDTO;
import com.jp.parkapi.web.exception.ErrorMessage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/users/clients/clients.insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/users/clients/clients.delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class ClientIT {

    @Autowired
    WebTestClient webTestClient;

    @Test
    public void createClientWithInValidDataReturnUserCreatedStatus() {
        ClientResponseDTO responseBody = webTestClient.post()
                .uri("/api/v1/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient,"joaopaulo@gmail.com", "123456"))
                .bodyValue(new ClientCreateDTO("João Paulo Faustino Teste", "79915035004"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ClientResponseDTO.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getId()).isNotNull();
        Assertions.assertThat(responseBody.getName()).isEqualTo("João Paulo Faustino Teste");
        Assertions.assertThat(responseBody.getCpf()).isEqualTo("79915035004");
    }

    @Test
    public void createClientWithExistingCpfReturnUserCreatedStatus409() {
        ErrorMessage responseBody = webTestClient.post()
                .uri("/api/v1/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient,"joaopaulo@gmail.com", "123456"))
                .bodyValue(new ClientCreateDTO("João Paulo Faustino Teste", "23764699027"))
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(409);
    }

    @Test
    public void createClientWithInvalidDataReturnUserCreatedStatus422() {
        ErrorMessage responseBody = webTestClient.post()
                .uri("/api/v1/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient,"joaopaulo@gmail.com", "123456"))
                .bodyValue(new ClientCreateDTO("João", "237646990271"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = webTestClient.post()
                .uri("/api/v1/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient,"joaopaulo@gmail.com", "123456"))
                .bodyValue(new ClientCreateDTO("", ""))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = webTestClient.post()
                .uri("/api/v1/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient,"joaopaulo@gmail.com", "123456"))
                .bodyValue(new ClientCreateDTO("", "237.646.9902-71"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);
    }

    @Test
    public void createClientWithNonUserPermissionReturnUserCreatedStatus403() {
        ErrorMessage responseBody = webTestClient.post()
                .uri("/api/v1/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient,"admin@gmail.com", "123456"))
                .bodyValue(new ClientCreateDTO("João Paulo Faustino Teste", "23764699027"))
                .exchange()
                .expectStatus().isEqualTo(403)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);
    }
}
