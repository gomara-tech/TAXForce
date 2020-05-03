package br.com.standardit.pva;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.FileSystemException;
import java.util.Properties;

import org.sikuli.basics.Debug;
import org.sikuli.script.ImagePath;
import org.sikuli.script.Key;
import org.sikuli.script.Screen;

import br.com.standardit.WaitResult;
import br.com.standardit.util;
import br.com.standardit.ws.WSpostFile;

public class AutomationContribuicoesPVA {
	Screen s = new Screen();
	UtilPVA utilPVA;
	ImportacaoPVA importacaoPVA;
	String pvaContribuicoesPath;
	String watchPath;
	String processadoPath;
	String habilitarWS;
	String imageFound = "";
	String arquivoImportar = "";
	int tempoMaximoImportacao = 0;
	Boolean salvarReciboEntrega;

	Runtime rt = Runtime.getRuntime();

	public AutomationContribuicoesPVA(Properties prop) {
		Debug.setDebugLevel(3);
		ImagePath.add(prop.getProperty("imagefolder"));

		pvaContribuicoesPath = prop.getProperty("pvaContribuicoesPath");
		
		watchPath = prop.getProperty("watchPath");
		processadoPath = prop.getProperty("processadoPath");
		habilitarWS = prop.getProperty("habilitarWS");
		
		tempoMaximoImportacao = Integer.parseInt(prop.getProperty("tempoMaximoImportacao"));
		salvarReciboEntrega = Boolean.parseBoolean(prop.getProperty("salvarReciboEntrega"));

		utilPVA = new UtilPVA(s);
		importacaoPVA = new ImportacaoPVA(s, tempoMaximoImportacao, watchPath, salvarReciboEntrega);
	}

	public boolean Start() {

		String[] cmd = { pvaContribuicoesPath };
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

		} catch (IOException e) {
			util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}

	// ------------------------------------------------------------------------------------------------------------
	public boolean Processar(String filename) {
		PrintStream out = null;
		boolean resultado;
		String processadoPathFile = "";
		try {
			processadoPathFile = processadoPath + "\\" + filename.toLowerCase().replaceAll(".txt", "");

			util.CreateFolder(processadoPathFile);
			util.DeleteFiles(processadoPathFile, false);

			String outfile = processadoPathFile + "\\log.txt";
			out = new PrintStream(new FileOutputStream(outfile));
			System.setOut(out);

			String arquivoImportar = watchPath + "\\" + filename;

			util.MoveFiles(watchPath + "\\" + filename, arquivoImportar);

			ArquivoFiscal arquivoFiscal = new ArquivoFiscal();
			arquivoFiscal.ExtrairDados(arquivoImportar);

			if (!this.Start()) {
				util.log.info("[" + util.getMetod() + "] - Processamento com erro [Carregando_APP_PVA]");
				util.PrintErro(watchPath + "//Erro[Carregando_APP_PVA].png");
				resultado = false;
			} else {

				this.ExcluirEscrituracao();
				util.sleep(5);
				resultado = this.Import(arquivoImportar);

				// this.FecharValidacao();
			}

			this.Finalizar();

			if (!resultado) {
				String errorPathFile = processadoPathFile + "\\Erro";
				util.CreateFolder(errorPathFile);
				util.sleep(1);
				util.MoveFiles(watchPath, errorPathFile);
			} else {
				util.MoveFiles(watchPath, processadoPathFile);
			}

		} catch (InterruptedException e) {
			util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
			e.printStackTrace();
			return false;
		} catch (FileNotFoundException e) {
			util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
			e.printStackTrace();
			return false;
		} catch (FileSystemException e) {
			util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
			e.printStackTrace();
			return false;
		}
		out.close();
		
		if (habilitarWS.equals("true")) {			
			try {
				String fileNametemp = filename.toLowerCase().replaceAll(".TXT", "").replaceAll(".txt", "");
				util.zipFolder(processadoPathFile, processadoPath+"\\"+fileNametemp+".zip");
				WSpostFile.postFile(processadoPath+"\\"+fileNametemp+".zip",fileNametemp,resultado);
				util.DeleteFiles(processadoPath+"\\"+fileNametemp+".zip", false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}

//-------------------------------------------------------------------------------------------------------
	public boolean Close() {

		s.type(Key.F4, Key.ALT);
		util.sleep(3);
		// String[] cmd = { "taskkill", "/F", "/IM",
		// _prop.getProperty("pvaContribuicoesPath") };
		// try {
		// rt.exec(cmd);
		// } catch (IOException e) {
		// e.printStackTrace();
		// return false;
		// }
		return true;
	}

	// -------------------------------------------------------------------------------------------------------
	public boolean Finalizar() {
		// Clica no botao finalizar
		WaitResult waitResult = utilPVA.utilAut.WaitFor(5, new String[] { "PVA_Atualizacao", "Botao_Sair" });
		if (waitResult.region != null) {
			waitResult.region.click();
			util.sleep(2);

		} else {

			String[] cmd1 = { "taskkill", "/IM", "mysqld-nt.exe", "/F" };
			String[] cmd2 = { "taskkill", "/IM", "mysqld-nt.exe *32", "/F" };
			String[] cmd3 = { "taskkill", "/IM", "javaw.exe", "/F" };
			String[] cmd4 = { "taskkill", "/IM", "javaw.exe *32", "/F" };
			String[] cmd5 = { "taskkill", "/IM", "DIEF-2018.exe *32", "/F" };

			try {
				rt.exec(cmd1);
				rt.exec(cmd2);
				rt.exec(cmd3);
				rt.exec(cmd4);
				rt.exec(cmd5);
			} catch (IOException e) {
				util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	public boolean Import(String file) {
		util.log.info("[" + util.getMetod() + "] - Importando arquivo: " + file);
		arquivoImportar = file;
		return importacaoPVA.Importar(arquivoImportar);
	}

	public boolean ExcluirEscrituracao() throws InterruptedException {
		if (!utilPVA.AtivarPVA())
			return false;

		s.type("e", Key.CTRL);
		WaitResult waitResult;
		if (SelecionarEscrituracao()) {
			util.log.info("[" + util.getMetod() + "] - Selecionou");
			waitResult = utilPVA.utilAut.WaitFor(3, new String[] { "confirmacaoExclusao", "ConfirmacaoExclusao2" });
			if (waitResult.region != null) {
				s.type(Key.ENTER);
				util.log.info("[" + util.getMetod() + "] - mensagem de confirmação");
				waitResult = utilPVA.utilAut.WaitFor(30, new String[] { "mensagemExclusao", "MensagemExclusao2" });
				if (waitResult.region != null) {
					util.log.info("[" + util.getMetod() + "] - mensagem de exclusao");
					s.type(Key.ENTER);
				} else
					util.log.info("[" + util.getMetod() + "] - não achou mensagem de exclusão");
			}
		} else {
			util.log.info("[" + util.getMetod() + "] - Não Selecionou");
			s.type(Key.ESC);
		}
		return true;
	}

	public boolean SelecionarEscrituracao() {

		util.log.info("[" + util.getMetod() + "] - Valida se existe escrituracao");
		WaitResult waitResult = utilPVA.utilAut.WaitFor(3, new String[] { "Sem_Escrituracao" });
		if (waitResult.region != null) {
			waitResult = utilPVA.utilAut.WaitFor(3, new String[] { "Sem_Escrituracao_Cancelar" });
			if (waitResult.region != null) {
				util.log.info("[" + util.getMetod() + "] - Click no Botão Cancelar (Sem Escrituracao)");
				waitResult.region.click();
			}
		} else {
			util.log.info("[" + util.getMetod() + "] - Deletando Escrituracao...");
			s.type(Key.DOWN);
			s.type(Key.ENTER);
			waitResult = utilPVA.utilAut.WaitFor(3, new String[] { "nenhumaLinhaSelecionada" });
			if (waitResult.region == null) {
				// s.type(Key.ENTER);
				return true;
			} else {
				util.log.info("[" + util.getMetod() + "] - Nenhuma linha selecionada");
				s.type(Key.ENTER);
			}
		}
		return false;
	}

	public void FecharValidacao() {
		util.log.info("[" + util.getMetod() + "] - Fechando Validacao");
		utilPVA.FecharValidacao();
	}

}
