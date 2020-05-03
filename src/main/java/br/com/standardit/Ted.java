package br.com.standardit;

import java.util.Properties;

import org.sikuli.script.Key;
import org.sikuli.script.Screen;

public class Ted {
	public UtilAutomation utilAut;
	Screen s = new Screen();
	String processandoPath;
	WaitResult waitResult;

	public Ted(Screen s, String processandoPath) {
		this.s = s;
		this.utilAut = new UtilAutomation(s);
		this.processandoPath = processandoPath;
	}

//-------------------------------------------------------------------------------------------------------
	public boolean teste(Properties prop) {

		waitResult = utilAut.WaitFor(30, new String[] { "TED\\TED_Home" });
		if (waitResult.region != null) {
			waitResult = utilAut.WaitFor(3, new String[] { "TED\\Insere_Email" });
			if (waitResult.region != null)
				s.type(prop.getProperty("Email_RS_TED"));

			waitResult = utilAut.WaitFor(3, new String[] { "TED\\Insere_Confirmacao_Email" });
			if (waitResult.region != null)
				s.type(prop.getProperty("Email_RS_TED"));

			waitResult = utilAut.WaitFor(3, new String[] { "TED\\Insere_Endereco_Comprovante" });
			if (waitResult.region != null)
				s.type(prop.getProperty("Email_RS_TED"));

			waitResult = utilAut.WaitFor(3, new String[] { "TED\\Botao_Enviar" });
			if (waitResult.region != null)
				waitResult.region.click();

		} else {
			return false;
		}

		return true;
	}

	public boolean EnviarArquivo(String file) {

		WaitResult waitResult = utilAut.WaitFor(10, new String[] { "GIA_MA\\SelecionarArquivo" });
		if (waitResult.region == null)
			return false;
		waitResult.region.click();

		s.type(file);
		s.type(Key.ENTER);

		waitResult = utilAut.WaitFor(3, new String[] { "GIA_MA\\Enviar" });
		if (waitResult.region == null)
			return false;
		waitResult.region.click();

		waitResult = utilAut.WaitFor(60,
				new String[] { "GIA_MA\\TransmissaoExecutadaComSucesso", "GIA_MA\\FalhaTransmissao" });
		if (waitResult.region == null)
			return false;

		if (waitResult.imageFound == "GIA_MA\\FalhaTransmissao") {
			util.log.error("[" + util.getMetod() + "] - Processamento com erro: [Falha_Na_Transmissao]");
			util.PrintErro(processandoPath + "//Error[Falha_Na_Transmissao].png");

			waitResult = utilAut.WaitFor(60, new String[] { "GIA_MA\\BotaoNo" });
			if (waitResult.region != null)
				waitResult.region.click();

			waitResult = utilAut.WaitFor(60, new String[] { "GIA_MA\\EncerrarTED", "GIA_MA\\EncerrarTED2" });
			if (waitResult.region == null)
				return false;
			waitResult.region.click();

			// this.ClicarCancelar();

			// this.ClicarCancelar();

			return false;
		} else {
			s.type(Key.ENTER);

			waitResult = utilAut.WaitFor(60, new String[] { "GIA_MA\\ComprovanteTransmissao" });
			if (waitResult.region == null)
				return false;

			util.PrintErro(file.toLowerCase().replace(".dcx", "_Comprovante.png").replace(".dec", "_Comprovante.png"));

			s.type("a", Key.CTRL);
			util.sleep(1);
			s.type("c", Key.CTRL);
			util.sleep(1);
			String content = util.paste(this);
			util.SaveStringToFile(content,
					file.toLowerCase().replace(".dcx", "_Comprovante.txt").replace(".dec", "_Comprovante.txt"));

			waitResult = utilAut.WaitFor(60, new String[] { "GIA_MA\\Fechar" });
			if (waitResult.region == null)
				return false;
			waitResult.region.click();

			waitResult = utilAut.WaitFor(60, new String[] { "GIA_MA\\EncerrarTED", "GIA_MA\\EncerrarTED2" });
			if (waitResult.region == null)
				return false;
			waitResult.region.click();

			// this.ClicarCancelar();

			return true;
		}
	}

//------------------------------------------------------------------------------------------------------------------------------------------
	public boolean EnviarArquivoRS(String file, String giaUsuario, String giaSenha) {

		WaitResult waitResult = utilAut.WaitFor(30, new String[] { "GIA_RS\\SelecionarArquivo" });
		if (waitResult.region == null)
			return false;
		waitResult.region.click();
		util.sleep(2);

		s.type(file);
		s.type(Key.ENTER);

		util.sleep(5);
		waitResult = utilAut.WaitFor(3, new String[] { "GIA_RS\\Enviar" });
		if (waitResult.region == null)
			return false;
		waitResult.region.click();

		waitResult = utilAut.WaitFor(10, new String[] { "GIA_RS\\Autenticacao_Remetente" });
		if (waitResult.region == null)
			return false;

		s.type(giaUsuario);
		s.type(Key.TAB);
		s.type(giaSenha);
		s.type(Key.TAB);
		s.type(giaSenha);
		s.type(Key.ENTER);
		//

		waitResult = utilAut.WaitFor(60,
				new String[] { "GIA_RS\\TransmissaoExecutadaComSucesso", "GIA_RS\\FalhaTransmissao" });
		if (waitResult.region == null)
			return false;

		if (waitResult.imageFound == "GIA_RS\\FalhaTransmissao") {
			util.log.error("[" + util.getMetod() + "] - Processamento com erro: [Falha_Na_Transmissao]");
			util.PrintErro(processandoPath + "//Error[Falha_Na_Transmissao].png");

			waitResult = utilAut.WaitFor(60, new String[] { "GIA_RS\\BotaoNo" });
			if (waitResult.region != null)
				waitResult.region.click();

			waitResult = utilAut.WaitFor(60, new String[] { "GIA_RS\\EncerrarTED", "GIA_RS\\EncerrarTED2" });
			if (waitResult.region == null)
				return false;
			waitResult.region.click();

			// this.ClicarCancelar();

			// this.ClicarCancelar();

			return false;
		} else {
			util.sleep(2);
			s.type(Key.ENTER);

			util.sleep(2);
			s.type(Key.ENTER);

			waitResult = utilAut.WaitFor(60, new String[] { "GIA_RS\\ComprovanteTransmissao" });
			if (waitResult.region == null)
				return false;

			util.log.info("[" + util.getMetod() + "] - file: " + file);

			util.PrintErro(file.toLowerCase().replace(".dcx", "_Comprovante.txt").replace(".dec", "_Comprovante.txt")
					.replace(".rcb", "_Comprovante.png").replace(".ted", "_Comprovante.png"));

			s.type("a", Key.CTRL);
			util.sleep(1);
			s.type("c", Key.CTRL);
			util.sleep(1);
			String content = util.paste(this);
			util.SaveStringToFile(content,
					file.toLowerCase().replace(".dcx", "_Comprovante.txt").replace(".dec", "_Comprovante.txt")
							.replace(".rcb", "_Comprovante.txt").replace(".ted", "_Comprovante.txt"));

			waitResult = utilAut.WaitFor(60, new String[] { "GIA_RS\\Fechar" });
			if (waitResult.region == null)
				return false;
			waitResult.region.click();

			waitResult = utilAut.WaitFor(60, new String[] { "GIA_RS\\EncerrarTED", "GIA_RS\\EncerrarTED2" });
			if (waitResult.region == null)
				return false;
			waitResult.region.click();

			// this.ClicarCancelar();

			waitResult = utilAut.WaitFor(5, new String[] { "TED\\Erro_na_transmissao_do_arquivo" });
			if (waitResult.region != null)
				waitResult = utilAut.WaitFor(5, new String[] { "TED\\Erro_na_transmissao_do_arquivo" });
			if (waitResult.region != null)
				waitResult.region.click();

			return true;
		}
	}
}
