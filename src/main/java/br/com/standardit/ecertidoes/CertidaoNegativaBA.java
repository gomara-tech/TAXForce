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
import br.com.standardit.util;
import br.com.standardit.core.BasePage;
import br.com.standardit.ws.ecertidoes.ECertidoes;
import ch.qos.logback.classic.Logger;

public class CertidaoNegativaBA extends BasePage {

	static WebDriver driver;

	public static final Logger log = (Logger) LoggerFactory.getLogger(CertidaoNegativaBA.class);

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

		Properties prop = new Properties();
		InputStream input;

		String pesquisarCnpj = br.com.standardit.ws.ecertidoes.ECertidoes.cnpj;
		String inscricaoEstadual = br.com.standardit.ws.ecertidoes.ECertidoes.inscricao;
		pesquisarCnpj = pesquisarCnpj.replaceAll(" ", "");
		inscricaoEstadual = inscricaoEstadual.replaceAll(" ", "");

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

			driver.get("https://www.sefaz.ba.gov.br");
			log.info("titulo da pagina: " + driver.getTitle());
			util.sleep(5);

			log.info("Clica em Inspetoria Eletronica");
			clicarNaImagem(5, "ECertidoes\\BA\\InspetoriaEletronica");
			util.sleep(5);

			log.info("Clica em Certidoes");
			clicarNaImagem(5, "ECertidoes\\BA\\Certidoes");

			log.info("Clica em Emissao/Debitos Tributos");
			clicarNaImagem(5, "ECertidoes\\BA\\DebitosTributos");
			util.sleep(10);

			if (pesquisarCnpj == "" || pesquisarCnpj == null) {
				clicarNaImagemEInsereTexto(5, "ECertidoes\\BA\\InscricaoEstadual", inscricaoEstadual);
			} else {
				clicarNaImagemEInsereTexto(5, "ECertidoes\\BA\\InserirCnpj", pesquisarCnpj);
			}

			clicarNaImagem(5, "ECertidoes\\BA\\ClicaNaImpressora");

			if (procurarImagem(2, new String[] { "ECertidoes\\BA\\NaoFoiPossivelObterDados",
					"ECertidoes\\BA\\NaoFoiPossivelObterDados2" })) {
				ECertidoes.descStatus = "2"; // "Inesistente";
			}

			if (procurarImagem(5, "ECertidoes\\BA\\CertidaoPositiva")) {
				ECertidoes.descStatus = "3"; // "gerado Com pendencias";
				util.SaveChromeToPdf2(processadoPath + "\\Certidao.pdf",
						new String[] { "ECertidoes\\BA\\CertidaoPositiva" });
			}

			if (procurarImagem(3, "ECertidoes\\BA\\CertidaoNegativa")) {
				if (procurarImagem(3, "ECertidoes\\BA\\CertidaoPositivaErro")) {
					ECertidoes.descStatus = "2"; // "Inexistente";
					util.SaveChromeToPdf2(processadoPath + "\\Certidao.pdf",
							new String[] { "ECertidoes\\BA\\CertidaoPositivaErro" });
				} else {
					ECertidoes.descStatus = "1"; // "gerado sem pendencias";
					util.SaveChromeToPdf2(processadoPath + "\\Certidao.pdf",
							new String[] { "ECertidoes\\BA\\CertidaoNegativa" });
				}
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
