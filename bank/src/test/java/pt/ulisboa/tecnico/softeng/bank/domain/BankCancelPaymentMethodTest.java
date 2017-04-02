package pt.ulisboa.tecnico.softeng.bank.domain;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import org.junit.After;

import pt.ulisboa.tecnico.softeng.bank.exception.BankException;

public class BankCancelPaymentMethodTest {
	
	Bank bank;
	private Account acc;
	
	@Before 
	public void setup(){
		this.bank = new Bank("Bk", "BK01");
		Client cl = new Client(this.bank, "Ana");
		this.acc = new Account(this.bank, cl);
		this.acc.deposit(150);
	}
	
	
	@Test
	public void success(){
		String ref = this.acc.withdraw(20);
		String res = Bank.cancelPayment(ref);
		
		Assert.assertNotNull(res);
		
		Operation opRef = this.bank.getOperation(ref);
		Operation opRes = this.bank.getOperation(res);
		
		Assert.assertEquals(opRef.getValue(), opRes.getValue());
		Assert.assertEquals(150, this.acc.getBalance());
	}
	
	@Test (expected = BankException.class)
	public void emptyRef(){
		Bank.cancelPayment("");
	}

	@Test (expected = BankException.class)
	public void blankRef(){
		Bank.cancelPayment("  ");
	}

	@Test (expected = BankException.class)
	public void depositRef(){
		String ref = this.acc.deposit(27);
		Bank.cancelPayment(ref);
	}
	
	
	@After
	public void tearDown(){
		Bank.banks.clear();
	}
}