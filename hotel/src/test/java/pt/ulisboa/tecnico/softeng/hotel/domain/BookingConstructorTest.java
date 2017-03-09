package pt.ulisboa.tecnico.softeng.hotel.domain;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException;

public class BookingConstructorTest {
	private Hotel hotel;
	int counter = 0;
	private int multiple = 10;
	
	@Before
	public void setUp() {
		hotel = new Hotel("HOTELID", "HotelName");
	}
	
	@Test
	public void successSingle() {
		LocalDate arrival = new LocalDate(2016, 12, 10);
		LocalDate departure = new LocalDate(2016, 12, 15);
		
		createBooking(hotel, arrival, departure);
	}
	
	@Test(expected = HotelException.class)
	public void failureSingleNull() {
		createBooking(null, null, null);
	}
	
	@Test(expected = HotelException.class)
	public void failureSingleDate() {
		LocalDate arrival = new LocalDate(2016, 12, 15);
		LocalDate departure = new LocalDate(2016, 12, 10);
		
		createBooking(hotel, arrival, departure);
	}
	
	@Test
	public void successMultiple() {
		LocalDate arrival = new LocalDate(2016, 12, 10);
		LocalDate departure = new LocalDate(2016, 12, 15);
		
		for(int index = 0; index < multiple; index++) {
			createBooking(hotel, arrival, departure);
		}
	}
	
	@Test(expected = HotelException.class)
	public void failureMultipleNull() {
		LocalDate arrival = new LocalDate(2016, 12, 10);
		LocalDate departure = new LocalDate(2016, 12, 15);
		
		for(int index = 0; index < multiple - 1; index++) {
			createBooking(hotel, arrival, departure);
		}
		
		createBooking(null, null, null);
	}
	
	@Test(expected = HotelException.class)
	public void failureMultipleDate() {
		LocalDate goodArrival = new LocalDate(2016, 12, 10);
		LocalDate goodDeparture = new LocalDate(2016, 12, 15);
		LocalDate badArrival = new LocalDate(2016, 12, 15);
		LocalDate badDeparture = new LocalDate(2016, 12, 10);
		
		for(int index = 0; index < multiple - 1; index++) {
			createBooking(hotel, goodArrival, goodDeparture);
		}
		
		createBooking(hotel, badArrival, badDeparture);
	}

	@After
	public void tearDown() {
		counter = 0;
		Hotel.hotels.clear();
		Booking.resetCounter();
	}
	
	private void createBooking(Hotel hotel, LocalDate arrival, LocalDate departure) throws HotelException {
		Booking booking = null;
		
		try {
			booking = new Booking(hotel, arrival, departure);
			counter = Integer.parseInt(booking.getReference().substring(Hotel.CODE_SIZE));
			
			Assert.assertTrue(booking.getReference().startsWith(hotel.getCode()));
			Assert.assertTrue(booking.getReference().length() > Hotel.CODE_SIZE);
			
			Assert.assertEquals(arrival, booking.getArrival());
			Assert.assertEquals(departure, booking.getDeparture());
		}
		catch(HotelException e) {
			Assert.assertNull(booking);
			throw e;
		}
		finally {
			Assert.assertEquals(Booking.getCounter(), counter);
		}
	}
}
