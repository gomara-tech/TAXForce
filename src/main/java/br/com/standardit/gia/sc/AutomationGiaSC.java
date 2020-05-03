package br.com.standardit.gia.sc;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Properties;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.sikuli.basics.Debug;
import org.sikuli.script.ImagePath;
import org.sikuli.script.Screen;

import br.com.standardit.util;
import br.com.standardit.core.DriverFactory;
import br.com.standardit.ws.WSpostFile;

public class AutomationGiaSC {
	String giaPath;
	String watchPath;
	String processadoPath;
	String habilitarWS;
	int tempoMaximoImportacao = 0;
	Screen s = new Screen();
	UtilGIASC utilGia;

	public AutomationGiaSC(Properties prop) {
		Debug.setDebugLevel(3);
		ImagePath.add(prop.getProperty("imagefolder"));

		watchPath = prop.getProperty("watchPath");
		processadoPath = prop.getProperty("processadoPath");
		habilitarWS = prop.getProperty("habilitarWS");

		tempoMaximoImportacao = Integer.parseInt(prop.getProperty("tempoMaximoImportacao"));

		utilGia = new UtilGIASC(s, watchPath);
	}

//--------------------------------------------------------------------------------------------------------------------------
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

			util.log.info("[" + util.getMetod() + "] - Instanciando JUnitCore.");
			JUnitCore jUnitCore = new JUnitCore();
			Result result = jUnitCore.run(br.com.standardit.gia.sc.GIA_SC.class);

			util.log.info("[" + util.getMetod() + "] - Result: " + result.getRunCount());
			util.log.info("[" + util.getMetod() + "] - Result: " + result.getFailureCount());
			Integer failure = result.getFailureCount();

			// if (utilGia.CheckProcessReturnErro(watchPath) &&
			// result.getFailureCount().equals(1))
			if (failure.equals(1))
				resultado = false;

			if (!resultado) {
				String errorPathFile = processadoPathFile + "\\Erro";
				util.CreateFolder(errorPathFile);
				util.sleep(1);
				util.MoveFiles(watchPath, errorPathFile);
			} else {
				util.MoveFiles(watchPath, processadoPathFile);
			}

			out.close();
			DriverFactory.killDriver();

		} catch (Exception e) {
			util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
			e.printStackTrace();
		}

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
}
