package br.com.standardit.core;

public class Propriedades {
	
	public static boolean FECHAR_BROWSER = false;
	
	//public static Browsers browser = Browsers.IE;
	public static Browsers browser = Browsers.CHROME;
	//public static Browsers browser = Browsers.FIREFOX;
	
	public static TipoExecucao TIPO_EXECUCAO = TipoExecucao.LOCAL;
	
	public static String NOME_CONTA_ALTERADA = "Conta Alterada " + System.nanoTime();  
	
	public enum Browsers {
		CHROME,
		FIREFOX,
		IE
	}
	public enum TipoExecucao {
		LOCAL,
		GRID,
		NUVEM
	}
}
