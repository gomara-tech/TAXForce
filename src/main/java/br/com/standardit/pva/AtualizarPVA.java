package br.com.standardit.pva;

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
import org.sikuli.script.Key;
import org.sikuli.script.Screen;
import org.slf4j.LoggerFactory;

import br.com.standardit.UtilAutomation;
import br.com.standardit.WaitResult;
import br.com.standardit.util;
import br.com.standardit.core.BasePage;
import br.com.standardit.core.DriverFactory;
import ch.qos.logback.classic.Logger;

public class AtualizarPVA extends BasePage {

	WebDriver driver;
	public static final Logger log = (Logger) LoggerFactory.getLogger(AtualizarPVA.class);

	@Before
	public void inicializa() {

		log.info("Abrindo navegador.");
		driver = getDriver();
	}

	@After
	public void finaliza() {
		log.info("Finalizando navegador.");
		driver.quit();
	}

	@Test
	public void Home() throws Exception {
		Screen s = new Screen();
		UtilAutomation utilAut = new UtilAutomation(s);

		Properties prop = new Properties();
		InputStream input;
		WaitResult waitResult;

		String watchPath = "";
		try {

			input = new FileInputStream("config.properties");
			prop.load(input);

			watchPath = prop.getProperty("watchPath");

			ImagePath.add(prop.getProperty("imagefolder"));

			driver.get(
					"http://receita.economia.gov.br/orientacao/tributaria/declaracoes-e-demonstrativos/sped-sistema-publico-de-escrituracao-digital/escrituracao-fiscal-digital-efd/escrituracao-fiscal-digital-efd");
			log.info("titulo da pagina: " + driver.getTitle());

			util.sleep(5);
			s.type(Key.PAGE_DOWN);
			s.type(Key.UP);
			s.type(Key.UP);
			s.type(Key.UP);

			waitResult = utilAut.WaitFor(10, new String[] { "NovoPva\\ClicaNoDownload32b" });
			if (waitResult.region != null) {
				waitResult.region.click();util.sleep(2);
			} else {
				throw new Exception("Imagem 'ClicaNoDownload32b' nao encontrada!");
			}

			waitResult = utilAut.WaitFor(10, new String[] { "NovoPva\\ClicaNoBotaoKeep" });
			if (waitResult.region != null) {
				waitResult.region.click();util.sleep(2);
			} // else {throw new Exception("Imagem 'ClicaNoBotaoKeep' nao encontrada!" );}

			waitResult = utilAut.WaitFor(400, new String[] { "NovoPva\\ClicaNoExecutavelDoChrome" });
			if (waitResult.region != null) {
				waitResult.region.doubleClick();util.sleep(2);
			} else {
				throw new Exception("Imagem 'ClicaNoExecutavelDoChrome' nao encontrada!");
			}

			waitResult = utilAut.WaitFor(30, new String[] { "NovoPva\\ClicaEmRun" });
			if (waitResult.region != null) {
				waitResult.region.click();util.sleep(2);
			} else {
				throw new Exception("Imagem 'ClicaEmRun' nao encontrada!");
			}

			waitResult = utilAut.WaitFor(60, new String[] { "NovoPva\\ClicaEmProximo", "NovoPva\\ClicaEmProximo0" });
			if (waitResult.region != null) {
				waitResult.region.click();util.sleep(2);
			} else {
				throw new Exception("Imagem 'ClicaEmProximo' nao encontrada!");
			}

			waitResult = utilAut.WaitFor(40, new String[] { "NovoPva\\ClicaEmProximo2", "NovoPva\\ClicaEmProximo0" });
			if (waitResult.region != null) {
				waitResult.region.click();util.sleep(2);
			} else {
				throw new Exception("Imagem 'ClicaEmProximo2' nao encontrada!");
			}

			waitResult = utilAut.WaitFor(40,
					new String[] { "NovoPva\\ClicaEmSimParaSobreescrever", "NovoPva\\ClicaEmSimParaSobreescrever2" });
			if (waitResult.region != null) {
				waitResult.region.click();
				util.sleep(10);
			} else {
				throw new Exception("Imagem 'ClicaEmSimParaSobreescrever' nao encontrada!");
			}

			waitResult = utilAut.WaitFor(60, new String[] { "NovoPva\\Terminado","NovoPva\\Terminado2" });
			waitResult = utilAut.WaitFor(10, new String[] { "NovoPva\\ClicaEmProximo3", "NovoPva\\ClicaEmProximo0" });
			if (waitResult.region != null) {
				waitResult.region.click();util.sleep(2);
				waitResult.region.click();util.sleep(2);
			} else {
				throw new Exception("Imagem 'ClicaEmProximo3' nao encontrada!");
			}

			waitResult = utilAut.WaitFor(10, new String[] { "NovoPva\\ClicaEmPronto", "NovoPva\\ClicaEmPronto2" });
			if (waitResult.region != null) {
				waitResult.region.click();util.sleep(2);
			} else {
				throw new Exception("Imagem 'ClicaEmPronto' nao encontrada!");
			}

		} catch (FileNotFoundException e) {
			util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			PrintErro(watchPath + "\\AtualizacaoPVA.png");
			util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
			e.printStackTrace();
		} finally {
			DriverFactory.killDriver();
		}
	}
}
