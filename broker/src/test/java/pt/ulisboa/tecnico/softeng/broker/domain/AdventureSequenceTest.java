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
import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException;
import pt.ulisboa.tecnico.softeng.bank.exception.BankException;
import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State;
import pt.ulisboa.tecnico.softeng.broker.interfaces.ActivityInterface;
import pt.ulisboa.tecnico.softeng.broker.interfaces.BankInterface;
import pt.ulisboa.tecnico.softeng.broker.interfaces.HotelInterface;
import pt.ulisboa.tecnico.softeng.hotel.domain.Room.Type;
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException;

@RunWith(JMockit.class)
public class AdventureSequenceTest {
	// declarations
	

	private static final String PAYMENT_CONFIRMATION = "PaymentConfirmation";
	private static final String ACTIVITY_CONFIRMATION = "ActivityConfirmation";
	private static final String ROOM_CONFIRMATION = "RoomConfirmation";
	
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
	//process_payment
	public void initialState() {
		Adventure adventure = new Adventure(broker, begin, end, AGE, IBAN, AMOUNT);
		Assert.assertEquals(State.PROCESS_PAYMENT, adventure.getState());
	}
	
	@Test 
	// process_payment -> cancelled
	public void processPaymentCancelled(@Mocked BankInterface bankInterface) {
		Adventure adventure = new Adventure(broker, begin, end, AGE, IBAN, AMOUNT);
		new Expectations() {
			{
				BankInterface.processPayment(IBAN, AMOUNT);
				this.result = new BankException();
			}
		};
		Assert.assertEquals(State.PROCESS_PAYMENT, adventure.getState());
		adventure.process();
		Assert.assertEquals(State.CANCELLED, adventure.getState());
	}
		
	@Test
	//process_payment -> reserve_activity
	public void reserveActivity(@Mocked BankInterface bankInterface) {
		Adventure adventure = new Adventure(broker, begin, end, AGE, IBAN, AMOUNT);
		new Expectations(){
			{
			BankInterface.processPayment(IBAN, AMOUNT);
			this.result = PAYMENT_CONFIRMATION;
			}			
		};
		Assert.assertEquals(State.PROCESS_PAYMENT, adventure.getState());
		adventure.process();
		Assert.assertEquals(State.RESERVE_ACTIVITY, adventure.getState());		
	}
	
	@Test
	//process_payment -> reserve_activity -> confirmed
	public void reserveActivityConfirmed(@Mocked BankInterface bankInterface, 
										@Mocked ActivityInterface activityInterface) {
		Adventure adventure = new Adventure(broker, begin, begin, AGE, IBAN, AMOUNT);
		new Expectations(){
			{
			BankInterface.processPayment(IBAN, AMOUNT);
			this.result = PAYMENT_CONFIRMATION;
			ActivityInterface.reserveActivity(begin, begin, AGE);
			this.result = ACTIVITY_CONFIRMATION;
			}			
		};
		Assert.assertEquals(State.PROCESS_PAYMENT, adventure.getState());
		adventure.process();
		Assert.assertEquals(State.RESERVE_ACTIVITY, adventure.getState());
		adventure.process();
		Assert.assertEquals(State.CONFIRMED, adventure.getState());		
	}
	
	@Test
	//process_payment -> reserve_activity -> undo
	public void reserveActivityUndo(@Mocked BankInterface bankInterface, 
									@Mocked ActivityInterface activityInterface) {
		Adventure adventure = new Adventure(broker, begin, end, AGE, IBAN, AMOUNT);
		new Expectations(){
			{
			BankInterface.processPayment(IBAN, AMOUNT);
			this.result = PAYMENT_CONFIRMATION;
			ActivityInterface.reserveActivity(begin, end, AGE);
			this.result = new ActivityException();
			}			
		};
		Assert.assertEquals(State.PROCESS_PAYMENT, adventure.getState());
		adventure.process();
		Assert.assertEquals(State.RESERVE_ACTIVITY, adventure.getState());
		adventure.process();
		Assert.assertEquals(State.UNDO, adventure.getState());		
	}
	
	@Test 
	// process_payment -> reserve_activity -> undo -> cancelled
	public void reserveActivityUndoCancelled(@Mocked BankInterface bankInterface, 
									@Mocked ActivityInterface activityInterface,
									@Mocked HotelInterface hotelInterface) {
		Adventure adventure = new Adventure(broker, begin, end, AGE, IBAN, AMOUNT);
		new Expectations() {
			{
				BankInterface.processPayment(IBAN, AMOUNT);
				this.result = PAYMENT_CONFIRMATION;
				ActivityInterface.reserveActivity(begin, end, AGE);
				this.result = new ActivityException();
			}
		};
		Assert.assertEquals(State.PROCESS_PAYMENT, adventure.getState());
		adventure.process();
		Assert.assertEquals(State.RESERVE_ACTIVITY, adventure.getState());
		adventure.process();
		Assert.assertEquals(State.UNDO, adventure.getState());
		adventure.process();
		Assert.assertEquals(State.CANCELLED, adventure.getState());
	}
	
	@Test
	//process_payment -> reserve_activity -> book_room
	public void bookRoom(@Mocked BankInterface bankInterface, 
								@Mocked ActivityInterface activityInterface) {
		Adventure adventure = new Adventure(broker, begin, end, AGE, IBAN, AMOUNT);
		new Expectations(){
			{
			BankInterface.processPayment(IBAN, AMOUNT);
			this.result = PAYMENT_CONFIRMATION;
			ActivityInterface.reserveActivity(begin, end, AGE);
			this.result = ACTIVITY_CONFIRMATION;
			}			
		};
		Assert.assertEquals(State.PROCESS_PAYMENT, adventure.getState());
		adventure.process();
		Assert.assertEquals(State.RESERVE_ACTIVITY, adventure.getState());
		adventure.process();
		Assert.assertEquals(State.BOOK_ROOM, adventure.getState());		
	}
	
	@Test
	//process_payment -> reserve_activity -> book_room -> confirmed
	public void bookRoomConfirmed(@Mocked BankInterface bankInterface, 
								@Mocked ActivityInterface activityInterface,
								@Mocked HotelInterface hotelInterface) {
		Adventure adventure = new Adventure(broker, begin, end, AGE, IBAN, AMOUNT);
		new Expectations(){
			{
			BankInterface.processPayment(IBAN, AMOUNT);
			this.result = PAYMENT_CONFIRMATION;
			ActivityInterface.reserveActivity(begin, end, AGE);
			this.result = ACTIVITY_CONFIRMATION;
			HotelInterface.reserveRoom(Type.SINGLE, begin, end);
			this.result = ROOM_CONFIRMATION;
			}			
		};
		Assert.assertEquals(State.PROCESS_PAYMENT, adventure.getState());
		adventure.process();
		Assert.assertEquals(State.RESERVE_ACTIVITY, adventure.getState());
		adventure.process();
		Assert.assertEquals(State.BOOK_ROOM, adventure.getState());
		adventure.process();
		Assert.assertEquals(State.CONFIRMED, adventure.getState());		
	}
	
	@Test
	//process_payment -> reserve_activity -> book_room -> undo
	public void bookRoomUndo(@Mocked BankInterface bankInterface, 
							@Mocked ActivityInterface activityInterface,
							@Mocked HotelInterface hotelInterface) {
		Adventure adventure = new Adventure(broker, begin, end, AGE, IBAN, AMOUNT);
		new Expectations(){
			{
			BankInterface.processPayment(IBAN, AMOUNT);
			this.result = PAYMENT_CONFIRMATION;
			ActivityInterface.reserveActivity(begin, end, AGE);
			this.result = ACTIVITY_CONFIRMATION;
			HotelInterface.reserveRoom(Type.SINGLE, begin, end);
			this.result = new HotelException();
			}			
		};
		Assert.assertEquals(State.PROCESS_PAYMENT, adventure.getState());
		adventure.process();
		Assert.assertEquals(State.RESERVE_ACTIVITY, adventure.getState());
		adventure.process();
		Assert.assertEquals(State.BOOK_ROOM, adventure.getState());
		adventure.process();
		Assert.assertEquals(State.UNDO, adventure.getState());		
	}
	
	@Test 
	// process_payment -> reserve_activity -> book_room -> undo -> cancelled
	public void bookRoomUndoCancelled(@Mocked BankInterface bankInterface, 
							@Mocked ActivityInterface activityInterface,
							@Mocked HotelInterface hotelInterface) {
		Adventure adventure = new Adventure(broker, begin, end, AGE, IBAN, AMOUNT);
		new Expectations() {
			{
				BankInterface.processPayment(IBAN, AMOUNT);
				this.result = PAYMENT_CONFIRMATION;
				ActivityInterface.reserveActivity(begin, end, AGE);
				this.result = ACTIVITY_CONFIRMATION;
				HotelInterface.reserveRoom(Type.SINGLE, begin, end);
				this.result = new HotelException();
			}			
		};
		Assert.assertEquals(State.PROCESS_PAYMENT, adventure.getState());
		adventure.process();
		Assert.assertEquals(State.RESERVE_ACTIVITY, adventure.getState());
		adventure.process();
		Assert.assertEquals(State.BOOK_ROOM, adventure.getState());
		adventure.process();
		Assert.assertEquals(State.UNDO, adventure.getState());
		adventure.process();
		Assert.assertEquals(State.CANCELLED, adventure.getState());
	}
	
	@After
	public void tearDown() {
		//tear down of variables in setup
		Broker.brokers.clear();		
	}
}