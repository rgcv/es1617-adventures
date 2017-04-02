package pt.ulisboa.tecnico.softeng.broker.domain;

import static pt.ulisboa.tecnico.softeng.hotel.domain.Room.Type.DOUBLE;
import static pt.ulisboa.tecnico.softeng.hotel.domain.Room.Type.SINGLE;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import pt.ulisboa.tecnico.softeng.broker.exception.RemoteAccessException;
import pt.ulisboa.tecnico.softeng.broker.interfaces.HotelInterface;
import pt.ulisboa.tecnico.softeng.hotel.dataobjects.RoomBookingData;
import pt.ulisboa.tecnico.softeng.hotel.domain.Hotel;
import pt.ulisboa.tecnico.softeng.hotel.domain.Room;
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException;


@RunWith(JMockit.class)
public class BulkRoomBookingGetReferenceMethodTest {
	
	private final int numRooms = 3;
	private final LocalDate arrivalDate = new LocalDate(2017, 04, 02);
	private final LocalDate departureDate = new LocalDate(2017, 04, 05);
	
	private final String hotelCode = "1234567";
	private final String hotelName = "California";
	
	BulkRoomBooking booking;
	Hotel hotel;
	
	@Before
	public void setUp() {
		hotel = new Hotel(hotelCode, hotelName);
		
		new Room(hotel, "1", SINGLE);
		new Room(hotel, "2", DOUBLE);
		new Room(hotel, "3", SINGLE);
		
		booking = new BulkRoomBooking(numRooms, arrivalDate, departureDate);
		
		booking.processBooking();
		
	}
		
	@Test
	public void success() {
		String result = booking.getReference("Double");
		RoomBookingData data = HotelInterface.getRoomBookingData(result);
		
		Assert.assertEquals(2, booking.getReferences().size());
		
		Assert.assertEquals("Double", data.getRoomType());
		Assert.assertEquals("2", data.getRoomNumber());
		Assert.assertEquals(hotelCode, data.getHotelCode());
		Assert.assertEquals(hotelName, data.getHotelName());
		Assert.assertEquals(arrivalDate, data.getArrival());
		Assert.assertEquals(departureDate, data.getDeparture());
		
	}
	
	@Test
	public void emptyBooking() {
		booking.getReference("Single");
		booking.getReference("Single");
		booking.getReference("Double");
		
		Assert.assertEquals(0, booking.getReferences().size());
		
		String result = booking.getReference("Single");
		
		Assert.assertNull(result);
		
	}
	
	
	@Test
	public void invalidType() {
		String result = booking.getReference("dummy");
		
		Assert.assertNull(result);
	}
	
	@Test
	public void maxRemoteErrors(@Mocked HotelInterface hotel) {
		new Expectations() {
			{
				hotel.getRoomBookingData(anyString);
				result = new RemoteAccessException();
			}
		};
		
		for(int i = 0; i < BulkRoomBooking.MAX_REMOTE_ERRORS; i++) {
			booking.getReference("Single");
		}
		
		Assert.assertTrue(booking.getStatus());
		
	}
	
	@Test
	public void catchHotelException(@Mocked HotelInterface hotel) {
		new Expectations() {
			{
				hotel.getRoomBookingData(anyString);
				result = new HotelException();
			}
		};
		
		booking.addRemoteError();
		
		booking.getReference("Single");
		
		Assert.assertEquals(0, booking.getRemoteErrors());
	}
	
	@After
	public void tearDown() {
		hotel.hotels.clear();
	}
	
}
