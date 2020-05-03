package br.com.standardit.ecertidoes;

import static br.com.standardit.core.DriverFactory.getDriver;

import java.io.FileInputStream;
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
import br.com.standardit.ws.ecertidoes.ECertidoes;
import ch.qos.logback.classic.Logger;

public class CertidaoNegativaPB extends BasePage {

	static WebDriver driver;

	public static final Logger log = (Logger) LoggerFactory.getLogger(CertidaoNegativaPB.class);

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

		String processadoPath = "";
		String pastaDownloads = "";
		WaitResult waitResult;

		Properties prop = new Properties();
		InputStream input;

		String pesquisarCnpj = br.com.standardit.ws.ecertidoes.ECertidoes.cnpj;
		String inscricaoEstadual = br.com.standardit.ws.ecertidoes.ECertidoes.inscricao;

		try {

			input = new FileInputStream("config.properties");
			prop.load(input);

			ImagePath.add(prop.getProperty("imagefolder"));

			processadoPath = prop.getProperty("processadoPath");
			if (pesquisarCnpj == "" || pesquisarCnpj == null) {
				processadoPath = processadoPath + "\\" + inscricaoEstadual;
			} else {
				processadoPath = processadoPath + "\\" + pesquisarCnpj;
			}

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

		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		} finally {
			if (ECertidoes.descStatus == "") {
				log.error("Cenario nao mapeado.");
				ECertidoes.descStatus = "4";
			}
			log.info("ECertidoes.descStatus: " + ECertidoes.descStatus);
			driver.manage().window().fullscreen();
			PrintErro(processadoPath + "\\Certidao.png");
		}

	}
}
