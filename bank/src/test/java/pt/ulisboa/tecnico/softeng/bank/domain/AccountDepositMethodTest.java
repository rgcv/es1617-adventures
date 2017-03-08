package pt.ulisboa.tecnico.softeng.bank.domain;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pt.ulisboa.tecnico.softeng.bank.exception.BankException;

import pt.ulisboa.tecnico.softeng.bank.exception.BankException;

public class AccountDepositMethodTest {
	private Bank bank;
	private Account account;

	@Before
	public void setUp() {
		this.bank = new Bank("Money", "BK01");
		Client client = new Client(this.bank, "António");
		this.account = new Account(this.bank, client);
	}

	@Test
	public void success() {
		String reference = this.account.deposit(50);

		Assert.assertEquals(50, this.account.getBalance());
		Operation operation = this.bank.getOperation(reference);
		Assert.assertEquals(Operation.Type.DEPOSIT, operation.getType());
		Assert.assertEquals(this.account, operation.getAccount());
		Assert.assertEquals(50, operation.getValue());
	}

	@Test
	public void zeroAmount() {
		int balance = this.account.getBalance();

		try {
			this.account.deposit(0);
			Assert.fail();
		} catch (BankException be) {
			Assert.assertEquals(balance, this.account.getBalance());
		}
	}

	@Test
	public void negativeAmount() {
		int balance = this.account.getBalance();

		try {
			this.account.deposit(-1);
			Assert.fail();
		} catch (BankException be) {
			Assert.assertEquals(balance, this.account.getBalance());
		}
	}

	@After
	public void tearDown() {
		Bank.banks.clear();
	}

}
