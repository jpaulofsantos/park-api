package com.jp.parkapi;

import com.jp.parkapi.web.dto.ClientCreateDTO;
import com.jp.parkapi.web.dto.ClientResponseDTO;
import com.jp.parkapi.web.dto.ParkingSpaceCreateDTO;
import com.jp.parkapi.web.dto.ParkingSpaceResponseDTO;
import com.jp.parkapi.web.exception.ErrorMessage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/users/spaces/spaces.insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/users/spaces/spaces.delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class SpaceIT {

    @Autowired
    WebTestClient webTestClient;

    @Test
    public void createSpaceWithValidDataReturnUserCreatedStatus201() {
        webTestClient.post()
                .uri("/api/v1/parkingspaces")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "admin@gmail.com", "123456"))
                .bodyValue(new ParkingSpaceCreateDTO("AB16", "FREE"))
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists(HttpHeaders.LOCATION);

    }

    @Test
    public void createSpaceWithExistingCodeReturnStatus409() {
        webTestClient.post()
                .uri("/api/v1/parkingspaces")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "admin@gmail.com", "123456"))
                .bodyValue(new ParkingSpaceCreateDTO("AB17", "FREE"))
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("status").isEqualTo(409)
                .jsonPath("method").isEqualTo("POST")
                .jsonPath("path").isEqualTo("/api/v1/parkingspaces");
    }

    @Test
    public void createSpaceWithInvalidValidDataReturnStatus422() {
        webTestClient.post()
                .uri("/api/v1/parkingspaces")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "admin@gmail.com", "123456"))
                .bodyValue(new ParkingSpaceCreateDTO("AB16", "EXISTING"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody()
                .jsonPath("status").isEqualTo(422);
    }

    @Test
    public void findSpaceByCodeWithValidDataReturnStatus200() {
        ParkingSpaceResponseDTO responseBody = webTestClient.get()
                .uri("/api/v1/parkingspaces/AB20")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "admin@gmail.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ParkingSpaceResponseDTO.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getId()).isEqualTo(10);
    }

    @Test
    public void findSpaceByCodeWithInvalidDataReturnStatus404() {
        ErrorMessage responseBody = webTestClient.get()
                .uri("/api/v1/parkingspaces/AB12")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "admin@gmail.com", "123456"))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(404);
    }

    @Test
    public void findSpaceByCodeWithClientUserReturnStatus403() {
        ErrorMessage responseBody = webTestClient.get()
                .uri("/api/v1/parkingspaces/AB20")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "teste21@gmail.com", "123456"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);
    }

    @Test
    public void createSpaceWithClientUserReturnStatus403() {
        ErrorMessage responseBody = webTestClient.post()
                .uri("/api/v1/parkingspaces")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "teste21@gmail.com", "123456"))
                .bodyValue(new ParkingSpaceCreateDTO("AB16", "FREE"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);

    }
}
