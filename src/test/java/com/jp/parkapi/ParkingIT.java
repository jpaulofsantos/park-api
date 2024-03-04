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

    @Test
    public void createCheckInWithClientCredentialsReturnErrorStatus403() {
        ParkingCreateDTO parkingCreateDTO = ParkingCreateDTO.builder()
                .plate("AED-2127").manufacturer("FIAT").model("PALIO").color("AZUL").clientCpf("23764699027")
                .build();

        testClient.post()
                .uri("/api/v1/parkings/check-in")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "teste21@gmail.com", "123456"))
                .bodyValue(parkingCreateDTO)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("status").isEqualTo("403")
                .jsonPath("path").isEqualTo("/api/v1/parkings/check-in")
                .jsonPath("method").isEqualTo("POST");
    }

    @Test
    public void createCheckInWithInvalidDataReturnErrorStatus422() {
        ParkingCreateDTO parkingCreateDTO = ParkingCreateDTO.builder()
                .plate("").manufacturer("").model("").color("").clientCpf("237646990274")
                .build();

        testClient.post()
                .uri("/api/v1/parkings/check-in")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "admin@gmail.com", "123456"))
                .bodyValue(parkingCreateDTO)
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody()
                .jsonPath("status").isEqualTo("422")
                .jsonPath("path").isEqualTo("/api/v1/parkings/check-in")
                .jsonPath("method").isEqualTo("POST");
    }

    @Sql(scripts = "/sql/users/parkings/parkings.insert.occupiedspaces.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/users/parkings/parkings.delete.occupiedspaces.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    public void createCheckInWithInvalidCpfReturnErrorStatus404() {
        ParkingCreateDTO parkingCreateDTO = ParkingCreateDTO.builder()
                .plate("AED-2127").manufacturer("VW").model("GOL").color("BRANCO").clientCpf("23764699027")
                .build();

        testClient.post()
                .uri("/api/v1/parkings/check-in")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "admin@gmail.com", "123456"))
                .bodyValue(parkingCreateDTO)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("status").isEqualTo("404")
                .jsonPath("path").isEqualTo("/api/v1/parkings/check-in")
                .jsonPath("method").isEqualTo("POST");
    }

    @Test
    public void findCheckInWithAdminCredentialsReturnStatus200() {

        testClient.get()
                .uri("/api/v1/parkings/check-in/{receipt}", "20240303-114646")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "admin@gmail.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("plate").isEqualTo("OUG-2020")
                .jsonPath("manufacturer").isEqualTo("VW")
                .jsonPath("model").isEqualTo("T-CROSS")
                .jsonPath("color").isEqualTo("BRANCO")
                .jsonPath("clientCpf").isEqualTo("30603384005")
                .jsonPath("receipt").isEqualTo("20240303-114646")
                .jsonPath("entryDate").isEqualTo("2024-03-03 11:46:46")
                .jsonPath("parkingSpaceCode").isEqualTo("AD10");
    }

    @Test
    public void findCheckInWithClientCredentialsReturnStatus200() {

        testClient.get()
                .uri("/api/v1/parkings/check-in/{receipt}", "20240303-114646")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "teste21@gmail.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("plate").isEqualTo("OUG-2020")
                .jsonPath("manufacturer").isEqualTo("VW")
                .jsonPath("model").isEqualTo("T-CROSS")
                .jsonPath("color").isEqualTo("BRANCO")
                .jsonPath("clientCpf").isEqualTo("30603384005")
                .jsonPath("receipt").isEqualTo("20240303-114646")
                .jsonPath("entryDate").isEqualTo("2024-03-03 11:46:46")
                .jsonPath("parkingSpaceCode").isEqualTo("AD10");
    }

    @Test
    public void findCheckInWithNonValidReceiptReturnStatus404() {

        testClient.get()
                .uri("/api/v1/parkings/check-in/{receipt}", "20240303-114647")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "teste21@gmail.com", "123456"))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("status").isEqualTo("404")
                .jsonPath("path").isEqualTo("/api/v1/parkings/check-in/20240303-114647")
                .jsonPath("method").isEqualTo("GET");
    }

}
