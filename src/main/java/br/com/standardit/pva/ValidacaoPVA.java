package br.com.standardit.pva;

import org.sikuli.script.Key;
import org.sikuli.script.Screen;

import br.com.standardit.WaitResult;
import br.com.standardit.util;

public class ValidacaoPVA {
	Screen s;
	int tempoMaximo = 0;
	String arquivoImportar;
	String processandoPath;
	UtilPVA utilPVA;
	// ExportarArquivoPVA exportarArquivoPVA;
	AssinaturaPVA assinaturaPVA;
	AtualizarTabelasPVA atualizarTabelasPVA;

	public ValidacaoPVA(Screen s, int tempoMaximoValidacao, String processandoPath, String arquivoImportar,
			Boolean salvarReciboEntrega) {
		this.s = s;
		this.tempoMaximo = tempoMaximoValidacao;
		this.processandoPath = processandoPath;
		this.arquivoImportar = arquivoImportar;

		utilPVA = new UtilPVA(s);
		// exportarArquivoPVA = new ExportarArquivoPVA(s,
		// arquivoImportar.toLowerCase().replace(".txt", "_Exportado.txt"), tempoMaximo,
		// processandoPath);
		assinaturaPVA = new AssinaturaPVA(s, tempoMaximo, processandoPath, salvarReciboEntrega);

		atualizarTabelasPVA = new AtualizarTabelasPVA(s);
	}

	public boolean ExecutarValidacao() {
		util.log.info("[" + util.getMetod() + "] - Executando a validação...");
		WaitResult waitResult = utilPVA.utilAut.WaitFor(3,
				new String[] { "Validacao_Executar", "Validacao_Executar2" });
		if (waitResult.region == null)
			return false;
		waitResult.region.click();

		ResultadoValidacao resultValidacao = AguardarValidacao(tempoMaximo);
		return AnalisarValidacao(resultValidacao, false);
	}

	public ResultadoValidacao AguardarValidacao(int seconds) {
		util.log.info("[" + util.getMetod() + "] - Aguardanto Validacao...");
		WaitResult waitResult = utilPVA.utilAut.WaitFor(seconds,
				new String[] { "AtualizarTabelas_Mensagem", "AtualizarTabelas_Mensagem2", "AtualizarTabelas_Fim",
						"Validacao_Mensagem_Ignorar", "Validacao_Fim_Com_Exito", "Validacao_Fim_Com_Aviso",
						"Validacao_Fim_Com_Erros", "Validacao_Fim_Com_Erros_Aviso", "Erro" });

		if (waitResult.region == null)
			return ResultadoValidacao.SEMRESULTADO;
		else {

			if (waitResult.imageFound == "Validacao_Fim_Com_Exito")
				return ResultadoValidacao.VALIDACAOCOMEXITO;
			else if (waitResult.imageFound == "Validacao_Fim_Com_Aviso")
				return ResultadoValidacao.VALIDACAOCOMAVISO;
			else if (waitResult.imageFound == "Validacao_Fim_Com_Erros"
					|| waitResult.imageFound == "Validacao_Fim_Com_Erros_Aviso" || waitResult.imageFound == "Erro")
				return ResultadoValidacao.VALIDACAOCOMERRO;
			else if (waitResult.imageFound == "AtualizarTabelas_Mensagem"
					|| waitResult.imageFound == "AtualizarTabelas_Mensagem2") {

				if (utilPVA.utilAut.Click("botaoOk")) {
					util.log.info("[" + util.getMetod() + "] - Cliquei OK para Atualizar...");
					util.sleep(2);

					waitResult = utilPVA.utilAut.WaitFor(seconds, new String[] { "AtualizarTabelas_Fim", "Erro" });
					if (waitResult.region == null)
						return ResultadoValidacao.SEMRESULTADO;
					else if (waitResult.imageFound == "AtualizarTabelas_Fim") {
						util.log.info("[" + util.getMetod() + "] - Fim da atualização das tabelas...");
						s.type(Key.ENTER);
						util.sleep(2);
						return AguardarValidacao(seconds);
					} else
						return ResultadoValidacao.SEMRESULTADO;
				} else
					return ResultadoValidacao.SEMRESULTADO;
			} else if (waitResult.imageFound == "AtualizarTabelas_Fim") {
				util.log.info("[" + util.getMetod() + "] - Fim da atualização das tabelas...");
				s.type(Key.ENTER);
				util.sleep(2);
				return AguardarValidacao(seconds);
			} else if (waitResult.imageFound == "Validacao_Mensagem_Ignorar") {
				s.type(Key.ENTER);
				return AguardarValidacao(seconds);
			} else
				return ResultadoValidacao.SEMRESULTADO;
		}
	}

	public boolean AnalisarValidacao(ResultadoValidacao resultValidacao, boolean primeiravez) {
		util.log.info("[" + util.getMetod() + "] - Verificando resultado da validacao...");
		switch (resultValidacao) {
		case VALIDACAOCOMEXITO:
			/*
			 * s.type(Key.ENTER);
			 * 
			 * if (primeiravez) return ExecutarValidacao(); else if (SalvarRelatorio(false))
			 * return exportarArquivoPVA.GerarArquivoEntrega();
			 * 
			 * return false;
			 */
		case VALIDACAOCOMAVISO:
			s.type(Key.ENTER);

			/*
			 * NÃO VAI MAIS VALIDAR 2X NEM GERAR ARQUIVO PARA ENTREGA if (primeiravez)
			 * return ExecutarValidacao(); else if (SalvarRelatorio(true)) return
			 * exportarArquivoPVA.GerarArquivoEntrega();
			 */

			if (SalvarRelatorio(resultValidacao == ResultadoValidacao.VALIDACAOCOMAVISO))
				return assinaturaPVA.AssinarArquivo();
			util.log.error("[" + util.getMetod() + "] - Processamento com erro: [Validacao com aviso]");
			util.PrintErro(processandoPath + "//Erro[Validando_Resultado].png");
			return false;
		case VALIDACAOCOMERRO:
			s.type(Key.ENTER);
			SalvarRelatorio(true);
			return false;
		case ATUALIZARTABELAS:
			if (utilPVA.utilAut.Click("botaoOk"))
				if (atualizarTabelasPVA.AguardarAtualizarTabelas()) {
					return AnalisarValidacao(AguardarValidacao(tempoMaximo), primeiravez);
				}

			return false;
		default:
			util.log.error("[" + util.getMetod() + "] - Saida pelo case [default]");
			util.PrintErro(processandoPath + "//Error.png");
			return false;
		}
	}

	public boolean SalvarRelatorio(boolean exibirErros) {
		WaitResult waitResult;
		util.log.info("[" + util.getMetod() + "] - Salvando Relatorio de Validação...");
		if (exibirErros) {
			waitResult = utilPVA.utilAut.WaitFor(3, new String[] { "Relatorio_Erro", "Relatorio_Erro2" });
			if (waitResult.region != null)
				waitResult.region.click();

			waitResult = utilPVA.utilAut.WaitFor(3, new String[] { "Relatorio_Advertencia", "Relatorio_Advertencia2" });
			if (waitResult.region != null)
				waitResult.region.click();

			waitResult = utilPVA.utilAut.WaitFor(3,
					new String[] { "Relatorio_Botao_Exibir", "Relatorio_Botao_Exibir2" });
			if (waitResult.region == null)
				return false;
			waitResult.region.click();

			utilPVA.waitAguardeFinish();
		}

		waitResult = utilPVA.utilAut.WaitFor(3, new String[] { "Relatorio_Botao_Salvar" });
		if (waitResult.region == null)
			return false;
		waitResult.region.click();

		// Inserindo Tratamento de erro apos clique do SALVAR
		waitResult = utilPVA.utilAut.WaitFor(1, new String[] { "Error_print_report" });
		if (waitResult.region != null) {
			int cont = 1;
			while (waitResult.region != null && cont <= 3) {
				util.log.info(
						"[" + util.getMetod() + "] - Processando Tratativa para o Erro_print_report: " + cont + "x3");

				s.type(Key.ENTER);

				waitResult = utilPVA.utilAut.WaitFor(1, new String[] { "Relatorio_Botao_Salvar" });
				if (waitResult.region == null)
					return false;
				waitResult.region.click();

				waitResult = utilPVA.utilAut.WaitFor(1, new String[] { "Error_print_report" });
				cont++;
			}
		}

		String file = processandoPath + "\\Resultado_Validacao.pdf";
		s.type(file);
		s.type(Key.ENTER);

		SaveProgress.isFileReady(file);
		return true;
	}

}
