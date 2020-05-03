package br.com.standardit.eguias;

import static br.com.standardit.core.DriverFactory.getDriver;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.sikuli.script.ImagePath;
import org.sikuli.script.Screen;
import org.slf4j.LoggerFactory;

import br.com.standardit.UtilAutomation;
import br.com.standardit.util;
import br.com.standardit.core.BasePage;
import br.com.standardit.ws.ecertidoes.ECertidoes;
import ch.qos.logback.classic.Logger;

public class CertidaoNegativaBA extends BasePage {

	static WebDriver driver;

	public static final Logger log = (Logger) LoggerFactory.getLogger(CertidaoNegativaBA.class);

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

		Properties prop = new Properties();
		InputStream input;

		String pesquisarCnpj = br.com.standardit.ws.ecertidoes.ECertidoes.cnpj;
		String inscricaoEstadual = br.com.standardit.ws.ecertidoes.ECertidoes.inscricao;
		String inscricaoEstadual2 = br.com.standardit.ws.ecertidoes.ECertidoes.inscricao;

		pesquisarCnpj = "123456789";
		inscricaoEstadual = "456456456";
		String dataIni = "01/01/2019";
		String dataFim = "31/01/2019";
		String dataRef = "01/2019";
		String qtdNotas = "2";
		String valorPrincipal = "100";
		String numNota = "100200300";
		String numNF = "400500600";
		String cenario = "1";
		try {
			input = new FileInputStream("config.properties");
			prop.load(input);

			ImagePath.add(prop.getProperty("imagefolder"));

			log.info("Acessar o site da sefaz para emissão de dae");
			driver.get(
					"https://sistemas.sefaz.ba.gov.br/sistemas/arasp/pagamento/modulos/dae/pagamento/dae_pagamento.aspx");
			log.info("titulo da pagina: " + driver.getTitle());
			util.sleep(5);

			log.info("Selecionar a opção 3(Código 1145) no campo icms - antecipação tributária");
			WebElement element = driver.findElement(By.id("PHConteudo_ddl_antecipacao_tributaria"));
			Select combo = new Select(element);
			combo.selectByIndex(3);
//			------------------------------------------------------------------------------------------------------------------------
			flegarOpcao(cenario);
			
			log.info("Inserir a inscrição estadual");
			WebElement textfield = (new WebDriverWait(driver, 10)).until(
					ExpectedConditions.presenceOfElementLocated(By.id("PHconteudoSemAjax_txt_num_inscricao_estad")));
			textfield.sendKeys("inscricaoEstadual");
			util.sleep(2);
//			-------------------------------------------------------------------------------------------------------------------------
			
			log.info("Inserir as datas de vencimento e pagamento");
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("document.getElementById('PHconteudoSemAjax_txt_dtc_vencimento').value = '" + dataIni
					+ "'; " + "document.getElementById('PHconteudoSemAjax_txt_dtc_max_pagamento').value = '" + dataFim
					+ "'; ");
			util.sleep(2);
			
			log.info("Inserir o valor principal");
			WebElement textfield1 = (new WebDriverWait(driver, 10))
					.until(ExpectedConditions.presenceOfElementLocated(By.id("PHconteudoSemAjax_txt_val_principal")));
			textfield1.sendKeys(valorPrincipal);
			util.sleep(2);
			
			log.info("Inserir o mês e ano de referência da dae");
			JavascriptExecutor js2 = (JavascriptExecutor) driver;
			js2.executeScript("document.getElementById('PHconteudoSemAjax_txt_mes_ano_referencia_6anos').value = '"
					+ dataRef + "'; ");
			util.sleep(2);
			
			log.info("Inserir o número da(s) nota(s) fiscal(is) até 15 notas");
			WebElement textfield3 = (new WebDriverWait(driver, 10))
					.until(ExpectedConditions.presenceOfElementLocated(By.id("PHconteudoSemAjax_txt_num_nota_fiscal")));
			textfield3.sendKeys(numNota);
			util.sleep(2);
			WebElement textfield4 = (new WebDriverWait(driver, 10)).until(
					ExpectedConditions.presenceOfElementLocated(By.id("PHconteudoSemAjax_txt_num_nota_fiscal2")));
			textfield4.sendKeys(numNF);
			util.sleep(2);

			log.info("Inserir a quantidade de notas");
			WebElement textfield5 = (new WebDriverWait(driver, 10))
					.until(ExpectedConditions.presenceOfElementLocated(By.id("PHconteudoSemAjax_txt_qtd_nota_fiscal")));
			textfield5.sendKeys(qtdNotas);
			util.sleep(2);
			
//			log.info("INSERIR INFORMAÇÕES COMPLEMENTARES(OPCIONAL)");
//						WebElement textfield6 = (new WebDriverWait(driver, 10))
//								.until(ExpectedConditions.presenceOfElementLocated(By.id("PHconteudoSemAjax_txt_des_informacoes_complementares")));
//						textfield6.sendKeys("TESTE");
			util.sleep(2);
			
			log.info("Clicar no botão para visualizar a dae");
			js2.executeScript(
					"document.getElementById('PHconteudoSemAjax_btn_visualizar').click(); console.log('funcionou'); ");

			log.info("Inserindo o cnpj");

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

	public static void flegarOpcao(String cenario) {
		if (cenario == "1") {
			log.info("Flegar a opção Emissão de DAE - Regime Normal - Comércio / Simples Nacional");
			WebElement radioButton1 = (new WebDriverWait(driver, 10))
					.until(ExpectedConditions.presenceOfElementLocated(By.id("PHConteudo_rb_dae_normal_1")));
			radioButton1.click();
			util.sleep(2);
		}
		if (cenario == "2") {
			log.info("Selecionar a opção 1 no campo Emissão de DAE - Prazos Especiais de Pagamentos");
			WebElement element1 = (new WebDriverWait(driver, 5))
  					.until(ExpectedConditions.presenceOfElementLocated(By.id("PHConteudo_ddl_campanha_receita")));
			Select combo1 = new Select(element1);
			combo1.selectByIndex(1);
		}
		if (cenario == "3") {
			log.info("Selecionar a opção 1(Código 2094) no campo icms - contribuinte não inscrito");
			WebElement element = driver.findElement(By.id("PHConteudo_ddl_contribuinte_nao_inscrito"));
			Select combo = new Select(element);
			combo.selectByIndex(1);
		}
		if (cenario == "4") {
			log.info("Selecionar a opção 1(Código 1880) no campo multas");
			WebElement element = driver.findElement(By.id("PHConteudo_ddl_multas"));
			Select combo = new Select(element);
			combo.selectByIndex(1);
		}
		
	}
	//

}
