package pt.ulisboa.tecnico.softeng.bank.domain;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pt.ulisboa.tecnico.softeng.bank.dataobjects.BankOperationData;
import pt.ulisboa.tecnico.softeng.bank.exception.BankException;

public class BankGetOperationDataMethodTest {
	private static final int AMOUNT = 300;
	private static final String DEPOSIT = "Deposit";
	private static final String WITHDRAW = "Withdraw";
	
	private Bank bank;
	private Client client;
	private Account account;
	
	@Before
	public void setUp() {
		this.bank = new Bank("Money", "BK01");
		this.client = new Client(this.bank, "António");
		this.account = new Account(this.bank, this.client);
	}
	
	@Test
	public void successDeposit() {
		String reference = this.account.deposit(AMOUNT);
		BankOperationData bod = Bank.getOperationData(reference);
		
		Assert.assertEquals(reference, bod.getReference());
		Assert.assertEquals(DEPOSIT, bod.getType());
		Assert.assertEquals(this.account.getIBAN(), bod.getIban());
		Assert.assertEquals(AMOUNT, bod.getValue());
		Assert.assertEquals(this.bank.getOperation(reference).getTime(), bod.getTime());
		
	}
	
	@Test
	public void successWithdraw() {
		this.account.deposit(1000);
		String reference = this.account.withdraw(AMOUNT);
		BankOperationData bod = Bank.getOperationData(reference);
		
		Assert.assertEquals(reference, bod.getReference());
		Assert.assertEquals(WITHDRAW, bod.getType());
		Assert.assertEquals(this.account.getIBAN(), bod.getIban());
		Assert.assertEquals(AMOUNT, bod.getValue());
		Assert.assertEquals(this.bank.getOperation(reference).getTime(), bod.getTime());
		
	}
	
	@Test(expected = BankException.class)
	public void nullReference() {
		Bank.getOperationData(null);
	}
	
	@Test(expected = BankException.class)
	public void emptyReference() {
		Bank.getOperationData(" ");
	}
	
	@Test(expected = BankException.class)
	public void wrongReference() {
		Bank.getOperationData("OlaEuSouUmErro");
	}
	
	@After
	public void tearDown() {
		Bank.banks.clear();
	}
}
