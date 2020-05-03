//
//package br.com.standardit.core;
//
//import org.openqa.selenium.Dimension;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.chrome.ChromeDriver;
//import org.openqa.selenium.firefox.FirefoxDriver;
//
//public class DriverFactory {
//	
//	private static WebDriver driver;
//	
//	private DriverFactory() {}
//	
//	public static WebDriver getDriver(){
////		if(driver == null) {
////			switch (Propriedades.browser) {
////				//case IE: driver = new IEDriver(); break;
////				case FIREFOX: driver = new FirefoxDriver(); break;
////				case CHROME: driver = new ChromeDriver(); break;
////			}
////			driver.manage().window().setSize(new Dimension(1260, 715));
////			//driver.manage().window().fullscreen();
////		}
//		if (driver == null) {
//			switch (Propriedades.browser) {
//			// case IE: driver = new IEDriver(); break;
//			case FIREFOX:
//				System.setProperty("webdriver.chrome.driver", "C:\\Taxforce\\RPA\\Automation\\Drivers\\chromedriver.exe");
//
//				driver = new FirefoxDriver();
//				break;
//			case CHROME:
//				
//				driver = new ChromeDriver();
//				break;
//			}
//			driver.manage().window().setSize(new Dimension(1260, 715));
//			// driver.manage().window().fullscreen();
//		}
//		return driver;
//	}
//
//	public static void killDriver(){
//		if(driver != null) {
//			driver.quit();
//			driver = null;
//		}
//	}
//}

package br.com.standardit.core;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;

public class DriverFactory extends BasePage {

	private static WebDriver driver;

	private DriverFactory() {
	}

	public static WebDriver getDriver() {
		try {

			Properties prop = new Properties();
			InputStream input;
			String driverPath = "";

			input = new FileInputStream("config.properties");
			prop.load(input);
			driverPath = prop.getProperty("driverPath");

			if (driver == null) {
				switch (Propriedades.browser) {
				// case IE: driver = new IEDriver(); break;
				case FIREFOX:
					driver = new FirefoxDriver();
					break;
				case CHROME:

					System.setProperty("webdriver.chrome.driver", driverPath + "\\chromedriver.exe");

					ChromeOptions options = new ChromeOptions();
					options.setBinary(driverPath + "\\Chrome\\Application\\chrome.exe");
					// Proxy proxy = new Proxy();
					// proxy.setHttpProxy("myhttpproxy:3337");
					// options.setCapability("proxy", proxy);

					driver = new ChromeDriver(options);
					break;
				}
				driver.manage().window().setSize(new Dimension(1260, 715));
				// driver.manage().window().fullscreen();
			}
		} catch (FileNotFoundException e) {
			log.error("[" + getMetod() + "] - " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			log.error("[" + getMetod() + "] - " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			log.error("[" + getMetod() + "] - " + e.getMessage());
			e.printStackTrace();
		}
		return driver;
	}

	public static void killDriver() {
		if (driver != null) {
			driver.quit();
			driver = null;
		}
	}
}
