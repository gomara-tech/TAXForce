package br.com.standardit.ecertidoes;

import static br.com.standardit.core.DriverFactory.getDriver;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

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
import br.com.standardit.captcha.TwoCaptchaDotComSolver;
import br.com.standardit.core.BasePage;
import br.com.standardit.ws.ecertidoes.ECertidoes;
import ch.qos.logback.classic.Logger;

public class CertidaoNegativaSP extends BasePage {

	static WebDriver driver;

	public static final Logger log = (Logger) LoggerFactory.getLogger(CertidaoNegativaSP.class);

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

			driver.get("https://www10.fazenda.sp.gov.br/CertidaoNegativaDeb/Pages/EmissaoCertidaoNegativa.aspx");
			log.info("titulo da pagina: " + driver.getTitle());

			log.info("Clica em CNPJ");
			clicarBotao(3, "MainContent_cnpjradio");
			util.sleep(3);

			log.info("Preenche CNPJ: " + pesquisarCnpj);
			escrever(3, "MainContent_txtDocumento", "");

			for (int i = 0; i < 24; i++)
				s.type(Key.LEFT);
			s.type(pesquisarCnpj);

			Boolean ResolucaoDoCaptcha = false;
			int contador = 0;
			while (ResolucaoDoCaptcha.equals(false)) {
				CaptureScreenOfSpecificElement("MainContent_imgcapcha", processadoPath);
				File file = new File(processadoPath + "\\MainContent_imgcapcha.png");
				String quebraDeCaptcha = TwoCaptchaDotComSolver.solveCaptcha(file);
				System.out.println("Resultado da Quebra de Captcha: " + quebraDeCaptcha);
				escrever("MainContent_txtConfirmaCaptcha", quebraDeCaptcha);
				clicarBotao("MainContent_btnPesquisar");
				ResolucaoDoCaptcha = validaProcessamentoDoCaptcha();

				if (contador == 10)
					break;
				contador++;
			}

			if (procurarImagem(3, new String[] { "ECertidoes\\SP\\Pendencias", "ECertidoes\\SP\\Pendencias2" })) {
				ECertidoes.descStatus = "3"; // "gerado com p04899316025706endencias";
			} else {
				if (procurarImagem(15, "ECertidoes\\SP\\BotaoImprimir")) {
					clicarNaImagem(2, "ECertidoes\\SP\\BotaoImprimir");
					// clicarBotao("MainContent_btnImpressao");

					procurarImagem(120, new String[] { "ECertidoes\\SP\\IconeDoDownload",
							"ECertidoes\\SP\\IconeDoDownload2", "ECertidoes\\SP\\IconeDoDownload3" });

					if (util.ProcuraArquivoEmPasta(pastaDownloads, "CND")) {
						
						if (MoveFiles(pastaDownloads, processadoPath)) {
							ECertidoes.descStatus = "1"; // "gerado sem pendencias";
							ECertidoes.validade = util.getDateWithPlusDays(180);
						}
					}
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

//----------------------------------------------------------------------------------------------------
	public static boolean MoveFiles(String sourceDir, String destDir) throws InterruptedException {
		Boolean retorno = false;
		Path source = Paths.get(sourceDir);
		Path dest = Paths.get(destDir);
		try {
			if (source.toFile().isDirectory()) {
				File[] content = source.toFile().listFiles();
				for (File file : content) {
					if (file.getName().contains("CND")) {
						util.log.info("[" + util.getMetod() + "] - Moving file from: " + file.getAbsolutePath()
								+ " to: " + destDir + "\\" + file.getName());
						Files.move(Paths.get(file.getAbsolutePath()), Paths.get(destDir + "\\" + file.getName()),
								StandardCopyOption.REPLACE_EXISTING);
						new File(destDir + "\\" + file.getName()).renameTo(new File(destDir + "\\Certidao.pdf"));
						TimeUnit.SECONDS.sleep(7);
						retorno = true;
					}
					// Aquarda um segundo para evitar erro ao copiar arquivo PDF
				}
			} else {
				util.log.info("[" + util.getMetod() + "] - Replace existing file.");
				Files.move(source, dest, StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (Exception e) {
			util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
			e.printStackTrace();
		}
		return retorno;
	}

	public Boolean validaProcessamentoDoCaptcha() {
		Boolean retorno = false;
		if (procurarImagem(2, "ECertidoes\\SP\\ResolucaoDoCaptcha"))
			retorno = true;
		if (procurarImagem(2, new String[] { "ECertidoes\\SP\\Pendencias", "ECertidoes\\SP\\Pendencias2" })) {
			retorno = true;
		}
		return retorno;
	}
}