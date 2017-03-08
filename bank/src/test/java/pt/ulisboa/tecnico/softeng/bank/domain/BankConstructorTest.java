package pt.ulisboa.tecnico.softeng.bank.domain;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pt.ulisboa.tecnico.softeng.bank.exception.BankException;

public class BankConstructorTest {

	@Before
	public void setUp() {

	}

	@Test
	public void success() {
		Bank bank = new Bank("Money", "BK01");

		Assert.assertEquals("Money", bank.getName());
		Assert.assertEquals("BK01", bank.getCode());
		Assert.assertEquals(1, Bank.banks.size());
		Assert.assertEquals(0, bank.getNumberOfAccounts());
		Assert.assertEquals(0, bank.getNumberOfClients());
	}

	@Test
	public void nullName() {
		try {
			new Bank(null, "BA01");
			Assert.fail();
		} catch (BankException be) {
			Assert.assertTrue(Bank.banks.isEmpty());
		}
	}

	@Test
	public void emptyName() {
		try {
			new Bank("", "BA01");
			Assert.fail();
		} catch (BankException be) {
			Assert.assertTrue(Bank.banks.isEmpty());
		}
	}

	@Test
	public void nullCode() {
		try {
			new Bank("Bank 1", null);
			Assert.fail();
		} catch (BankException be) {
			Assert.assertTrue(Bank.banks.isEmpty());
		}
	}

	@Test
	public void emptyCode() {
		try {
			new Bank("Bank 1", "");
			Assert.fail();
		} catch (BankException be) {
			Assert.assertTrue(Bank.banks.isEmpty());
		}
	}

	@Test
    public void shortCode() {
	    try {
	        new Bank("Bank 1", "BA1");
	        Assert.fail();
        } catch (BankException be) {
	        Assert.assertTrue(Bank.banks.isEmpty());
        }
    }

	@Test
    public void longCode() {
	    try {
	        new Bank("Bank 1", "BA001");
	        Assert.fail();
        } catch (BankException be) {
	        Assert.assertTrue(Bank.banks.isEmpty());
        }
    }

	@Test
    public void uniqueCode() {
	    Bank bank = new Bank("Bank 1", "BA01");

	    try {
	        new Bank("Bank 2", "BA01");
	        Assert.fail();
        } catch (BankException be) {
            Assert.assertEquals(1, Bank.banks.size());
            Assert.assertTrue(Bank.banks.contains(bank));
        }
    }

	@After
	public void tearDown() {
		Bank.banks.clear();
	}
}
