package br.com.standardit.gia.ma;

import org.sikuli.script.Key;
import org.sikuli.script.Screen;

import br.com.standardit.Ted;
import br.com.standardit.UtilAutomation;
import br.com.standardit.WaitResult;
import br.com.standardit.util;

public class UtilGIAMA {

	Screen s = new Screen();
	public UtilAutomation utilAut;
	String processandoPath;
	public Ted ted;
	String watchPath;

	public UtilGIAMA(Screen s, String processandoPath, String watchPath) {
		this.s = s;
		this.utilAut = new UtilAutomation(s);
		this.processandoPath = processandoPath;
		this.ted = new Ted(s, processandoPath);
		this.watchPath = watchPath;
	}

	public boolean AtivarPrograma() {
		WaitResult waitResult = utilAut.WaitFor(3, new String[] { "GIA_MA\\Titulo" });
		if (waitResult.region == null) {
			util.log.error("[" + util.getMetod() + "] - Processamento com erro: [Carregando_APP_GIA-MA]");
			util.PrintErro(watchPath + "\\Erro[Carregando_APP_GIA-MA].png");
			return false;
		}
		waitResult.region.click();
		return true;
	}

	public boolean ClicarCancelar() {
		WaitResult waitResult = utilAut.WaitFor(3, new String[] { "GIA_MA\\Cancelar" });
		if (waitResult.region == null)
			return false;
		waitResult.region.click();
		return true;
	}

	public boolean Encerrar() {
		WaitResult waitResult = utilAut.WaitFor(3, new String[] { "GIA_MA\\Encerrar" });
		if (waitResult.region == null)
			return false;
		waitResult.region.click();
		return true;
	}

	public void ForcarSaida() {
		s.type(Key.F4, Key.ALT);
	}

	public boolean MenuImportar() {
		WaitResult waitResult = utilAut.WaitFor(3, new String[] { "GIA_MA\\MenuImportar" });
		if (waitResult.region == null)
			return false;
		waitResult.region.click();

		waitResult = utilAut.WaitFor(3, new String[] { "GIA_MA\\ArquivoSintegra" });
		if (waitResult.region == null)
			return false;
		waitResult.region.click();

		return true;
	}

	public boolean ImportarArquivo() {
		s.type(Key.TAB);
		s.type(Key.DOWN);
		s.type("I", Key.ALT);
		s.type(Key.ENTER);

		WaitResult waitResult = utilAut.WaitFor(30,
				new String[] { "GIA_MA\\ArquivoImportadoComSucesso", "GIA_MA\\CriticasImportacao" });

		if (waitResult.region == null)
			return false;
		else {

			if (waitResult.imageFound == "GIA_MA\\ArquivoImportadoComSucesso") {
				s.type(Key.ENTER);
				s.type("C", Key.ALT);
			} else {
				return false;
			}
		}

		return true;
	}

	public boolean SelecionarArquivo() {

		WaitResult waitResult = utilAut.WaitFor(30, new String[] { "GIA_MA\\AbrirDeclaracao" });
		if (waitResult.region == null)
			return false;
		waitResult.region.click();

		waitResult = utilAut.WaitFor(30, new String[] { "GIA_MA\\Declaracao" });
		if (waitResult.region == null)
			return false;
		waitResult.region.click();

		waitResult = utilAut.WaitFor(30, new String[] { "GIA_MA\\GridDeclaracao", "GIA_MA\\SelecioneContribuinte" });
		if (waitResult.region == null)
			return false;

		if (waitResult.imageFound == "GIA_MA\\SelecioneContribuinte") {
			s.type(Key.ENTER);
			return false;
		}

		waitResult.region.click();
		s.type(Key.DOWN);

		return true;
	}

	public boolean ExcluirArquivo() {
		if (this.SelecionarArquivo()) {
			WaitResult waitResult = utilAut.WaitFor(30, new String[] { "GIA_MA\\ExcluirDeclaracao" });
			if (waitResult.region == null)
				return false;
			waitResult.region.click();

			waitResult = utilAut.WaitFor(3, new String[] { "GIA_MA\\InformePeriodoValido" });
			if (waitResult.region == null)
				s.type(Key.ENTER);

			s.type(Key.ENTER);
			// s.type("C", Key.ALT);
			// s.type("C", Key.ALT);

			this.ClicarCancelar();
		}
		util.sleep(2);
		this.ClicarCancelar();
		return true;
	}

	public boolean ExcluirContribuinte() {
		WaitResult waitResult = utilAut.WaitFor(30, new String[] { "GIA_MA\\AbrirDeclaracao" });
		if (waitResult.region == null)
			return false;
		waitResult.region.click();

		waitResult = utilAut.WaitFor(30, new String[] { "GIA_MA\\BotaoNovoContribuinte" });
		if (waitResult.region == null)
			return false;
		waitResult.region.click();

		waitResult = utilAut.WaitFor(3, new String[] { "GIA_MA\\GridContribuinte" });
		if (waitResult.region == null)
			return false;
		waitResult.region.click();

		s.type(Key.DOWN);

		waitResult = utilAut.WaitFor(3, new String[] { "GIA_MA\\BotaoExcluirContribuinte" });
		if (waitResult.region == null)
			return false;
		waitResult.region.click();

		util.sleep(2);
		s.type(Key.ENTER);

		util.sleep(2);
		s.type(Key.ENTER);

		waitResult = utilAut.WaitFor(3, new String[] { "GIA_MA\\Sair" });
		if (waitResult.region == null)
			return false;
		waitResult.region.click();

		this.ClicarCancelar();

		util.sleep(2);
		this.ClicarCancelar();
		return true;
	}

	public boolean GerarApuracaoImposto() {
		if (this.SelecionarArquivo()) {
			WaitResult waitResult = utilAut.WaitFor(30, new String[] { "GIA_MA\\Declaracao2" });
			if (waitResult.region == null)
				return false;
			waitResult.region.click();

			waitResult = utilAut.WaitFor(3, new String[] { "GIA_MA\\ApuracaoImposto" });
			if (waitResult.region == null)
				return false;
			waitResult.region.click();

			waitResult = utilAut.WaitFor(3, new String[] { "GIA_MA\\GravarApuracaoImposto" });
			if (waitResult.region == null)
				return false;
			waitResult.region.click();

			waitResult = utilAut.WaitFor(3, new String[] { "GIA_MA\\Sair" });
			if (waitResult.region == null)
				return false;
			waitResult.region.click();

			if (utilAut.Existe("GIA_MA\\Aviso", 10)) {
				return false;
			}

			this.ClicarCancelar();
		}
		return true;
	}

	public boolean GerarArquivo() {
		if (this.SelecionarArquivo()) {

			WaitResult waitResult = utilAut.WaitFor(3, new String[] { "GIA_MA\\GerarArquivo" });
			if (waitResult.region == null)
				return false;
			waitResult.region.click();

			waitResult = utilAut.WaitFor(3, new String[] { "GIA_MA\\PrimeiraDIEF" });
			if (waitResult.region == null)
				return false;

			s.type(Key.ENTER);

			waitResult = utilAut.WaitFor(3, new String[] { "GIA_MA\\GeracaoArquivoDIEF" });
			if (waitResult.region == null)
				return false;

			s.type("G", Key.ALT);

			waitResult = utilAut.WaitFor(3, new String[] { "GIA_MA\\Informacao" });
			if (waitResult.region == null)
				return false;

			s.type(Key.ENTER);

			waitResult = utilAut.WaitFor(3, new String[] { "GIA_MA\\AbrirTED" });
			if (waitResult.region == null)
				return false;

			s.type(Key.ENTER);

			waitResult = utilAut.WaitFor(3, new String[] { "GIA_MA\\Informacao" });
			if (waitResult.region != null)
				s.type(Key.ENTER);
		}
		return true;
	}

	public boolean EnviarArquivo(String file) {

		boolean retorno = ted.EnviarArquivo(file);
		if (!retorno)
			this.ClicarCancelar();

		this.ClicarCancelar();
		return retorno;

		/*
		 * WaitResult waitResult = utilAut.WaitFor(10, new String[] {
		 * "GIA_MA\\SelecionarArquivo" }); if (waitResult.region == null) return false;
		 * waitResult.region.click();
		 * 
		 * s.type(file); s.type(Key.ENTER);
		 * 
		 * waitResult = utilAut.WaitFor(3, new String[] { "GIA_MA\\Enviar" }); if
		 * (waitResult.region == null) return false; waitResult.region.click();
		 * 
		 * waitResult = utilAut.WaitFor(60, new String[] {
		 * "GIA_MA\\TransmissaoExecutadaComSucesso", "GIA_MA\\FalhaTransmissao" }); if
		 * (waitResult.region == null) return false;
		 * 
		 * if(waitResult.imageFound=="GIA_MA\\FalhaTransmissao") {
		 * util.PrintErro(processandoPath + "//Error.png");
		 * 
		 * waitResult = utilAut.WaitFor(60, new String[] { "GIA_MA\\BotaoNo" }); if
		 * (waitResult.region != null) waitResult.region.click();
		 * 
		 * waitResult = utilAut.WaitFor(60, new String[] { "GIA_MA\\EncerrarTED",
		 * "GIA_MA\\EncerrarTED2" }); if (waitResult.region == null) return false;
		 * waitResult.region.click();
		 * 
		 * this.ClicarCancelar();
		 * 
		 * this.ClicarCancelar();
		 * 
		 * return false; } else { s.type(Key.ENTER);
		 * 
		 * waitResult = utilAut.WaitFor(60, new String[] {
		 * "GIA_MA\\ComprovanteTransmissao" }); if (waitResult.region == null) return
		 * false;
		 * 
		 * util.PrintErro(file.toLowerCase().replace(".dcx", "_Comprovante.png"));
		 * 
		 * waitResult = utilAut.WaitFor(60, new String[] { "GIA_MA\\Fechar" }); if
		 * (waitResult.region == null) return false; waitResult.region.click();
		 * 
		 * waitResult = utilAut.WaitFor(60, new String[] { "GIA_MA\\EncerrarTED",
		 * "GIA_MA\\EncerrarTED2" }); if (waitResult.region == null) return false;
		 * waitResult.region.click();
		 * 
		 * this.ClicarCancelar();
		 * 
		 * return true; }
		 */
	}

}
