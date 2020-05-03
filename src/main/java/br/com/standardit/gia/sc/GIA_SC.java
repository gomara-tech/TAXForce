package br.com.standardit.gia.sc;

import static br.com.standardit.core.DriverFactory.getDriver;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.sikuli.script.ImagePath;
import org.sikuli.script.Key;
import org.sikuli.script.Screen;
import org.slf4j.LoggerFactory;

import br.com.standardit.UtilAutomation;
import br.com.standardit.util;
import br.com.standardit.core.BasePage;
import ch.qos.logback.classic.Logger;

public class GIA_SC extends BasePage {

	static WebDriver driver;
	public static final Logger log = (Logger) LoggerFactory.getLogger(GIA_SC.class);

	@Before
	public void inicializa() {
		log.info("[" + getMetod() + "] - Abrindo navegador...");
		driver = getDriver();
	}

	@After
	public void finaliza() {
		log.info("[" + getMetod() + "] - Finalizando navegador...");
		driver.quit();
	}

	@Test
	public void Home() throws Exception {

		Screen s = new Screen();
		UtilAutomation utilAut = new UtilAutomation(s);

		Properties prop = new Properties();
		InputStream input;
		String watchPath = "";
		String transmitirArquivo = "";

		String usuario = br.com.standardit.ws.WSGetFile.userObrigacao;
		String senha = br.com.standardit.ws.WSGetFile.passObrigacao;

		try {

			input = new FileInputStream("config.properties");
			prop.load(input);

			watchPath = prop.getProperty("watchPath");
			transmitirArquivo = prop.getProperty("transmitirArquivo");

			File folder = new File(watchPath);
			ImagePath.add(prop.getProperty("imagefolder"));

			if (usuario == "")
				usuario = prop.getProperty("giaSCUsuario");
			if (senha == "")
				senha = prop.getProperty("giaSCSenha");

			driver.get("https://tributario.sef.sc.gov.br/tax.NET/Login.aspx?ReturnUrl=%2ftax.net%2fdefault.aspx");
			log.info("[" + getMetod() + "] - Titulo da pagina: " + driver.getTitle());

			escrever(1, "Body_ctl00_tbxUsername", usuario);

			escrever(1, "Body_ctl00_tbxUserPassword", senha);

			log.info("[" + getMetod() + "] - Preenche o login");
			clicarBotaoPorXPath(5, "//a[@id='Body_ctl00_btnLogin']/span");
			util.sleep(20);

			// clicarBotao(1, "Body_Main_ctl04_lkbTab2");
			clicarBotaoPorXPath(10, "//*[@id=\'Body_Main_ctl04_lkbTab2\']");

			clicarBotaoPorXPath(5, "//div[@id='Body_Main_ctl04_tabProfiles_trvProfiles']/div[3]/div/div[2]");

			clicarBotaoPorXPath(5, "//div[3]/div[2]/div[54]/div/div[2]/a[2]");

			clicarNaImagem(30, "GIA_SC\\BotaoProcurar");

			clicarNaImagemEInsereTexto(10, "GIA_SC\\Seleciona_Arquivo", watchPath + "\\" + getFileToProcess(folder));

			clicarNaImagem(30, "GIA_SC\\BotaoOpen");

			clicarNaImagem(30, "GIA_SC\\BotaoValidar");

			procurarImagem(15, "GIA_SC\\BotaoEnviar");

			if (transmitirArquivo.equals("true")) {

				clicarNaImagem(1, "GIA_SC\\BotaoEnviar");

				if (procurarImagem(15, "GIA_SC\\ErroAoEnviar")) {
					Assert.assertEquals("1", "0");
				} else {

					clicarNaImagem(25, new String[] { "GIA_SC\\Pagina_Protocolo", "GIA_SC\\CabecalhoProtocolo" });

					s.type(Key.CTRL, Key.END);
					util.sleep(1);

					clicarNaImagem(15, "GIA_SC\\Imprimir");

					clicarNaImagem(15, new String[] { "GIA_SC\\BotaoPrint", "GIA_SC\\BotaoImprimir" });

					clicarNaImagemEInsereTexto(15, "GIA_SC\\InserirCaminhoDoArquivo",
							watchPath + "\\" + getFileToProcess(folder).replace(".txt", "").replace(".TXT", ""));

					clicarNaImagem(15, "GIA_SC\\BotaoSalvar");

					if (procurarImagem(15, "GIA_SC\\BotaoFecharPDF"))
						clicarNaImagem(1, "GIA_SC\\BotaoFecharPDF");

					Assert.assertEquals("1", "1");
				}
			} else {
				Assert.assertEquals("1", "1");
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			PrintErro(watchPath + "\\Erro_Na_Classe[" + getMetod() + "].png");
			Assert.assertEquals("1", "0");
		} finally {
			driver.manage().window().fullscreen();
			PrintErro(watchPath + "\\Print[" + getMetod() + "].png");
		}
	}

// --------------------------------------------------------------------------------------------------
	public static String getFileToProcess(final File folder) {
		log.info("[" + getMetod() + "] - Lendo diretorio " + folder.toString());
		for (final File fileEntry : folder.listFiles())
			if (!fileEntry.isDirectory())
				if (fileEntry.getName().contains("DIME-SC") || fileEntry.getName().contains("GIA_SC")) {
					return fileEntry.getName();
				}
		return null;
	}

}
