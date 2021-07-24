package TestingQA.TestDemoMavenProject;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v91.network.Network;
import org.openqa.selenium.devtools.v91.network.model.Response;

class Global {

	// Creating a driver object referencing WebDriver interface
	public ChromeDriver driver = null;
	public String search = "cricket bat";

}

public class App extends Global

{

	@SuppressWarnings("deprecation")
	public void browserLaunch() {

		// Setting the webdriver.chrome.driver property to its executable's location
		System.setProperty("webdriver.chrome.driver",
				"C:\\mavenProject\\TestDemoMavenProject\\driver\\chromedriver.exe");

		// Instantiating driver object
		driver = new ChromeDriver();

		DevTools devTools = driver.getDevTools();
		devTools.createSession();
		devTools.send(Network.enable(Optional.of(1000), Optional.of(1000), Optional.of(1000)));
		devTools.addListener(Network.responseReceived(), response -> {

			Response res = response.getResponse();

			if (res.getStatus().toString().startsWith("4") || res.getStatus().toString().startsWith("5")) {
				System.out.println(res.getUrl() + "is failing with status code: " + res.getStatus()
						+ " :- and failure response duration: " + res.getResponseTime());

			}

		});

		driver.get("https://www.amazon.in/");
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(2000, TimeUnit.SECONDS);

	}

	public void page() {

		driver.findElement(By.id("twotabsearchtextbox")).sendKeys(search);
		driver.findElement(By.id("nav-search-submit-button")).click();

		List<WebElement> list_of_value = driver.findElements(By.xpath("//a[@class='a-link-normal s-no-outline']"));
		System.out.println("Total New window open: " + list_of_value.size());
		for (int i = 0; i < list_of_value.size(); i++) {

			driver.findElement(
					By.xpath("(//a[@class='a-link-normal s-no-outline'])" + "[" + Integer.valueOf(i + 1) + "]"))
					.click();

			String Product_Name = driver.findElement(By.xpath(
					"(//span[@class='a-size-medium a-color-base a-text-normal'])" + "[" + Integer.valueOf(i + 1) + "]"))
					.getText();
			String Product_Price = driver
					.findElement(By.xpath("(//span[@class='a-price-whole'])" + "[" + Integer.valueOf(i + 1) + "]"))
					.getText();
			System.out.println("Product Name: " + Product_Name);
			System.out.println("Product Price: " + Product_Price);

			boolean amazon_prime = driver
					.findElement(By.xpath(
							"(//i[@class='a-icon a-icon-prime a-icon-medium'])" + "[" + Integer.valueOf(i + 1) + "]"))
					.isDisplayed();

			if (amazon_prime == true) {

				System.out.println("Amazon Prime: Yes");
			} else {

				System.out.println("Amazon Prime: No");
			}

		}

	}

	public void page2() {

		driver.findElement(By.xpath("//button[@class='_2KpZ6l _2doB4z']")).sendKeys(Keys.ESCAPE);

		List<WebElement> list_of_value = driver.findElements(By.xpath("//a[@class='a-link-normal s-no-outline']"));
		System.out.println(list_of_value);
		System.out.println("Total New window open: " + list_of_value.size());

	}

	public void close() {

		driver.close();
	}

	public void string() {

		for (int i = 0; i < 5; i++) {

			for (int j = 5; j < i; j--) {

				System.out.print("*");

			}

			System.out.println();

		}

	}

	public static void main(String[] args) {

		App ap = new App();
		ap.browserLaunch();
		ap.page();
		// ap.close();
		// ap.string();
	}
}
