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
import br.com.standardit.util;
import br.com.standardit.core.BasePage;
import br.com.standardit.ws.ecertidoes.ECertidoes;
import ch.qos.logback.classic.Logger;

public class CertidaoNegativaCE extends BasePage {

	WebDriver driver;

	public static final Logger log = (Logger) LoggerFactory.getLogger(CertidaoNegativaCE.class);

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

			driver.get("https://servicos.sefaz.ce.gov.br/internet/index.asp");
			log.info("titulo da pagina: " + driver.getTitle());

			if (procurarImagem(2, "ECertidoes\\CE\\PageNoSecure")) {
				clicarNaImagem(1, "ECertidoes\\CE\\PageNoSecure");
				clicarNaImagem(1, "ECertidoes\\CE\\ProceedToUnsafe");
			}

			waitResult = utilAut.WaitFor(20, new String[] { "ECertidoes\\CE\\Certidoes.png" });
			if (waitResult.region != null) {
				waitResult.region.click();
			}

			waitResult = utilAut.WaitFor(10, new String[] { "ECertidoes\\CE\\CertidaoNegativa.png" });
			if (waitResult.region != null) {
				waitResult.region.click();
				util.MouseMove2();
			}

			waitResult = utilAut.WaitFor(10, new String[] { "ECertidoes\\CE\\EmissaoCertidao.png" });
			if (waitResult.region != null) {
				waitResult.region.click();
			}

			waitResult = utilAut.WaitFor(10, new String[] { "ECertidoes\\CE\\SelecionaCNPJ.png" });
			if (waitResult.region != null) {
				waitResult.region.click();
				waitResult.region.click();
			}

			waitResult = utilAut.WaitFor(10, new String[] { "ECertidoes\\CE\\CNPJ.png" });
			if (waitResult.region != null) {
				util.sleep(1);
				waitResult.region.click();
				util.sleep(1);
				s.type(pesquisarCnpj);
			}

			waitResult = utilAut.WaitFor(10, new String[] { "ECertidoes\\CE\\BotaoEmitir.png" });
			if (waitResult.region != null) {
				waitResult.region.click();
			}

			/*
			 * Gerado sem pendencias = 2, Gerado com pendencias = 3, Não gerado = 4, Erro =
			 * 5 (editado), Inexistente = 6
			 */

			waitResult = utilAut.WaitFor(5, new String[] { "ECertidoes\\CE\\ComPendencias.png" });
			if (waitResult.region != null) {
				ECertidoes.descStatus = "1"; // "Gerado com pendencias";
			}
			waitResult = utilAut.WaitFor(5, new String[] { "ECertidoes\\CE\\MSGNaoFoiPossivelEmitir.png" });
			if (waitResult.region != null)
				ECertidoes.descStatus = "3";// "pendencias";

			waitResult = utilAut.WaitFor(5,
					new String[] { "ECertidoes\\CE\\SemPendencias.png", "ECertidoes\\CE\\SemPendencias2.png" });
			if (waitResult.region != null) {
				waitResult = utilAut.WaitFor(2, new String[] { "ECertidoes\\CE\\Inexistente.png" });
				if (waitResult.region != null) {
					ECertidoes.descStatus = "2";// Inexistente
				} else {
					ECertidoes.descStatus = "1"; // "Gerado sem pendencias";
					ECertidoes.validade = util.getDateWithPlusDays(60);
				}
				util.SaveChromeToPdf2(processadoPath + "\\Certidao.pdf",
						new String[] { "ECertidoes\\CE\\SemPendencias.png", "ECertidoes\\CE\\SemPendencias2.png" });
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
