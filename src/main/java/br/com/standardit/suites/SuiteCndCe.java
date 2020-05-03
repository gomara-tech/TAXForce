package br.com.standardit.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import br.com.standardit.ecertidoes.CertidaoNegativaCE;

@RunWith(Suite.class)
@SuiteClasses({
	CertidaoNegativaCE.class
})
public class SuiteCndCe {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}
}
