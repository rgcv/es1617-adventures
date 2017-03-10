package pt.ulisboa.tecnico.softeng.bank.domain;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pt.ulisboa.tecnico.softeng.bank.exception.BankException;

public class ClientConstructorMethodTest {
	Bank bank;

	@Before
	public void setUp() {
		this.bank = new Bank("Money", "BK01");
	}

	@Test
	public void success() {
		Client client = new Client(this.bank, "António");

		Assert.assertEquals("António", client.getName());
		Assert.assertTrue(client.getID().length() >= 1);
		Assert.assertTrue(this.bank.hasClient(client));
	}
	/* -----------------------------------------------------------*/
	
	@Test
	public void nullName() {
		try{
			new Client (this.bank, null);
			Assert.fail();
		} catch (BankException e){
			Assert.assertEquals(0, this.bank.getNumberOfClients());
		}
	}
	
	@Test
	public void emptyName() {
		try{
			new Client(this.bank, " ");
			Assert.fail();
		} catch (BankException e){
			Assert.assertEquals(0, this.bank.getNumberOfClients());
		}
	}
	
	@Test
	public void nullBank() {
		try{
			new Client (null, "Josefina");
			Assert.fail();
		} catch (BankException e){
			Assert.assertEquals(0, this.bank.getNumberOfClients());
		}
	}
	
	/* -----------------------------------------------------------*/
	
	@After
	public void tearDown() {
		Bank.banks.clear();
	}

}
