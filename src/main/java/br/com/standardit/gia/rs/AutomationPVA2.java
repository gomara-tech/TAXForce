package br.com.standardit.gia.rs;

import java.util.Properties;

import org.sikuli.basics.Debug;
import org.sikuli.script.ImagePath;
import org.sikuli.script.Key;
import org.sikuli.script.Screen;

import br.com.standardit.WaitResult;
import br.com.standardit.util;
import br.com.standardit.pva.ArquivoFiscal;
import br.com.standardit.pva.ImportacaoPVA;
import br.com.standardit.pva.UtilPVA;

public class AutomationPVA2 {
	Screen s = new Screen();
	UtilPVA utilPVA;
	ImportacaoPVA importacaoPVA;
	String pvaPath;
	String watchPath;
	String processadoPath;
	String imageFound = "";
	String arquivoImportar = "";
	int tempoMaximoImportacao = 0;
	Boolean salvarReciboEntrega;

	Runtime rt = Runtime.getRuntime();

	public AutomationPVA2(Properties prop) {
		Debug.setDebugLevel(3);
		ImagePath.add(prop.getProperty("imagefolder"));

		pvaPath = prop.getProperty("pvaPath");
		watchPath = prop.getProperty("watchPath");
		processadoPath = prop.getProperty("processadoPath");
		tempoMaximoImportacao = Integer.parseInt(prop.getProperty("tempoMaximoImportacao"));
		salvarReciboEntrega = Boolean.parseBoolean(prop.getProperty("salvarReciboEntrega"));

		utilPVA = new UtilPVA(s);
		importacaoPVA = new ImportacaoPVA(s, tempoMaximoImportacao, watchPath, salvarReciboEntrega);
	}

//---------------------------------------------------------------------------------------------------------------------
	public boolean Start() {
		String[] cmd = { pvaPath };
		try {
			rt.exec(cmd);
			util.sleep(2);

			if (!utilPVA.existeOutraInstancia()) {

				WaitResult waitResult = utilPVA.utilAut.WaitFor(60, new String[] { "aguarde" });
				if (waitResult.region != null)
					utilPVA.waitAguardeFinish();
				else
					return false;

				waitResult = utilPVA.utilAut.WaitFor(5, new String[] { "PVA_Atualizacao", "PVA_Erro_Database" });
				if (waitResult.region != null)
					return false;

			}
		} catch (Exception e) {
			util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}

//----------------------------------------------------------------------------------------------------------------
	public boolean Processar(String filename) {
		try {
			String arquivoImportar = watchPath + "\\" + filename;
			String processadoPathFile = processadoPath + "\\" + filename.toLowerCase().replaceAll(".txt", "");
			boolean resultado = false;

			ArquivoFiscal arquivoFiscal = new ArquivoFiscal();
			arquivoFiscal.ExtrairDados(arquivoImportar);

			if (!this.Start()) {
				util.log.error("[" + util.getMetod() + "] - Processamento com erro: [Carregando_APP_PVA]");
				util.PrintErro(watchPath + "//Error[Carregando_APP_PVA].png");
			} else {
				this.ExcluirEscrituracao();
				util.sleep(5);
				resultado = this.Import(arquivoImportar);
			}

			this.Finalizar();

			if (!resultado) {
				String errorPathFile = processadoPathFile + "\\Erro";
				util.CreateFolder(errorPathFile);
				util.MoveFiles(watchPath, errorPathFile);
				return false;
			}

		} catch (Exception e) {
			util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}

//-----------------------------------------------------------------------------------------------------------------------
	public boolean ExcluirEscrituracao() throws InterruptedException {
		if (!utilPVA.AtivarPVA())
			return false;

		s.type("e", Key.CTRL);
		WaitResult waitResult;
		if (SelecionarEscrituracao()) {
			util.log.info("[" + util.getMetod() + "] - Selecionou");
			waitResult = utilPVA.utilAut.WaitFor(3, new String[] { "confirmacaoExclusao" });
			if (waitResult.region != null) {
				s.type(Key.ENTER);
				util.log.info("[" + util.getMetod() + "] - mensagem de confirmação");
				waitResult = utilPVA.utilAut.WaitFor(30, new String[] { "mensagemExclusao" });
				if (waitResult.region != null) {
					util.log.info("[" + util.getMetod() + "] - mensagem de exclusao");
					s.type(Key.ENTER);
				}
			}
		} else {
			util.log.info("[" + util.getMetod() + "] - Não Selecionou");
			s.type(Key.ESC);
		}
		return true;
	}

//--------------------------------------------------------------------------------------------------------
	public boolean Import(String file) {
		arquivoImportar = file;
		return importacaoPVA.Importar(arquivoImportar);
	}

//--------------------------------------------------------------------------------------------------------
	public boolean SelecionarEscrituracao() {
		s.type(Key.DOWN);
		s.type(Key.ENTER);
		WaitResult waitResult = utilPVA.utilAut.WaitFor(3, new String[] { "nenhumaLinhaSelecionada" });
		if (waitResult.region == null) {
			// s.type(Key.ENTER);
			return true;
		} else {
			util.log.info("[" + util.getMetod() + "] - Nenhuma linha selecionada");
			s.type(Key.ENTER);
			return false;
		}
	}

//--------------------------------------------------------------------------------------------------------	
	public void Finalizar() {
		// Clica no botao finalizar
		WaitResult waitResult = utilPVA.utilAut.WaitFor(5, new String[] { "PVA_Atualizacao", "Botao_Sair" });
		if (waitResult.region != null) {
			waitResult.region.click();
			util.sleep(3);
		} else {
			util.closeOldSessions();
		}
	}

//------------------------------------------------------------------------------------------------------------
	public void FecharValidacao() {
		utilPVA.FecharValidacao();
	}
}
