package ru.matveyder.NauJava.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import ru.matveyder.NauJava.entity.Role;
import ru.matveyder.NauJava.repository.RoleRepository;
import ru.matveyder.NauJava.service.UserService;

import static io.restassured.RestAssured.given;

/**
 * RestAssured тесты на MessageController.
 * Проверяет HTTP-статусы REST-эндпоинтов с учётом Spring Security.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class MessageControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleRepository roleRepository;

    private static final String TEST_USER = "test_admin_" + System.currentTimeMillis();
    private static final String TEST_PASS = "testpass123";

    /// Сессионная кука после авторизации.
    private String sessionCookie;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;

        roleRepository.findByTitle("ADMIN").orElseGet(() -> {
            Role role = new Role();
            role.setTitle("ADMIN");
            role.setDescription("Администратор");
            role.setLevel(100);
            return roleRepository.save(role);
        });

        try {
            userService.registerUser(TEST_USER, TEST_PASS, TEST_USER + "@test.com");
        } catch (IllegalArgumentException e) {
        }

        sessionCookie = getSessionCookie();
    }

    /**
     * Выполняет вход и возвращает JSESSIONID авторизованной сессии.
     */
    private String getSessionCookie() {
        Response loginPage = given()
                .when()
                .get("/login")
                .then()
                .statusCode(200)
                .extract().response();

        String csrfToken = loginPage.htmlPath()
                .getString("**.find { it.@name == '_csrf' }.@value");
        String initialCookie = loginPage.cookie("JSESSIONID");

        Response loginResponse = given()
                .cookie("JSESSIONID", initialCookie)
                .contentType(ContentType.URLENC)
                .formParam("username", TEST_USER)
                .formParam("password", TEST_PASS)
                .formParam("_csrf", csrfToken)
                .redirects().follow(false)
                .when()
                .post("/login")
                .then()
                .statusCode(302)
                .extract().response();

        return loginResponse.cookie("JSESSIONID");
    }

    /// Позитивный сценарий: запрос сообщений по автору и статусу → 200 OK.
    @Test
    void testFindByAuthorAndIsRead_Returns200() {
        given()
                .cookie("JSESSIONID", sessionCookie)
                .queryParam("authorId", 1L)
                .queryParam("isRead", false)
                .when()
                .get("/api/messages/by-author")
                .then()
                .statusCode(200);
    }

    /// Позитивный сценарий: запрос сообщений по названию комнаты → 200 OK.
    @Test
    void testFindByChatRoomName_Returns200() {
        given()
                .cookie("JSESSIONID", sessionCookie)
                .queryParam("roomName", "TestRoom")
                .when()
                .get("/api/messages/by-room")
                .then()
                .statusCode(200);
    }

    /// Позитивный сценарий: Criteria API запрос по автору и статусу → 200 OK.
    @Test
    void testFindByAuthorCriteria_Returns200() {
        given()
                .cookie("JSESSIONID", sessionCookie)
                .queryParam("authorId", 1L)
                .queryParam("isRead", true)
                .when()
                .get("/api/messages/by-author-criteria")
                .then()
                .statusCode(200);
    }

    /// Негативный сценарий: запрос без авторизации → 302 редирект на логин.
    @Test
    void testFindByAuthor_Unauthorized_Redirects() {
        given()
                .redirects().follow(false)
                .queryParam("authorId", 1L)
                .queryParam("isRead", false)
                .when()
                .get("/api/messages/by-author")
                .then()
                .statusCode(302);
    }

    /// Негативный сценарий: запрос без обязательного параметра → 400 Bad Request.
    @Test
    void testFindByAuthor_MissingParam_Returns400() {
        given()
                .cookie("JSESSIONID", sessionCookie)
                .queryParam("isRead", false)
                .when()
                .get("/api/messages/by-author")
                .then()
                .statusCode(400);
    }
}