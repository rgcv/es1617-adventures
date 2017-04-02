package pt.ulisboa.tecnico.softeng.hotel.domain;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException;

public class BookingConstructorTest {
	// To avoid code repetition
	LocalDate arrival = new LocalDate(2016, 12, 10);
	LocalDate departure = new LocalDate(2016, 12, 15);
	private Hotel hotel;
	int counter = 0;
	private int multiple = 10;
	
	@Before
	public void setUp() {
		hotel = new Hotel("HOTELID", "HotelName");
	}
	
	@Test
	public void successSingle() {
		createBooking(hotel, arrival, departure);
	}
	
	@Test(expected = HotelException.class)
	public void nullHotel() {
		new Booking(null, null, null); // This should tested individually
	}
	
	// failure: nullArrival
	// failure: nullDeparture

	/* This shouldn't faill
	@Test(expected = HotelException.class)
	public void failureSingleDate() {
		new Booking(hotel, arrival, arrival);
	}*/
	
	@Test
	public void successMultiple() {
		for(int index = 0; index < multiple; index++) {
			createBooking(hotel, arrival, departure);
		}
	}

	/**
	 * Actually, this is testing the creating of bookings when the first argument is null,
	 * which is already done above in nullHotel.
	 *
	@Test(expected = HotelException.class)
	public void failureMultipleNull() {
		for(int index = 0; index < multiple - 1; index++) {
			createBooking(hotel, arrival, departure);
		}
		
		createBooking(null, null, null); // Again, this is only testing the first null
	}
	 */
	
	// Actually this is testing the creation of Bookings with invalid dates
	@Test(expected = HotelException.class)
	public void failureDepartureBeforeArrival() { 
		new Booking(hotel, arrival, arrival.minusDays(1));
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
