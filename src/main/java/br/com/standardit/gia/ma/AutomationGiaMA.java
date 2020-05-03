package br.com.standardit.gia.ma;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

import org.sikuli.basics.Debug;
import org.sikuli.script.ImagePath;
import org.sikuli.script.Key;
import org.sikuli.script.Screen;

import br.com.standardit.util;
import br.com.standardit.ws.WSpostFile;

public class AutomationGiaMA {
	String CNPJ;
	String DataInicial;
	String DataFinal;
	String NomeArquivo;

	String giaPath;
	String watchPath;
	String processadoPath;
	String habilitarWS;
	String giaPathImportacao;
	String giaPathExportacao;

	int tempoMaximoImportacao = 0;
	Screen s = new Screen();
	UtilGIAMA utilGia;

	public AutomationGiaMA(Properties prop) {
		Debug.setDebugLevel(3);
		ImagePath.add(prop.getProperty("imagefolder"));

		giaPath = prop.getProperty("giaMaPath");
		watchPath = prop.getProperty("watchPath");
		processadoPath = prop.getProperty("processadoPath");
		habilitarWS = prop.getProperty("habilitarWS");
		tempoMaximoImportacao = Integer.parseInt(prop.getProperty("tempoMaximoImportacao"));
		giaPathImportacao = prop.getProperty("giaMaPathImportacao") + "\\importar.txt";
		giaPathExportacao = prop.getProperty("giaMAPathExportacao");

		utilGia = new UtilGIAMA(s, watchPath, watchPath);
	}

	// -------------------------------------------------------------------------------------------------------------------
	public boolean Start() {

		String[] cmd = { this.giaPath };
		try {
			Runtime rt = Runtime.getRuntime();
			rt.exec(cmd);
			util.sleep(2);

			return utilGia.AtivarPrograma();

		} catch (IOException e) {
			util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	// -------------------------------------------------------------------------------------------------------------------
	public void Processar(String filename) {
		boolean resultado = true;
		PrintStream out = null;
		String processadoPathFile = "";
		try {
			processadoPathFile = processadoPath + "\\" + filename.toLowerCase().replaceAll(".txt", "");

			util.DeleteFiles(processadoPathFile, true);
			util.CreateFolder(processadoPathFile);

			String outfile = processadoPathFile + "\\log.txt";
			out = new PrintStream(new FileOutputStream(outfile));
			System.setOut(out);

			if (!this.Start())
				util.log.info("[" + util.getMetod() + "] - Erro ao abrir Programa");

			utilGia.ClicarCancelar();
			utilGia.ExcluirArquivo();
			utilGia.ExcluirContribuinte();

			ExtrairDados(watchPath + "\\" + filename);
			String nomePath = this.CNPJ + "_" + this.DataInicial + "_" + this.DataFinal;
			this.NomeArquivo = nomePath + ".txt";

			String arquivoImportar = watchPath + "\\" + this.NomeArquivo;

			util.MoveFiles(watchPath + "\\" + filename, arquivoImportar);
			util.CopyFile(arquivoImportar, giaPathImportacao);

			resultado = this.Importar(arquivoImportar);

			if (!resultado) {
				util.PrintErro(watchPath + "\\Erro[Importando_Arquivo].png");
				s.type("R", Key.ALT);
				s.type(Key.ENTER);
				utilGia.ForcarSaida();
			}

			this.Close();

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
		} catch (FileNotFoundException e) {
			util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
			e.printStackTrace();
		} catch (FileSystemException e) {
			util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
			e.printStackTrace();
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

	}

//--------------------------------------------------------------------------------------------------------------------------------------
	public boolean ExtrairDados(String filename) {

		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line;
			line = br.readLine();
			br.close();
			br = null;

			CNPJ = line.substring(2, 16);

			String anoref = line.substring(107, 111);
			String mesref = line.substring(111, 113);
			String diaref = line.substring(113, 115);

			// DataInicial = line.substring(107, 115);
			DataInicial = diaref + mesref + anoref;

			anoref = line.substring(115, 119);
			mesref = line.substring(119, 121);
			diaref = line.substring(121, 123);

			// DataFinal = line.substring(115, 123);
			DataFinal = diaref + mesref + anoref;

		} catch (IOException e) {
			util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean Close() {

		// s.type(Key.F4, Key.ALT);

		utilGia.Encerrar();

		util.sleep(3);
		return true;
	}

	private boolean Importar(String filename) {
		boolean resultado = true;

		// IMPORTAR O ARQUIVO SINTEGRA
		resultado = utilGia.MenuImportar();
		if (!resultado)
			return resultado;

		resultado = utilGia.ImportarArquivo();
		if (!resultado)
			return resultado;

		resultado = utilGia.GerarApuracaoImposto();
		if (!resultado) {
			return resultado;
		}

		resultado = utilGia.GerarArquivo();
		if (!resultado)
			return resultado;

		String arquivoGerado = getArquivoGerado();

		if (arquivoGerado == null)
			return false;

		String copiaArquivoGerado = filename.toLowerCase().replace(".txt", "_Gerado.DCX");

		try {
			Files.copy(Paths.get(giaPathExportacao + "\\" + arquivoGerado), Paths.get(copiaArquivoGerado),
					StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
			e.printStackTrace();
		}

		resultado = utilGia.EnviarArquivo(copiaArquivoGerado);
		if (!resultado)
			return resultado;

		try {
			Files.delete(Paths.get(giaPathExportacao + "\\" + arquivoGerado));
		} catch (IOException e) {
			util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
			e.printStackTrace();
		}

		return resultado;
	}

	private String getArquivoGerado() {
		File folder = new File(giaPathExportacao);

		util.log.info("[" + util.getMetod() + "] - Lendo diretorio: " + folder.toString());
		for (final File fileEntry : folder.listFiles())
			if (!fileEntry.isDirectory())
				if (fileEntry.getName().endsWith(".DCX"))
					return fileEntry.getName();

		return null;

	}
}
