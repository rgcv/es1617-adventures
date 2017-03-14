package pt.ulisboa.tecnico.softeng.broker.domain;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pt.ulisboa.tecnico.softeng.broker.exception.BrokerException;

public class AdventureConstructorMethodTest {
	public static final String IBAN = "BK011234567";
	public static final int AGE = 20;
	public static final int AMOUNT = 300;

	private Broker broker;
	private LocalDate begin = new LocalDate(2016, 12, 19);
	private LocalDate end   = new LocalDate(2016, 12, 21);

	@Before
	public void setUp() {
		this.broker = new Broker("BR01", "eXtremeADVENTURE");
	}

	@Test
	public void success() {
		Adventure adventure = new Adventure(this.broker, this.begin, this.end, AGE, IBAN, AMOUNT);

		Assert.assertEquals(this.broker, adventure.getBroker());
		Assert.assertEquals(this.begin, adventure.getBegin());
		Assert.assertEquals(this.end, adventure.getEnd());
		Assert.assertEquals(AGE, adventure.getAge());
		Assert.assertEquals(IBAN, adventure.getIBAN());
		Assert.assertEquals(AMOUNT, adventure.getAmount());
		Assert.assertTrue(this.broker.hasAdventure(adventure));

		Assert.assertNull(adventure.getBankPayment());
		Assert.assertNull(adventure.getActivityBooking());
		Assert.assertNull(adventure.getRoomBooking());
	}

	@Test
	public void nullBroker() {
		try {
			new Adventure(null, this.begin, this.end, AGE, IBAN, AMOUNT);
			Assert.fail();
		} catch(BrokerException be) {
			Assert.assertEquals(0, this.broker.getNumberOfAdventures());
		}
	}

	@Test
	public void nullBeginDate() {
		try {
			new Adventure(this.broker, null, this.end, AGE, IBAN, AMOUNT);
			Assert.fail();
		} catch(BrokerException be) {
			Assert.assertEquals(0, this.broker.getNumberOfAdventures());
		}
	}

	@Test
	public void nullEndDate() {
		try {
			new Adventure(this.broker, this.begin, null, AGE, IBAN, AMOUNT);
			Assert.fail();
		} catch(BrokerException be) {
			Assert.assertEquals(0, this.broker.getNumberOfAdventures());
		}
	}

	@Test
	public void endDateBeforeBegin() {
		try {
			new Adventure(this.broker, this.end, this.begin, AGE, IBAN, AMOUNT);
			Assert.fail();
		} catch(BrokerException be) {
			Assert.assertEquals(0, this.broker.getNumberOfAdventures());
		}
	}

	@Test
    public void atLeastOneDayApart() {
	    try {
	        new Adventure(this.broker, this.begin, this.begin, AGE, IBAN, AMOUNT);
	        Assert.fail();
        } catch(BrokerException be) {
	        Assert.assertEquals(0, this.broker.getNumberOfAdventures());
        }
    }

	@Test
	public void ageLessThanMinimum() {
		try {
			new Adventure(this.broker, this.begin, this.end, 17, IBAN, AMOUNT);
			Assert.fail();
		} catch(BrokerException be) {
			Assert.assertEquals(0, this.broker.getNumberOfAdventures());
		}
	}

	@Test
	public void ageGreaterThanMaximum() {
		try {
			new Adventure(this.broker, this.begin, this.end, 101, IBAN, AMOUNT);
			Assert.fail();
		} catch(BrokerException be) {
			Assert.assertEquals(0, this.broker.getNumberOfAdventures());
		}
	}

	@Test
	public void nullIBAN() {
		try {
			new Adventure(this.broker, this.begin, this.end, AGE, null, AMOUNT);
			Assert.fail();
		} catch(BrokerException be) {
			Assert.assertEquals(0, this.broker.getNumberOfAdventures());
		}
	}

	@Test
	public void emptyIBAN() {
		try {
			new Adventure(this.broker, this.begin, this.end, AGE, "", AMOUNT);
			Assert.fail();
		} catch(BrokerException be) {
			Assert.assertEquals(0, this.broker.getNumberOfAdventures());
		}
	}

	@Test
	public void blankIBAN() {
		try {
			new Adventure(this.broker, this.begin, this.end, AGE, "    ", AMOUNT);
			Assert.fail();
		} catch(BrokerException be) {
			Assert.assertEquals(0, this.broker.getNumberOfAdventures());
		}
	}

	@Test
	public void negativeAmount() {
		try {
			new Adventure(this.broker, this.begin, this.end, AGE, IBAN, -1);
			Assert.fail();
		} catch(BrokerException be) {
			Assert.assertEquals(0, this.broker.getNumberOfAdventures());
		}
	}

	@Test
	public void zeroAmount() {
		try {
			new Adventure(this.broker, this.begin, this.end, AGE, IBAN, 0);
			Assert.fail();
		} catch(BrokerException be) {
			Assert.assertEquals(0, this.broker.getNumberOfAdventures());
		}
	}

	@After
	public void tearDown() {
		Broker.brokers.clear();
	}

}
