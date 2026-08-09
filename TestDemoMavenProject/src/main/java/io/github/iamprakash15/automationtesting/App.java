package io.github.iamprakash15.automationtesting;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/** Runs a configurable Amazon product search using Selenium. */
public final class App implements AutoCloseable {

    private static final String DEFAULT_BASE_URL = "https://www.amazon.in/";
    private static final Duration DEFAULT_WAIT = Duration.ofSeconds(15);
    private static final By SEARCH_INPUT = By.id("twotabsearchtextbox");
    private static final By SEARCH_BUTTON = By.id("nav-search-submit-button");
    private static final By RESULT_CARDS = By.cssSelector("div[data-component-type='s-search-result']");
    private static final By PRODUCT_NAME = By.cssSelector("h2 span");
    private static final By PRODUCT_PRICE = By.cssSelector(".a-price .a-offscreen");
    private static final By PRODUCT_LINK = By.cssSelector("h2 a");
    private static final By PRIME_BADGE = By.cssSelector(".a-icon-prime");

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final String baseUrl;

    public App(WebDriver driver) {
        this(driver, DEFAULT_WAIT, configuredBaseUrl());
    }

    App(WebDriver driver, Duration waitTimeout, String baseUrl) {
        this.driver = Objects.requireNonNull(driver, "driver must not be null");
        this.wait = new WebDriverWait(driver, Objects.requireNonNull(waitTimeout, "waitTimeout must not be null"));
        this.baseUrl = requireHttpUrl(baseUrl);
    }

    /** Creates Chrome with Selenium Manager handling the correct driver binary. */
    public static App launch() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-dev-shm-usage");
        if (configuredBoolean("headless", "HEADLESS", false)) {
            options.addArguments("--headless=new", "--window-size=1920,1080");
        }
        return new App(new ChromeDriver(options));
    }

    public List<Product> search(String query) {
        String normalizedQuery = requireNonBlank(query, "query");
        driver.get(baseUrl);

        WebElement searchInput = wait.until(ExpectedConditions.elementToBeClickable(SEARCH_INPUT));
        searchInput.clear();
        searchInput.sendKeys(normalizedQuery);
        wait.until(ExpectedConditions.elementToBeClickable(SEARCH_BUTTON)).click();

        List<WebElement> cards;
        try {
            cards = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(RESULT_CARDS));
        } catch (TimeoutException exception) {
            throw new IllegalStateException(
                    "No product results appeared. The page layout may have changed or Amazon may have shown a challenge page.",
                    exception);
        }

        List<Product> products = new ArrayList<>();
        for (WebElement card : cards) {
            toProduct(card).ifPresent(products::add);
        }
        return List.copyOf(products);
    }

    private static Optional<Product> toProduct(WebElement card) {
        String name = findText(card, PRODUCT_NAME).orElse("");
        if (name.isBlank()) {
            return Optional.empty();
        }

        String price = findText(card, PRODUCT_PRICE).orElse("Not available");
        String url = first(card, PRODUCT_LINK)
                .map(link -> Objects.toString(link.getAttribute("href"), ""))
                .orElse("");
        boolean primeEligible = !card.findElements(PRIME_BADGE).isEmpty();
        return Optional.of(new Product(name, price, primeEligible, url));
    }

    private static Optional<String> findText(WebElement root, By selector) {
        return first(root, selector)
                .map(App::textContent)
                .filter(text -> !text.isBlank());
    }

    private static Optional<WebElement> first(WebElement root, By selector) {
        List<WebElement> elements = root.findElements(selector);
        return elements.isEmpty() ? Optional.empty() : Optional.of(elements.get(0));
    }

    private static String textContent(WebElement element) {
        String visibleText = element.getText();
        if (visibleText != null && !visibleText.isBlank()) {
            return visibleText.trim();
        }
        return Objects.toString(element.getDomProperty("textContent"), "").trim();
    }

    private static String configuredBaseUrl() {
        return configuredValue("baseUrl", "AMAZON_BASE_URL", DEFAULT_BASE_URL);
    }

    private static boolean configuredBoolean(String property, String environmentVariable, boolean fallback) {
        return Boolean.parseBoolean(configuredValue(property, environmentVariable, Boolean.toString(fallback)));
    }

    private static String configuredValue(String property, String environmentVariable, String fallback) {
        String propertyValue = System.getProperty(property);
        if (propertyValue != null && !propertyValue.isBlank()) {
            return propertyValue.trim();
        }
        String environmentValue = System.getenv(environmentVariable);
        return environmentValue == null || environmentValue.isBlank() ? fallback : environmentValue.trim();
    }

    private static String requireHttpUrl(String value) {
        String normalized = requireNonBlank(value, "baseUrl");
        String lowercase = normalized.toLowerCase(Locale.ROOT);
        if (!lowercase.startsWith("https://") && !lowercase.startsWith("http://")) {
            throw new IllegalArgumentException("baseUrl must use http or https");
        }
        return normalized;
    }

    private static String requireNonBlank(String value, String name) {
        String normalized = Objects.requireNonNull(value, name + " must not be null").trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return normalized;
    }

    @Override
    public void close() {
        driver.quit();
    }

    public static void main(String[] args) {
        String query = args.length == 0 ? "cricket bat" : String.join(" ", args);
        try (App app = App.launch()) {
            List<Product> products = app.search(query);
            System.out.printf("Found %d products for '%s'%n", products.size(), query);
            products.forEach(product -> System.out.printf(
                    "%s | %s | Prime: %s | %s%n",
                    product.name(), product.price(), product.primeEligible() ? "Yes" : "No", product.url()));
        }
    }
}
