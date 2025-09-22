package org.example;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.ru.И;
import org.openqa.selenium.*;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

public class BeginningClassStep {
    protected WebDriver driver;
    protected WebDriverWait wait;

    // Определение полей
    private final By FIRST_NAME = By.cssSelector("input[name='firstname'], input[placeholder='Имя'], input[placeholder='First Name']");
    private final By LAST_NAME = By.cssSelector("input[name='lastname'], input[placeholder='Фамилия'], input[placeholder='Last Name']");
    private final By EMAIL = By.cssSelector("input[name='email'], input[placeholder='E-Mail'], input[type='email']");
    private final By PASSWORD = By.cssSelector("input[name='password'], input[placeholder='Пароль'], input[placeholder='Password']");
    private final By AGREE = By.name("agree");
    private final By CONTINUE_BTN = By.xpath(
            "//button[normalize-space()='Продолжить' or @id='button-register' or @type='submit'] | //input[@value='Продолжить' or @id='button-register' or @type='submit']"
    );

    // Количество мс для демонстрации экрана после завершения теста
    private static final long END_SCREEN_MS = 2000;

    @Before
    public void setUp(Scenario scenario) {
        try {
            System.out.println("Инициализация Edge драйвера для сценария: " + scenario.getName());

            // Пути к драйверам
            Path edgePath = Paths.get("src", "test", "resources", "msedgedriver.exe");

            if (Files.exists(edgePath)) {
                System.setProperty("webdriver.edge.driver", edgePath.toAbsolutePath().toString());
                System.out.println("Найден Edge драйвер: " + edgePath.toAbsolutePath());
            } else {
                throw new RuntimeException("Драйвер Edge не найден по пути: " + edgePath.toAbsolutePath());
            }

            EdgeOptions options = new EdgeOptions();
            options.addArguments("--start-maximized");
            options.addArguments("--disable-notifications");
            options.addArguments("--remote-allow-origins=*");

            driver = new EdgeDriver(options);
            System.out.println("Edge драйвер успешно инициализирован");

            // Инициализируем wait после создания драйвера
            wait = new WebDriverWait(driver, Duration.ofSeconds(15));

            driver.manage().window().maximize();
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));

        } catch (Exception e) {
            System.out.println("Ошибка инициализации Edge драйвера: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Не удалось инициализировать Edge драйвер", e);
        }
    }

    @И("открыта страница opencart: {string}")
    public void openPage(String url) {
        if (driver == null) {
            throw new IllegalStateException("WebDriver не инициализирован");
        }

        try {
            System.out.println("Открываем страницу: " + url);
            driver.get(url);

            // Ждем загрузки страницы
            wait.until(webDriver ->
                    ((JavascriptExecutor) webDriver)
                            .executeScript("return document.readyState")
                            .equals("complete"));

            System.out.println("Страница успешно загружена");
            sleep(500);

        } catch (Exception e) {
            System.out.println("Ошибка при открытии страницы: " + e.getMessage());
            throw new RuntimeException("Не удалось открыть страницу: " + url, e);
        }
    }

    @И("поле Имя заполняется значением {string}")
    public void поле_Имя_заполняется_значением(String str){
        scroll(driver.findElement(CONTINUE_BTN));
        type(FIRST_NAME, str);
    }

    @И("поле Фамилия заполняется значением {string}")
    public void поле_Фамилия_заполняется_значением(String str){
        type(LAST_NAME, str);
    }

    @И("поле E-Mail заполняется значением {string}")
    public void поле_EMail_заполняется_значением(String str){
        type(EMAIL, str);
    }

    @И("поле Пароль заполняется значением {string}")
    public void поле_Пароль_заполняется_значением(String str){
        type(PASSWORD, str);
    }

    @И("выполнено нажатие на кнопку соглашения с политикой конфиденциальности")
    public void соглашение_с_политикой_конфиденциальности(){
        try {
            WebElement cb = driver.findElement(AGREE);
            if (!cb.isSelected()) cb.click();
        } catch (NoSuchElementException e) {
            click(By.xpath("//label[contains(.,'Privacy Policy') or contains(.,'Политика конфиденциальности')]"));
        }
    }

    protected void click(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    @И("выполнено нажатие на кнопку Продолжить")
    public void выполнено_нажатие_на_кнопку_продолжить(){
        click(CONTINUE_BTN);
    }

    @After
    public void afterEachTest() {
        sleep(END_SCREEN_MS);
        if (driver != null) {
            driver.quit();
            driver = null;
            wait = null;
        }
    }

    protected void type(By locator, String text) {
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(locator));
        el.clear();
        el.sendKeys(text);
    }

    public void scroll(WebElement element){
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
        sleep(1000);
    }

    protected static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {}
    }
}