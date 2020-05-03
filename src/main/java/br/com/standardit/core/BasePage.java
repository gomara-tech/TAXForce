package br.com.standardit.core;

import static br.com.standardit.core.DriverFactory.getDriver;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.sikuli.script.ImagePath;
import org.sikuli.script.Screen;
import org.slf4j.LoggerFactory;

import br.com.standardit.UtilAutomation;
import br.com.standardit.WaitResult;
import ch.qos.logback.classic.Logger;

public class BasePage {
	Screen s = new Screen();
	UtilAutomation utilAut = new UtilAutomation(s);

	Properties prop = new Properties();
	InputStream input;
	WaitResult waitResult;
	String watchPath = "";

	public static final Logger log = (Logger) LoggerFactory.getLogger(BasePage.class);

	/********* TextField e TextArea ************/
	public void escrever(By by, String texto) {
		log.info("[" + getMetod() + "] - findElement: " + by);
		getDriver().findElement(by).clear();
		getDriver().findElement(by).sendKeys(texto);
	}

	public void escrever(String id_campo, String texto) {
		escrever(By.id(id_campo), texto);
	}

	public void escreverByName(String name_campo, String texto) {
		escrever(By.name(name_campo), texto);
	}

	public void escrever(int sleep, String id_campo, String texto) {
		escrever(By.id(id_campo), texto);
		utilAut.sleep(sleep);
	}

	public String obterValorCampo(String id_campo) {
		return getDriver().findElement(By.id(id_campo)).getAttribute("value");
	}

	/********* Radio e Check ************/

	public void clicarRadio(By by) {
		log.info("[" + getMetod() + "] - findElement: " + by);
		getDriver().findElement(by).click();
	}

	public void clicarRadio(String id) {
		clicarRadio(By.id(id));
	}

	public boolean isRadioMarcado(String id) {
		log.info("[" + getMetod() + "] - findElement: " + id);
		return getDriver().findElement(By.id(id)).isSelected();
	}

	public void clicarCheck(String id) {
		log.info("[" + getMetod() + "] - findElement: " + id);
		getDriver().findElement(By.id(id)).click();
	}

	public boolean isCheckMarcado(String id) {
		log.info("[" + getMetod() + "] - findElement: " + id);
		return getDriver().findElement(By.id(id)).isSelected();
	}

	/********* Combo ************/

	public void selecionarCombo(String id, String valor) {
		log.info("[" + getMetod() + "] - findElement: " + id + " value: " + valor);
		WebElement element = getDriver().findElement(By.id(id));
		Select combo = new Select(element);
		combo.selectByVisibleText(valor);
	}

	public void deselecionarCombo(String id, String valor) {
		log.info("[" + getMetod() + "] - findElement: " + id + " value: " + valor);
		WebElement element = getDriver().findElement(By.id(id));
		Select combo = new Select(element);
		combo.deselectByVisibleText(valor);
	}

	public String obterValorCombo(String id) {
		log.info("[" + getMetod() + "] - findElement: " + id);
		WebElement element = getDriver().findElement(By.id(id));
		Select combo = new Select(element);
		return combo.getFirstSelectedOption().getText();
	}

	public List<String> obterValoresCombo(String id) {
		log.info("[" + getMetod() + "] - findElement: " + id);
		WebElement element = getDriver().findElement(By.id("elementosForm:esportes"));
		Select combo = new Select(element);
		List<WebElement> allSelectedOptions = combo.getAllSelectedOptions();
		List<String> valores = new ArrayList<String>();
		for (WebElement opcao : allSelectedOptions) {
			valores.add(opcao.getText());
		}
		return valores;
	}

	public int obterQuantidadeOpcoesCombo(String id) {
		WebElement element = getDriver().findElement(By.id(id));
		Select combo = new Select(element);
		List<WebElement> options = combo.getOptions();
		return options.size();
	}

	public boolean verificarOpcaoCombo(String id, String opcao) {
		WebElement element = getDriver().findElement(By.id(id));
		Select combo = new Select(element);
		List<WebElement> options = combo.getOptions();
		for (WebElement option : options) {
			if (option.getText().equals(opcao)) {
				return true;
			}
		}
		return false;
	}

	public void selecionarComboPrime(String radical, String valor) {
		clicarRadio(By.xpath("//*[@id='" + radical + "_input']/../..//span"));
		clicarRadio(By.xpath("//*[@id='" + radical + "_items']//li[.='" + valor + "']"));
	}

	/********* Botao ************/

	public void clicarBotao(By by) {
		log.info("[" + getMetod() + "] - findElement: " + by);
		getDriver().findElement(by).click();
	}

	public void clicarBotao(String id) {
		clicarBotao(By.id(id));
	}

	public void clicarBotao(int segundos, String id) {
		clicarBotao(By.id(id));
		utilAut.sleep(segundos);
	}

	public void clicarBotaoByClass(int segundos, String classe) {
		clicarBotao(By.className(classe));
		utilAut.sleep(segundos);
	}

	public void clicarBotaoPorImg(String img) {
		// img[@alt='Guias ISS']
		// clicarBotao(By.xpath(img));
		// driver.findElement(By.Xpath("//strong[contains(text(),'" + service +"')]"));
		// driver.findElement(By.xpath("//*[@class='ng-binding']")).click();
		// driver.findElement(By.partialLinkText("Sequential")).click();
		// selenium.click("xpath=//a[contains(@href,'nova-guia') and
		// @id='oldcontent']");
		// selenium.click("xpath=//a[contains(@href,'nova-guia') and
		// @id='oldcontent']");
		// clicarBotao(By.xpath("//a[contains(@href,'" + img +"')]"));
		// getDriver().findElement(By.xpath("//img[@alt='Guias ISS']")).click();
		getDriver().findElement(By.xpath("//li[6]/a/img")).click();
		//// li[6]/a/img
		// img[@alt='Guias ISS']

	}

	public void clicarBotaoPorTexto(String texto) {
		clicarBotao(By.xpath("//button[.='" + texto + "']"));
	}

	public void clicarBotaoPorXPath(String texto) {
		log.info("[" + getMetod() + "] - findXPath: " + texto);
		clicarBotao(By.xpath(texto));
	}

	public void clicarBotaoPorXPath(int segundos, String texto) {
		log.info("[" + getMetod() + "] - findXPath: " + texto);
		clicarBotao(By.xpath(texto));
		utilAut.sleep(segundos);
	}

	public String obterValueElemento(String id) {
		return getDriver().findElement(By.id(id)).getAttribute("value");
	}

	public String getElementByName(String name) {
		return getDriver().findElement(By.name(name)).getAttribute("value");
	}

	// getDriver().findElement(By.xpath("//td[5]/a"))
	public static String getElementByXPath(String texto) {
		// return getDriver().findElement(By.xpath(texto)).getAttribute("value");
		return getDriver().findElement(By.xpath(texto)).getText();
	}

	// driver.findElements(By.cssSelector("a[title='List of Users']");

	/********* Link ************/

	public void clicarLink(String link) {
		getDriver().findElement(By.linkText(link)).click();
	}

	/********* Textos ************/

	public String obterTexto(By by) {
		return getDriver().findElement(by).getText();
	}

	public String obterTexto(String id) {
		return obterTexto(By.id(id));
	}

	/********* Alerts ************/

	public String alertaObterTexto() {
		Alert alert = getDriver().switchTo().alert();
		return alert.getText();
	}

	public String alertaObterTextoEAceita() {
		Alert alert = getDriver().switchTo().alert();
		String valor = alert.getText();
		alert.accept();
		return valor;
	}

	public String alertaObterTextoENega() {
		Alert alert = getDriver().switchTo().alert();
		String valor = alert.getText();
		alert.dismiss();
		return valor;
	}

	public void alertaEscrever(String valor) {
		Alert alert = getDriver().switchTo().alert();
		alert.sendKeys(valor);
		alert.accept();
	}

	/********* Frames e Janelas ************/

	public void entrarFrame(String id) {
		getDriver().switchTo().frame(id);
	}

	public void sairFrame() {
		getDriver().switchTo().defaultContent();
	}

	public void trocarJanela(String id) {
		getDriver().switchTo().window(id);
	}

	/************** JS *********************/

	public Object executarJS(String cmd, Object... param) {
		JavascriptExecutor js = (JavascriptExecutor) getDriver();
		return js.executeScript(cmd, param);
	}

	/************** Tabela *********************/

	public WebElement obterCelula(String colunaBusca, String valor, String colunaBotao, String idTabela) {
		// procurar coluna do registro
		WebElement tabela = getDriver().findElement(By.xpath("//*[@id='" + idTabela + "']"));
		int idColuna = obterIndiceColuna(colunaBusca, tabela);

		// encontrar a linha do registro
		int idLinha = obterIndiceLinha(valor, tabela, idColuna);

		// procurar coluna do botao
		int idColunaBotao = obterIndiceColuna(colunaBotao, tabela);

		// clicar no botao da celula encontrada
		WebElement celula = tabela.findElement(By.xpath(".//tr[" + idLinha + "]/td[" + idColunaBotao + "]"));
		return celula;
	}

	public void clicarBotaoTabela(String colunaBusca, String valor, String colunaBotao, String idTabela) {
		WebElement celula = obterCelula(colunaBusca, valor, colunaBotao, idTabela);
		celula.findElement(By.xpath(".//input")).click();

	}

	protected int obterIndiceLinha(String valor, WebElement tabela, int idColuna) {
		List<WebElement> linhas = tabela.findElements(By.xpath("./tbody/tr/td[" + idColuna + "]"));
		int idLinha = -1;
		for (int i = 0; i < linhas.size(); i++) {
			if (linhas.get(i).getText().equals(valor)) {
				idLinha = i + 1;
				break;
			}
		}
		return idLinha;
	}

	protected int obterIndiceColuna(String coluna, WebElement tabela) {
		List<WebElement> colunas = tabela.findElements(By.xpath(".//th"));
		int idColuna = -1;
		for (int i = 0; i < colunas.size(); i++) {
			if (colunas.get(i).getText().equals(coluna)) {
				idColuna = i + 1;
				break;
			}
		}
		return idColuna;
	}

	// ----------------------------------------------------------------------------------
	public static String getMetod() {
		String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
		// methodName = Thread.currentThread().getStackTrace()[1].getMethodName(); //
		// Pega o método atual
		return methodName;
	}

	// ----------------------------------------------------------------------------------
	public static void PrintErro(String file) {
		try {
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			Dimension screenSize = toolkit.getScreenSize();
			Rectangle screenRect = new Rectangle(screenSize);
			Robot robot = new Robot();
			BufferedImage image = robot.createScreenCapture(screenRect);

			ImageIO.write(image, "png", new File(file));
		} catch (IOException e) {
			log.error("[" + getMetod() + "] - " + e.getMessage());
			e.printStackTrace();
		} catch (AWTException e) {
			log.error("[" + getMetod() + "] - " + e.getMessage());
			e.printStackTrace();
		}
	}

	// -----------------------------------------------------------------------
	public Boolean procurarImagem(int timeout, String imagem) {
		try {
			input = new FileInputStream("config.properties");
			prop.load(input);
			watchPath = prop.getProperty("watchPath");
			ImagePath.add(prop.getProperty("imagefolder"));

			waitResult = utilAut.WaitFor(timeout, new String[] { imagem });
			if (waitResult.region != null) {
				log.info("[" + getMetod() + "] - Imagem " + imagem + " encontrada.");
				return true;
			}

		} catch (Exception e) {
			log.error("[" + getMetod() + "] - " + e.getMessage());
			// PrintErro(watchPath + "\\Erro1.png");
			Assert.assertEquals("1", "0");
		}
		return false;
	}

	// -----------------------------------------------------------------------
	public Boolean procurarImagem(int timeout, String[] imagem) {
		try {
			input = new FileInputStream("config.properties");
			prop.load(input);
			watchPath = prop.getProperty("watchPath");
			ImagePath.add(prop.getProperty("imagefolder"));

			waitResult = utilAut.WaitFor(timeout, imagem );
			if (waitResult.region != null) {
				log.info("[" + getMetod() + "] - Imagem " + imagem + " encontrada.");
				return true;
			}

		} catch (Exception e) {
			log.error("[" + getMetod() + "] - " + e.getMessage());
			// PrintErro(watchPath + "\\Erro1.png");
			Assert.assertEquals("1", "0");
		}
		return false;
	}

	// -----------------------------------------------------------------------

	public void clicarNaImagem(int timeout, String imagem) {
		try {
			input = new FileInputStream("config.properties");
			prop.load(input);
			watchPath = prop.getProperty("watchPath");
			ImagePath.add(prop.getProperty("imagefolder"));

			waitResult = utilAut.WaitFor(timeout, new String[] { imagem });
			if (waitResult.region != null) {
				waitResult.region.click();
				log.info("[" + getMetod() + "] - Click na imagem " + imagem);
			} else {
				throw new Exception("Imagem " + imagem + " nao encontrada.");
			}

		} catch (Exception e) {
			log.error("[" + getMetod() + "] - " + e.getMessage());
			// PrintErro(watchPath + "\\Erro1.png");
			Assert.assertEquals("1", "0");
		}
	}
	
	public void clicarNaImagem(int timeout, String[] imagem) {
		try {
			input = new FileInputStream("config.properties");
			prop.load(input);
			watchPath = prop.getProperty("watchPath");
			ImagePath.add(prop.getProperty("imagefolder"));

			waitResult = utilAut.WaitFor(timeout,  imagem );
			if (waitResult.region != null) {
				waitResult.region.click();
				log.info("[" + getMetod() + "] - Click na imagem " + imagem);
			} else {
				throw new Exception("Imagem " + imagem + " nao encontrada.");
			}

		} catch (Exception e) {
			log.error("[" + getMetod() + "] - " + e.getMessage());
			// PrintErro(watchPath + "\\Erro1.png");
			Assert.assertEquals("1", "0");
		}
	}

	// -----------------------------------------------------------------------
	public void clicarNaImagemEInsereTexto(int timeout, String imagem, String text) {
		Screen s = new Screen();
		UtilAutomation utilAut = new UtilAutomation(s);

		Properties prop = new Properties();
		InputStream input;
		WaitResult waitResult;
		String watchPath = "";

		try {
			input = new FileInputStream("config.properties");
			prop.load(input);
			watchPath = prop.getProperty("watchPath");
			// File folder = new File(watchPath);
			ImagePath.add(prop.getProperty("imagefolder"));

			waitResult = utilAut.WaitFor(timeout, new String[] { imagem });
			if (waitResult.region != null) {
				waitResult.region.click();
				s.type(text);
				log.info("[" + getMetod() + "] - Click na imagem " + imagem);
			} else {
				throw new Exception("Imagem " + imagem + " nao encontrada.");
			}

		} catch (Exception e) {
			log.error("[" + getMetod() + "] - " + e.getMessage());
			// PrintErro(watchPath + "\\Erro1.png");
			Assert.assertEquals("1", "0");
		}
	}

//----------------------------------------------------------------------------------------------------------	
	public void CaptureScreenOfSpecificElement(String stringDoElement, String outputFolder) throws IOException {

		WebElement ele = getDriver().findElement(By.id(stringDoElement));
		File screenshot = ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.FILE);
		BufferedImage fullImg = ImageIO.read(screenshot);

		org.openqa.selenium.Point point = ele.getLocation();

		int eleWidth = ele.getSize().getWidth();
		int eleHeight = ele.getSize().getHeight();

		BufferedImage eleScreenshot = fullImg.getSubimage(point.getX(), point.getY(), eleWidth, eleHeight);

		ImageIO.write(eleScreenshot, "png", screenshot);

		FileUtils.copyFile(screenshot, new File(outputFolder + "\\" + stringDoElement + ".png"));
	}

	// ----------------------------------------------------------------------------------------------------------
	public void CaptureScreenOfSpecificElement(String stringDoElement, String outputFolder, String nomeArquivo)
			throws IOException {

		WebElement ele = getDriver().findElement(By.id(stringDoElement));
		File screenshot = ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.FILE);
		BufferedImage fullImg = ImageIO.read(screenshot);

		org.openqa.selenium.Point point = ele.getLocation();

		int eleWidth = ele.getSize().getWidth();
		int eleHeight = ele.getSize().getHeight();

		BufferedImage eleScreenshot = fullImg.getSubimage(point.getX(), point.getY(), eleWidth, eleHeight);

		ImageIO.write(eleScreenshot, "png", screenshot);

		FileUtils.copyFile(screenshot, new File(outputFolder + "\\" + nomeArquivo + ".png"));
	}

//	public static final Logger log = (Logger) LoggerFactory.getLogger(BasePage.class);
///********* TextField e TextArea ************/
//	
//	public void escrever(By by, String texto){
//		getDriver().findElement(by).clear();
//		getDriver().findElement(by).sendKeys(texto);
//	}
//
//	public void escrever(String id_campo, String texto){
//		escrever(By.id(id_campo), texto);
//	}
//	
//	public String obterValorCampo(String id_campo) {
//		return getDriver().findElement(By.id(id_campo)).getAttribute("value");
//	}
//	
//	/********* Radio e Check ************/
//	
//	public void clicarRadio(By by) {
//		getDriver().findElement(by).click();
//	}
//	
//	public void clicarRadio(String id) {
//		clicarRadio(By.id(id));
//	}
//	
//	public boolean isRadioMarcado(String id){
//		return getDriver().findElement(By.id(id)).isSelected();
//	}
//	
//	public void clicarCheck(String id) {
//		getDriver().findElement(By.id(id)).click();
//	}
//	
//	public boolean isCheckMarcado(String id){
//		return getDriver().findElement(By.id(id)).isSelected();
//	}
//	
//	/********* Combo ************/
//	
//	public void selecionarCombo(String id, String valor) {
//		WebElement element = getDriver().findElement(By.id(id));
//		Select combo = new Select(element);
//		combo.selectByVisibleText(valor);
//	}
//	
//	public void deselecionarCombo(String id, String valor) {
//		WebElement element = getDriver().findElement(By.id(id));
//		Select combo = new Select(element);
//		combo.deselectByVisibleText(valor);
//	}
//
//	public String obterValorCombo(String id) {
//		WebElement element = getDriver().findElement(By.id(id));
//		Select combo = new Select(element);
//		return combo.getFirstSelectedOption().getText();
//	}
//	
//	public List<String> obterValoresCombo(String id) {
//		WebElement element = getDriver().findElement(By.id("elementosForm:esportes"));
//		Select combo = new Select(element);
//		List<WebElement> allSelectedOptions = combo.getAllSelectedOptions();
//		List<String> valores = new ArrayList<String>();
//		for(WebElement opcao: allSelectedOptions) {
//			valores.add(opcao.getText());
//		}
//		return valores;
//	}
//	
//	public int obterQuantidadeOpcoesCombo(String id){
//		WebElement element = getDriver().findElement(By.id(id));
//		Select combo = new Select(element);
//		List<WebElement> options = combo.getOptions();
//		return options.size();
//	}
//	
//	public boolean verificarOpcaoCombo(String id, String opcao){
//		WebElement element = getDriver().findElement(By.id(id));
//		Select combo = new Select(element);
//		List<WebElement> options = combo.getOptions();
//		for(WebElement option: options) {
//			if(option.getText().equals(opcao)){
//				return true;
//			}
//		}
//		return false;
//	}
//	
//	public void selecionarComboPrime(String radical, String valor) {
//		clicarRadio(By.xpath("//*[@id='"+radical+"_input']/../..//span"));
//		clicarRadio(By.xpath("//*[@id='"+radical+"_items']//li[.='"+valor+"']"));
//	}
//	
//	/********* Botao ************/
//	
//	public void clicarBotao(By by) {
//		getDriver().findElement(by).click();
//	}
//	public void clicarBotao(String id) {
//		clicarBotao(By.id(id));
//	}
//	
//	public void clicarBotaoPorImg(String img){
//		//img[@alt='Guias ISS']
//		//clicarBotao(By.xpath(img));
//		//driver.findElement(By.Xpath("//strong[contains(text(),'" + service +"')]"));
//		//driver.findElement(By.xpath("//*[@class='ng-binding']")).click();
//		//driver.findElement(By.partialLinkText("Sequential")).click();
//		//selenium.click("xpath=//a[contains(@href,'nova-guia') and @id='oldcontent']");
//		//selenium.click("xpath=//a[contains(@href,'nova-guia') and @id='oldcontent']");
//		//clicarBotao(By.xpath("//a[contains(@href,'" + img +"')]"));
//		//getDriver().findElement(By.xpath("//img[@alt='Guias ISS']")).click();
//		getDriver().findElement(By.xpath("//li[6]/a/img")).click();
//		////li[6]/a/img
//		//img[@alt='Guias ISS']
//		
//	}
//	
//	public void clicarBotaoPorTexto(String texto){
//		clicarBotao(By.xpath("//button[.='"+texto+"']"));
//	}
//	
//	public void clicarBotaoPorXPath(String texto){
//		clicarBotao(By.xpath(texto));
//	}
//
//	public String obterValueElemento(String id) {
//		return getDriver().findElement(By.id(id)).getAttribute("value");
//	}
//
//	public String getElementByName(String name) {
//		return getDriver().findElement(By.name(name)).getAttribute("value");
//	}
//	
//	//getDriver().findElement(By.xpath("//td[5]/a"))
//	public String getElementByXPath(String texto) {
//		//return getDriver().findElement(By.xpath(texto)).getAttribute("value");
//		return getDriver().findElement(By.xpath(texto)).getText();
//	}
//
//
//	/********* Link ************/
//	
//	public void clicarLink(String link) {
//		getDriver().findElement(By.linkText(link)).click();
//	}
//	
//	/********* Textos ************/
//	
//	public String obterTexto(By by) {
//		return getDriver().findElement(by).getText();
//	}
//	
//	public String obterTexto(String id) {
//		return obterTexto(By.id(id));
//	}
//	
//	/********* Alerts ************/
//	
//	public String alertaObterTexto(){
//		Alert alert = getDriver().switchTo().alert();
//		return alert.getText();
//	}
//	
//	public String alertaObterTextoEAceita(){
//		Alert alert = getDriver().switchTo().alert();
//		String valor = alert.getText();
//		alert.accept();
//		return valor;
//		
//	}
//	
//	public String alertaObterTextoENega(){
//		Alert alert = getDriver().switchTo().alert();
//		String valor = alert.getText();
//		alert.dismiss();
//		return valor;
//		
//	}
//	
//	public void alertaEscrever(String valor) {
//		Alert alert = getDriver().switchTo().alert();
//		alert.sendKeys(valor);
//		alert.accept();
//	}
//	
//	/********* Frames e Janelas ************/
//	
//	public void entrarFrame(String id) {
//		getDriver().switchTo().frame(id);
//	}
//	
//	public void sairFrame(){
//		getDriver().switchTo().defaultContent();
//	}
//	
//	public void trocarJanela(String id) {
//		getDriver().switchTo().window(id);
//	}
//	
//	/************** JS *********************/
//	
//	public Object executarJS(String cmd, Object... param) {
//		JavascriptExecutor js = (JavascriptExecutor) getDriver();
//		return js.executeScript(cmd, param);
//	}
//	
//	/************** Tabela *********************/
//	
//	public WebElement obterCelula(String colunaBusca, String valor, String colunaBotao, String idTabela){
//		//procurar coluna do registro
//		WebElement tabela = getDriver().findElement(By.xpath("//*[@id='"+idTabela+"']"));
//		int idColuna = obterIndiceColuna(colunaBusca, tabela);
//		
//		//encontrar a linha do registro
//		int idLinha = obterIndiceLinha(valor, tabela, idColuna);
//		
//		//procurar coluna do botao
//		int idColunaBotao = obterIndiceColuna(colunaBotao, tabela);
//		
//		//clicar no botao da celula encontrada
//		WebElement celula = tabela.findElement(By.xpath(".//tr["+idLinha+"]/td["+idColunaBotao+"]"));
//		return celula;
//	}
//	
//	public void clicarBotaoTabela(String colunaBusca, String valor, String colunaBotao, String idTabela){
//		WebElement celula = obterCelula(colunaBusca, valor, colunaBotao, idTabela);
//		celula.findElement(By.xpath(".//input")).click();
//		
//	}
//
//	protected int obterIndiceLinha(String valor, WebElement tabela, int idColuna) {
//		List<WebElement> linhas = tabela.findElements(By.xpath("./tbody/tr/td["+idColuna+"]"));
//		int idLinha = -1;
//		for(int i = 0; i < linhas.size(); i++) {
//			if(linhas.get(i).getText().equals(valor)) {
//				idLinha = i+1;
//				break;
//			}
//		}
//		return idLinha;
//	}
//
//	protected int obterIndiceColuna(String coluna, WebElement tabela) {
//		List<WebElement> colunas = tabela.findElements(By.xpath(".//th"));
//		int idColuna = -1;
//		for(int i = 0; i < colunas.size(); i++) {
//			if(colunas.get(i).getText().equals(coluna)) {
//				idColuna = i+1;
//				break;
//			}
//		}
//		return idColuna;
//	}
//	public static String getMetod() {
//		String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
//		// methodName = Thread.currentThread().getStackTrace()[1].getMethodName(); //
//		// Pega o método atual
//		return methodName;
//	}

}
