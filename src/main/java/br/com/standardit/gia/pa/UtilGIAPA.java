package br.com.standardit.gia.pa;

import org.sikuli.script.Key;
import org.sikuli.script.Screen;

import br.com.standardit.UtilAutomation;
import br.com.standardit.WaitResult;
import br.com.standardit.util;

public class UtilGIAPA {
	
	Screen s = new Screen();
	public UtilAutomation utilAut;
	String pathInstalacao;
	String watchPath;
	
	public UtilGIAPA(Screen s, String pathInstalacao, String watchPath) {
		this.s = s;
		this.utilAut = new UtilAutomation(s);
		this.pathInstalacao = pathInstalacao;
		this.watchPath = watchPath;
	}
	
	public boolean AtivarPrograma() {
		WaitResult waitResult = utilAut.WaitFor(30, new String[] { "GIA_PA\\Titulo" });
		if (waitResult.region == null) {
			br.com.standardit.util.PrintErro(watchPath + "\\Erro.png");
			return false;
		}
		
		waitResult.region.click();
		util.sleep(1);
		return true;
	}

	public boolean Encerrar() {
		this.AtivarPrograma();
		s.type(Key.F4, Key.ALT);
		return true;
	}
	
	public boolean MenuImportar() {
		return this.utilAut.Click("GIA_PA\\MenuImportarDeclaracaoCompleta");
	}
	
	public boolean SelecionarArquivo(String filename) {
		if(!this.utilAut.Click("GIA_PA\\BotaoImportarSelecionarArquivo"))
			return false;
		util.sleep(1);
		s.type(filename);
		s.type(Key.ENTER);
		return true;
	}
	
	public boolean AnalisarArquivo() {
		if(!this.utilAut.Click("GIA_PA\\BotaoAnalisarArquivo"))
			return false;
		
		WaitResult waitResult = this.utilAut.WaitFor(60, new String[] { "GIA_PA\\MensagemNaoHouveErros", "GIA_PA\\Erro" });
		
		//Se não encontrar nada, retorne erro
		if (waitResult.region == null)
			return false;
		
		//Se encontrar mensagem de sucesso, retorna true
		if(waitResult.imageFound == "GIA_PA\\MensagemNaoHouveErros")
			return true;

		return false;
		//return this.utilAut.Existe("GIA_PA\\MensagemNaoHouveErros", 60);
	}
	
	public boolean ImportarArquivo() {
		if(!this.utilAut.Click("GIA_PA\\BotaoImportar"))
			return false;
		
		WaitResult waitResult = this.utilAut.WaitFor(60, new String[] { "GIA_PA\\MensagemImportacaoSucesso", "GIA_PA\\MensagemImportacaoErro", "GIA_PA\\Erro" });
		
		//Se não encontrar nada, retorne erro
		if (waitResult.region == null)
			return false;
		
		//Se encontrar mensagem de sucesso, retorna true
		if(waitResult.imageFound == "GIA_PA\\MensagemImportacaoSucesso") {
			s.type(Key.ENTER);
			
			return true;
		}
		return false;
	}
	
	public boolean SalvarErrosAnalise(String file) {
		
		if(this.utilAut.Click("GIA_PA\\GridColunaStatus")) {
			s.type("a", Key.CTRL);
			util.sleep(1);
			s.type("c", Key.CTRL);
			util.sleep(1);
			String content = util.paste(this);
			util.SaveStringToFile(content, file);
		}
		
		return true;
	}
	public boolean FecharImportacao() {
		return this.utilAut.Click("GIA_PA\\BotaoImportarSair");
	}

	public boolean MenuAbrirDeclaracao() {
		return this.utilAut.Click("GIA_PA\\MenuAbrirDeclaracao");
	}

	public boolean Consultar() {
		if(!this.utilAut.Click("GIA_PA\\BotaoConsultar"))
			return false;
		if(this.utilAut.Existe("GIA_PA\\BuscaSemResultado", 3)) {
			s.type(Key.ENTER);
			return false;
		}
		
		return true;
	}
	
	public boolean ClicarDeclaracao() {
		if(!this.utilAut.Click("GIA_PA\\CabecalhoGridAbrirDeclaracao"))
			return false;
		s.type(Key.TAB);
		s.type(Key.TAB);
		s.type(Key.TAB);
		s.type(Key.TAB);
		s.type(Key.TAB);
		
		return true;
	}
	
	public boolean ClicarAbrirDeclaracao() {
		if(!this.ClicarDeclaracao())
			return false;
		
		return (this.utilAut.Click("GIA_PA\\BotaoAbrirDeclaracao"));
	}

	public boolean ClicarExcluir() {
		
		if(!this.utilAut.Click("GIA_PA\\BotaoExcluirDeclaracao"))
			return false;
		
		if(this.utilAut.Existe("GIA_PA\\MensagemConfirmaExclusao", 3)){
			s.type(Key.ENTER);
			if(this.utilAut.Existe("GIA_PA\\MensagemExclusao")){
				s.type(Key.ENTER);
			}
		}
		
		return true; 
	}

	public boolean ClicarSair() {
		return (this.utilAut.Click("GIA_PA\\BotaoSair"));
	}

	public boolean GerarAnexoIII() {
		 if (!this.utilAut.Click("GIA_PA\\BotaoAnexoIII"))
			 return false;
		 
		 if(this.utilAut.Existe("GIA_PA\\Advertencia") || this.utilAut.Existe("GIA_PA\\Advertencia2"))
			 s.type(Key.ENTER);
			 //this.utilAut.Click("GIA_PA\\BotaoOK");
		 
		 if (!this.utilAut.Click("GIA_PA\\CheckOutrasObrigacoes"))
			 return false;
		 
		 if(this.utilAut.Existe("GIA_PA\\MensagemAdvertenciaExclusao"))
			 s.type(Key.ENTER);
			 
		 s.type(Key.F4, Key.ALT);
		 
		 if(this.utilAut.Existe("GIA_PA\\MensagemDeclaracaoSalvaSucesso"))
			 s.type(Key.ENTER);
		 
		 return true;
	}
/*
	public boolean GerarAnexoII() {
		 if (!this.utilAut.Click("GIA_PA\\BotaoAnexoII"))
			 return false;
		 
		 if(this.utilAut.Existe("GIA_PA\\Advertencia") || this.utilAut.Existe("GIA_PA\\Advertencia2"))
			 this.utilAut.Click("GIA_PA\\BotaoOK");
		 
		 
		 s.type(Key.F4, Key.ALT);
		 
		 if(this.utilAut.Existe("GIA_PA\\MensagemDeclaracaoSalvaSucesso"))
			 s.type(Key.ENTER);
		 
		 return true;
	}
*/
	
	public boolean ResumoApuracoes() {
		if (!this.utilAut.Click("GIA_PA\\BotaoResumoApuracoes"))
			if (!this.utilAut.Click("GIA_PA\\BotaoResumoApuracoes2"))
				return false;
		
		if (!this.utilAut.Click("GIA_PA\\CabecalhoGridResumo"))
			 return false;
		
		int i = 1;
		while(i < 20) {
			s.type(Key.TAB);
			s.type(Key.F2);
			i++;
		}
			
		if (!this.utilAut.Click("GIA_PA\\BotaoProximo"))
			 return false;
		
		if(this.utilAut.Existe("GIA_PA\\IconeInformacao"))
			 s.type(Key.ENTER);
		
		if (!this.utilAut.Click("GIA_PA\\CabecalhoGridResumo"))
			 return false;
		
		i = 1;
		while(i < 30) {
			s.type(Key.TAB);
			s.type(Key.F2);
			i++;
		}
		
		if (!this.utilAut.Click("GIA_PA\\BotaoSalvar"))
			 return false;
		
		if(this.utilAut.Existe("GIA_PA\\MensagemDeclaracaoSalvaSucesso"))
			 s.type(Key.ENTER);
		
		s.type(Key.F4, Key.ALT);
		
		return true;
	}
	
	public boolean CheckList(String filename) {
		
		boolean retorno = true;
		
		if(!Consultar())
			return false;
		
		if(!ClicarDeclaracao())
			return false;

		if (!this.utilAut.Click("GIA_PA\\BotaoCheckList"))
			 return false;
		
		WaitResult waitResult = this.utilAut.WaitFor(60, new String[] { "GIA_PA\\MensagemDeclaracaoCorreta", "GIA_PA\\Erro" });
		
		//Se não encontrar nada, retorne erro
		if (waitResult.region == null || waitResult.imageFound == "GIA_PA\\Erro") {
			br.com.standardit.util.PrintErro(watchPath + "\\" + filename +  ".png");
			retorno = false;
		}
		
		s.type(Key.ENTER);
		
		if(this.utilAut.Click("GIA_PA\\BotaoImprimirCheckList")) {
			waitResult = this.utilAut.WaitFor(60, new String[] { "GIA_PA\\IconeInformacao", "GIA_PA\\Erro" });
			
			if (waitResult.region == null || waitResult.imageFound == "GIA_PA\\Erro") {
				br.com.standardit.util.PrintErro(watchPath + "\\" + filename +  ".png");
				retorno = false;
			}
			s.type(Key.TAB);
			s.type(Key.ENTER);
			
			try {
				util.MoveFiles(pathInstalacao + "\\CheckList", watchPath);
			} catch (InterruptedException e) {
				util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
				e.printStackTrace();
			}
		}
		
		if(retorno) {
			if (!this.utilAut.Click("GIA_PA\\BotaoGerarArquivo"))
				 return false;
			
			if (!(this.utilAut.Existe("GIA_PA\\MensagemMidiaGeradaSucesso") || this.utilAut.Existe("GIA_PA\\MensagemArquivoExiste")))
				 return false;
			
			s.type(Key.ENTER);
		}
		
		s.type(Key.F4, Key.ALT);
		
		return retorno;
	}
	
	public boolean FecharDeclaracao() {
		if (!this.utilAut.Click("GIA_PA\\BotaoSair"))
			 return false;
		
		if(this.utilAut.Existe("GIA_PA\\MensagemSalvarDeclaracao", 3))
			 s.type(Key.ENTER);
		
		if(this.utilAut.Existe("GIA_PA\\MensagemDeclaracaoSalvaSucesso", 3))
			 s.type(Key.ENTER);
		
		return true;
	}

}
