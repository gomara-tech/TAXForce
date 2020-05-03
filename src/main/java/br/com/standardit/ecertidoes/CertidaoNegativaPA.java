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

public class CertidaoNegativaPA extends BasePage {

	WebDriver driver;
	
	public static final Logger log = (Logger) LoggerFactory.getLogger(CertidaoNegativaPA.class);

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
			}else {
				processadoPath = processadoPath + "\\" + pesquisarCnpj;
			}

			driver.get("https://app.sefa.pa.gov.br/emissao-certidao/template.action");
			log.info("titulo da pagina: " + driver.getTitle());

			clicarNaImagem(15, "ECertidoes\\PA\\Selecione");
			util.sleep(1);

			if (inscricaoEstadual == "" || inscricaoEstadual == null) {
				s.type(Key.DOWN);
				s.type(Key.DOWN);
				s.type(Key.ENTER);
			} else {
				s.type(Key.DOWN);
				s.type(Key.DOWN);
				s.type(Key.DOWN);
				s.type(Key.ENTER);
			}

			if (inscricaoEstadual == "" || inscricaoEstadual == null) {
				clicarNaImagemEInsereTexto(5, "ECertidoes\\PA\\InserirCNPJ", pesquisarCnpj);
			} else {
				clicarNaImagemEInsereTexto(5, "ECertidoes\\PA\\InserirCNPJ", inscricaoEstadual);
			}

			clicarNaImagem(15, "ECertidoes\\PA\\BotaoContinuar");
			
			util.sleep(15);
			
			if (procurarImagem(2, "ECertidoes\\PA\\ErroCnpj")) {
				ECertidoes.descStatus = "4"; // "Nao gerado";
			}

			if (procurarImagem(2, "ECertidoes\\PA\\Pendencias")) {
				ECertidoes.descStatus = "3"; // "pendencia";
			}

			if (procurarImagem(2, "ECertidoes\\PA\\Inexistente")) {
				ECertidoes.descStatus = "2";
			}

			if (procurarImagem(2, "ECertidoes\\PA\\VisualizarCertidao")) {
				clicarNaImagem(15, "ECertidoes\\PA\\VisualizarCertidao");
				
				if (procurarImagem(2, "ECertidoes\\PA\\NaoExisteCertidao")) {
					ECertidoes.descStatus = "3"; // "pendencia";
				}

				if (procurarImagem(2, "ECertidoes\\PA\\Certidao")) {
					util.SaveChromeToPdf2(processadoPath + "\\Certidao.pdf", new String [] {"ECertidoes\\PA\\Certidao"});
					ECertidoes.descStatus = "1"; // "sem pendencia";
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
