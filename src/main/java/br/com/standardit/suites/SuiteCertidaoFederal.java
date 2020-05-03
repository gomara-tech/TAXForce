package br.com.standardit.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import br.com.standardit.ecertidoes.Federal.CertidaoFederal;

@RunWith(Suite.class)
@SuiteClasses({
	CertidaoFederal.class
})
public class SuiteCertidaoFederal {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}
}
