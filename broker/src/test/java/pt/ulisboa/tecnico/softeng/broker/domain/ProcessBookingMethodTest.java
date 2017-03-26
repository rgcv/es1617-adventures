package pt.ulisboa.tecnico.softeng.broker.domain;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import pt.ulisboa.tecnico.softeng.broker.interfaces.HotelInterface;
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException;

@RunWith(JMockit.class)
public class ProcessBookingMethodTest {
	private final int numRooms = 5;
	private final LocalDate arrival = new LocalDate(2017, 03, 24);
	private final LocalDate departure = new LocalDate(2017, 03, 27);

	private final boolean ACCEPTED = false;
	private final boolean CANCELLED = true;

	private void assertProcessBookingFailed(BulkRoomBooking booking) {
		try {
			booking.processBooking();
		}
		catch(final HotelException e) {
			Assert.assertEquals(arrival, booking.getArrival());
			Assert.assertEquals(departure, booking.getDeparture());
			Assert.assertEquals(CANCELLED, booking.getStatus());
			Assert.assertTrue(booking.getReferences().isEmpty());
		}
	}

	@Test
	public void failureBadDates(@Mocked HotelInterface hotelInterface) {
		new Expectations() {
			{
				HotelInterface.bulkBooking(numRooms, departure, arrival);
				result = new HotelException();
			}
		};

		BulkRoomBooking booking = new BulkRoomBooking(numRooms, departure, arrival);
		assertProcessBookingFailed(booking);
	}

	@Test
	public void failureNegativeRooms(@Mocked HotelInterface hotelInterface) {
		new Expectations() {
			{
				HotelInterface.bulkBooking(-numRooms, arrival, departure);
				result = new HotelException();
			}
		};
		
		BulkRoomBooking booking = new BulkRoomBooking(-numRooms, arrival, departure);
		assertProcessBookingFailed(booking);
	}

	@Test
	public void failureNoRoomsAvailable(@Mocked HotelInterface hotelInterface) {
		new Expectations() {
			{
				HotelInterface.bulkBooking(numRooms, arrival, departure);
				result = new HotelException();
			}
		};

		BulkRoomBooking booking = new BulkRoomBooking(numRooms, arrival, departure);
		assertProcessBookingFailed(booking);
	}

	@Test
	public void success(@Mocked HotelInterface hotelInterface) {
		new Expectations() {
			{
				HotelInterface.bulkBooking(numRooms, arrival, departure);

				final Set<String> result = new HashSet<String>();
				for(int i = 0; i < numRooms; i++) {
					//TODO: Replace with actual references
					result.add(UUID.randomUUID().toString());
				}

				this.result = result;
			}
		};
		
		BulkRoomBooking booking = new BulkRoomBooking(numRooms, arrival, departure);
		booking.processBooking();

		Assert.assertEquals(arrival, booking.getArrival());
		Assert.assertEquals(departure, booking.getDeparture());
		Assert.assertEquals(ACCEPTED, booking.getStatus());
		Assert.assertEquals(numRooms, booking.getReferences().size());

		final Set<String> references = booking.getReferences();
		for(final String reference : references) {
			Assert.assertNotNull(reference);
			Assert.assertTrue(reference.length() > 0);
		}
	}
}
