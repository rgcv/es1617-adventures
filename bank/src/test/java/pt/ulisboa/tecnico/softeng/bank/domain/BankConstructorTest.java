package pt.ulisboa.tecnico.softeng.bank.domain;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pt.ulisboa.tecnico.softeng.bank.exception.BankException;

public class BankConstructorTest {

	private static final String BANK_NAME = "Money";
	private static final String BANK_CODE = "BK01";

	@Before
	public void setUp() {

	}

	@Test
	public void success() {
		Bank bank = new Bank(BANK_NAME, BANK_CODE);

		Assert.assertEquals(BANK_NAME, bank.getName());
		Assert.assertEquals(BANK_CODE, bank.getCode());
		Assert.assertEquals(1, Bank.banks.size());
		Assert.assertEquals(0, bank.getNumberOfAccounts());
		Assert.assertEquals(0, bank.getNumberOfClients());
	}

	@Test
	public void nullName() {
		try {
			new Bank(null, BANK_CODE);
			Assert.fail();
		} catch (BankException be) {
			Assert.assertTrue(Bank.banks.isEmpty());
		}
	}

	@Test
	public void emptyName() {
		try {
			new Bank("", BANK_CODE);
			Assert.fail();
		} catch (BankException be) {
			Assert.assertTrue(Bank.banks.isEmpty());
		}
	}

	@Test
	public void nameWithBlanks() {
		try {
			new Bank("     ", BANK_CODE);
			Assert.fail();
		} catch (BankException be) {
			Assert.assertTrue(Bank.banks.isEmpty());
		}
	}

	@Test
	public void nullCode() {
		try {
			new Bank(BANK_NAME, null);
			Assert.fail();
		} catch (BankException be) {
			Assert.assertTrue(Bank.banks.isEmpty());
		}
	}

	@Test
	public void emptyCode() {
		try {
			new Bank(BANK_NAME, "");
			Assert.fail();
		} catch (BankException be) {
			Assert.assertTrue(Bank.banks.isEmpty());
		}
	}

	@Test
	public void codeWithBlanks() {
		try {
			new Bank(BANK_NAME, "    ");
			Assert.fail();
		} catch (BankException be) {
			Assert.assertTrue(Bank.banks.isEmpty());
		}
	}

	@Test
    public void shortCode() {
	    try {
	        new Bank(BANK_NAME, "BK1");
	        Assert.fail();
        } catch (BankException be) {
	        Assert.assertTrue(Bank.banks.isEmpty());
        }
    }

	@Test
    public void longCode() {
	    try {
	        new Bank(BANK_NAME, "BK001");
	        Assert.fail();
        } catch (BankException be) {
	        Assert.assertTrue(Bank.banks.isEmpty());
        }
    }

	@Test
    public void uniqueCode() {
	    Bank bank = new Bank(BANK_NAME, BANK_CODE);

	    try {
	        new Bank("Monee", BANK_CODE);
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
