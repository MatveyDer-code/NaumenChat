package ru.matveyder.NauJava.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import ru.matveyder.NauJava.entity.Role;
import ru.matveyder.NauJava.repository.RoleRepository;
import ru.matveyder.NauJava.service.UserService;

import java.time.Duration;

/**
 * UI-тесты с использованием Selenium WebDriver.
 * Поднимает Spring Boot на случайном порту и проверяет сценарии входа и выхода.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
class AuthSeleniumTest {

    /// Порт на котором запущено приложение.
    @LocalServerPort
    private int port;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleRepository roleRepository;

    private WebDriver driver;
    private WebDriverWait wait;
    private String baseUrl;

    private static final String TEST_USER = "selenium_user";
    private static final String TEST_PASS = "selenium_pass123";

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;

        // Создаём роль USER если не существует
        roleRepository.findByTitle("USER").orElseGet(() -> {
            Role role = new Role();
            role.setTitle("USER");
            role.setDescription("Обычный пользователь");
            role.setLevel(1);
            return roleRepository.save(role);
        });

        try {
            userService.registerUser(TEST_USER, TEST_PASS, TEST_USER + "@test.com");
        } catch (IllegalArgumentException e) {
        }

        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    /// Тест успешного входа в приложение.
    @Test
    void testSuccessfulLogin() {
        driver.get(baseUrl + "/login");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username")))
                .sendKeys(TEST_USER);
        driver.findElement(By.name("password")).sendKeys(TEST_PASS);
        driver.findElement(By.cssSelector("input[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/view/users"));
        Assertions.assertTrue(driver.getCurrentUrl().contains("/view/users"),
                "После входа ожидался редирект на /view/users");

        WebElement table = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.tagName("table"))
        );
        Assertions.assertTrue(table.isDisplayed(), "Таблица пользователей не отображается");
    }

    /// Тест неуспешного входа с неверными данными.
    @Test
    void testFailedLogin_WrongCredentials() {
        driver.get(baseUrl + "/login");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username")))
                .sendKeys("wrong_user");
        driver.findElement(By.name("password")).sendKeys("wrong_pass");
        driver.findElement(By.cssSelector("input[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/login?error"));
        WebElement errorMsg = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[style*='color: red']"))
        );
        Assertions.assertTrue(errorMsg.isDisplayed(), "Сообщение об ошибке не отображается");
    }

    /// Тест успешного выхода из приложения.
    @Test
    void testSuccessfulLogout() {
        // Входим
        driver.get(baseUrl + "/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username")))
                .sendKeys(TEST_USER);
        driver.findElement(By.name("password")).sendKeys(TEST_PASS);
        driver.findElement(By.cssSelector("input[type='submit']")).click();
        wait.until(ExpectedConditions.urlContains("/view/users"));

        // Выходим
        WebElement logoutBtn = wait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']"))
        );
        logoutBtn.click();

        wait.until(ExpectedConditions.urlContains("/login"));
        Assertions.assertTrue(driver.getCurrentUrl().contains("/login"),
                "После выхода ожидался редирект на /login");
    }
}