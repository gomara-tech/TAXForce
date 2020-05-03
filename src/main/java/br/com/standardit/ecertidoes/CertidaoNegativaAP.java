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

public class CertidaoNegativaAP extends BasePage {

	static WebDriver driver;

	public static final Logger log = (Logger) LoggerFactory.getLogger(CertidaoNegativaAP.class);

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

			driver.get("http://www.sefaz.ap.gov.br/sate/seg/SEGf_AcessarFuncao.jsp?cdFuncao=DIA_060");
			log.info("titulo da pagina: " + driver.getTitle());

			clicarNaImagem(15, "ECertidoes\\AP\\RadioCnpj");

			clicarNaImagemEInsereTexto(5, "ECertidoes\\AP\\PreencherCnpj", pesquisarCnpj);

			clicarNaImagem(10, "ECertidoes\\AP\\BotaoAvancar");

			if (procurarImagem(60, "ECertidoes\\AP\\GeradoSemPendencia")) {
				if (procurarImagem(2, "ECertidoes\\AP\\Inexistente")) {
					ECertidoes.descStatus = "2"; // "Inexistente";
					ECertidoes.validade = util.getDateWithPlusDays(60);
				} else {
					ECertidoes.descStatus = "1"; // "gerado sem pendencias";
					ECertidoes.validade = util.getDateWithPlusDays(60);
				}
				util.SaveChromeToPdf2(processadoPath + "\\Certidao.pdf",
						new String[] { "ECertidoes\\AP\\GeradoSemPendencia" });
			}

			if (procurarImagem(2, "ECertidoes\\AP\\CertidaoComDebito"))
				ECertidoes.descStatus = "3"; // pendencias

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
