package br.com.standardit.pva;

import org.sikuli.script.Key;
import org.sikuli.script.Screen;

import br.com.standardit.WaitResult;
import br.com.standardit.util;

public class TransmissaoPVA {
	Screen s;
	int tempoMaximo = 0;
	UtilPVA utilPVA;
	String processandoPath;
	Boolean salvarReciboEntrega;

	public TransmissaoPVA(Screen s, int tempoMaximo, String processandoPath, Boolean salvarReciboEntrega) {
		this.s = s;
		this.tempoMaximo = tempoMaximo;
		this.processandoPath = processandoPath;
		this.salvarReciboEntrega = salvarReciboEntrega;
		utilPVA = new UtilPVA(s);
	}

	public boolean TransmitirArquivo() {
		utilPVA.FecharValidacao();

		if (!utilPVA.HabilitarTransmissao(processandoPath))
			return true;

		util.log.info("[" + util.getMetod() + "] - Transmitindo arquivo");
		if (!utilPVA.AtivarPVA())
			return false;

		s.type("t", Key.CTRL);
		s.type(Key.DOWN);
		s.type(Key.ENTER);

		ResultadoTransmissao result = AguardarTransmissao(tempoMaximo);

		// Inserindo Tratamento de erro para erro na Transmissao
		WaitResult waitResult = utilPVA.utilAut.WaitFor(1, new String[] { "Erro_Arquivo_Nao_Foi_Transmitido" });
		if (waitResult.region != null) {
			int cont = 1;
			while (waitResult.region != null && cont <= 3) {
				util.log.info("[" + util.getMetod()
						+ "] - Processando Tratativa para o Erro_Arquivo_Nao_Foi_Transmitido: " + cont + "x3");

				s.type(Key.ENTER);

				util.log.info("[" + util.getMetod() + "] - Transmitindo arquivo novamente..." + cont + "x3");
				s.type("t", Key.CTRL);
				s.type(Key.DOWN);
				s.type(Key.ENTER);

				result = AguardarTransmissao(tempoMaximo);

				waitResult = utilPVA.utilAut.WaitFor(1, new String[] { "Erro_Arquivo_Nao_Foi_Transmitido" });
				cont++;
			}
		}

		return AnalisarTransmissao(result);
	}

	private ResultadoTransmissao AguardarTransmissao(int seconds) {
		util.log.info("[" + util.getMetod() + "] - Aguardanto Transmissão...");
		WaitResult waitResult = utilPVA.utilAut.WaitFor(seconds,
				new String[] { "Transmissao_MensagemSucesso", "Transmissao_MensagemSucesso2", "Erro" });

		if (waitResult.region == null)
			return ResultadoTransmissao.SEMRESULTADO;
		else {
			if (waitResult.imageFound == "Transmissao_MensagemSucesso"
					|| waitResult.imageFound == "Transmissao_MensagemSucesso2") {
				// Gambiarra pq na falha da transmissão está sendo mostrada mensagem de sucesso
				// quando acontecer isso, tenta achar mensagem de erro, se não achar continua
				waitResult = utilPVA.utilAut.WaitFor(10, new String[] { "Erro" });
				if (waitResult.imageFound == "Erro")
					return ResultadoTransmissao.ERRO;
				else
					return ResultadoTransmissao.TRANSMITIDOCOMEXITO;
			} else if (waitResult.imageFound == "Erro")
				return ResultadoTransmissao.ERRO;
			else
				return ResultadoTransmissao.SEMRESULTADO;
		}
	}

	private boolean AnalisarTransmissao(ResultadoTransmissao result) {
		util.log.info("[" + util.getMetod() + "] - Verificando resultado da transmissão...");
		switch (result) {
		case TRANSMITIDOCOMEXITO:
			s.type(Key.ENTER);

			if (this.salvarReciboEntrega) {
				if (SalvarRelatorio()) {
					util.sleep(2);
					s.type(Key.F4, Key.ALT);
					return true;
				}
				util.log.error("[" + util.getMetod() + "] - Processamento com erro: [Salvando_Recibo_Entrega]");
				util.PrintErro(processandoPath + "//Error[Salvando_Recibo_Entrega ].png");
				return false;
			}
			return true;
		case ERRO:
			util.log.error("[" + util.getMetod() + "] - Saida pelo case [ERRO]");
			util.PrintErro(processandoPath + "//Error.png");
			s.type(Key.ENTER);
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

		waitResult = utilPVA.utilAut.WaitFor(240, new String[] { "Relatorio_Botao_Salvar" });
		if (waitResult.region == null)
			return false;
		waitResult.region.click();

		s.type(processandoPath + "\\Resultado_Transmissao.pdf");
		util.sleep(1);
		s.type(Key.ENTER);

		return true;
	}
}
