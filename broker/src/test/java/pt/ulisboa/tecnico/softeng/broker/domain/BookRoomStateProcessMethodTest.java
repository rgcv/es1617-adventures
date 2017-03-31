package pt.ulisboa.tecnico.softeng.broker.domain;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Injectable;
import mockit.Mocked;
import mockit.StrictExpectations;
import mockit.integration.junit4.JMockit;
import pt.ulisboa.tecnico.softeng.hotel.domain.Room;
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException;
import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State;
import pt.ulisboa.tecnico.softeng.broker.exception.RemoteAccessException;
import pt.ulisboa.tecnico.softeng.broker.interfaces.HotelInterface;

@RunWith(JMockit.class)
public class BookRoomStateProcessMethodTest {
	
	private static final String ROOM_CONFIRMATION = "RoomConfirmation";

	private static final String IBAN = "BK0121";
	private static final int AMOUNT = 200;
	
	private final LocalDate begin = new LocalDate(2016, 12, 10);
	private final LocalDate end = new LocalDate(2016, 12, 15);
	
	private Adventure adventure;

	@Injectable
	private Broker broker;

	@Before
	public void setUp() {
		this.adventure = new Adventure(this.broker, this.begin, this.end, 20, IBAN, AMOUNT);
		this.adventure.setState(State.BOOK_ROOM);
	}
	
	@Test
	public void success(@Mocked final HotelInterface hotelInterface) {
		new StrictExpectations() {
			{
				HotelInterface.reserveRoom(Room.Type.SINGLE, begin, end);
				this.result = ROOM_CONFIRMATION;
			}
		};
		
		this.adventure.process();
		
		Assert.assertEquals(Adventure.State.CONFIRMED, this.adventure.getState());
		Assert.assertEquals(ROOM_CONFIRMATION, this.adventure.getRoomConfirmation());
	}
	
	@Test 
	public void oneHotelException(@Mocked final HotelInterface hotelInterface) {
		new StrictExpectations() {
			{
				HotelInterface.reserveRoom(Room.Type.SINGLE, begin, end);
				this.result = new HotelException();
			}
		};
		
		this.adventure.process();
		
		Assert.assertEquals(Adventure.State.UNDO, this.adventure.getState());
	}
	
	@Test
	public void oneRemoteException(@Mocked final HotelInterface hotelInterface) {
		new StrictExpectations() {
			{
				HotelInterface.reserveRoom(Room.Type.SINGLE, begin, end);
				this.result = new RemoteAccessException();
			}
		};
		
		this.adventure.process();
		Assert.assertEquals(Adventure.State.BOOK_ROOM, this.adventure.getState());
	}
	
	@Test
	public void reserveRoomAccessRemoteException(@Mocked final HotelInterface hotelInterface) {
		new StrictExpectations() {
			{
				HotelInterface.reserveRoom(Room.Type.SINGLE, begin, end);
				this.times = BookRoomState.MAX_REMOTE_ERRORS;
				this.result = new RemoteAccessException();
			}
		};
		for(int i = 0; i < BookRoomState.MAX_REMOTE_ERRORS; i++)
			this.adventure.process();
		
		Assert.assertEquals(Adventure.State.UNDO, this.adventure.getState());
	}
}