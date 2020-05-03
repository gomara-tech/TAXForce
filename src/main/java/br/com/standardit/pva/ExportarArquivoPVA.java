package br.com.standardit.pva;

import org.sikuli.script.Key;
import org.sikuli.script.Screen;

import br.com.standardit.WaitResult;
import br.com.standardit.util;

public class ExportarArquivoPVA {
	Screen s;
	int tempoMaximo = 0;
	String arquivoExportar;
	String processandoPath;

	UtilPVA utilPVA;
	AssinaturaPVA assinaturaPVA;

	public ExportarArquivoPVA(Screen s, String arquivoExportar, int tempoMaximo, String processandoPath,
			Boolean salvarReciboEntrega) {
		this.s = s;
		this.arquivoExportar = arquivoExportar;
		this.tempoMaximo = tempoMaximo;
		this.processandoPath = processandoPath;

		utilPVA = new UtilPVA(s);
		assinaturaPVA = new AssinaturaPVA(s, tempoMaximo, processandoPath, salvarReciboEntrega);
	}

	public boolean GerarArquivoEntrega() {
		utilPVA.FecharValidacao();
		util.log.info("[" + util.getMetod() + "] - Gerando " + arquivoExportar);
		if (!utilPVA.AtivarPVA())
			return false;

		s.type("g", Key.CTRL);
		s.type(Key.DOWN);
		s.type(Key.ENTER);

		WaitResult waitResult = utilPVA.utilAut.WaitFor(3, new String[] { "GerarEntrega_Salvar" });
		if (waitResult.region == null)
			return false;

		s.type(arquivoExportar);
		s.type(Key.ENTER);

		ResultadoGeracao result = AguardarGeracao(tempoMaximo);

		return AnalisarGeracao(result);
	}

	public ResultadoGeracao AguardarGeracao(int seconds) {
		util.log.info("[" + util.getMetod() + "] - Aguardanto Geracao...");
		WaitResult waitResult = utilPVA.utilAut.WaitFor(seconds, new String[] { "GerarEntrega_Sucesso" });

		if (waitResult.region == null)
			return ResultadoGeracao.SEMRESULTADO;
		else {
			if (waitResult.imageFound == "GerarEntrega_Sucesso")
				return ResultadoGeracao.GERADOCOMEXITO;
			else
				return ResultadoGeracao.SEMRESULTADO;
		}
	}

	public boolean AnalisarGeracao(ResultadoGeracao result) {
		util.log.info("[" + util.getMetod() + "] - Verificando resultado da geracao...");
		switch (result) {
		case GERADOCOMEXITO:
			s.type(Key.ENTER);

			return assinaturaPVA.AssinarArquivo();
		default:
			util.log.error("[" + util.getMetod() + "] - Saida pelo Case [default]");
			util.PrintErro(processandoPath + "//Erro[Analisando_Geracao].png");
			return false;
		}

	}
}
