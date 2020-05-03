package br.com.standardit.gia.sc;

import java.io.File;

import org.sikuli.script.Screen;

import br.com.standardit.UtilAutomation;
import br.com.standardit.util;

public class UtilGIASC {
	Screen s = new Screen();
	String watchPath;
	public UtilAutomation utilAut;

	public UtilGIASC(Screen s, String watchPath) {
		this.s = s;
		this.utilAut = new UtilAutomation(s);
		this.watchPath = watchPath;
	}

//--------------------------------------------------------------------------------------------------	
	public Boolean CheckProcessReturnErro(String folderString) {
		final File folder = new File(folderString);
		util.log.info("[" + util.getMetod() + "] - Lendo diretorio " + folder.toString());
		for (final File fileEntry : folder.listFiles())
			if (!fileEntry.isDirectory())
				if (fileEntry.getName().contains("Erro")) {
					return true;
				}
		return false;
	}
}
