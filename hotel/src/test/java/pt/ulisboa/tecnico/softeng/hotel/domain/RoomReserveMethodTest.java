package pt.ulisboa.tecnico.softeng.hotel.domain;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pt.ulisboa.tecnico.softeng.hotel.domain.Room.Type;
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException;

public class RoomReserveMethodTest {
	Room room;

	@Before
	public void setUp() {
		Hotel hotel = new Hotel("XPTO123", "Lisboa");
		this.room = new Room(hotel, "01", Type.SINGLE);
	}

	@Test
	public void success() {
		LocalDate arrival = new LocalDate(2016, 12, 19);
		LocalDate departure = new LocalDate(2016, 12, 24);
		Booking booking = this.room.reserve(Type.SINGLE, arrival, departure);

		Assert.assertTrue(booking.getReference().length() > 0);
		Assert.assertEquals(arrival, booking.getArrival());
		Assert.assertEquals(departure, booking.getDeparture());
	}
		
	@Test
	public void wrongType(){
		try{
			LocalDate arrival = new LocalDate(2016, 12, 25);
			LocalDate departure = new LocalDate(2016, 12, 27);
			this.room.reserve(Type.DOUBLE, arrival, departure);	
			Assert.fail();
		} catch (HotelException he){
			Assert.assertEquals(0, this.room.getBookings().size());
		}	
	}
	
	// failure: nullType
	// failure: nullArrival
	// failure: nullDeparture
	
	// failure: alreadyReserved
	
	@After
	public void tearDown() {
		Hotel.hotels.clear();
	}
}
