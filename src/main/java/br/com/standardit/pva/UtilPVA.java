package br.com.standardit.pva;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.sikuli.script.FindFailed;
import org.sikuli.script.Key;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;

import br.com.standardit.UtilAutomation;
import br.com.standardit.WaitResult;
import br.com.standardit.util;

public class UtilPVA {
	Screen s = new Screen();
	public UtilAutomation utilAut;

	public UtilPVA(Screen s) {
		this.s = s;
		this.utilAut = new UtilAutomation(s);
	}

	public void waitAguardeFinish() {
		try {
			Region aguarde = null;
			do {
				aguarde = s.wait("aguarde.png");
				util.sleep(2);
			} while (aguarde != null);
		} catch (FindFailed e) {
			util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
		}
	}

	public boolean existeOutraInstancia() {
		try {
			Region outraInstancia = s.find("outrainstanciaexistente.png");
			if (outraInstancia != null) {
				s.type(Key.ENTER);
				return true;
			}
		} catch (FindFailed e) {
			util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
		}
		return false;
	}

	public boolean AtivarPVA() {
		WaitResult waitResult = utilAut.WaitFor(3,
				new String[] { "PVA_Titulo", "PVA_Titulo2", "PVAContribuicoes_Titulo" });
		if (waitResult.region == null)
			return false;
		waitResult.region.click();
		return true;
	}

	public boolean FecharValidacao() {
		if (!AtivarPVA())
			return false;

		s.type("f", Key.CTRL);

		return true;
	}

//----------------------------------------------------------------------------------------
	public boolean HabilitarTransmissao(String folderString) {
		Properties prop = new Properties();
		InputStream input;
		String transmitirArquivo = "";

		try {
			input = new FileInputStream("config.properties");
			prop.load(input);
	
			final File folder = new File(folderString);
			for (final File fileEntry : folder.listFiles()) {
				util.log.info("[" + util.getMetod() + "] - Nome do arquivo: " + fileEntry.getName());
				if (fileEntry.getName().endsWith(".txt") || fileEntry.getName().endsWith(".TXT")) {
					if (fileEntry.getName().contains("GIA_RS") || fileEntry.getName().contains("gia_rs"))
						return false;
				}
			}

			transmitirArquivo = prop.getProperty("transmitirArquivo");
			
			if (!transmitirArquivo.equals("true")) {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

}
