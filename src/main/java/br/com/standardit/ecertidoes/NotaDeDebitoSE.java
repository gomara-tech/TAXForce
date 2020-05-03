package br.com.standardit.ecertidoes;

import static br.com.standardit.core.DriverFactory.getDriver;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.sikuli.script.ImagePath;
import org.sikuli.script.Key;
import org.sikuli.script.Screen;
import org.slf4j.LoggerFactory;

import br.com.standardit.UtilAutomation;
import br.com.standardit.WaitResult;
import br.com.standardit.core.BasePage;
import ch.qos.logback.classic.Logger;

public class NotaDeDebitoSE extends BasePage {

	static WebDriver driver;
	public static final Logger log = (Logger) LoggerFactory.getLogger(NotaDeDebitoSE.class);

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

		input = new FileInputStream("config.properties");
		prop.load(input);

		String watchPath = prop.getProperty("watchPath");

		ImagePath.add(prop.getProperty("imagefolder"));
		// -----------------------------------------
		driver.get("http://www.sefaz.se.gov.br/acesso-do-usuario");
		log.info("titulo da pagina1: " + driver.getTitle());
		Assert.assertEquals("1", "1");

		TimeUnit.SECONDS.sleep(1);
		s.type(Key.PAGE_DOWN);

		waitResult = utilAut.WaitFor(10, new String[] { "ECertidoes\\SE\\Certificado.png" });
		if (waitResult.region != null) {
			waitResult.region.click();
		}

		waitResult = utilAut.WaitFor(10, new String[] { "ECertidoes\\SE\\IncricaoEstadual.png" });
		if (waitResult.region != null) {
			waitResult.region.click();
			s.type("112223322");
		}

		waitResult = utilAut.WaitFor(10, new String[] { "ECertidoes\\SE\\InserindoSenha.png" });
		if (waitResult.region != null) {
			waitResult.region.click();
			s.type("11222");
		}

		waitResult = utilAut.WaitFor(10, new String[] { "ECertidoes\\SE\\BotaoOk2.png" });
		if (waitResult.region != null) {
			waitResult.region.click();
		}

		System.out.println("teste");
	}
}
