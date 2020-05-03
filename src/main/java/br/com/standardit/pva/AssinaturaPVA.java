package br.com.standardit.pva;

import org.sikuli.script.Key;
import org.sikuli.script.Screen;

import br.com.standardit.WaitResult;
import br.com.standardit.util;

public class AssinaturaPVA {
	Screen s;
	int tempoMaximo = 0;
	UtilPVA utilPVA;
	String processandoPath;
	TransmissaoPVA transmissaoPVA;

	public AssinaturaPVA(Screen s, int tempoMaximo, String processandoPath, Boolean salvarReciboEntrega) {
		this.s = s;
		this.tempoMaximo = tempoMaximo;
		this.processandoPath = processandoPath;

		utilPVA = new UtilPVA(s);
		transmissaoPVA = new TransmissaoPVA(s, tempoMaximo, processandoPath, salvarReciboEntrega);
	}

	public boolean AssinarArquivo() {
		utilPVA.FecharValidacao();
		util.log.info("[" + util.getMetod() + "] - Assinando arquivo");
		if (!utilPVA.AtivarPVA())
			return false;

		s.type("s", Key.CTRL);
		util.sleep(1);// Adicionado 1s para carregamento da tela
		s.type(Key.DOWN);
		util.sleep(1);// Adicionado 1s para carregamento da tela
		s.type(Key.ENTER);
		util.sleep(5);

		WaitResult waitResult = utilPVA.utilAut.WaitFor(3, new String[] { "nenhumaCertificado" });
		if (waitResult.region != null) {
			util.log.error("[" + util.getMetod() + "] - Processamento com erro: [Nenhum_Certificado_Encontrado]");
			util.PrintErro(processandoPath + "//Erro[Nenhum_Certificado_Encontrado].png");
			s.type(Key.ESC);

			waitResult = utilPVA.utilAut.WaitFor(3, new String[] { "Cancelar" });
			if (waitResult.region != null) {
				waitResult.region.click();
				return false;
			}
		}

		s.type(Key.DOWN);
		waitResult = utilPVA.utilAut.WaitFor(3, new String[] { "Assinar_Botao", "Assinar_Botao2" });
		if (waitResult.region == null) {
			util.log.error("[" + util.getMetod() + "] - Processamento com erro: [Clicando_Botao_Assinar]");
			util.PrintErro(processandoPath + "//Erro[Clicando_Botao_Assinar].png");
			return false;
		}

		waitResult.region.click();

		ResultadoAssinatura result = AguardarAssinatura(tempoMaximo);

		return AnalisarAssinatura(result);
	}

	private ResultadoAssinatura AguardarAssinatura(int seconds) {
		util.log.info("[" + util.getMetod() + "] - Aguardanto Assinatura...");
		WaitResult waitResult = utilPVA.utilAut.WaitFor(seconds,
				new String[] { "Assinar_Resultado", /* "Assinar_Crypto", */"CertificadoInvalido" });

		if (waitResult.region == null)
			return ResultadoAssinatura.SEMRESULTADO;
		else {
			if (waitResult.imageFound == "Assinar_Resultado")
				return ResultadoAssinatura.ASSINADOCOMEXITO;
			else if (waitResult.imageFound == "CertificadoInvalido")
				return ResultadoAssinatura.CERTIFICADOINVALIDO;
			/*
			 * else if (waitResult.imageFound == "Assinar_Crypto") { s.type(Key.ENTER);
			 * return AguardarAssinatura(seconds); }
			 */
			else
				return ResultadoAssinatura.SEMRESULTADO;
		}
	}

	private boolean AnalisarAssinatura(ResultadoAssinatura result) {
		util.log.info("[" + util.getMetod() + "] - Verificando resultado da geracao...");
		switch (result) {
		case ASSINADOCOMEXITO:
			if (SalvarRelatorio()) {
				util.sleep(2);
				s.type(Key.F4, Key.ALT);

				return transmissaoPVA.TransmitirArquivo();
			}
			util.log.error("[" + util.getMetod() + "] - Transmitindo Arquivo");
			util.PrintErro(processandoPath + "//Erro[Transmitindo_Arquivo].png");
			return false;
		case CERTIFICADOINVALIDO:
			util.log.error("[" + util.getMetod() + "] - Certificado_Invalido");
			util.PrintErro(processandoPath + "//Error[Certificado_Invalido].png");
			s.type(Key.ESC);
			return false;
		default:
			util.log.error("[" + util.getMetod() + "] - Saida pelo case [default]");
			util.PrintErro(processandoPath + "//Error.png");
			return false;
		}

	}

	public boolean SalvarRelatorio() {
		util.log.info("[" + util.getMetod() + "] - Salvando o resultado...");
		WaitResult waitResult;
		// region = WaitFor(3, new String[] { "Relatorio_Erro", "Relatorio_Erro2" });
		// if (region != null)
		// region.click();

		waitResult = utilPVA.utilAut.WaitFor(5, new String[] { "Relatorio_Botao_Salvar" });
		if (waitResult.region == null)
			return false;
		waitResult.region.click();

		s.type(processandoPath + "\\Resultado_Assinatura.pdf");
		util.sleep(1);
		s.type(Key.ENTER);

		return true;
	}
}
