package br.com.standardit.pva;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.FileSystemException;
import java.util.Properties;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.sikuli.basics.Debug;
import org.sikuli.script.ImagePath;
import org.sikuli.script.Key;
import org.sikuli.script.Screen;

import br.com.standardit.WaitResult;
import br.com.standardit.util;
import br.com.standardit.ws.WSpostFile;

public class AutomationPVA {
	Screen s = new Screen();
	UtilPVA utilPVA;
	ImportacaoPVA importacaoPVA;
	String pvaPath;
	String watchPath;
	String processadoPath;
	String habilitarWS;
	String imageFound = "";
	String arquivoImportar = "";
	int tempoMaximoImportacao = 0;
	Boolean salvarReciboEntrega;

	Runtime rt = Runtime.getRuntime();

	public AutomationPVA(Properties prop) {
		Debug.setDebugLevel(3);
		ImagePath.add(prop.getProperty("imagefolder"));

		pvaPath = prop.getProperty("pvaPath");
		watchPath = prop.getProperty("watchPath");
		processadoPath = prop.getProperty("processadoPath");
		habilitarWS = prop.getProperty("habilitarWS");
		tempoMaximoImportacao = Integer.parseInt(prop.getProperty("tempoMaximoImportacao"));
		salvarReciboEntrega = Boolean.parseBoolean(prop.getProperty("salvarReciboEntrega"));

		utilPVA = new UtilPVA(s);
		importacaoPVA = new ImportacaoPVA(s, tempoMaximoImportacao, watchPath, salvarReciboEntrega);
	}

//------------------------------------------------------------------------------------------------------
	public boolean Processar(String filename) {
		boolean resultado;
		PrintStream out = null;
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
				util.zipFolder(processadoPathFile, processadoPath + "\\" + fileNametemp + ".zip");
				WSpostFile.postFile(processadoPath + "\\" + fileNametemp + ".zip", fileNametemp, resultado);
				util.DeleteFiles(processadoPath + "\\" + fileNametemp + ".zip", false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}	
		
		return true;
	}

//---------------------------------------------------------------------------------------
	public boolean Start() {
		String[] cmd = { pvaPath };
		WaitResult waitResult = null;
		try {

			rt.exec(cmd);util.sleep(20);

			utilPVA.existeOutraInstancia();

			waitResult = utilPVA.utilAut.WaitFor(5, new String[] { "PVA_Erro_Database" });
			if (waitResult.region != null) {
				this.Finalizar();
				rt.exec(cmd);util.sleep(20);
			}

			if (validaMsgDeAtualizacao()) {
				rt.exec(cmd);util.sleep(20);
			}
			
			waitResult = utilPVA.utilAut.WaitFor(20, new String[] { "aguarde" });
			if (waitResult.region != null)
				utilPVA.waitAguardeFinish();
			//else
				//return false;

			
		} catch (Exception e) {
			util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}

	// ------------------------------------------------------------------------------------------------
	public boolean validaMsgDeAtualizacao() {
		WaitResult waitResult = utilPVA.utilAut.WaitFor(5, new String[] { "NovoPva\\NovaVersaoDisponivel" });
		if (waitResult.region != null) {
			this.Finalizar();
			util.closeOldSessions();
			util.log.info("[" + util.getMetod() + "] - Iniciando Atualizacao do PVA.");
			JUnitCore jUnitCore = new JUnitCore();
			//Result result = jUnitCore.run(br.com.standardit.pva.AtualizarPVA.class);
			jUnitCore.run(br.com.standardit.pva.AtualizarPVA.class);
			return true;
		}
		return false;
	}

//------------------------------------------------------------------------------------------------------------
	public boolean Finalizar() {

		WaitResult waitResult = utilPVA.utilAut.WaitFor(5,
				new String[] { "NovoPva\\NovaVersaoDisponivel", "PVA_Erro_Database" });
		if (waitResult.region != null) {
			util.log.error("[" + util.getMetod() + "] - Fecha MSG de Erro do SPED.");
			s.type(Key.ENTER);
			waitResult = utilPVA.utilAut.WaitFor(1, new String[] { "botaoOk" });
			if (waitResult.region != null) {
				waitResult.region.click();
	 			util.sleep(2);
			}
		}
		// Clica no botao finalizar
		waitResult = utilPVA.utilAut.WaitFor(5, new String[] { "PVA_Atualizacao", "Botao_Sair" });
		if (waitResult.region != null) {
			waitResult.region.click();
			util.sleep(2);

		} else {

			String[] cmd1 = { "taskkill", "/IM", "mysqld-nt.exe", "/F" };
			String[] cmd2 = { "taskkill", "/IM", "mysqld-nt.exe *32", "/F" };
			String[] cmd3 = { "taskkill", "/IM", "javaw.exe", "/F" };
			String[] cmd4 = { "taskkill", "/IM", "javaw.exe *32", "/F" };
			String[] cmd5 = { "taskkill", "/IM", "DIEF-2018.exe *32", "/F" };
			String[] cmd6 = { "taskkill", "/IM", "chromedriver.exe", "/F" };
			try {
				rt.exec(cmd1);
				rt.exec(cmd2);
				rt.exec(cmd3);
				rt.exec(cmd4);
				rt.exec(cmd5);
				rt.exec(cmd6);
			} catch (IOException e) {
				util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
				e.printStackTrace();
				return false;
			}
		}

		return true;
	}

//------------------------------------------------------------------------------------
	public boolean Import(String file) {
		util.log.info("[" + util.getMetod() + "] - Importando arquivo: " + file);
		arquivoImportar = file;
		return importacaoPVA.Importar(arquivoImportar);
	}

//------------------------------------------------------------------------------------
	public boolean ExcluirEscrituracao() throws InterruptedException {
		if (!utilPVA.AtivarPVA())
			return false;

		util.log.info("[" + util.getMetod() + "] - Excluir Escrituracao");
		s.type("e", Key.CTRL);
		WaitResult waitResult;
		if (SelecionarEscrituracao()) {
			util.log.info("[" + util.getMetod() + "] - Selecionou");
			waitResult = utilPVA.utilAut.WaitFor(3, new String[] { "confirmacaoExclusao" });
			if (waitResult.region != null) {
				s.type(Key.ENTER);
				util.log.info("[" + util.getMetod() + "] - Mensagem de confirmacao");
				waitResult = utilPVA.utilAut.WaitFor(30, new String[] { "mensagemExclusao" });
				if (waitResult.region != null) {
					util.log.info("[" + util.getMetod() + "] - Mensagem de exclusao");
					s.type(Key.ENTER);
				} else {
					util.log.info("[" + util.getMetod() + "] - Nao achou mensagem de exclusao");
				}
			}
		} else {
			util.log.error("[" + util.getMetod() + "] - Nao Selecionou");
			s.type(Key.ESC);
		}

		return true;
	}

//---------------------------------------------------------------------------------------------------------
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

	// ------------------------------------------------------------------------------------------------------
	public void FecharValidacao() {
		util.log.info("[" + util.getMetod() + "] - Fechando Validacao.");
		utilPVA.FecharValidacao();
	}

//--------------------------------------------------------------------------------------------------
	public boolean validaMsgErroAoConectarBancoDeDados() {
		WaitResult waitResult = utilPVA.utilAut.WaitFor(5, new String[] { "PVA_Erro_Database" });
		if (waitResult.region != null) {
			this.Finalizar();
			return true;
		}
		return false;
	}

	// --------------------------------------------------------------------------------------------
	public boolean Close() {

		s.type(Key.F4, Key.ALT);
		util.sleep(3);
		// String[] cmd = { "taskkill", "/F", "/IM", _prop.getProperty("pvaPath") };
		// try {
		// rt.exec(cmd);
		// } catch (IOException e) {
		// e.printStackTrace();
		// return false;
		// }
		return true;
	}

}
