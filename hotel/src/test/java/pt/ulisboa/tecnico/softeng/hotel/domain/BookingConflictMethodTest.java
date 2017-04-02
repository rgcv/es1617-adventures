package pt.ulisboa.tecnico.softeng.hotel.domain;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException;

public class BookingConflictMethodTest {
	Booking booking;
	LocalDate arrival = new LocalDate(2016, 12, 10);
	LocalDate departure = new LocalDate(2016, 12, 15);
	String reference;

	@Before
	public void setUp() {
		final Hotel hotel = new Hotel("HOTELID", "HotelName");
		booking = new Booking(hotel, arrival, departure);
		reference = booking.getReference();
	}

	@Test
	public void noConflictBefore() {
		Assert.assertFalse(booking.conflict(arrival.minusDays(5), arrival.minusDays(1)));
	}
	
	@Test
	public void noConflictAfter() {
		Assert.assertFalse(booking.conflict(departure.plusDays(1), departure.plusDays(5)));
	}

	@Test
	public void conflictBetweenEqualStart() {
		Assert.assertTrue(booking.conflict(arrival, departure.minusDays(1)));
	}

	@Test
	public void conflictBetweenEqualEnd() {
		Assert.assertTrue(booking.conflict(arrival.plusDays(1), departure));
	}

	@Test
	public void conflictOverlapStart() {
		Assert.assertTrue(booking.conflict(arrival.plusDays(2), departure.plusDays(5)));
	}

	@Test
	public void conflictOverlapEnd() {
		Assert.assertTrue(booking.conflict(arrival.minusDays(5), arrival.plusDays(2)));
	}

	@Test
	public void conflictOverlapEqualStart() {
		Assert.assertTrue(booking.conflict(arrival, departure.plusDays(5)));
	}

	@Test
	public void conflictOverlapEqualEnd() {
		Assert.assertTrue(booking.conflict(arrival.minusDays(5), departure));
	}

	@Test
	public void conflictOverlapEqualBoth() {
		Assert.assertTrue(booking.conflict(arrival, departure));
	}

	@Test
	public void conflictOverlapBoth() {
		Assert.assertTrue(booking.conflict(arrival.minusDays(5), departure.plusDays(5)));
	}

	@Test
	public void conflictEqualStart() {
		Assert.assertFalse(booking.conflict(arrival.minusDays(5), arrival));
	}

	@Test
	public void conflictEqualEnd() {
		Assert.assertFalse(booking.conflict(departure, departure.plusDays(5)));
	}

	@Test(expected = HotelException.class)
	public void conflictNull() {
		booking.conflict(null, null);
	}
	
	@After
	public void tearDown() {
		// When using @After annotation the code will be executed after each test,
		// therefore we write this asserts here. Or create a proper method for them
		// using the @After annotation.
		Assert.assertEquals(booking.getReference(), reference);
		Assert.assertEquals(booking.getArrival(), arrival);
		Assert.assertEquals(booking.getDeparture(), departure);
		
		Hotel.hotels.clear();
		Booking.resetCounter();
	}	
}
