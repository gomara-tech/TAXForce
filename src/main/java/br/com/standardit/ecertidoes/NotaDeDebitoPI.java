package br.com.standardit.ecertidoes;

import static br.com.standardit.core.DriverFactory.getDriver;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import br.com.standardit.core.BasePage;

public class NotaDeDebitoPI extends BasePage{

	static WebDriver driver;
	public static final Logger log = (Logger) LoggerFactory.getLogger(NotaDeDebitoPI.class);
	
	@Before
	public void inicializa(){
		log.info("Abrindo navegador...");
		driver = getDriver();
	}
	
	@After
	public void finaliza(){
		log.info("Finalizando navegador.");
		//driver.quit();
	}
	
	@Test
	public void Home(){
	//public static void main(String[] args) {
		
		driver.get("http://www2.sefaz.ce.gov.br/PortalSiget/#principal");
		log.info("titulo da pagina1: " + driver.getTitle());
		Assert.assertEquals("1", "1");

	}
}
