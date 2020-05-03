package br.com.standardit.gia.rs;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Properties;

import org.sikuli.script.ImagePath;
import org.sikuli.script.Screen;

import br.com.standardit.Ted;
import br.com.standardit.util;
import br.com.standardit.ws.WSpostFile;

public class AutomationGiaRS {
	String CNPJ;
	String DataInicial;
	String DataFinal;
	String NomeArquivo;

	String giaPath;
	String watchPath;
	String processadoPath;
	String habilitarWS;
	String transmitirArquivo;
	String giaRSProcessarSped;
	String giaUsuario;
	String giaSenha;
	String giaSpPathExportacao;

	int tempoMaximoImportacao = 0;
	Screen s = new Screen();
	UtilGIARS utilGia;
	AutomationPVA2 pva;
	public Ted ted;

	public AutomationGiaRS(Properties prop) {
		// Debug.setDebugLevel(3);
		ImagePath.add(prop.getProperty("imagefolder"));

		giaPath = prop.getProperty("giaRSPath");

		watchPath = prop.getProperty("watchPath");
		processadoPath = prop.getProperty("processadoPath");
		habilitarWS = prop.getProperty("habilitarWS");
		transmitirArquivo = prop.getProperty("transmitirArquivo");
		giaRSProcessarSped = prop.getProperty("giaRSProcessarSped");
		tempoMaximoImportacao = Integer.parseInt(prop.getProperty("tempoMaximoImportacao"));

		giaUsuario = br.com.standardit.ws.WSGetFile.userObrigacao;
		if (giaUsuario == "")
			giaUsuario = prop.getProperty("giaRSUsuario");

		giaSenha = br.com.standardit.ws.WSGetFile.passObrigacao;
		if (giaSenha == "")
			giaSenha = prop.getProperty("giaRSSenha");

		giaSpPathExportacao = prop.getProperty("giaSpPathExportacao");

		utilGia = new UtilGIARS(s, watchPath);

		pva = new AutomationPVA2(prop);
	}

//-------------------------------------------------------------------------------------------------------------
	public void Processar(String filename) {

		boolean resultado = true;
		PrintStream out = null;
		String processadoPathFile = "";

		try {
			processadoPathFile = processadoPath + "\\"
					+ filename.toLowerCase().replaceAll(".TXT", "").replaceAll(".txt", "");

			util.DeleteFiles(processadoPathFile, true);
			util.CreateFolder(processadoPathFile);

			String outfile = processadoPathFile + "\\log.txt";
			out = new PrintStream(new FileOutputStream(outfile));
			System.setOut(out);

			// Chamada para Assinatura dos arquivo via Sped
			if (giaRSProcessarSped.equals("true"))
				resultado = pva.Processar(filename);
			// resultado = pva.Processar(filename);

			if (resultado) {
				if (!utilGia.Start(this.giaPath)) {
					util.log.error("[" + util.getMetod() + "] - Processamento com erro: [Carregando_APP_GIA-RS]");
					// util.PrintErro(watchPath + "\\Erro[Carregando_APP_GIA-RS].png");
					resultado = false;
				} else {
					if (!utilGia.ValidarDados(watchPath + "\\" + filename, tempoMaximoImportacao, giaUsuario,
							giaSenha)) {
						util.log.error("[" + util.getMetod() + "] - Processamento com erro: [Validando_Dados]");
						// util.PrintErro(watchPath + "\\Erro[Validando_Dados].png");
						resultado = false;
					} else {
						if (!utilGia.Validar_Divergencias(watchPath + "\\" + filename, giaUsuario, giaSenha)) {
							util.log.error(
									"[" + util.getMetod() + "] - Processamento com erro: [Validando_Divergencias]");
							// util.PrintErro(watchPath + "\\Erro[Validando_Divergencias].png");
							resultado = false;
						} else {
							if (!utilGia.GeraArquivosParaTransmissao(watchPath, giaUsuario, giaSenha)) {
								util.log.error("[" + util.getMetod()
										+ "] - Processamento com erro: [Gerando_Arquivos_Para_Transmissao]");
								// util.PrintErro(watchPath + "\\Erro[Gerando_Arquivos_Para_Transmissao].png");
								resultado = false;
							} else {
								if (transmitirArquivo.equals("true")) {
									util.log.info("[" + util.getMetod() + "] - File:" + watchPath + "\\"
											+ utilGia.getTedFileName(watchPath));
									this.ted = new Ted(this.s, watchPath);
									if (!this.ted.EnviarArquivoRS(watchPath + "\\" + utilGia.getTedFileName(watchPath),
											giaUsuario, giaSenha)) {
										util.log.error("[" + util.getMetod()
												+ "] - Processamento com erro: [TED_Transmissao]");
										// util.PrintErro(watchPath + "\\Erro[TED_Transmissao].png");
										resultado = false;
									}
								}
							}
						}
					}
				}
			}

			util.ClearLog(processadoPathFile + "\\log.txt", giaSenha);
			util.ClearLog(processadoPathFile + "\\log.txt", giaUsuario);

			if (!resultado) {
				String errorPathFile = processadoPathFile + "\\Erro";
				util.CreateFolder(errorPathFile);
				util.ValidaSeGerouLog(errorPathFile);
				util.MoveFiles(watchPath, errorPathFile);
			} else {
				util.MoveFiles(watchPath, processadoPathFile);
			}

			utilGia.Close();
			out.close();

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
