package br.com.standardit.ecertidoes;

import static br.com.standardit.core.DriverFactory.getDriver;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

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

public class NotaDeDebitoPA extends BasePage {

	static WebDriver driver;
	public static final Logger log = (Logger) LoggerFactory.getLogger(NotaDeDebitoPA.class);

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

			https: // app.sefa.pa.gov.br/consulta-obrigacoes
			driver.get("https://app.sefa.pa.gov.br/pservicos/autenticacao");

			// log.info("titulo da pagina: " + driver.getTitle());

			// escrever("MainContent_txtDocumento","09447797865");

			// escrever("MainContent_txtDocumento","09447797865");

			// clicarBotao("MainContent_cnpjradio");

		} catch (FileNotFoundException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}

	}
}
