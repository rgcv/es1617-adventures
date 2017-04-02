package pt.ulisboa.tecnico.softeng.broker.domain;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Injectable;
import mockit.Mocked;
import mockit.StrictExpectations;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import pt.ulisboa.tecnico.softeng.bank.dataobjects.BankOperationData;
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException;
import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State;
import pt.ulisboa.tecnico.softeng.broker.exception.RemoteAccessException;
import pt.ulisboa.tecnico.softeng.broker.interfaces.ActivityInterface;
import pt.ulisboa.tecnico.softeng.broker.interfaces.BankInterface;
import pt.ulisboa.tecnico.softeng.broker.interfaces.HotelInterface;

@RunWith(JMockit.class)
public class UndoStateProcessMethodTest {
    private static final int AGE = 20;
    private static final String IBAN = "BK01987654321";
    private static final int AMOUNT = 300;
    private final LocalDate begin = new LocalDate(2016, 12, 19);
    private final LocalDate end = new LocalDate(2016, 12, 21);
    
	private static final String PAYMENT_CANCELLATION = "PaymentCancellation";
	private static final String ACTIVITY_CANCELLATION = "ActivityCancellation";
	private static final String ROOM_CANCELLATION = "RoomCancellation";
	private static final String ROOM_CONFIRMATION = "RoomConfirmation";
	private static final String ACTIVITY_CONFIRMATION = "ActivityConfirmation";
	private static final String PAYMENT_CONFIRMATION = "PaymentConfirmation";

    private Adventure adventure;

    @Injectable
    private Broker broker;

    @Before
    public void setUp() {
        this.adventure = new Adventure(this.broker, this.begin, this.end, AGE, IBAN, AMOUNT);
        this.adventure.setState(Adventure.State.UNDO);
    }

    @Test
    public void CancellPaymentFirstHotelException(@Mocked final BankInterface bankInterface) {
		this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);
                
		new StrictExpectations() {
			{
				BankInterface.cancelPayment(PAYMENT_CONFIRMATION);
				this.result = new HotelException();
			}
		};
	
		this.adventure.process();
		Assert.assertEquals(Adventure.State.UNDO, this.adventure.getState());
	}
    
    @Test
    public void CancellPaymentFirstRemoteException(@Mocked final BankInterface bankInterface) {
		this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);
                
		new StrictExpectations() {
			{
				BankInterface.cancelPayment(PAYMENT_CONFIRMATION);
				this.result = new RemoteAccessException();
			}
		};
	
		this.adventure.process();
		Assert.assertEquals(Adventure.State.UNDO, this.adventure.getState());
	}
    
    @Test
    public void cancellActivityFirstHotelException(@Mocked final ActivityInterface activityInterface) {
    	this.adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION);
		
    	new StrictExpectations() {
			{
				ActivityInterface.cancelReservation(ACTIVITY_CONFIRMATION);
				this.result = new HotelException();
			}
		};
		this.adventure.process();
		Assert.assertEquals(Adventure.State.UNDO, this.adventure.getState());
    }
    
    @Test
    public void cancellActivityFirstRemoteAccessException(@Mocked final ActivityInterface activityInterface) {
    	this.adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION);
    	
    	new StrictExpectations() {
    		{
    			ActivityInterface.cancelReservation(ACTIVITY_CONFIRMATION);
    			this.result = new RemoteAccessException();
    		}
    	};
    	this.adventure.process();
    	Assert.assertEquals(Adventure.State.UNDO, this.adventure.getState());
    }
    
    @Test
    public void cancellRoomFirstHotelException(@Mocked final HotelInterface hotelInterface) {
    	this.adventure.setRoomConfirmation(ROOM_CONFIRMATION);
    	
    	new StrictExpectations() {
    		{
    			HotelInterface.cancelBooking(ROOM_CONFIRMATION);
    			this.result = new HotelException();
    		}
    	};
    	this.adventure.process();
    	Assert.assertEquals(Adventure.State.UNDO, this.adventure.getState());
    }
    
	@Test
	public void cancellPaymentFirstRemoteAccessException(@Mocked final BankInterface bankInterface) {
		this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);
		new StrictExpectations() {
			{
				BankInterface.cancelPayment(PAYMENT_CONFIRMATION);
				this.result = new RemoteAccessException();
			}
		};

		this.adventure.process();

		Assert.assertEquals(Adventure.State.UNDO, this.adventure.getState());
	}

	@Test
	public void CancelledPaymentException(@Mocked final BankInterface bankInterface) {
		this.adventure.setPaymentCancellation(PAYMENT_CANCELLATION);
		this.adventure.process();
		Assert.assertEquals(Adventure.State.CANCELLED, this.adventure.getState());
	}
	
	@Test
	public void CancelledRoomException(@Mocked final HotelInterface hotelInterface) {
		this.adventure.setRoomCancellation(ROOM_CANCELLATION);
		this.adventure.process();
		Assert.assertEquals(Adventure.State.CANCELLED, this.adventure.getState());
	}
	
	@Test
	public void CancelledActivityException(@Mocked final ActivityInterface activityInterface) {
		this.adventure.setActivityCancellation(ACTIVITY_CANCELLATION);
		this.adventure.process();
		Assert.assertEquals(Adventure.State.CANCELLED, this.adventure.getState());
	}
	
	@Test
	public void NotConfirmedCancellPaymentException(@Mocked final BankInterface bankInterface) {
		this.adventure.process();
		Assert.assertEquals(Adventure.State.CANCELLED, this.adventure.getState());
	}
	
	@Test
	public void NotConfirmedCancellActivityException(@Mocked final ActivityInterface activityInterface) {
		this.adventure.process();
		Assert.assertEquals(Adventure.State.CANCELLED, this.adventure.getState());
	}
	
	@Test
	public void NotConfirmedCancellRoomException(@Mocked final HotelInterface hotelInterfacec) {
		this.adventure.process();
		Assert.assertEquals(Adventure.State.CANCELLED, this.adventure.getState());
	}
}
