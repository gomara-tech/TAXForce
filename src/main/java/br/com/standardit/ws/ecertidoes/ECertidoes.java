package br.com.standardit.ws.ecertidoes;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Properties;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import br.com.standardit.Ziper;
import br.com.standardit.util;
import br.com.standardit.core.DriverFactory;

public class ECertidoes {

	public static String cnpj = "";
	public static String inscricao = "";
	public static String descStatus = "";
	public static String validade = "";

	public static void processoMain() {
		try {

			JSONObject jsonGet = new JSONObject();
			JSONObject jsonPost = new JSONObject();

			jsonGet = WSgetProcess.getEventInWS();

			Integer status = (Integer) jsonGet.get("status");

			if (status == 200) {
				jsonPost = identificadorDeCertidao(jsonGet);
			}

		} catch (JSONException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			// log.error("[" + getMetod() + "] - " + e.getMessage());
			e.printStackTrace();
		}

	}

	public static JSONObject identificadorDeCertidao(JSONObject json) {
		JSONObject jsonRetorno = new JSONObject();
		Properties prop = new Properties();
		InputStream input;

		PrintStream out = null;
		String processadoPath = "";
		String pastaDownloads = "";
		
		try {

			input = new FileInputStream("config.properties");
			prop.load(input);

			processadoPath = prop.getProperty("processadoPath");
			pastaDownloads = prop.getProperty("PastaDownloads");

			String estado = (String) json.get("estado");
			String tipo = (String) json.get("tipo");
			String evento = (String) json.get("evento");
			String esfera = (String) json.get("esfera");

			cnpj = (String) json.get("cnpj");
			inscricao = (String) json.get("inscricao");
			validade = "";
			descStatus = "";

			if (cnpj == "" || cnpj == null) {
				processadoPath = processadoPath + "\\" + inscricao;
			} else {
				processadoPath = processadoPath + "\\" + cnpj;
			}

			util.CreateFolder(processadoPath);
			util.DeleteFiles(processadoPath, false);
			util.DeleteFiles(pastaDownloads, false);
			
			String outfile = processadoPath + "\\log.txt";
			out = new PrintStream(new FileOutputStream(outfile));
			System.setOut(out);

			JUnitCore jUnitCore = new JUnitCore();

			if (esfera.equals("Federal")) {
				System.out.println("Executing E-Cetidoes[" + esfera + "] + CNPJ: " + cnpj);
				Result result = jUnitCore.run(br.com.standardit.ecertidoes.Federal.CertidaoFederal.class);
				System.out.println("Returno jUnitCore: " + result.getFailureCount());
				util.sleep(2);
				if ((descStatus.equals("3") || descStatus.equals("5"))
						& util.fileExists(processadoPath + "\\Certidao.pdf")) {
					Ziper.zipOneFile(processadoPath + "\\Certidao.pdf", processadoPath + "\\Certidao.zip");
				} else {
					Ziper.zipOneFile(processadoPath + "\\Certidao.png", processadoPath + "\\Certidao.zip");
				}
			} else {
				if (tipo.equals("CND") & estado.equals("CE")) {
					System.out.println("Executing E-Cetidoes[" + tipo + "] Estado[" + estado + "] + CNPJ: " + cnpj);
					Result result = jUnitCore.run(br.com.standardit.ecertidoes.CertidaoNegativaCE.class);
					System.out.println("Returno jUnitCore: " + result.getFailureCount());
					util.sleep(2);
					if ((descStatus.equals("1") || descStatus.equals("2"))
							& util.fileExists(processadoPath + "\\Certidao.pdf"))
						Ziper.zipOneFile(processadoPath + "\\Certidao.pdf", processadoPath + "\\Certidao.zip");
					else
						Ziper.zipOneFile(processadoPath + "\\Certidao.png", processadoPath + "\\Certidao.zip");
				}
				if (tipo.equals("CND") & estado.equals("SP")) {
					System.out.println("Executing E-Cetidoes[" + tipo + "] Estado[" + estado + "] + CNPJ: " + cnpj);
					Result result = jUnitCore.run(br.com.standardit.ecertidoes.CertidaoNegativaSP.class);
					System.out.println("Returno jUnitCore: " + result.getFailureCount());
					util.sleep(2);
					if (descStatus.equals("1") & util.fileExists(processadoPath + "\\Certidao.pdf")) {
						Ziper.zipOneFile(processadoPath + "\\Certidao.pdf", processadoPath + "\\Certidao.zip");
					} else {
						Ziper.zipOneFile(processadoPath + "\\Certidao.png", processadoPath + "\\Certidao.zip");
					}
				}
				if (tipo.equals("CND") & estado.equals("MA")) {
					System.out.println("Executing E-Cetidoes[" + tipo + "] Estado[" + estado + "] + CNPJ: " + cnpj);
					Result result = jUnitCore.run(br.com.standardit.ecertidoes.CertidaoNegativaMA.class);
					System.out.println("Returno jUnitCore: " + result.getFailureCount());
					util.sleep(2);
					if (descStatus.equals("1") & util.fileExists(processadoPath + "\\Certidao.pdf")) {
						Ziper.zipOneFile(processadoPath + "\\Certidao.pdf", processadoPath + "\\Certidao.zip");
					} else {
						Ziper.zipOneFile(processadoPath + "\\Certidao.png", processadoPath + "\\Certidao.zip");
					}
				}
				if (tipo.equals("CND") & estado.equals("RN")) {
					System.out.println("Executing E-Cetidoes[" + tipo + "] Estado[" + estado + "] + CNPJ: " + cnpj);
					Result result = jUnitCore.run(br.com.standardit.ecertidoes.CertidaoNegativaRN.class);
					System.out.println("Returno jUnitCore: " + result.getFailureCount());
					util.sleep(2);
					if (descStatus.equals("1") & util.fileExists(processadoPath + "\\Certidao.pdf"))
						Ziper.zipOneFile(processadoPath + "\\Certidao.pdf", processadoPath + "\\Certidao.zip");
					else
						Ziper.zipOneFile(processadoPath + "\\Certidao.png", processadoPath + "\\Certidao.zip");
				}
				if (tipo.equals("CND") & estado.equals("TO")) {
					System.out.println("Executing E-Cetidoes[" + tipo + "] Estado[" + estado + "] + CNPJ: " + cnpj);
					Result result = jUnitCore.run(br.com.standardit.ecertidoes.CertidaoNegativaTO.class);
					System.out.println("Returno jUnitCore: " + result.getFailureCount());
					util.sleep(2);
					if ((descStatus.equals("1") || descStatus.equals("2"))
							& util.fileExists(processadoPath + "\\Certidao.pdf"))
						Ziper.zipOneFile(processadoPath + "\\Certidao.pdf", processadoPath + "\\Certidao.zip");
					else
						Ziper.zipOneFile(processadoPath + "\\Certidao.png", processadoPath + "\\Certidao.zip");
				}
				if (tipo.equals("CND") & estado.equals("AP")) {
					System.out.println("Executing E-Cetidoes[" + tipo + "] Estado[" + estado + "] + CNPJ: " + cnpj);
					Result result = jUnitCore.run(br.com.standardit.ecertidoes.CertidaoNegativaAP.class);
					System.out.println("Returno jUnitCore: " + result.getFailureCount());
					util.sleep(2);
					if ((descStatus.equals("1") || descStatus.equals("2"))
							& util.fileExists(processadoPath + "\\Certidao.pdf"))
						Ziper.zipOneFile(processadoPath + "\\Certidao.pdf", processadoPath + "\\Certidao.zip");
					else
						Ziper.zipOneFile(processadoPath + "\\Certidao.png", processadoPath + "\\Certidao.zip");
				}
				if (tipo.equals("CND") & estado.equals("BA")) {
					System.out.println("Executing E-Cetidoes[" + tipo + "] Estado[" + estado + "] + CNPJ: " + cnpj);
					Result result = jUnitCore.run(br.com.standardit.ecertidoes.CertidaoNegativaBA.class);
					System.out.println("Returno jUnitCore: " + result.getFailureCount());
					util.sleep(2);
					if ((descStatus.equals("1") || descStatus.equals("3"))
							& util.fileExists(processadoPath + "\\Certidao.pdf"))
						Ziper.zipOneFile(processadoPath + "\\Certidao.pdf", processadoPath + "\\Certidao.zip");
					else
						Ziper.zipOneFile(processadoPath + "\\Certidao.png", processadoPath + "\\Certidao.zip");
				}

				if (tipo.equals("CND") & estado.equals("PE")) {
					System.out.println("Executing E-Cetidoes[" + tipo + "] Estado[" + estado + "] + CNPJ: " + cnpj);
					Result result = jUnitCore.run(br.com.standardit.ecertidoes.CertidaoNegativaPE.class);
					System.out.println("Returno jUnitCore: " + result.getFailureCount());
					util.sleep(2);
					if ((descStatus.equals("1") || descStatus.equals("2"))
							& util.fileExists(processadoPath + "\\Certidao.pdf"))
						Ziper.zipOneFile(processadoPath + "\\Certidao.pdf", processadoPath + "\\Certidao.zip");
					else
						Ziper.zipOneFile(processadoPath + "\\Certidao.png", processadoPath + "\\Certidao.zip");
				}
				if (tipo.equals("CND") & estado.equals("PA")) {
					System.out.println("Executing E-Cetidoes[" + tipo + "] Estado[" + estado + "] + CNPJ: " + cnpj);
					Result result = jUnitCore.run(br.com.standardit.ecertidoes.CertidaoNegativaPA.class);
					System.out.println("Returno jUnitCore: " + result.getFailureCount());
					util.sleep(2);
					if (descStatus.equals("1") & util.fileExists(processadoPath + "\\Certidao.pdf")) {
						Ziper.zipOneFile(processadoPath + "\\Certidao.pdf", processadoPath + "\\Certidao.zip");
					} else {
						Ziper.zipOneFile(processadoPath + "\\Certidao.png", processadoPath + "\\Certidao.zip");
					}
				}
				if (tipo.equals("CND") & estado.equals("AL")) {
					System.out.println("Executing E-Cetidoes[" + tipo + "] Estado[" + estado + "] + CNPJ: " + cnpj);
					Result result = jUnitCore.run(br.com.standardit.ecertidoes.CertidaoNegativaAL.class);
					System.out.println("Returno jUnitCore: " + result.getFailureCount());
					util.sleep(2);
					if (descStatus.equals("1") & util.fileExists(processadoPath + "\\Certidao.pdf")) {
						Ziper.zipOneFile(processadoPath + "\\Certidao.pdf", processadoPath + "\\Certidao.zip");
					} else {
						Ziper.zipOneFile(processadoPath + "\\Certidao.png", processadoPath + "\\Certidao.zip");
					}
				}
				if (tipo.equals("CND") & estado.equals("AM")) {
					System.out.println("Executing E-Cetidoes[" + tipo + "] Estado[" + estado + "] + CNPJ: " + cnpj);
					Result result = jUnitCore.run(br.com.standardit.ecertidoes.CertidaoNegativaAM.class);
					System.out.println("Returno jUnitCore: " + result.getFailureCount());
					util.sleep(2);
					if (descStatus.equals("1") & util.fileExists(processadoPath + "\\Certidao.pdf")) {
						Ziper.zipOneFile(processadoPath + "\\Certidao.pdf", processadoPath + "\\Certidao.zip");
					} else {
						Ziper.zipOneFile(processadoPath + "\\Certidao.png", processadoPath + "\\Certidao.zip");
					}
				}
				if (tipo.equals("CND") & estado.equals("PB")) {
					System.out.println("Executing E-Cetidoes[" + tipo + "] Estado[" + estado + "] + CNPJ: " + cnpj);
					Result result = jUnitCore.run(br.com.standardit.ecertidoes.CertidaoNegativaPB.class);
					System.out.println("Returno jUnitCore: " + result.getFailureCount());
					util.sleep(2);
					if (descStatus.equals("1") & util.fileExists(processadoPath + "\\Certidao.pdf")) {
						Ziper.zipOneFile(processadoPath + "\\Certidao.pdf", processadoPath + "\\Certidao.zip");
					} else {
						Ziper.zipOneFile(processadoPath + "\\Certidao.png", processadoPath + "\\Certidao.zip");
					}
				}
				if (tipo.equals("CND") & estado.equals("PI")) {
					System.out.println("Executing E-Cetidoes[" + tipo + "] Estado[" + estado + "] + CNPJ: " + cnpj);
					Result result = jUnitCore.run(br.com.standardit.ecertidoes.CertidaoNegativaPI.class);
					System.out.println("Returno jUnitCore: " + result.getFailureCount());
					util.sleep(2);
					if (descStatus.equals("1") & util.fileExists(processadoPath + "\\Certidao.pdf")) {
						Ziper.zipOneFile(processadoPath + "\\Certidao.pdf", processadoPath + "\\Certidao.zip");
					} else {
						Ziper.zipOneFile(processadoPath + "\\Certidao.png", processadoPath + "\\Certidao.zip");
					}
				}
				if (tipo.equals("CND") & estado.equals("SE")) {
					System.out.println("Executing E-Cetidoes[" + tipo + "] Estado[" + estado + "] + CNPJ: " + cnpj);
					Result result = jUnitCore.run(br.com.standardit.ecertidoes.CertidaoNegativaSE.class);
					System.out.println("Returno jUnitCore: " + result.getFailureCount());
					util.sleep(2);
					if (descStatus.equals("1") & util.fileExists(processadoPath + "\\Certidao.pdf")) {
						Ziper.zipOneFile(processadoPath + "\\Certidao.pdf", processadoPath + "\\Certidao.zip");
					} else {
						Ziper.zipOneFile(processadoPath + "\\Certidao.png", processadoPath + "\\Certidao.zip");
					}
				}

			}

			WSPostProcess.returnFile(processadoPath + "\\Certidao.zip", descStatus, validade, evento);

		} catch (JSONException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} finally {
			processadoPath = prop.getProperty("processadoPath");
			DriverFactory.killDriver();
			out.close();
		}
		return jsonRetorno;
	}

}
