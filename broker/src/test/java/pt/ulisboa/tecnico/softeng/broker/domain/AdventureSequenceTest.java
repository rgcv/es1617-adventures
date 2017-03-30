package pt.ulisboa.tecnico.softeng.broker.domain;

import org.joda.time.LocalDate;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Assert;

import mockit.Expectations;
import mockit.Mocked;
import mockit.StrictExpectations;
import mockit.integration.junit4.JMockit;
import pt.ulisboa.tecnico.softeng.bank.exception.BankException;
import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State;
import pt.ulisboa.tecnico.softeng.broker.interfaces.ActivityInterface;
import pt.ulisboa.tecnico.softeng.broker.interfaces.BankInterface;
import pt.ulisboa.tecnico.softeng.broker.interfaces.HotelInterface;

@RunWith(JMockit.class)
public class AdventureSequenceTest {
	// declarations
	
	private final LocalDate begin = new LocalDate(2017,07,15);
	private final LocalDate end = new LocalDate(2017,07,31);
	private static final int AGE = 22;
	private static final int AMOUNT = 500;
	private static final String IBAN = new String("BK011234567");
	private Broker broker;	
	
	@Before
	public void setUp() {
		this.broker = new Broker("BR01", "eXtremeAdventure");
	}
	
	@Test
	public void initialState() {
		Adventure adventure = new Adventure(broker, begin, end, AGE, IBAN, AMOUNT);
		Assert.assertEquals(State.PROCESS_PAYMENT, adventure.getState());
	}
		
	@Test
	public void reserveActivity() {
		Adventure adventure = new Adventure(broker, begin, end, AGE, IBAN, AMOUNT);
		adventure.process();
		Assert.assertEquals(State.RESERVE_ACTIVITY, adventure.getState());		
	}
	
	@Test
	public void bookRoom() {
		Adventure adventure = new Adventure(broker, begin, end, AGE, IBAN, AMOUNT);
		adventure.process();
		adventure.process();
		Assert.assertEquals(State.BOOK_ROOM, adventure.getState());
	}
	
	@Test
	public void confirmed() {
		Adventure adventure = new Adventure(broker, begin, end, AGE, IBAN, AMOUNT);
		adventure.process();
		adventure.process();
		adventure.process();
		Assert.assertEquals(State.CONFIRMED, adventure.getState());
	}
	
	@After
	public void tearDown() {
		//tear down of variables in setup
		Broker.brokers.clear();		
	}
}