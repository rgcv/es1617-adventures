package pt.ulisboa.tecnico.softeng.broker.domain;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.StrictExpectations;
import mockit.Tested;
import mockit.integration.junit4.JMockit;

import pt.ulisboa.tecnico.softeng.activity.domain.Booking;
import pt.ulisboa.tecnico.softeng.hotel.domain.Hotel;
import pt.ulisboa.tecnico.softeng.hotel.domain.Room;

@RunWith(JMockit.class)
public class BulkRoomBookingProcessBookingMethodTest {
	private final String hotelCode = "HOTELID";
	private final String hotelName = "hotelName";
	private final int hotelNumberOfRooms = (new Random().nextInt(200) + 1);
	
	private final int numberOfRooms = (int) Math.floor(hotelNumberOfRooms / (new Random().nextInt(200) + 1));
	private final int badNumberOfRooms = hotelNumberOfRooms + 1;
	
	private LocalDate arrival = new LocalDate(2017, 03, 24);
	private LocalDate departure = new LocalDate(2017, 03, 27);

	private static final boolean NOT_CANCELLED = false;
	private static final boolean CANCELLED = true;
	
	private static final int NO_EXCEPTION = 0;
	private static final int HOTEL_EXCEPTION = 1;
	private static final int REMOTE_ERROR = 2; 
	
	@Tested Hotel hotel;

	@Before
	public void setUp() {
		hotel = new Hotel(hotelCode, hotelName);
		
		for(int roomNumber = 1; roomNumber <= hotelNumberOfRooms; roomNumber++) {
			new Room(hotel, Integer.toString(roomNumber), (roomNumber % 2 == 0) ? Room.Type.SINGLE : Room.Type.DOUBLE);
		}
	}
	
	@Test
	public void success() {
		final BulkRoomBooking booking = new BulkRoomBooking(numberOfRooms, arrival, departure);
		Set<String> processResult = new HashSet<String>();
		
		new StrictExpectations(hotel, booking, processResult) {
			{
				booking.processBooking();
				
				for(int i = 1; i <= numberOfRooms; i++) {
					processResult.add(hotelCode + i);
				}
			}
		};
		booking.getReferences().addAll(processResult); //Simulates execution of processBooking
		
		booking.processBooking();

		Assert.assertEquals(arrival, booking.getArrival());
		Assert.assertEquals(departure, booking.getDeparture());
		Assert.assertEquals(NOT_CANCELLED, booking.getStatus());
		Assert.assertEquals(numberOfRooms, booking.getNumber());
		Assert.assertEquals(numberOfRooms, booking.getReferences().size());

		final Set<String> references = booking.getReferences();
		for(final String reference : references) {
			Assert.assertNotNull(reference);
			Assert.assertTrue(reference.startsWith(hotelCode));
		}
	}

	// TODO successTwice

	@Test
	public void failureNegativeRooms() {
		final BulkRoomBooking booking = new BulkRoomBooking(-1, arrival, departure);
		
		new StrictExpectations(hotel, booking) {
			{
				booking.processBooking();
			}
		};

		assertProcessBookingFailed(booking, NOT_CANCELLED, HOTEL_EXCEPTION);
	}
	
	@Test
	public void failureNullDates() {
		LocalDate temp1 = arrival;
		LocalDate temp2 = departure;
		arrival = null;
		departure = null;
		
		final BulkRoomBooking booking = new BulkRoomBooking(numberOfRooms, arrival, departure);
		
		new StrictExpectations(hotel, booking) {
			{
				booking.processBooking();
			}
		};
		
		assertProcessBookingFailed(booking, NOT_CANCELLED, HOTEL_EXCEPTION);
		
		arrival = temp1;
		departure = temp2;
	}
	
	@Test
	public void failureBadDates() {
		LocalDate temp = departure;
		departure = arrival;
		arrival = temp;
		
		final BulkRoomBooking booking = new BulkRoomBooking(numberOfRooms, arrival, departure);
		
		new StrictExpectations(hotel, booking) {
			{
				booking.processBooking();
			}
		};
		
		assertProcessBookingFailed(booking, NOT_CANCELLED, HOTEL_EXCEPTION);
		
		temp = departure;
		departure = arrival;
		arrival = temp;
	}

	@Test
	public void failureCancelled() {
		// FIXME EXPECTATIONS?
		final BulkRoomBooking booking = new BulkRoomBooking(numberOfRooms, arrival, departure);
		booking.setStatus(CANCELLED);
		
		assertProcessBookingFailed(booking, CANCELLED, NO_EXCEPTION);
	}
	
	@Test
	public void failureNoHotelsAvailable() {
		final BulkRoomBooking booking = new BulkRoomBooking(numberOfRooms, arrival, departure);
		
		new StrictExpectations(hotel, booking) {
			{
				booking.processBooking();
			}
		};
		
		Hotel.hotels.clear();

		assertProcessBookingFailed(booking, NOT_CANCELLED, HOTEL_EXCEPTION);
	}
	
	@Test
	public void failureNoRoomsAvailable() {
		final BulkRoomBooking booking = new BulkRoomBooking(numberOfRooms, arrival, departure);
		
		new StrictExpectations(hotel, booking) {
			{
				booking.processBooking();
			}
		};
		
		hotel.getRooms().clear();
	
		assertProcessBookingFailed(booking, NOT_CANCELLED, HOTEL_EXCEPTION);
	}
	
	@Test
	public void failureTooManyRooms() {
		final BulkRoomBooking booking = new BulkRoomBooking(badNumberOfRooms, arrival, departure);
		
		new StrictExpectations(hotel, booking) {
			{	
				booking.processBooking();
			}
		};

		assertProcessBookingFailed(booking, NOT_CANCELLED, HOTEL_EXCEPTION);
	}
	
	@Test
	public void failureRemoteAccessException() {
		final BulkRoomBooking booking = new BulkRoomBooking(numberOfRooms, arrival, departure);
		
		new StrictExpectations(hotel, booking) {
			{	
				booking.processBooking();
			}
		};

		assertProcessBookingFailed(booking, NOT_CANCELLED, REMOTE_ERROR);
	}
	
	@Test
	public void failureTooManyHotelExceptions() {
		final BulkRoomBooking booking = new BulkRoomBooking(badNumberOfRooms, arrival, departure);

		// THIS IS NOT HOW YOU SHOULD DO IT... USE THE EXPECTATIONS
		for(int i = 0; i < BulkRoomBooking.MAX_HOTEL_EXCEPTIONS - 1; i++) {
			booking.addHotelException();
		}

		new StrictExpectations(hotel, booking) {
			{
				booking.processBooking();
			}
		};

		assertProcessBookingFailed(booking, CANCELLED, HOTEL_EXCEPTION);
	}

	@Test
	public void failureTooManyRemoteAccessExceptions() {
		final BulkRoomBooking booking = new BulkRoomBooking(numberOfRooms, arrival, departure);

		// THIS IS NOT HOW YOU SHOULD DO IT... USE THE EXPECTATIONS
		for(int i = 0; i < BulkRoomBooking.MAX_REMOTE_ERRORS - 1; i++) {
			booking.addRemoteError();
		}
		
		new StrictExpectations(hotel, booking) {
			{	
				booking.processBooking();
			}
		};

		assertProcessBookingFailed(booking, CANCELLED, REMOTE_ERROR);
	}
	
	@After
	public void tearDown() {
		Hotel.hotels.clear();
		Booking.resetCounter();
	}

	private void assertProcessBookingFailed(BulkRoomBooking booking, boolean cancel, int exceptionType) {
		booking.processBooking();
			
		if(exceptionType == HOTEL_EXCEPTION) { 
			booking.addHotelException(); 
		}
		else if(exceptionType == REMOTE_ERROR) { 
			booking.addRemoteError(); 
		};
		
		if(booking.getHotelExceptions() == BulkRoomBooking.MAX_HOTEL_EXCEPTIONS
			|| booking.getRemoteErrors() == BulkRoomBooking.MAX_REMOTE_ERRORS) {
			booking.setStatus(CANCELLED);
		}
			
		ensureNoChanges(booking, cancel, exceptionType);	
	}
	
	private void ensureNoChanges(BulkRoomBooking booking, boolean cancel, int exceptionType) {
		Assert.assertEquals(arrival, booking.getArrival());
		Assert.assertEquals(departure, booking.getDeparture());
		Assert.assertEquals(cancel, booking.getStatus());
		
		if(exceptionType == HOTEL_EXCEPTION) {
			Assert.assertTrue(booking.getHotelExceptions() <= BulkRoomBooking.MAX_HOTEL_EXCEPTIONS
							&& booking.getHotelExceptions() >= 0);
		}
		else if(exceptionType == REMOTE_ERROR){
			Assert.assertTrue(booking.getRemoteErrors() <= BulkRoomBooking.MAX_REMOTE_ERRORS 
							&& booking.getRemoteErrors() >= 0);
		}
		
		Assert.assertTrue(booking.getReferences().isEmpty());
	}

	// TODO maxMinusOneHotelException
	// TODO hotelExceptionValueIsResetBySuccess
	// TODO hotelExceptionValueIsResetByRemoteException
	// TODO maxMinusOneRemoteException
	// TODO remoteExceptionValueIsResetBySuccess
	// TODO remoteExceptionValueIsResetByHotelException
}
