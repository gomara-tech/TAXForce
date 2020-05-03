package br.com.standardit.ecertidoes;

import static br.com.standardit.core.DriverFactory.getDriver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.sikuli.script.ImagePath;
import org.sikuli.script.Screen;
import org.slf4j.LoggerFactory;

import com.lowagie.text.Document;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfWriter;

import br.com.standardit.UtilAutomation;
import br.com.standardit.WaitResult;
import br.com.standardit.util;
import br.com.standardit.captcha.TwoCaptchaDotComSolver;
import br.com.standardit.core.BasePage;
import br.com.standardit.ws.ecertidoes.ECertidoes;
import ch.qos.logback.classic.Logger;

public class CertidaoNegativaMA extends BasePage {

	WebDriver driver;

	public static final Logger log = (Logger) LoggerFactory.getLogger(CertidaoNegativaMA.class);

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

			driver.get("https://sistemas1.sefaz.ma.gov.br/portalsefaz/jsp/menu/view.jsf?codigo=16");
			log.info("titulo da pagina: " + driver.getTitle());

			clicarBotaoPorXPath("//div[@id='servicos']/ul/li/a/div/h3");
			util.sleep(5);

			if (!inscricaoEstadual.equals("")) {
				escrever("form1:inscricaoEstadual", inscricaoEstadual);

				Boolean ResolucaoDoCaptcha = false;
				int contador = 0;
				while (ResolucaoDoCaptcha.equals(false)) {
					CaptureScreenOfSpecificElement("form1:captcha", processadoPath, "form");
					File file = new File(processadoPath + "\\form.png");
					String quebraDeCaptcha = TwoCaptchaDotComSolver.solveCaptcha(file);
					System.out.println("Resultado da Quebra de Captcha: " + quebraDeCaptcha);

					clicarNaImagemEInsereTexto(2, "ECertidoes\\MA\\Codigo", quebraDeCaptcha);
					clicarNaImagem(3, "ECertidoes\\MA\\EmitirCertidao");
					ResolucaoDoCaptcha = validaProcessamentoDoCaptcha();

					if (contador == 8)
						break;
					contador++;
				}
			} else {
				escrever("form1:inscricaoEstadual", "00000000");
				clicarNaImagemEInsereTexto(2, "ECertidoes\\MA\\Codigo", "ERRO");
				ECertidoes.descStatus = "2";
			}

			if (procurarImagem(2, "ECertidoes\\MA\\CertidaoNegativa")) {
				ECertidoes.descStatus = "1"; // "gerado sem pendencias";
				ECertidoes.validade = util.getDateWithPlusDays(120);
				util.SaveChromeToPdf(processadoPath + "\\Certidao.pdf", "ECertidoes\\MA\\CertidaoNegativa");
			}

			if (procurarImagem(2, "ECertidoes\\MA\\EmpresaDevedora"))
				ECertidoes.descStatus = "3"; // "gerado Com pendencias";

			if (procurarImagem(2, "ECertidoes\\MA\\Inexistente"))
				ECertidoes.descStatus = "2"; // "gerado Com pendencias";

			if (procurarImagem(2, "ECertidoes\\MA\\CodigoImagemInvalida"))
				ECertidoes.descStatus = "4"; // "Erro no captcha";

			if (procurarImagem(2, new String [] {"ECertidoes\\MA\\Invalida","ECertidoes\\MA\\Invalida2"}))
				ECertidoes.descStatus = "2"; // "Inexistente";

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

	// --------------------------------------------------------------------------------------
	public Boolean validaProcessamentoDoCaptcha() {
		Boolean retorno = false;
		if (procurarImagem(5, "ECertidoes\\MA\\PrefeituraMA"))
			retorno = true;
		if (procurarImagem(1, "ECertidoes\\MA\\EmpresaDevedora"))
			retorno = true;
		if (procurarImagem(1, "ECertidoes\\MA\\Invalida"))
			retorno = true;
		if (procurarImagem(1, "ECertidoes\\MA\\Inexistente"))
			retorno = true;

		return retorno;
	}

	public static void ConvertImageToPDF(String input, String output) {
		Document document = new Document();
		// String input = "c:/temp/capture.png"; // .gif and .jpg are ok too!
		// String output = "c:/temp/capture.pdf";
		try {
			FileOutputStream fos = new FileOutputStream(output);
			PdfWriter writer = PdfWriter.getInstance(document, fos);
			writer.open();
			document.open();
			document.add(Image.getInstance(input));
			document.close();
			writer.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}
