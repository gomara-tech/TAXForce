package br.com.standardit.pva;

import org.sikuli.script.Key;
import org.sikuli.script.Screen;

import br.com.standardit.WaitResult;
import br.com.standardit.util;

public class ImportacaoPVA {
	Screen s;
	int tempoMaximoImportacao = 0;
	String arquivoImportar;
	UtilPVA utilPVA;
	ValidacaoPVA validacaoPVA;
	String processandoPath;
	AtualizarTabelasPVA atualizarTabelasPVA;
	Boolean salvarReciboEntrega;

	public ImportacaoPVA(Screen s, int tempoMaximoImportacao, String processandoPath, Boolean salvarReciboEntrega) {
		this.s = s;
		this.tempoMaximoImportacao = tempoMaximoImportacao;
		this.processandoPath = processandoPath;
		this.salvarReciboEntrega = salvarReciboEntrega;

		utilPVA = new UtilPVA(s);
		atualizarTabelasPVA = new AtualizarTabelasPVA(s);
	}

	public boolean Importar(String arquivoImportar) {
		this.arquivoImportar = arquivoImportar;
		validacaoPVA = new ValidacaoPVA(s, tempoMaximoImportacao, processandoPath, arquivoImportar,
				salvarReciboEntrega);

		util.log.info("[" + util.getMetod() + "] - Importando " + arquivoImportar);
		if (!utilPVA.AtivarPVA())
			return false;

		s.type("i", Key.CTRL);
		s.type(arquivoImportar);
		s.type(Key.ENTER);

		ResultadoImportacao resultImportacao = AguardarImportacao(tempoMaximoImportacao);

		return AnalisarImportacao(resultImportacao);

	}

	public ResultadoImportacao AguardarImportacao(int seconds) {
		util.log.info("[" + util.getMetod() + "] - Aguardanto Importacao...");
		WaitResult waitResult = utilPVA.utilAut.WaitFor(seconds,
				new String[] { "AvisoFimImportacaoExito", "AvisoEscrituracaoAssinada", "AtualizarTabelas_Mensagem",
						"AtualizarTabelas_Mensagem2", "Importacao_PeriodoExistente", "Erro" });

		if (waitResult.region == null)
			return ResultadoImportacao.SEMRESULTADO;
		else {
			if (waitResult.imageFound == "AvisoFimImportacaoExito")
				return ResultadoImportacao.IMPORTADOCOMEXITO;
			else if (waitResult.imageFound == "AvisoEscrituracaoAssinada")
				return ResultadoImportacao.IMPORTADOASSINADOSOMENTELEITURA;
			else if (waitResult.imageFound == "AtualizarTabelas_Mensagem"
					|| waitResult.imageFound == "AtualizarTabelas_Mensagem2")
				return ResultadoImportacao.ATUALIZARTABELAS;
			else if (waitResult.imageFound == "Importacao_PeriodoExistente")
				return ResultadoImportacao.PERIODOEXISTENTE;
			else if (waitResult.imageFound == "Erro")
				return ResultadoImportacao.ERRO;
			else
				return ResultadoImportacao.SEMRESULTADO;
		}
	}

	public boolean AnalisarImportacao(ResultadoImportacao resultImportacao) {
		util.log.info("[" + util.getMetod() + "] - Verificando resultado da importacao...");
		switch (resultImportacao) {
		case IMPORTADOCOMEXITO:
			s.type(Key.ENTER);
			ResultadoValidacao resultValidacao = validacaoPVA.AguardarValidacao(tempoMaximoImportacao);
			return validacaoPVA.AnalisarValidacao(resultValidacao, true);
		case IMPORTADOASSINADOSOMENTELEITURA:
			util.log.error("[" + util.getMetod() + "] - Processamento com erro: [Importado_Assinado_Somente_Leitura]");
			util.PrintErro(processandoPath + "//Erro[Importado_Assinado_Somente_Leitura].png");
			s.type(Key.ENTER);
			return false;
		case ATUALIZARTABELAS:
			if (utilPVA.utilAut.Click("botaoOk"))
				if (atualizarTabelasPVA.AguardarAtualizarTabelas()) {
					ResultadoImportacao resultImp = AguardarImportacao(tempoMaximoImportacao);
					return AnalisarImportacao(resultImp);
				}
			util.log.error("[" + util.getMetod() + "] - Processamento com erro: [Atualizando_Tabelas]");
			util.PrintErro(processandoPath + "//Error[Atualizando_Tabelas].png");
			return false;
		case PERIODOEXISTENTE:
			s.type(Key.ENTER);
			ResultadoImportacao result = AguardarImportacao(tempoMaximoImportacao);
			return AnalisarImportacao(result);
		case ERRO:
			util.log.error("[" + util.getMetod() + "] - Saida pelo case [ERRO]");
			util.PrintErro(processandoPath + "//Error.png");
			s.type(Key.ESC);
			return false;
		default:
			util.log.error("[" + util.getMetod() + "] - Saida pelo case [defalt]");
			util.PrintErro(processandoPath + "//Error.png");
			return false;
		}

	}
}
