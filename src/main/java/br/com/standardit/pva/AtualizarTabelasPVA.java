package br.com.standardit.pva;

import org.sikuli.script.Key;
import org.sikuli.script.Screen;

import br.com.standardit.WaitResult;
import br.com.standardit.util;

public class AtualizarTabelasPVA {

	Screen s;
	UtilPVA utilPVA;

	public AtualizarTabelasPVA(Screen s) {
		this.s = s;

		utilPVA = new UtilPVA(s);
	}

	public boolean AguardarAtualizarTabelas() {
		util.log.info("[" + util.getMetod() + "] - Aguardanto Atualização das tabelas...");
		WaitResult waitResult = utilPVA.utilAut.WaitFor(0, new String[] { "AtualizarTabelas_Fim" });
		if (waitResult.region == null)
			return false;
		s.type(Key.ENTER);
		return true;
	}

}
