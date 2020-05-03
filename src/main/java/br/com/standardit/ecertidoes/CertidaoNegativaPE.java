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

public class CertidaoNegativaPE extends BasePage {

	static WebDriver driver;

	public static final Logger log = (Logger) LoggerFactory.getLogger(CertidaoNegativaPE.class);

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

			driver.get("http://efisco.sefaz.pe.gov.br/sfi_com_sca/PRMontarMenuAcesso");
			log.info("titulo da pagina: " + driver.getTitle());

			if (procurarImagem(5, "ECertidoes\\PE\\TelaDeAdvertencia"))
				clicarNaImagem(3, "ECertidoes\\PE\\TelaDeAdvertencia");

			
			escrever("buscaPorTermoInput", "16399");
			util.sleep(3);

			clicarBotao("buscaPorTermoLupa");
			util.sleep(3);
			
			clicarBotaoPorXPath("//div[@id='areaArvoreMenu']/div/div/div/a/a[3]");
			util.sleep(3);
			
			selecionarCombo("cdTipoDocumento", "CNPJ");
			util.sleep(3);
			
			escrever("nuDocumento", pesquisarCnpj);
			util.sleep(3);
			
			if (procurarImagem(5, "ECertidoes\\PE\\ErroCnpjInvalido")) {
				ECertidoes.descStatus = "2";
			} else {
				clicarBotao("btt_emitir");
			}

			if (procurarImagem(10, "ECertidoes\\PE\\VisualizarImprimir")) {
				clicarNaImagem(3, "ECertidoes\\PE\\VisualizarImprimir");

				if (procurarImagem(3, "ECertidoes\\PE\\Inexistente")) {
					ECertidoes.descStatus = "2";
				} else {
					ECertidoes.descStatus = "1"; // "gerado sem pendencias";
				}
				util.SaveChromeToPdf2(processadoPath + "\\Certidao.pdf",
						new String[] { "ECertidoes\\PE\\CertifidaoNegativa" });
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
