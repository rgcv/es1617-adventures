package pt.ulisboa.tecnico.softeng.hotel.domain;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException;

public class BookingConflictMethodTest {
	Booking booking;
	LocalDate arrival;
	LocalDate departure;
	String reference;

	@Before
	public void setUp() {
		final Hotel hotel = new Hotel("HOTELID", "HotelName");

		arrival = new LocalDate(2016, 12, 10);
		departure = new LocalDate(2016, 12, 15);
		booking = new Booking(hotel, arrival, departure);
		reference = booking.getReference();
	}

	@Test
	public void noConflictBefore() {
		final LocalDate arrival = new LocalDate(2016, 12, 5);
		final LocalDate departure = new LocalDate(2016, 12, 9);
		
		checkConflict(arrival, departure);
	}
	
	@Test
	public void noConflictAfter() {
		final LocalDate arrival = new LocalDate(2016, 12, 16);
		final LocalDate departure = new LocalDate(2016, 12, 20);

		checkConflict(arrival, departure);
	}
	
	@Test(expected = HotelException.class)
	public void conflictBetweenEqualStart() {
		final LocalDate arrival = new LocalDate(2016, 12, 10);
		final LocalDate departure = new LocalDate(2016, 12, 14);

		checkConflict(arrival, departure);
	}
	
	@Test(expected = HotelException.class)
	public void conflictBetweenEqualEnd() {
		final LocalDate arrival = new LocalDate(2016, 12, 11);
		final LocalDate departure = new LocalDate(2016, 12, 15);

		checkConflict(arrival, departure);
	}

	@Test(expected = HotelException.class)
	public void conflictOverlapStart() {
		final LocalDate arrival = new LocalDate(2016, 12, 12);
		final LocalDate departure = new LocalDate(2016, 12, 20);

		checkConflict(arrival, departure);
	}

	@Test(expected = HotelException.class)
	public void conflictOverlapEnd() {
		final LocalDate arrival = new LocalDate(2016, 12, 5);
		final LocalDate departure = new LocalDate(2016, 12, 12);

		checkConflict(arrival, departure);
	}
	
	@Test(expected = HotelException.class)
	public void conflictOverlapEqualStart() {
		final LocalDate arrival = new LocalDate(2016, 12, 10);
		final LocalDate departure = new LocalDate(2016, 12, 20);
		
		checkConflict(arrival, departure);
	}
	
	@Test(expected = HotelException.class)
	public void conflictOverlapEqualEnd() {
		final LocalDate arrival = new LocalDate(2016, 12, 5);
		final LocalDate departure = new LocalDate(2016, 12, 15);
		
		checkConflict(arrival, departure);
	}
	
	@Test(expected = HotelException.class)
	public void conflictOverlapEqualBoth() {
		final LocalDate arrival = new LocalDate(2016, 12, 10);
		final LocalDate departure = new LocalDate(2016, 12, 15);

		checkConflict(arrival, departure);
	}
	
	@Test(expected = HotelException.class)
	public void conflictOverlapBoth() {
		final LocalDate arrival = new LocalDate(2016, 12, 5);
		final LocalDate departure = new LocalDate(2016, 12, 20);

		checkConflict(arrival, departure);
	}
	
	@Test(expected = HotelException.class)
	public void conflictEqualStart() {
		final LocalDate arrival = new LocalDate(2016, 12, 5);
		final LocalDate departure = new LocalDate(2016, 12, 10);

		checkConflict(arrival, departure);
	}
	
	@Test(expected = HotelException.class)
	public void conflictEqualEnd() {
		final LocalDate arrival = new LocalDate(2016, 12, 15);
		final LocalDate departure = new LocalDate(2016, 12, 20);

		checkConflict(arrival, departure);
	}
	
	@Test(expected = HotelException.class)
	public void conflictNull() {
		final LocalDate arrival = null;
		final LocalDate departure = null;
		
		checkConflict(arrival, departure);
	}
	
	@After
	public void tearDown() {
		Hotel.hotels.clear();
		Booking.resetCounter();
	}
	
	private void checkConflict(LocalDate arrival, LocalDate departure) throws HotelException {
		try {
			Assert.assertFalse(booking.conflict(arrival, departure));
		}
		finally {
			ensureNoChanges();
		}
	}
	
	private void ensureNoChanges() {
		Assert.assertEquals(booking.getReference(), reference);
		Assert.assertEquals(booking.getArrival(), arrival);
		Assert.assertEquals(booking.getDeparture(), departure);
	}
}
