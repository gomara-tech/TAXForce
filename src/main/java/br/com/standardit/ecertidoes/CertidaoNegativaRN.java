package br.com.standardit.ecertidoes;

import static br.com.standardit.core.DriverFactory.getDriver;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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

public class CertidaoNegativaRN extends BasePage {

	static WebDriver driver;

	public static final Logger log = (Logger) LoggerFactory.getLogger(CertidaoNegativaRN.class);

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

			driver.get("https://uvt2.set.rn.gov.br/#/services/certidao-negativa/emitir");
			log.info("titulo da pagina: " + driver.getTitle());
			util.sleep(5);
			
			Boolean ResolucaoDoCaptcha = false;
			if (pesquisarCnpj == "" || pesquisarCnpj == null) {
				if (inscricaoEstadual == "" || inscricaoEstadual == null) {
					escrever("captcha", ".....");
					clicarNaImagem(3, "ECertidoes\\RN\\BotaoEmitir");
					ResolucaoDoCaptcha = true;
				}else {
					escrever(10, "identificacao", inscricaoEstadual);
				}
			}else {
				escrever(10, "identificacao", pesquisarCnpj);
			}

			int contador = 0;
			while (ResolucaoDoCaptcha.equals(false)) {
				CaptureScreenOfSpecificElementByClass("img-responsive", processadoPath, "form");
				File file = new File(processadoPath + "\\form.png");
				String quebraDeCaptcha = TwoCaptchaDotComSolver.solveCaptcha(file);
				System.out.println("Resultado da Quebra de Captcha: " + quebraDeCaptcha);

				escrever("captcha", quebraDeCaptcha.toUpperCase());
				util.sleep(1);
				s.type(Key.TAB);
				util.sleep(2);
				s.type(Key.ENTER);
				util.sleep(5);
				//clicarNaImagem(3, "ECertidoes\\RN\\BotaoEmitir");
				

				s.type(Key.PAGE_DOWN);

				ResolucaoDoCaptcha = validaProcessamentoDoCaptcha();

				s.type(Key.PAGE_DOWN);

				if (contador == 8)
					break;

				contador++;
			}
			
			util.sleep(5);
			
			if (procurarImagem(5, "ECertidoes\\RN\\ErroInstabilidade"))
				ECertidoes.descStatus = "4";

			if (procurarImagem(5, "ECertidoes\\RN\\CaptchaInvalido"))
				ECertidoes.descStatus = "4";

			if (procurarImagem(5, new String [] {"ECertidoes\\RN\\CNPJInvalido","ECertidoes\\RN\\CNPJInvalido2"}))
				ECertidoes.descStatus = "2";

			if (procurarImagem(5,
					new String[] { "ECertidoes\\RN\\MotivoDaNaoEmissao", "ECertidoes\\RN\\MotivoDaNaoEmissao2" })) {
				s.type(Key.PAGE_DOWN);
				ECertidoes.descStatus = "3"; // "gerado com pendencias ou de outra UF";
			}

			if (procurarImagem(5, "ECertidoes\\RN\\CertidaoEmitida")) {
				util.sleep(2);
				MoveFiles(pastaDownloads, processadoPath);
				ECertidoes.descStatus = "1"; // "gerado SEM pendencias ou de outra UF";
				ECertidoes.validade = util.getDateWithPlusDays(30);
			}
			System.out.println("ECertidoes.descStatus: " + ECertidoes.descStatus);
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

//-----------------------------------------------------------------------------------------------------
	public Boolean validaProcessamentoDoCaptcha() {
		Screen s = new Screen();
		Boolean retorno = false;
		Boolean processando;
		if (procurarImagem(5, "ECertidoes\\RN\\TelaDeEnvioPorEmail")) {
			clicarNaImagemEInsereTexto(3, "ECertidoes\\RN\\PreencherEmail", "ecertidoes@taxforce.com.br");
			clicarNaImagem(3, "ECertidoes\\RN\\GerarCertidao");
			util.sleep(5);
			for (int i = 0; i < 180; i++) {
				if (!procurarImagem(5, "ECertidoes\\RN\\Carregando")) {
					s.type(Key.PAGE_DOWN);
					retorno = true;
					break;
				}
			}
		}

		if (procurarImagem(1, "ECertidoes\\RN\\CaptchaInvalido")) {
			clicarNaImagem(1, "ECertidoes\\RN\\CaptchaInvalidoBotaoOk");
			clicarNaImagem(4, "ECertidoes\\RN\\NovaImagem");
			util.sleep(5);
		} else {
			if (procurarImagem(1, "ECertidoes\\RN\\CertidaoEmitida"))
				retorno = true;
		}
		if (procurarImagem(1, new String [] {"ECertidoes\\RN\\CNPJInvalido","ECertidoes\\RN\\CNPJInvalido2"})) {
			retorno = true;
		}	
		return retorno;
	}

//-------------------------------------------------------------------------------------------------------	
	public void CaptureScreenOfSpecificElementByClass(String stringDoElement, String outputFolder, String nomeArquivo)
			throws IOException {

		WebElement ele = getDriver().findElement(By.className(stringDoElement));
		File screenshot = ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.FILE);
		BufferedImage fullImg = ImageIO.read(screenshot);

		org.openqa.selenium.Point point = ele.getLocation();

		int eleWidth = ele.getSize().getWidth();
		int eleHeight = ele.getSize().getHeight();

		BufferedImage eleScreenshot = fullImg.getSubimage(point.getX(), point.getY(), eleWidth, eleHeight);

		ImageIO.write(eleScreenshot, "png", screenshot);

		FileUtils.copyFile(screenshot, new File(outputFolder + "\\" + nomeArquivo + ".png"));
	}

	// ----------------------------------------------------------------------------------------------------
	public static void MoveFiles(String sourceDir, String destDir) throws InterruptedException {
		Path source = Paths.get(sourceDir);
		Path dest = Paths.get(destDir);
		try {
			if (source.toFile().isDirectory()) {
				File[] content = source.toFile().listFiles();
				for (File file : content) {
					if (file.getName().contains("certidaoconjunta")) {
						util.log.info("[" + util.getMetod() + "] - Moving file from: " + file.getAbsolutePath()
								+ " to: " + destDir + "\\" + file.getName());
						Files.move(Paths.get(file.getAbsolutePath()), Paths.get(destDir + "\\" + file.getName()),
								StandardCopyOption.REPLACE_EXISTING);
						new File(destDir + "\\" + file.getName()).renameTo(new File(destDir + "\\Certidao.pdf"));
						TimeUnit.SECONDS.sleep(3);
					}
					// Aquarda um segundo para evitar erro ao copiar arquivo PDF
				}
			} else {
				util.log.info("[" + util.getMetod() + "] - Replace existing file.");
				Files.move(source, dest, StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (java.nio.file.FileSystemException fsEx) {
			util.log.error("[" + util.getMetod() + "] - FileSystemException: " + fsEx.getMessage());
			// TimeUnit.SECONDS.sleep(5);
			// MoveFiles(sourceDir, destDir);
		} catch (IOException e) {
			util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
			e.printStackTrace();
		}

	}
}