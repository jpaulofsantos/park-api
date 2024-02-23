package com.jp.parkapi;

import com.jp.parkapi.web.dto.CustomPageImpl;
import com.jp.parkapi.web.dto.UserCreateDTO;
import com.jp.parkapi.web.dto.UserPasswordDTO;
import com.jp.parkapi.web.dto.UserResponseDTO;
import com.jp.parkapi.web.exception.ErrorMessage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/users/users.insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/users/users.delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UserIT {

    @Autowired
    WebTestClient testClient;

    @Test
    public void createUserWithValidUsernameAndPasswordReturnUserCreatedStatus201() {
        UserResponseDTO responseBody = testClient.post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserCreateDTO("teste30@gmail.com", "123456"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(UserResponseDTO.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getId()).isNotNull();
        Assertions.assertThat(responseBody.getUsername()).isEqualTo("teste30@gmail.com");
        Assertions.assertThat(responseBody.getRole()).isEqualTo("CLIENT");
    }

    @Test
    public void createUserWithInvalidUsernameReturnStatus422() {
        ErrorMessage responseBody = testClient.post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserCreateDTO("", "123456"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = testClient.post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserCreateDTO("teste@", "123456"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = testClient.post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserCreateDTO("teste@email", "123456"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);
    }

    @Test
    public void createUserWithInvalidPasswordReturnStatus422() {
        ErrorMessage responseBody = testClient.post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserCreateDTO("teste40@gmail.com", "1234567"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = testClient.post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserCreateDTO("teste40@gmail.com", "12345"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = testClient.post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserCreateDTO("teste40@gmail.com", ""))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);
    }

    @Test
    public void createUserWithRepeatedUsernameReturnStatus409() {
        ErrorMessage responseBody = testClient.post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserCreateDTO("teste20@gmail.com", "123456"))
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(409);
    }

    @Test
    public void findUserWithExistingIdReturnStatus200() {
        UserResponseDTO responseBody = testClient.get()
                .uri("/api/v1/users/99")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient,"admin@gmail.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponseDTO.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getId()).isEqualTo(99);
        Assertions.assertThat(responseBody.getUsername()).isEqualTo("admin@gmail.com");
        Assertions.assertThat(responseBody.getRole()).isEqualTo("ADMIN");

        responseBody = testClient.get()
                .uri("/api/v1/users/101")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient,"admin@gmail.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponseDTO.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getId()).isEqualTo(101);
        Assertions.assertThat(responseBody.getUsername()).isEqualTo("teste21@gmail.com");
        Assertions.assertThat(responseBody.getRole()).isEqualTo("CLIENT");

        responseBody = testClient.get()
                .uri("/api/v1/users/101")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient,"teste21@gmail.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponseDTO.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getId()).isEqualTo(101);
        Assertions.assertThat(responseBody.getUsername()).isEqualTo("teste21@gmail.com");
        Assertions.assertThat(responseBody.getRole()).isEqualTo("CLIENT");
    }

    @Test
    public void findUserWithNonExistingIdReturnStatus404() {
        ErrorMessage responseBody = testClient.get()
                .uri("/api/v1/users/10")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient,"admin@gmail.com", "123456"))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(404);

    }

    @Test
    public void findUserWithUserSearchingForAnotherUserWithExistingIdReturnStatus403() {
        ErrorMessage responseBody = testClient.get()
                .uri("/api/v1/users/102")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient,"teste21@gmail.com", "123456"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);

    }

    @Test
    public void updateUserPasswordWithCorrectParametersForCurrentPasswordAndNewPasswordAndConfirmPassWordReturnStatus204() {
        testClient.patch()
                .uri("/api/v1/users/101")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient,"teste21@gmail.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserPasswordDTO("123456", "123457", "123457"))
                .exchange()
                .expectStatus().isNoContent();

        testClient.patch()
                .uri("/api/v1/users/99")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient,"admin@gmail.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserPasswordDTO("123456", "123457", "123457"))
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    public void updateUserPasswordWithDifferentUsersIdsReturnStatus403() {
        ErrorMessage responseBody = testClient.patch()
                .uri("/api/v1/users/50")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient,"admin@gmail.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserPasswordDTO("123456", "123457", "123457"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);

        responseBody = testClient.patch()
                .uri("/api/v1/users/50")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient,"teste21@gmail.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserPasswordDTO("123456", "123457", "123457"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);

    }

    @Test
    public void updateUserPasswordWithExistingIdAndIncorrectParametersForCurrentPasswordReturnStatus422() {
        ErrorMessage responseBody = testClient.patch()
                .uri("/api/v1/users/101")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient,"teste21@gmail.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserPasswordDTO("", "", ""))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = testClient.patch()
                .uri("/api/v1/users/101")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient,"teste21@gmail.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserPasswordDTO("123456", "123", "123"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = testClient.patch()
                .uri("/api/v1/users/101")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient,"teste21@gmail.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserPasswordDTO("1234567", "1237777", "1237777"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

    }

    @Test
    public void updateUserPasswordWithExistingIdAndPasswordDoesNotMatchReturnStatus400() {
        ErrorMessage responseBody = testClient.patch()
                .uri("/api/v1/users/101")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient,"teste21@gmail.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserPasswordDTO("123456", "123457", "123458"))
                .exchange()
                .expectStatus().isEqualTo(400)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(400);

        responseBody = testClient.patch()
                .uri("/api/v1/users/101")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient,"teste21@gmail.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserPasswordDTO("123459", "123457", "123457"))
                .exchange()
                .expectStatus().isEqualTo(400)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(400);

    }

    @Test
    public void findAllUsersPagedAndReturnStatus200() {

        Page<UserResponseDTO> responseEntity = testClient.get()
                .uri("/api/v1/users")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient,"admin@gmail.com", "123456"))
                .exchange().expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<CustomPageImpl>() {})
                .returnResult()
                .getResponseBody();

        Assertions.assertThat(responseEntity).isNotNull();
        Assertions.assertThat(responseEntity.getTotalElements()).isEqualTo(11);
    }

    @Test
    public void findAllUsersPagedAndReturnStatus403() {

        ErrorMessage responseEntity = testClient.get()
                .uri("/api/v1/users")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient,"teste21@gmail.com", "123456"))
                .exchange().expectStatus()
                .isForbidden()
                .expectBody(new ParameterizedTypeReference<ErrorMessage>() {})
                .returnResult()
                .getResponseBody();

        Assertions.assertThat(responseEntity).isNotNull();
        Assertions.assertThat(responseEntity.getStatus()).isEqualTo(403);
    }
}
