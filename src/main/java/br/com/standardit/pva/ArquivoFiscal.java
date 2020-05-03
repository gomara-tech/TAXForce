package br.com.standardit.pva;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;

import br.com.standardit.util;

public class ArquivoFiscal {

	private String CNPJ;
	private String DT_INI;
	private String DT_FIM;
	private String Retificadora;
	
	public boolean ArquivoRetificacao(String filename) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line;
			line = br.readLine();
			util.log.info("[" + util.getMetod() + "] - Firstline is : " + line);
			br.close();
			br = null;
			
			String[] fields = line.split(Pattern.quote("|"));
			
			setRetificadora(fields[3]);
			
		} catch (IOException e) {
			util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean ExtrairDados(String filename) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line;
			line = br.readLine();
			util.log.info("[" + util.getMetod() + "] - Firstline is : " + line);
			br.close();
			br = null;
			
			String[] fields = line.split(Pattern.quote("|"));
			
			setDT_INI(fields[4]);
			setDT_FIM(fields[5]);
			setCNPJ(fields[7]);
			
		} catch (IOException e) {
			util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public String getCNPJ() {
		return CNPJ;
	}
	
	public void setCNPJ(String cNPJ) {
		CNPJ = cNPJ;
	}

	public String getDT_INI() {
		return DT_INI;
	}

	public void setDT_INI(String dT_INI) {
		DT_INI = dT_INI;
	}

	public String getDT_FIM() {
		return DT_FIM;
	}

	public void setDT_FIM(String dT_FIM) {
		DT_FIM = dT_FIM;
	}

	public String getRetificadora() {
		return Retificadora;
	}

	public void setRetificadora(String retificadora) {
		Retificadora = retificadora;
	}
}
