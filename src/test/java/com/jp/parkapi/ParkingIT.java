package com.jp.parkapi;

import com.jp.parkapi.web.dto.ParkingCreateDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/users/parkings/parkings.insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/users/parkings/parkings.delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class ParkingIT {

    @Autowired
    WebTestClient testClient;

    @Test
    public void createCheckInWithValidDataReturnCreatedAndLocation() {
         ParkingCreateDTO parkingCreateDTO = ParkingCreateDTO.builder()
                .plate("AED-2127").manufacturer("FIAT").model("PALIO").color("AZUL").clientCpf("23764699027")
                .build();

        testClient.post()
                .uri("/api/v1/parkings/check-in")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "admin@gmail.com", "123456"))
                .bodyValue(parkingCreateDTO)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists(HttpHeaders.LOCATION)
                .expectBody()
                .jsonPath("plate").isEqualTo("AED-2127")
                .jsonPath("manufacturer").isEqualTo("FIAT")
                .jsonPath("model").isEqualTo("PALIO")
                .jsonPath("color").isEqualTo("AZUL")
                .jsonPath("clientCpf").isEqualTo("23764699027")
                .jsonPath("receipt").exists()
                .jsonPath("entryDate").exists()
                .jsonPath("parkingSpaceCode").exists();
    }
}
