package br.com.standardit.ecertidoes.Federal;

import static br.com.standardit.core.DriverFactory.getDriver;

import java.io.File;
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
import br.com.standardit.util;
import br.com.standardit.captcha.TwoCaptchaDotComSolver;
import br.com.standardit.core.BasePage;
import br.com.standardit.ws.ecertidoes.ECertidoes;
import ch.qos.logback.classic.Logger;

public class CertidaoFederal extends BasePage {

	WebDriver driver;
	public static final Logger log = (Logger) LoggerFactory.getLogger(CertidaoFederal.class);

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

		String processadoPath = "";

		String pesquisarCnpj = br.com.standardit.ws.ecertidoes.ECertidoes.cnpj;
		// String pesquisarCnpj = "13921538000180";// gsw ok
		// String pesquisarCnpj = "61230314000175";//libs negativa
		// String pesquisarCnpj = "04899316043860";//cenario 3
		// String pesquisarCnpj = "04899316000118";//cenario 4

		try {

			input = new FileInputStream("config.properties");
			prop.load(input);

			processadoPath = prop.getProperty("processadoPath");
			processadoPath = processadoPath + "\\" + pesquisarCnpj;
			util.CreateFolder(processadoPath);

			ImagePath.add(prop.getProperty("imagefolder"));

			driver.get(
					"http://servicos.receita.fazenda.gov.br/Servicos/certidao/CNDConjuntaInter/InformaNICertidao.asp?tipo=1");
			log.info("titulo da pagina: " + driver.getTitle());
			util.sleep(10);

			escreverByName("NI", pesquisarCnpj);

			clicarBotao(2, "button1");

			CaptureScreenOfSpecificElement("imgCaptchaSerpro", processadoPath);

			File file = new File(processadoPath + "\\imgCaptchaSerpro.png");
			String quebraDeCaptcha = TwoCaptchaDotComSolver.solveCaptcha(file);
			System.out.println("Resultado da Quebra de Captcha: " + quebraDeCaptcha);
			escrever(1, "txtTexto_captcha_serpro_gov_br", quebraDeCaptcha);

			// TODO: Erro_captcha incluir tratamento

			clicarBotao(2, "submit1");

			if (procurarImagem(5, "ECertidoes\\Federal\\NovaCertidao"))
				clicarBotaoPorXPath(2, "//a[contains(text(),'Emissão de nova certidão')]");

			if (procurarImagem(20, "ECertidoes\\Federal\\CertidaoOK")) {
				ECertidoes.descStatus = "1"; // "Gerado sem pendencias";
				ECertidoes.validade = util.getDateWithPlusDays(180);
				util.SaveChromeToPdf(processadoPath + "\\Certidao.pdf", "ECertidoes\\Federal\\LogoFazenda");
			}

			if (procurarImagem(1, "ECertidoes\\Federal\\CertidaoComPendencias")) {
				ECertidoes.descStatus = "3"; // "Gerado com pendencias";
				ECertidoes.validade = util.getDateWithPlusDays(180);
				util.SaveChromeToPdf(processadoPath + "\\Certidao.pdf", "ECertidoes\\Federal\\LogoFazenda");
			}

			if (procurarImagem(1, "ECertidoes\\Federal\\CnpjNaoMatriz")) {
				ECertidoes.descStatus = "4"; // "Nao gerado pendencias";
			}

			if (procurarImagem(1, "ECertidoes\\Federal\\DadosInsuficientes")) {
				ECertidoes.descStatus = "4"; // "Nao gerado pendencias";
			}

			if (procurarImagem(1, "ECertidoes\\Federal\\ConsultaEmProcessamento")) {
				ECertidoes.descStatus = "4"; // "Nao gerado pendencias";
			}

		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		} finally {
			if (ECertidoes.descStatus == "") {
				log.error("Cenario nao mapeado.");
				ECertidoes.descStatus = "4";
			}
			driver.manage().window().fullscreen();
			PrintErro(processadoPath + "\\Certidao.png");
		}
	}
}
