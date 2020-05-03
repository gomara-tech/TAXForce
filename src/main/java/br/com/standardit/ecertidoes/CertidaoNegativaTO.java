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
import org.sikuli.script.Key;
import org.sikuli.script.Screen;
import org.slf4j.LoggerFactory;

import br.com.standardit.UtilAutomation;
import br.com.standardit.WaitResult;
import br.com.standardit.util;
import br.com.standardit.core.BasePage;
import br.com.standardit.ws.ecertidoes.ECertidoes;
import ch.qos.logback.classic.Logger;

public class CertidaoNegativaTO extends BasePage {

	static WebDriver driver;

	public static final Logger log = (Logger) LoggerFactory.getLogger(CertidaoNegativaTO.class);

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
			pastaDownloads = prop.getProperty("PastaDownloads");
			processadoPath = prop.getProperty("processadoPath");
			if (pesquisarCnpj == "" || pesquisarCnpj == null) {
				processadoPath = processadoPath + "\\" + inscricaoEstadual;
			} else {
				processadoPath = processadoPath + "\\" + pesquisarCnpj;
			}

			driver.get(
					"http://www.sefaz.to.gov.br/empresa/certidao-e-situacao-fiscal/cnd---certidao-negativa-de-debitos/");
			log.info("titulo da pagina: " + driver.getTitle());

			log.info("Click em Emitir CND");
			clicarBotaoPorXPath("//a[contains(text(),'Emitir CND')]");

			log.info("Preencher CPF");
			escrever("_NUM_CNPJ", pesquisarCnpj);

			log.info("Click em Emitir CND");
			clicarBotaoPorXPath("//input[@name='BUTTON2']");

			if (procurarImagem(40, "ECertidoes\\TO\\ImprimirCND")) {
				clicarNaImagem(1, "ECertidoes\\TO\\ImprimirCND");
				if (procurarImagem(40, "ECertidoes\\TO\\CertidaoSemDebitos")) {
					SaveChromeToPdf(processadoPath + "\\Certidao.pdf", "ECertidoes\\TO\\CertidaoSemDebitos");

					if (procurarImagem(10, "ECertidoes\\TO\\Inexistente")) {
						ECertidoes.descStatus = "2"; // "Inexistentes";
						ECertidoes.validade = util.getDateWithPlusDays(30);
					} else {
						ECertidoes.descStatus = "1"; // "gerado sem pendencias";
						ECertidoes.validade = util.getDateWithPlusDays(30);
					}
				}
			}

			if (procurarImagem(2, "ECertidoes\\TO\\CNPJInvalido")) {
				ECertidoes.descStatus = "4"; // "Nao foi possivel emitir";
			}

			if (procurarImagem(2, "ECertidoes\\TO\\CertidaoComDebito")) {
				ECertidoes.descStatus = "3";
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

//---------------------------------------------------------------------------------------------------------
	public static void SaveChromeToPdf(String fileAdrress, String imageToClick) {
		Screen s = new Screen();

		UtilAutomation utilAut = new UtilAutomation(s);
		WaitResult waitResult;

		waitResult = utilAut.WaitFor(10, new String[] { imageToClick });
		if (waitResult.region != null) {
			waitResult.region.rightClick();
			util.sleep(2);
			s.type(Key.DOWN);
			util.sleep(1);
			s.type(Key.DOWN);
			util.sleep(1);
			s.type(Key.DOWN);
			util.sleep(1);
			s.type(Key.DOWN);
			util.sleep(1);
			s.type(Key.ENTER);
			util.sleep(2);
		}

		waitResult = utilAut.WaitFor(10, new String[] { "ImprimirPDF\\ButtonPrint", "ImprimirPDF\\Imprimir" });
		if (waitResult.region != null) {
			waitResult = utilAut.WaitFor(10, new String[] { "ImprimirPDF\\MicrosoftPrintToPdf" });
			if (waitResult.region != null) {
				waitResult.region.click();
				util.sleep(1);
				s.type(Key.DOWN);
				util.sleep(1);
				s.type(Key.ENTER);
				util.sleep(2);

				waitResult = utilAut.WaitFor(10, new String[] { "ImprimirPDF\\ButtonSave" });
				if (waitResult.region != null) {
					waitResult.region.click();
					util.sleep(1);
					s.type(Key.BACKSPACE);
					util.sleep(2);

					waitResult = utilAut.WaitFor(10,
							new String[] { "ImprimirPDF\\FileName", "ImprimirPDF\\FileName2" });
					if (waitResult.region != null) {
						waitResult.region.click();
						util.sleep(1);
						s.type(Key.BACKSPACE);
						util.sleep(2);
						s.type(fileAdrress);
						waitResult = utilAut.WaitFor(10, new String[] { "ImprimirPDF\\ButtonSaveAs" });
						if (waitResult.region != null) {
							waitResult.region.click();

							waitResult = utilAut.WaitFor(1, new String[] { "ImprimirPDF\\ConfirmSaveAs" });
							if (waitResult.region != null) {
								s.type(Key.LEFT);
								util.sleep(1);
								s.type(Key.ENTER);
								util.sleep(2);
							}
						}
					}
				}
			}
		} else {
			System.out.println("Erro no Botao Imprimir");
		}
	}
}
