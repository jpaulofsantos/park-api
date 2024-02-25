package com.jp.parkapi;

import com.jp.parkapi.web.dto.*;
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

    @Test
    public void findClientWithValidPermissionExistingIdReturnUserCreatedStatus200() {
        ClientResponseDTO responseBody = webTestClient.get()
                .uri("/api/v1/clients/2")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient,"admin@gmail.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ClientResponseDTO.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getId()).isEqualTo(2);
        Assertions.assertThat(responseBody.getName()).isEqualTo("Marcio Rodrigues");
        Assertions.assertThat(responseBody.getCpf()).isEqualTo("23764699027");
    }

    @Test
    public void findClientWithInValidPermissionClientExistingIdReturnUserCreatedStatus403() {
        ErrorMessage responseBody = webTestClient.get()
                .uri("/api/v1/clients/2")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient,"teste21@gmail.com", "123456"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);
    }

    @Test
    public void findClientWithValidPermissionAdminNonExistingIdReturnUserCreatedStatus404() {
        ErrorMessage responseBody = webTestClient.get()
                .uri("/api/v1/clients/1")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient,"admin@gmail.com", "123456"))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(404);
    }

    @Test
    public void findAllClientsPageableReturnStatus200() {
        PageableDTO responseBody = webTestClient.get()
                .uri("/api/v1/clients")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient,"admin@gmail.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(PageableDTO.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getNumberOfElements()).isEqualTo(2);
        Assertions.assertThat(responseBody.getNumber()).isEqualTo(0);

        responseBody = webTestClient.get()
                .uri("/api/v1/clients?size=1&page=1")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient,"admin@gmail.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(PageableDTO.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getNumberOfElements()).isEqualTo(1);
        Assertions.assertThat(responseBody.getNumber()).isEqualTo(1);
    }

    @Test
    public void findAllClientsPageableReturnStatus403() {
        ErrorMessage responseBody = webTestClient.get()
                .uri("/api/v1/clients")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient,"teste21@gmail.com", "123456"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);
    }
}
