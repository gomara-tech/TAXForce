package br.com.standardit.gia.sp;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sikuli.script.Key;
import org.sikuli.script.Screen;

import br.com.standardit.UtilAutomation;
import br.com.standardit.WaitResult;
import br.com.standardit.util;

public class UtilGIASP {
	Screen s = new Screen();
	String watchPath;
	WaitResult waitResult;
	public UtilAutomation utilAut;

	public UtilGIASP(Screen s, String watchPath) {
		this.s = s;
		this.utilAut = new UtilAutomation(s);
		this.watchPath = watchPath;
	}

	public boolean AtivarPrograma() {
		util.log.info("[" + util.getMetod() + "] - Procurando imagem: GIA_SP\\Titulo");
		WaitResult waitResult = utilAut.WaitFor(30, new String[] { "GIA_SP\\Titulo" });
		if (waitResult.region == null) {
			util.log.error("[" + util.getMetod() + "] - Imagem nao encontrada: GIA_SP\\Titulo");
			br.com.standardit.util.PrintErro(watchPath + "\\Erro.png");
			return false;
		}
		waitResult.region.click();
		util.sleep(1);
		waitResult.region.click();

		return true;
	}

	public void ValidaAlertaSeguranca() {
		util.log.info("[" + util.getMetod() + "] - Valida msg de alerta 'AlertSecurity'");
		WaitResult waitResult = utilAut.WaitFor(5, new String[] { "GIA_SP\\AlertSecurity" });
		if (waitResult.region != null) {
			waitResult = utilAut.WaitFor(5, new String[] { "GIA_SP\\AlertSecurityFlag" });
			if (waitResult.region != null) {
				waitResult.region.click();
			}
			waitResult = utilAut.WaitFor(5, new String[] { "GIA_SP\\AlertSecurityOK" });
			if (waitResult.region != null) {
				waitResult.region.click();
				util.sleep(5);
			}
		}
	}

	// --------------------------------------------------------------------------------------------------------------
	public void ValidarSeExisteContribuinte() {
		util.log.info("[" + util.getMetod() + "] - Valida se existe registros antigos");
		WaitResult waitResult = utilAut.WaitFor(10, new String[] { "GIA_SP\\ValidaSeExisteCadastro" });

		if (this.MigrarDatabase()) {
			this.ClickNo();
			util.sleep(1);
			s.type(Key.ENTER);

			if (this.MigrarArquivoDatabase()) {
				this.ClicarNao();
				util.sleep(1);
				s.type(Key.ENTER);
			}

		} else {
			if (waitResult.region != null) {
				waitResult = utilAut.WaitFor(10, new String[] { "GIA_SP\\BotaoExcluirRegistro" });
				if (waitResult.region != null) {
					waitResult.region.click();
					this.ClicarYes();
					util.sleep(3);
					this.ClicarOk();
				}
			} else {
				if (this.MigrarDatabase()) {
					this.ClickNo();
					util.sleep(1);
					s.type(Key.ENTER);
				}

				if (this.MigrarArquivoDatabase()) {
					this.ClicarNao();
					util.sleep(1);
					s.type(Key.ENTER);
				}
			}
		}

		this.ClicarFechar();
		util.sleep(1);
		this.ClickYes();

	}

	public boolean Encerrar() {
		this.AtivarPrograma();
		s.type(Key.F4, Key.ALT);
		util.sleep(3);
		ClicarSim();
		return true;
	}

	public void Encerrar2() {
		s.type(Key.F4, Key.ALT);
		util.sleep(3);
		s.type(Key.LEFT);
		s.type(Key.ENTER);
	}

	public void FechaMSGAtualizacaoDatabases() {
		waitResult = utilAut.WaitFor(10, new String[] { "GIA_SP\\MigrarParaVersoesAnterioes" });
		if (waitResult.region != null) {
			utilAut.Click("GIA_SP\\BotaoNao2");
		}
		waitResult = utilAut.WaitFor(10, new String[] { "GIA_SP\\FecharMsgParaMigrarBanco" });
		if (waitResult.region != null) {
			utilAut.Click("GIA_SP\\BotaoOk");
		}

	}

	public boolean ClicarNao() {
		return utilAut.Click("GIA_SP\\BotaoNao");
	}

	public void ClickNo() {
		utilAut.Click("GIA_SP\\BotaoNo");
	}

	public void ClickYes() {
		utilAut.Click("GIA_SP\\BotaoYes");
	}

	public boolean ClicarSim() {
		return utilAut.Click("GIA_SP\\BotaoSim");
	}

	public boolean ClicarYes() {
		return utilAut.Click("GIA_SP\\BotaoYes2");
	}

	public boolean ClicarFechar() {
		return utilAut.Click("GIA_SP\\BotaoFechar");
	}

	public boolean ClicarOk() {
		waitResult = utilAut.WaitFor(10, new String[] { "GIA_SP\\BotaoOk", "GIA_SP\\BotaoOk2", "GIA_SP\\BotaoOk3" });
		if (waitResult.region != null) {
			waitResult.region.click();
			return true;
		}
		return false;
		// return utilAut.Click("GIA_SP\\BotaoOk","GIA_SP\\BotaoOk2");
	}

	public boolean ClicarExcluir() {
		return utilAut.Click("GIA_SP\\BotaoExcluir");
	}

//	public boolean ClicarImportar() {
//		return utilAut.Click("GIA_SP\\BotaoImportar");
//	}

	public void ClicarImportar() {

		utilAut.Click("GIA_SP\\BotaoImportar");

		WaitResult waitResult = utilAut.WaitFor(15,
				new String[] { "GIA_SP\\MensagemSobrepor", "GIA_SP\\MensagemSobrepor2" });
		if (waitResult.region != null)
			ClicarYes();

		waitResult = utilAut.WaitFor(15, new String[] { "GIA_SP\\MensagemImportacao", "GIA_SP\\MensagemImportacao2" });
		if (waitResult.region != null) {
			waitResult.region.click();
			ClicarOk();
		}
	}

	public boolean ClicarMenuUtilitario() {
		return utilAut.Click("GIA_SP\\MenuUtilitario");
	}

	public boolean ClicarMenuGia() {
		return utilAut.Click("GIA_SP\\MenuGia");
	}

	public boolean MigrarArquivoDatabase() {
		return utilAut.Existe("GIA_SP\\MensagemArquivoDatabase", 10);
	}

	public boolean MigrarDatabase() {
		return utilAut.Existe("GIA_SP\\MigrarDatabase", 10);
	}

	public boolean TelaContribuinteAtiva() {
		return utilAut.Existe("GIA_SP\\TelaContribuinte", 10);
	}

	public boolean MenuImportar() {
		ClicarMenuUtilitario();
		util.sleep(2);
		s.type("i");
		return true;
	}

	public boolean MenuContribuinte() {
		// ClicarMenuGia();
		s.type("g", Key.ALT);
		util.sleep(1);
		s.type("c");
		return true;
	}

	public boolean MenuGerarGia() {
		// ClicarMenuGia();
		s.type("g", Key.ALT);
		util.sleep(1);
		s.type("g");
		return true;
	}

	public boolean ClicarSelecionarArquivo() {
		return utilAut.Click("GIA_SP\\SelecionarArquivo");
	}

	public boolean MensagemConsistencia() {
		return utilAut.Existe("GIA_SP\\MensagemConclusaoConsistencia", 60);
	}

	public boolean MensagemImportacao() {
		return utilAut.Existe("GIA_SP\\MensagemImportacao", 60);
	}

	public boolean MensagemExclusaoContribuinte() {
		return utilAut.Existe("GIA_SP\\ConfirmaExclusaoContribuinte", 10);
	}

	public String ResultadoImportacao() {
		WaitResult waitResult = utilAut.WaitFor(60,
				new String[] { "GIA_SP\\MensagemImportacao", "GIA_SP\\MensagemSobrepor" });
		if (waitResult.region == null)
			return "";
		return waitResult.imageFound;

	}

	public boolean ClicarSelecionar() {
		return utilAut.Click("GIA_SP\\BotaoSelecionar");
	}

	public boolean ClicarAbaConsistir() {
		return utilAut.Click("GIA_SP\\TabGiaNaoConsistida");
	}

	public boolean ClicarConsistir() {
		return utilAut.Click("GIA_SP\\BotaoConsistir");
	}

	public boolean MensagemFimConsistencia() {
		return utilAut.Existe("GIA_SP\\MensagemConsistenciaSucesso2", 60);
	}

	public boolean MensagemAviso() {
		return utilAut.Existe("GIA_SP\\Aviso2", 10);
	}

	public boolean ClicarAbaNormal() {
		return utilAut.Click("GIA_SP\\TabGiaNormal");
	}

	public boolean ClicarGerarNormal() {
		return utilAut.Click("GIA_SP\\BotaoGerarGiaNormal");
	}

	public boolean MensagemSalvarArquivo() {
		return utilAut.Existe("GIA_SP\\MensagemSalvarOutroLocal", 60);
	}

	public boolean MensagemAbrirNavegador() {
		return utilAut.Existe("GIA_SP\\MensagemAbrirTransmissao", 60);
	}

	public boolean Web_ContribuinteSelecionado() {
		return utilAut.Existe("GIA_SP\\Web_ContribuinteSelecionado");
	}

	public boolean Web_SelecionarContribuinte() {
		if (!utilAut.Click("GIA_SP\\Web_Selecione"))
			return false;
		s.type(Key.DOWN);
		s.type(Key.TAB);
		return true;
	}

	public boolean Web_Autenticacao(String giaUsuario, String giaSenha) {
		if (!utilAut.Click("GIA_SP\\Web_InputUsuario"))
			return false;
		s.type(giaUsuario);

		if (!utilAut.Click("GIA_SP\\Web_InputSenha"))
			return false;
		s.type(giaSenha);
		s.type(Key.ENTER);

		return true;
	}

	public boolean Web_LinkGia() {
		return utilAut.Click("GIA_SP\\Web_LinkGia", 60);
	}

	public boolean Web_LinkEnvioGia() {
		return utilAut.Click("GIA_SP\\Web_LinkEnvioGia", 60);
	}

	public boolean Web_BotaoSelecionarGia() {
		return utilAut.Click("GIA_SP\\Web_BotaoSelecionarGia", 60);
	}

	public boolean Web_BotaoEnviarGia() {
		return utilAut.Click("GIA_SP\\Web_BotaoEnviarGia", 60);
	}

	public boolean Web_BotaoImprimir() {
		return utilAut.Click("GIA_SP\\Web_BotaoImprimir", 60);
	}

	public boolean Web_BotaoSalvarComprovante() {
		return utilAut.Click("GIA_SP\\Web_BotaoSalvarComprovante", 60);
	}

	public boolean Web_MenuServico() {
		return utilAut.Click("GIA_SP\\Web_MenuServico", 60);
	}

	public boolean Web_MenuServico2() {
		return utilAut.Click("GIA_SP\\Web_MenuServico2", 60);
	}

	public boolean Web_MenuICMS() {
		return utilAut.Click("GIA_SP\\Web_MenuICMS", 60);
	}

	// -------------------------------------------------------------------------------
	public String getIECodeByFileName(String fileAddress) {
		String codigoNomeArq = "";

		Pattern p = Pattern.compile("\\d{3}");
		Matcher m = p.matcher(fileAddress);

		while (m.find())
			codigoNomeArq = m.group();

		return codigoNomeArq;
	}

	// -------------------------------------------------------------------------------
	@SuppressWarnings("resource")
	public String[] getRegisterByIECode(String codeByFileName, String fileAddress) {

		String codigoIE = "";
		String[] fields = null;

		try {

			BufferedReader br;
			br = new BufferedReader(new FileReader(fileAddress));
			String line;
			line = br.readLine();

			while ((line = br.readLine()) != null) {
				fields = line.split(Pattern.quote("|"));
				codigoIE = fields[0];
				if (codigoIE.equals(codeByFileName)) {
					return fields;
				}
			}

			br.close();
			br = null;

		} catch (FileNotFoundException e) {
			util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
}
