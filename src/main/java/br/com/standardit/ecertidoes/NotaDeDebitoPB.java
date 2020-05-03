package br.com.standardit.ecertidoes;

import static br.com.standardit.core.DriverFactory.getDriver;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.sikuli.script.ImagePath;
import org.sikuli.script.Screen;
import org.slf4j.LoggerFactory;

import br.com.standardit.UtilAutomation;
import br.com.standardit.WaitResult;
import br.com.standardit.core.BasePage;
import ch.qos.logback.classic.Logger;

public class NotaDeDebitoPB extends BasePage {

	static WebDriver driver;
	public static final Logger log = (Logger) LoggerFactory.getLogger(NotaDeDebitoPB.class);

	@Before
	public void inicializa() {
		log.info("Abrindo navegador...");
		driver = getDriver();
	}

	@After
	public void finaliza() {
		log.info("Finalizando navegador.");
		// driver.quit();
	}

	@Test
	public void Home() throws Exception {
		Screen s = new Screen();
		UtilAutomation utilAut = new UtilAutomation(s);

		Properties prop = new Properties();
		InputStream input;
		WaitResult waitResult;

		try {
			input = new FileInputStream("config.properties");
			prop.load(input);
			String watchPath = prop.getProperty("watchPath");
			ImagePath.add(prop.getProperty("imagefolder"));

			driver.get("https://www.receita.pb.gov.br/ser/servirtual");
			log.info("titulo da pagina: " + driver.getTitle());

			waitResult = utilAut.WaitFor(10, new String[] { "ECertidoes\\PB\\Login.png" });
			if (waitResult.region != null) {
				waitResult.region.click();
				s.type("ELC00012");
			}
			waitResult = utilAut.WaitFor(10, new String[] { "ECertidoes\\PB\\Senha.png" });
			if (waitResult.region != null) {
				waitResult.region.click();
				s.type("ELC00012");
			}

			waitResult = utilAut.WaitFor(10, new String[] { "ECertidoes\\PB\\CertificadoDigital.png" });
			if (waitResult.region != null) {
				waitResult.region.click();
			}

			waitResult = utilAut.WaitFor(10, new String[] { "ECertidoes\\PB\\CertificadoOK.png" });
			if (waitResult.region != null) {
				waitResult.region.click();
			}

			TimeUnit.SECONDS.sleep(5);

		} catch (FileNotFoundException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}

}
