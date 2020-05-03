package br.com.standardit;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;


public class IdentificadorObrigacao {

	public static IdentficacaoArquivoViewModel IdentificarArquivo(String filename) {
		IdentficacaoArquivoViewModel identificacao = new IdentficacaoArquivoViewModel();
		try {
			identificacao = isSpedFIscal(filename);
			if (identificacao.TipoObrigacao != null)
				return identificacao;

			identificacao = isSpedContribuicoes(filename);
			if (identificacao.TipoObrigacao != null)
				return identificacao;

			identificacao = isGIA_MA(filename);
			if (identificacao.TipoObrigacao != null)
				return identificacao;

			identificacao = isGIA_PA(filename);
			if (identificacao.TipoObrigacao != null)
				return identificacao;

			identificacao = isGIA_SP(filename);
			if (identificacao.TipoObrigacao != null)
				return identificacao;

			identificacao = isGIA_RS(filename);
			if (identificacao.TipoObrigacao != null)
				return identificacao;

			identificacao = isGIA_SC(filename);
			if (identificacao.TipoObrigacao != null)
				return identificacao;

		} catch (Exception e) {
			util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
			return identificacao;
		}
		return identificacao;
	}

//------------------------------------------------------------------------
	private static IdentficacaoArquivoViewModel isSpedFIscal(String filename) {
		IdentficacaoArquivoViewModel identificacao = new IdentficacaoArquivoViewModel();
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String firstLine;
			firstLine = br.readLine();
			br.close();
			br = null;

			if (util.isNullOrBlank(firstLine))
				return identificacao;

			String[] fields = firstLine.split(Pattern.quote("|"));
			if (fields.length < 7)
				return identificacao;

			String dtIni = fields[4];
			String dtFim = fields[5];
			String cnpj = fields[7];
			//String uf = fields[9];

			if (dtIni.length() != 8)
				return identificacao;
			if (dtFim.length() != 8)
				return identificacao;
			if (cnpj.length() != 14)
				return identificacao;
			if (filename.contains("GIA_RS"))
				return identificacao;

			Date dateIni;
			Date dateFim;

			DateFormat df = new SimpleDateFormat("ddMMyyyy");

			try {
				dateIni = df.parse(dtIni);
			} catch (ParseException e) {
				util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
				return identificacao;
			}

			try {
				dateFim = df.parse(dtFim);
			} catch (ParseException e) {
				util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
				return identificacao;
			}

			if (!util.isCNPJ(cnpj))
				return identificacao;

			identificacao.TipoObrigacao = TipoObrigacao.SPEDFiscal;
			identificacao.CNPJ = cnpj;
			identificacao.DataInicial = dateIni;
			identificacao.DataFinal = dateFim;
			identificacao.RAZAO_SOCIAL = fields[6];

			return identificacao;

		} catch (Exception e) {
			util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
			e.printStackTrace();
			return identificacao;
		}
	}

//------------------------------------------------------------------------
	private static IdentficacaoArquivoViewModel isSpedContribuicoes(String filename) {
		IdentficacaoArquivoViewModel identificacao = new IdentficacaoArquivoViewModel();
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String firstLine;
			firstLine = br.readLine();
			br.close();
			br = null;

			if (util.isNullOrBlank(firstLine))
				return identificacao;

			String[] fields = firstLine.split(Pattern.quote("|"));
			if (fields.length < 7)
				return identificacao;

			String dtIni = fields[6];
			String dtFim = fields[7];
			String cnpj = fields[9];

			if (dtIni.length() != 8)
				return identificacao;
			if (dtFim.length() != 8)
				return identificacao;
			if (cnpj.length() != 14)
				return identificacao;

			Date dateIni;
			Date dateFim;

			DateFormat df = new SimpleDateFormat("ddMMyyyy");

			try {
				dateIni = df.parse(dtIni);
			} catch (ParseException e) {
				util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
				return identificacao;
			}

			try {
				dateFim = df.parse(dtFim);
			} catch (ParseException e) {
				util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
				return identificacao;
			}

			if (!util.isCNPJ(cnpj))
				return identificacao;

			identificacao.TipoObrigacao = TipoObrigacao.SPEDContribuicoes;
			identificacao.CNPJ = cnpj;
			identificacao.DataInicial = dateIni;
			identificacao.DataFinal = dateFim;
			identificacao.RAZAO_SOCIAL = fields[8];

			return identificacao;

		} catch (Exception e) {
			util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
			e.printStackTrace();
			return identificacao;
		}
	}

//------------------------------------------------------------------------        
	private static IdentficacaoArquivoViewModel isGIA_MA(String filename) {
		IdentficacaoArquivoViewModel identificacao = new IdentficacaoArquivoViewModel();
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String firstLine;
			firstLine = br.readLine();
			br.close();
			br = null;

			if (util.isNullOrBlank(firstLine))
				return identificacao;

			if (firstLine.length() < 125)
				return identificacao;

			if (!firstLine.startsWith("10"))
				return identificacao;

			String cnpj = firstLine.substring(2, 16);
			String dtIni = firstLine.substring(107, 115);
			String dtFim = firstLine.substring(115, 123);

			Date dateIni;
			Date dateFim;

			DateFormat df = new SimpleDateFormat("yyyyMMdd");

			try {
				dateIni = df.parse(dtIni);
			} catch (ParseException e) {
				util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
				return identificacao;
			}

			try {
				dateFim = df.parse(dtFim);
			} catch (ParseException e) {
				util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
				return identificacao;
			}

			if (!util.isCNPJ(cnpj))
				return identificacao;

			identificacao.TipoObrigacao = TipoObrigacao.GIA_MA;
			identificacao.CNPJ = cnpj;
			identificacao.DataInicial = dateIni;
			identificacao.DataFinal = dateFim;
			identificacao.UF = "MA";
		} catch (Exception e) {
			util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
			e.printStackTrace();
			return identificacao;
		}
		return identificacao;
	}

//------------------------------------------------------------------------
	private static IdentficacaoArquivoViewModel isGIA_PA(String filename) {
		IdentficacaoArquivoViewModel identificacao = new IdentficacaoArquivoViewModel();
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String firstLine;
			firstLine = br.readLine();
			br.close();
			br = null;

			if (util.isNullOrBlank(firstLine))
				return identificacao;

			if (firstLine.length() < 50)
				return identificacao;

			if (!firstLine.startsWith("88"))
				return identificacao;

			String cnpj = "";
			String mesref = "";
			String anoref = "";

			if (firstLine.length() == 687) {
				cnpj = firstLine.substring(23, 37);
				mesref = firstLine.substring(5, 7);
				anoref = firstLine.substring(7, 11);
			} else {
				cnpj = firstLine.substring(21, 35);
				mesref = firstLine.substring(5, 7);
				anoref = "20" + firstLine.substring(7, 9);
			}

			String dtIni = "01" + mesref + anoref;

			SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy");
			Date date = df.parse(dtIni);

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);

			String dtFim = String.valueOf(calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) + mesref + anoref;

			Date dateIni;
			Date dateFim;
			try {
				dateIni = df.parse(dtIni);
			} catch (ParseException e) {
				util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
				return identificacao;
			}

			try {
				dateFim = df.parse(dtFim);
			} catch (ParseException e) {
				util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
				return identificacao;
			}

			if (!util.isCNPJ(cnpj))
				return identificacao;

			identificacao.TipoObrigacao = TipoObrigacao.GIA_PA;
			identificacao.CNPJ = cnpj;
			identificacao.DataInicial = dateIni;
			identificacao.DataFinal = dateFim;
			identificacao.UF = "PA";
		} catch (Exception e) {
			util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
			e.printStackTrace();
			return identificacao;
		}
		return identificacao;
	}

//------------------------------------------------------------------------
	private static IdentficacaoArquivoViewModel isGIA_SP(String filename) {
		IdentficacaoArquivoViewModel identificacao = new IdentficacaoArquivoViewModel();
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String firstLine;
			firstLine = br.readLine();
			String secondLine = br.readLine();
			br.close();
			br = null;

			if (util.isNullOrBlank(firstLine))
				return identificacao;

			if (!firstLine.startsWith("01"))
				return identificacao;

			if (!secondLine.startsWith("05"))
				return identificacao;

			String cnpj = secondLine.substring(14, 28);
			String anoref = secondLine.substring(37, 41);
			String mesref = secondLine.substring(41, 43);

			String dtIni = "01" + mesref + anoref;

			SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy");
			Date date = df.parse(dtIni);

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);

			String dtFim = String.valueOf(calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) + mesref + anoref;

			Date dateIni;
			Date dateFim;
			try {
				dateIni = df.parse(dtIni);
			} catch (ParseException e) {
				util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
				return identificacao;
			}

			try {
				dateFim = df.parse(dtFim);
			} catch (ParseException e) {
				util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
				return identificacao;
			}

			if (!util.isCNPJ(cnpj))
				return identificacao;

			identificacao.TipoObrigacao = TipoObrigacao.GIA_SP;
			identificacao.CNPJ = cnpj;
			identificacao.DataInicial = dateIni;
			identificacao.DataFinal = dateFim;
			identificacao.UF = "SP";

		} catch (Exception e) {
			util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
			e.printStackTrace();
			return identificacao;
		}
		return identificacao;
	}

	// ------------------------------------------------------------------------
	private static IdentficacaoArquivoViewModel isGIA_RS(String filename) {
		IdentficacaoArquivoViewModel identificacao = new IdentficacaoArquivoViewModel();
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String firstLine;
			firstLine = br.readLine();
			br.close();
			br = null;

			if (util.isNullOrBlank(firstLine))
				return identificacao;

			String[] fields = firstLine.split(Pattern.quote("|"));
			if (fields.length < 7)
				return identificacao;

			String dtIni = fields[4];
			String dtFim = fields[5];
			String cnpj = fields[7];
			//String uf = fields[9];

			if (dtIni.length() != 8)
				return identificacao;
			if (dtFim.length() != 8)
				return identificacao;
			if (cnpj.length() != 14)
				return identificacao;
			if (!filename.contains("GIA_RS"))
				return identificacao;

			Date dateIni;
			Date dateFim;

			DateFormat df = new SimpleDateFormat("ddMMyyyy");

			try {
				dateIni = df.parse(dtIni);
			} catch (ParseException e) {
				util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
				return identificacao;
			}

			try {
				dateFim = df.parse(dtFim);
			} catch (ParseException e) {
				util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
				return identificacao;
			}

			if (!util.isCNPJ(cnpj))
				return identificacao;

			identificacao.TipoObrigacao = TipoObrigacao.GIA_RS;
			identificacao.CNPJ = cnpj;
			identificacao.DataInicial = dateIni;
			identificacao.DataFinal = dateFim;
			identificacao.RAZAO_SOCIAL = fields[6];
			identificacao.UF = "RS";
		} catch (Exception e) {
			util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
			e.printStackTrace();
			return identificacao;
		}
		return identificacao;
	}

//------------------------------------------------------------------------
	private static IdentficacaoArquivoViewModel isGIA_SC(String filename) {
		IdentficacaoArquivoViewModel identificacao = new IdentficacaoArquivoViewModel();
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String firstLine;
			firstLine = br.readLine();
			br.close();
			br = null;

			if (util.isNullOrBlank(firstLine))
				return identificacao;

			if (filename.contains("DIME-SC") || filename.contains("GIA_SC") ) {
				identificacao.TipoObrigacao = TipoObrigacao.GIA_SC;
				return identificacao;

			}

//            String[] fields = firstLine.split(Pattern.quote("|"));
//            if (fields.length < 7)
//                return identificacao;
//
//            String dtIni = fields[4];
//            String dtFim = fields[5];
//            String cnpj = fields[7];
//            String uf   = fields[9];
//            
//            if (dtIni.length() != 8)
//                return identificacao;
//            if (dtFim.length() != 8)
//                return identificacao;
//            if (cnpj.length() != 14)
//                return identificacao;
//            if (!filename.contains("GIA_RS"))
//            	return identificacao;
//
//            Date dateIni;
//            Date dateFim;
//            
//            DateFormat df = new SimpleDateFormat("ddMMyyyy");
//
//            try {
//            	dateIni =  df.parse(dtIni);	
//            } catch (ParseException pe) {
//            	return identificacao;
//            }
//            
//            try {
//            	dateFim =  df.parse(dtFim);
//            } catch (ParseException pe) {
//            	return identificacao;
//            }
//            
//            if (!util.isCNPJ(cnpj))
//                return identificacao;
//
//            identificacao.TipoObrigacao = TipoObrigacao.GIA_RS;
//            identificacao.CNPJ = cnpj;
//            identificacao.DataInicial = dateIni;
//            identificacao.DataFinal = dateFim;
//            identificacao.RAZAO_SOCIAL = fields[6];
//            identificacao.UF = "RS";
		} catch (Exception e) {
			util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
			e.printStackTrace();
			return identificacao;
		}
		return identificacao;
	}
}
