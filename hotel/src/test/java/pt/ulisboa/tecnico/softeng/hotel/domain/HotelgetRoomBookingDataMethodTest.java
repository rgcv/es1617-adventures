package pt.ulisboa.tecnico.softeng.hotel.domain;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pt.ulisboa.tecnico.softeng.hotel.dataobjects.RoomBookingData;
import pt.ulisboa.tecnico.softeng.hotel.domain.Room.Type;
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException;

public class HotelgetRoomBookingDataMethodTest {
	
	private final static String REFERENCE = "XPTO12334";
	private final static String DIFREFERENCE = "WrongBookingReference";
	
	private final static String HCODE = "XPTO123";
	private final static String HNAME = "Lisboa";
	
	private final static String RNUMBER = "01";
	
	private final static String RTYPE = "Single";
	
	private final LocalDate arrival = new LocalDate(2016, 12, 19);
	private final LocalDate departure = new LocalDate(2016, 12, 21);
	
	private Hotel hotel;
	
	@Before
	public void setUp() {
		hotel = new Hotel(HCODE, HNAME);
	}

	//Success test
	
	@Test
	public void success() {
		Room room = new Room(hotel, RNUMBER, Type.SINGLE);
		room.reserve(Type.SINGLE, arrival, departure);
		Assert.assertEquals(1,hotel.getRooms().size());
		Assert.assertEquals(1, room.getBookings().size());
		
		
		RoomBookingData rbd = Hotel.getRoomBookingData(REFERENCE);
		
		Assert.assertEquals(REFERENCE, rbd.getReference());
		Assert.assertEquals(HNAME, rbd.getHotelName());
		Assert.assertEquals(HCODE, rbd.getHotelCode());
		Assert.assertEquals(RNUMBER, rbd.getRoomNumber());
		Assert.assertEquals(RTYPE, rbd.getRoomType());
		Assert.assertEquals(arrival, rbd.getArrival());
		Assert.assertEquals(departure, rbd.getDeparture());

	}
	
	//Argument testing

	@Test(expected = HotelException.class)
	public void nullReference() {
		Hotel.getRoomBookingData(null);
	}
	
	@Test(expected = HotelException.class)
	public void blankReference() {
		Hotel.getRoomBookingData("      ");
	}

	@Test(expected = HotelException.class)
	public void emptyReference() {
		Hotel.getRoomBookingData("");
	}
	
	//Failure tests
	
	@Test(expected = HotelException.class)
	public void absentHotel() {
		Hotel.hotels.clear();
		Hotel.getRoomBookingData(REFERENCE);
	}
	

	@Test(expected = HotelException.class)
	public void absentRoom() {
		Hotel.getRoomBookingData(REFERENCE);
	}
	
	@Test(expected = HotelException.class)
	public void absentBooking() {
		Room room = new Room(hotel, RNUMBER, Type.SINGLE);
		hotel.addRoom(room);

		Hotel.getRoomBookingData(REFERENCE);
	}
	
	@Test()
	public void referenceNonexistent() {
		Room room = null;
		try {
			room = new Room(hotel, RNUMBER, Type.SINGLE);
			room.reserve(Type.SINGLE, arrival, departure);
		} catch (HotelException he) {
			Assert.fail("Test not conclusive");
		}
		try {
			Hotel.getRoomBookingData(DIFREFERENCE);
			Assert.fail();
		} catch (HotelException he) {
			for(Booking b : room.getBookings())
				Assert.assertNotEquals(REFERENCE, b.getReference());
		}
		
	}

	@After
	public void tearDown() {
		Hotel.hotels.clear();
	}
}
