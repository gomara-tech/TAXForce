package br.com.standardit.gia.sp;

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

public class GIA_SP extends BasePage {

	static WebDriver driver;
	public static final Logger log = (Logger) LoggerFactory.getLogger(GIA_SP.class);

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
	public void Home() {

		Screen s = new Screen();
		UtilAutomation utilAut = new UtilAutomation(s);

		Properties prop = new Properties();
		InputStream input;
		String watchPath = "";
		String usuario = br.com.standardit.ws.WSGetFile.userObrigacao;
		String senha = br.com.standardit.ws.WSGetFile.passObrigacao;

		try {
			input = new FileInputStream("config.properties");
			prop.load(input);
			watchPath = prop.getProperty("watchPath");
			File folder = new File(watchPath);
			ImagePath.add(prop.getProperty("imagefolder"));

			if (usuario == "")
				usuario = prop.getProperty("giaSpUsuario");
			if (senha == "")
				senha = prop.getProperty("giaSpSenha");

			driver.get("https://www3.fazenda.sp.gov.br/CAWEB/Account/Login.aspx");
			util.sleep(5);
			log.info("[" + getMetod() + "] - titulo da pagina: " + driver.getTitle());

			escrever(1, "ConteudoPagina_txtUsuario", usuario);

			escrever(1, "ConteudoPagina_txtSenha", senha);

			clicarBotao(5, "ConteudoPagina_btnAcessar");

			clicarNaImagem(10, "GIA_SP\\ClicaEmNovaGia");
			// clicarBotaoPorXPath(2, "//img[@alt='Nova GIA']");

			clicarBotaoPorXPath(5, "//td[2]/a/font");

			clicarNaImagem(10, "GIA_SP\\BotaoEscolherArquivo");

			clicarNaImagemEInsereTexto(15, "GIA_SP\\FileName", watchPath + "\\" + getFileToTransmit(folder));

			clicarNaImagem(10, "GIA_SP\\BotaoClose");
			util.sleep(5);

			clicarNaImagem(5, "GIA_SP\\ClicaNaTelaParaMudarOFoco");
			s.type(Key.END);
			util.sleep(1);

			clicarNaImagem(10, "GIA_SP\\ClicaEmEnviarArquivos");

			log.info("Clica em cofirmar envio");
			util.sleep(3);
			s.type(Key.ENTER);

			util.sleep(3);
			util.SaveChromeToPdf2(watchPath + "\\ReciboDeEnvio.pdf", new String[] { "GIA_SP\\Protocolo" });

		} catch (Exception e) {
			log.error("[" + getMetod() + "] - " + e.getMessage());
			PrintErro(watchPath + "\\Erro.png");
			e.printStackTrace();
			Assert.assertEquals("1", "0");
		} finally {
			Assert.assertEquals("1", "1");
			driver.manage().window().fullscreen();
		}
	}

	// -------------------------------------------------------------------------------------
	public static String getFileToTransmit(final File folder) {
		log.info("[" + getMetod() + "] - Lendo diretorio " + folder.toString());
		for (final File fileEntry : folder.listFiles())
			if (!fileEntry.isDirectory())
				if (fileEntry.getName().contains(".SFZ")) {
					return fileEntry.getName();
				}
		return null;
	}
}
