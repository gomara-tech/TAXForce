package br.com.standardit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Properties;

import br.com.standardit.gia.ma.AutomationGiaMA;
import br.com.standardit.gia.pa.AutomationGiaPA;
import br.com.standardit.gia.rs.AutomationGiaRS;
import br.com.standardit.gia.sc.AutomationGiaSC;
import br.com.standardit.gia.sp.AutomationGiaSP;
import br.com.standardit.pva.AutomationContribuicoesPVA;
import br.com.standardit.pva.AutomationPVA;
import br.com.standardit.ws.WSGetFile;
import br.com.standardit.ws.ecertidoes.ECertidoes;

public class Program {

	static String watchPath;
	static String eCertidoes;
	static String transmitir;
	static String habilitarWS;
	static String tempoDeEsperaWS;

	public static void main(String[] args) {
		Properties prop = new Properties();
		InputStream input;

		try {
			/*
			 * 
			 */

			System.out.println("Automate TaxForce V 1.7.4.1");

			input = new FileInputStream("config.properties");
			prop.load(input);

			watchPath = prop.getProperty("watchPath");
			eCertidoes = prop.getProperty("eCertidoes");
			transmitir = prop.getProperty("transmitirArquivo");
			habilitarWS = prop.getProperty("habilitarWS");
			tempoDeEsperaWS = prop.getProperty("tempoDeEsperaWS");

			AutomationPVA pva = new AutomationPVA(prop);
			AutomationContribuicoesPVA pvaC = new AutomationContribuicoesPVA(prop);
			AutomationGiaMA giaMA = new AutomationGiaMA(prop);
			AutomationGiaSP giaSP = new AutomationGiaSP(prop);
			AutomationGiaPA giaPA = new AutomationGiaPA(prop);
			AutomationGiaRS giaRS = new AutomationGiaRS(prop);
			AutomationGiaSC giaSC = new AutomationGiaSC(prop);

			File folderPVA = new File(watchPath);

			PrintStream stdout = System.out;
			while (true) {

				util.closeOldSessions();

				String filename = util.getFileToProcess(folderPVA);
				if (filename != null) {
					System.out.println("Processando arquivo " + filename);

					IdentficacaoArquivoViewModel identificacao = IdentificadorObrigacao
							.IdentificarArquivo(watchPath + "\\" + filename);
					switch (identificacao.TipoObrigacao) {
					case SPEDFiscal:
						pva.Processar(filename);
						break;
					case SPEDContribuicoes:
						pvaC.Processar(filename);
						break;
					case GIA_MA:
						giaMA.Processar(filename);
						break;
					case GIA_SP:
						giaSP.Processar(filename);
						break;
					case GIA_PA:
						giaPA.Processar(filename);
						break;
					case GIA_RS:
						giaRS.Processar(filename);
						break;
					case GIA_SC:
						giaSC.Processar(filename);
						break;
					default:
						break;
					}
				}
				System.setOut(stdout);

				/*----------------------------------------------------------
				 * Busca de arquivos Via WS
				 --------------------------------------------------------- */
				if (habilitarWS.equals("true")) {
					String nomeArquivo = WSGetFile.getFile(watchPath);
					if (nomeArquivo != null) {
						util.unzip(watchPath + "\\Obrigacao.zip", watchPath);
						util.DeleteOneFile(watchPath, "Obrigacao.zip");
					}
				}

				/*----------------------------------------------------------
				 * Consulta Ecertidoes Via WS
				 --------------------------------------------------------- */
				if (eCertidoes.equals("true"))
					ECertidoes.processoMain();

				/*----------------------------------------------------------
				 * Tempo de consulta 
				 *----------------------------------------------------------*/
				util.sleep(Integer.parseInt(tempoDeEsperaWS));
				util.MouseMove();
			}

		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}
