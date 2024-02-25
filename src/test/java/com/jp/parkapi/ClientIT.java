package com.jp.parkapi;

import com.jp.parkapi.web.dto.ClientCreateDTO;
import com.jp.parkapi.web.dto.ClientResponseDTO;
import com.jp.parkapi.web.dto.UserCreateDTO;
import com.jp.parkapi.web.dto.UserResponseDTO;
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
    public void createClientWithValidDataReturnUserCreatedStatus201() {
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
}
