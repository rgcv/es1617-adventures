package pt.ulisboa.tecnico.softeng.bank.domain;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pt.ulisboa.tecnico.softeng.bank.exception.BankException;

public class AccountConstructorMethodTest {
	Bank bank;
	Client client;

	@Before
	public void setUp() {
		this.bank = new Bank("Money", "BK01");
		this.client = new Client(this.bank, "António");
	}

	@Test
	public void success() {
		Account account = new Account(this.bank, this.client);

		Assert.assertEquals(this.bank, account.getBank());
		Assert.assertTrue(account.getIBAN().startsWith(this.bank.getCode()));
		Assert.assertEquals(this.client, account.getClient());
		Assert.assertEquals(0, account.getBalance());
		Assert.assertEquals(1, this.bank.getNumberOfAccounts());
		Assert.assertTrue(this.bank.hasClient(this.client));
	}


	@Test
	public void nullClient() {
		try {
			new Account(this.bank, null);
			Assert.fail();
		}
		catch(BankException e) {
			Assert.assertEquals(0, this.bank.getNumberOfAccounts());
		}
	}
	
	@Test
	public void nullBank() {
		try{
			new Account(null, this.client);
			Assert.fail();
		} catch (BankException e){
			Assert.assertEquals(0, this.bank.getNumberOfAccounts());
		}
	}
	
	@Test
	public void clientDoesNotBelongToBank() {
		try {
			new Account(this.bank, new Client(new Bank("Dollar", "AK01"), "Antonio"));
			Assert.fail();
		}
		catch (BankException e){
			Assert.assertEquals(0, this.bank.getNumberOfAccounts());
		}
	}
	
	
	@After
	public void tearDown() {
		Bank.banks.clear();
	}

}
