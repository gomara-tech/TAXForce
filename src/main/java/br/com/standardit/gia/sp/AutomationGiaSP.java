package br.com.standardit.gia.sp;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.sikuli.script.ImagePath;
import org.sikuli.script.Key;
import org.sikuli.script.Screen;
import org.slf4j.LoggerFactory;

import br.com.standardit.util;
import br.com.standardit.core.DriverFactory;
import br.com.standardit.ws.WSpostFile;
import ch.qos.logback.classic.Logger;

public class AutomationGiaSP {
	String CNPJ;
	String DataInicial;
	String DataFinal;
	String NomeArquivo;

	String InscricaoEstadual;

	String giaPath;
	String watchPath;
	String processadoPath;
	String habilitarWS;
	String giaSpPathExportacao;
	String giaSpPathExportacaoTemp;

	int tempoMaximoImportacao = 0;
	Screen s = new Screen();
	UtilGIASP utilGia;

	public static final Logger log = (Logger) LoggerFactory.getLogger(AutomationGiaSP.class);

	public AutomationGiaSP(Properties prop) {
		// Debug.setDebugLevel(3);
		ImagePath.add(prop.getProperty("imagefolder"));

		giaPath = prop.getProperty("giaSpPath");

		watchPath = prop.getProperty("watchPath");
		processadoPath = prop.getProperty("processadoPath");
		habilitarWS = prop.getProperty("habilitarWS");

		tempoMaximoImportacao = Integer.parseInt(prop.getProperty("tempoMaximoImportacao"));

		giaSpPathExportacao = prop.getProperty("giaSpPathExportacao");
		giaSpPathExportacaoTemp = prop.getProperty("giaSpPathExportacaoTemp");

		utilGia = new UtilGIASP(s, watchPath);

	}

//------------------------------------------------------------------------------------------------------------------
	public boolean Start() {
		String[] cmd = { this.giaPath };
		try {
			Runtime rt = Runtime.getRuntime();
			log.info("[" + util.getMetod() + "] - Executando aplicacao.");
			rt.exec(cmd);
			util.sleep(2);

			utilGia.AtivarPrograma();

			utilGia.ValidarSeExisteContribuinte();

		} catch (IOException e) {
			log.error("[" + util.getMetod() + "] - " + e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}

//-------------------------------------------------------------------------------------------------------------
	public void Processar(String filename) {
		boolean resultado = true;
		PrintStream out = null;
		String processadoPathFile = "";
		try {
			processadoPathFile = processadoPath + "\\"
					+ filename.toLowerCase().replaceAll(".txt", "").replaceAll(".TXT", "");

			util.DeleteFiles(processadoPathFile, true);
			util.CreateFolder(processadoPathFile);

			String outfile = processadoPathFile + "\\log.txt";
			out = new PrintStream(new FileOutputStream(outfile));
			System.setOut(out);

			JUnitCore jUnitCore = new JUnitCore();
			Result resultJU = new Result();
			if (!this.Start())
				util.log.info("[" + util.getMetod() + "] - Erro ao abrir Programa. ");

			ExtrairDados(watchPath + "\\" + filename);

			String nomePath = this.CNPJ + "_" + this.DataInicial + "_" + this.DataFinal;
			this.NomeArquivo = nomePath + ".txt";

			resultado = this.Importar(filename);
			if (!resultado)
				util.PrintErro(watchPath + "\\Erro[Importacao].png");

			resultado = this.EnviarGia();
			if (!resultado)
				util.PrintErro(watchPath + "\\Erro[EnviarGia].png");

			this.Close();

			if (resultado) {
				resultJU = jUnitCore.run(br.com.standardit.gia.sp.GIA_SP.class);

				Integer failure = resultJU.getFailureCount();
				if (failure.equals(1))
					resultado = false;
			}

			if (!resultado) {
				util.PrintErro(watchPath + "\\Erro.png");
				s.type("R", Key.ALT);

				s.type(Key.ENTER);
				utilGia.ClicarFechar();
			}

			if (!resultado) {
				String errorPathFile = processadoPathFile + "\\Erro";
				util.CreateFolder(errorPathFile);
				util.sleep(1);
				util.MoveFiles(watchPath, errorPathFile);
			} else {
				util.MoveFiles(watchPath, processadoPathFile);
			}

		} catch (Exception e) {
			util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
			e.printStackTrace();
		}

		DriverFactory.killDriver();
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

//--------------------------------------------------------------------------------------------------------------------
	private boolean Importar(String filename) {
		boolean resultado = true;

		// IMPORTAR O ARQUIVO SINTEGRA
		resultado = utilGia.MenuImportar();
		if (!resultado)
			return resultado;

		if (utilGia.ClicarSelecionarArquivo()) {
			util.sleep(3);
			s.type(watchPath + "\\" + filename);
			s.type(Key.ENTER);
		} else
			return false;

		if (utilGia.MensagemConsistencia())
			utilGia.ClicarOk();
		else
			return false;

		utilGia.ClicarImportar();

		if (!utilGia.ClicarFechar())
			return false;

		return resultado;
	}

//------------------------------------------------------------------------------------------------------------------
	private boolean EnviarGia() {
		boolean resultado = true;

		try {

			resultado = utilGia.MenuGerarGia();

			if (!utilGia.ClicarAbaConsistir())
				return false;
			if (!utilGia.ClicarConsistir())
				return false;

			if (!utilGia.MensagemFimConsistencia())
				return false;
			s.type(Key.ENTER);

			// Essa msg fecha o fluxo em caso de vedadeira
			if (utilGia.MensagemAviso()) {
				util.log.info("[" + util.getMetod() + "] - Fecha msg de Inconcistencia.");
				util.PrintErro(watchPath + "\\Erro[Arquivo_Inconcistente].png");
				s.type(Key.ENTER);
				utilGia.ClicarFechar();
				return false;
			}

			if (!utilGia.ClicarAbaNormal())
				return false;

			if (!utilGia.ClicarGerarNormal())
				return false;

			if (!utilGia.MensagemSalvarArquivo())
				return false;

			if (!utilGia.ClicarYes())
				return false;

			util.sleep(2);
			s.type(Key.HOME);
			s.type(watchPath + "\\");
			s.type(Key.ENTER);

			if (!utilGia.MensagemAbrirNavegador())
				return false;

			utilGia.ClickNo();

			utilGia.ClicarFechar();

		} catch (Exception e) {
			util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
			e.printStackTrace();
			return false;
		}
		return resultado;
	}

	// --------------------------------------------------------------------------------------------------------------
//	private String getArquivoGerado() {
//		File folder = new File(this.watchPath);
//
//		util.log.info("[" + util.getMetod() + "] - Lendo diretorio " + folder.toString());
//		for (final File fileEntry : folder.listFiles())
//			if (!fileEntry.isDirectory())
//				if (fileEntry.getName().endsWith(".SFZ") || fileEntry.getName().endsWith(".sfz"))
//					return fileEntry.getName();
//
//		return null;
//
//	}
//
//	// --------------------------------------------------------------------------------------------------------------
	public boolean ExtrairDados(String filename) {

		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			br.readLine();
			String secondLine = br.readLine();
			br.close();
			br = null;

			CNPJ = secondLine.substring(14, 28);

			String mesref = secondLine.substring(41, 43);
			String anoref = secondLine.substring(37, 41);

			DataInicial = "01" + mesref + anoref;

			SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy");
			Date date = df.parse(DataInicial);

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);

			DataFinal = String.valueOf(calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) + mesref + anoref;

		} catch (IOException e) {
			util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
			e.printStackTrace();
			return false;
		} catch (ParseException e) {
			util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}

//--------------------------------------------------------------------------------------------------
	public void Close() {
		utilGia.Encerrar();
		utilGia.Encerrar2();
	}
}
