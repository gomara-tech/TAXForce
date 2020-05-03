package br.com.standardit.gia.pa;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.FileSystemException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import org.sikuli.basics.Debug;
import org.sikuli.script.ImagePath;
import org.sikuli.script.Key;
import org.sikuli.script.Screen;

import br.com.standardit.Ted;
import br.com.standardit.util;
import br.com.standardit.ws.WSpostFile;

public class AutomationGiaPA {
	String CNPJ;
	String DataInicial;
	String DataFinal;
	String NomeArquivo;

	String giaPath;
	String watchPath;
	String processadoPath;
	String habilitarWS;
	String giaPathExportacao;
	String giaPathInstalacao;

	int tempoMaximoImportacao = 0;
	Screen s = new Screen();
	UtilGIAPA utilGia;
	public Ted ted;

	public AutomationGiaPA(Properties prop) {
		Debug.setDebugLevel(3);
		ImagePath.add(prop.getProperty("imagefolder"));

		giaPath = prop.getProperty("giaPaPath");

		watchPath = prop.getProperty("watchPath");
		processadoPath = prop.getProperty("processadoPath");
		habilitarWS = prop.getProperty("habilitarWS");

		tempoMaximoImportacao = Integer.parseInt(prop.getProperty("tempoMaximoImportacao"));
		giaPathExportacao = prop.getProperty("giaPaPathExportacao");
		giaPathInstalacao = prop.getProperty("giaPaPathInstalacao");

		utilGia = new UtilGIAPA(s, giaPathInstalacao, watchPath);
		this.ted = new Ted(s, watchPath);
	}

//-------------------------------------------------------------------------------------------------------------
	public boolean Start() {

		String[] cmd = { this.giaPath };
		try {
			Runtime rt = Runtime.getRuntime();
			rt.exec(cmd);
			util.sleep(2);

			utilGia.AtivarPrograma();

			return true;
		} catch (IOException e) {
			util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

//----------------------------------------------------------------------------------------------------------------
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

			util.DeleteFiles(giaPathInstalacao + "\\Importacao", false);
			util.DeleteFiles(giaPathInstalacao + "\\CheckList", false);
			util.DeleteFiles(giaPathInstalacao + "\\Geradas", false);

			if (!this.Start()) {
				util.log.info("[" + util.getMetod() + "] - Erro ao abrir Programa");
			}

			this.ExcluirDeclaracaoExistente();

			ExtrairDados(watchPath + "\\" + filename);

			String nomePath = this.CNPJ + "_" + this.DataInicial + "_" + this.DataFinal;
			this.NomeArquivo = nomePath + ".txt";

			resultado = this.Importar(filename);

			if (!resultado) {
				utilGia.FecharImportacao();
			} else {
				util.log.info("[" + util.getMetod() + "] - Acessando metodo Validar");
				resultado = this.Validar(filename);

				if (resultado) {
					String fileExportado = getArquivoGerado(giaPathExportacao, watchPath);

					resultado = this.EnviarGia(fileExportado);

					if (!resultado) {
						util.log.error("[" + util.getMetod() + "] - Processamento com erro: [Enviar Gia]");
						util.PrintErro(watchPath + "\\Erro[Transmissao].png");
						s.type("R", Key.ALT);
						s.type(Key.ENTER);
					}
				}
			}

			if (!resultado) {
				String errorPathFile = processadoPathFile + "\\Erro";
				util.CreateFolder(errorPathFile);
				util.MoveFiles(watchPath, errorPathFile);
			} else {
				util.MoveFiles(watchPath, processadoPathFile);
			}

			this.Close();
		} catch (Exception e) {
			util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
			e.printStackTrace();
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
	}

//-----------------------------------------------------------------------------------------------------------------------------------------------
	public boolean ExtrairDados(String filename) {

		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line;
			line = br.readLine();
			br.close();
			br = null;

			String mesref = "";
			String anoref = "";

			if (line.length() == 687) {
				CNPJ = line.substring(23, 37);
				mesref = line.substring(5, 7);
				anoref = line.substring(7, 11);
			} else {
				CNPJ = line.substring(21, 35);
				mesref = line.substring(5, 7);
				anoref = "20" + line.substring(7, 9);
			}

			DataInicial = "01" + mesref + anoref;

			SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy");
			Date date = df.parse(DataInicial);

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);

			DataFinal = String.valueOf(calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) + mesref + anoref;

		} catch (Exception e) {
			util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean Close() {
		utilGia.Encerrar();

		util.sleep(3);
		return true;
	}

	private boolean EnviarGia(String file) {
		// chamar o menu

		this.utilGia.AtivarPrograma();
		s.type("t", Key.ALT);

		return this.ted.EnviarArquivo(file);
		// return true;
	}

	private void ExcluirDeclaracaoExistente() {
		if (!utilGia.MenuAbrirDeclaracao())
			return;
		if (!utilGia.Consultar())
			return;

		if (!utilGia.ClicarDeclaracao())
			return;

		if (!utilGia.ClicarExcluir())
			return;

		if (!utilGia.ClicarSair())
			return;

	}

	private boolean Validar(String filename) {
		if (!utilGia.MenuAbrirDeclaracao())
			return false;

		if (!utilGia.Consultar())
			return false;

		if (!utilGia.ClicarAbrirDeclaracao())
			return false;

		util.log.info("[" + util.getMetod() + "] - ResumoApuracoes");
		if (!utilGia.ResumoApuracoes())
			return false;

		try {
			util.DeleteFiles(giaPathExportacao, true);
		} catch (FileSystemException e) {
			util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
		}

		util.log.info("[" + util.getMetod() + "] - CheckList");
		if (!utilGia.CheckList(filename))
			return false;

		util.log.info("[" + util.getMetod() + "] - FecharDeclaracao");
		if (!utilGia.FecharDeclaracao())
			return false;

		return true;
	}

	private boolean Importar(String filename) {
		if (!utilGia.MenuImportar())
			return false;

		if (!utilGia.SelecionarArquivo(watchPath + "\\" + filename))
			return false;

		if (!utilGia.AnalisarArquivo()) {
			util.log.error("[" + util.getMetod() + "] - Processamento com erro: [Analisando_Arquivo]");
			util.PrintErro(watchPath + "\\Erro[Analisando_Arquivo].png");
			s.type(Key.ENTER);

			// utilGia.SalvarErrosAnalise(watchPath + "\\Erro_Importacao.txt");
			return false;
		}

		if (!utilGia.ImportarArquivo()) {
			util.log.error("[" + util.getMetod() + "] - Processamento com erro: [Importando_Arquivo]");
			util.PrintErro(watchPath + "\\Erro[Importando_Arquivo].png");
			s.type(Key.ENTER);

			utilGia.SalvarErrosAnalise(watchPath + "\\Erro_Importacao.txt");
			return false;
		}

		if (!utilGia.FecharImportacao())
			return false;

		return true;
	}

	private String getArquivoGerado(String path, String pathTo) {
		File folder = new File(path);

		util.log.info("[" + util.getMetod() + "] - Lendo diretorio " + folder.toString());
		for (final File fileEntry : folder.listFiles())
			if (!fileEntry.isDirectory()) {
				if (fileEntry.getName().toLowerCase().endsWith(".dec"))
					return MoveFile(fileEntry.getParent(), fileEntry.getName(), pathTo);
			} else {
				return getArquivoGerado(fileEntry.getAbsolutePath(), pathTo);
			}
		return null;
	}

	private String MoveFile(String path, String name, String pathTo) {
		String origem = path + "\\" + name;
		String destino = pathTo + "\\" + name;

		try {
			util.MoveFiles(origem, destino);
		} catch (InterruptedException e) {
			util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
			return "";
		}

		return destino;
	}
}
