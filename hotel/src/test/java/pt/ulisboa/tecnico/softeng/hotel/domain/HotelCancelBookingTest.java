package pt.ulisboa.tecnico.softeng.hotel.domain;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pt.ulisboa.tecnico.softeng.hotel.domain.Room.Type;
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException;

public class HotelCancelBookingTest {
	private final LocalDate arrival = new LocalDate(2016, 12, 19);
	private final LocalDate departure = new LocalDate(2016, 12, 21);
	private Room room;
	private Room room2;	
	private String roomConfirmation, roomConfirmation2;

	@Before
	public void setUp() {
		Hotel hotel = new Hotel("XPTO123", "Paris");
		new Booking(hotel, arrival, departure);;
		this.room = new Room(hotel, "001" , Type.SINGLE);
		this.room2 = new Room(hotel, "002" , Type.DOUBLE);
		roomConfirmation = Hotel.reserveRoom(Type.SINGLE, arrival, departure);
		roomConfirmation2 = Hotel.reserveRoom(Type.DOUBLE, arrival, departure);
	}
	
	@Test
	public void success() {
		Hotel.cancelBooking(roomConfirmation);
		Hotel.cancelBooking(roomConfirmation2);
		
		Assert.assertEquals(0, this.room.getNumberOfBookings());
		Assert.assertEquals(true , room.isFree(Type.SINGLE, arrival, departure));
		Assert.assertEquals(true , room2.isFree(Type.DOUBLE, arrival, departure));
		
	}
	
	@Test(expected = HotelException.class)
	public void nullBooking(){
		Hotel.cancelBooking(null);
	}
	
	
	@Test(expected = HotelException.class)
	public void noBookingEmptyString(){
		Hotel.cancelBooking("");
	}
	
	@Test(expected = HotelException.class)
	public void noBookingWhiteSpace(){
		Hotel.cancelBooking(" ");
	}
	
	@Test(expected = HotelException.class)
	public void inexistingBooking(){
		Hotel.cancelBooking("XPTO1235");
	}	
	
	@Test
	public void doubleCancelBooking(){
		Hotel.cancelBooking(roomConfirmation);
		try {
			Hotel.cancelBooking(roomConfirmation);
			fail();
		} catch (HotelException he) {
			Assert.assertEquals(0, this.room.getNumberOfBookings());
		}
	}	
	

	@After
	public void tearDown() {
		Hotel.hotels.clear();
	}

}