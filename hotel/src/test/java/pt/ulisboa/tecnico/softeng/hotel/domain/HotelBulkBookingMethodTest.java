package pt.ulisboa.tecnico.softeng.hotel.domain;

import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.joda.time.LocalDate;

import pt.ulisboa.tecnico.softeng.hotel.domain.Room.Type;
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException;

public class HotelBulkBookingMethodTest {
    private static final String CODE = "HOLY350";
    private final LocalDate arrival = new LocalDate(2017, 3, 18);
    private final LocalDate departure = new LocalDate(2017, 3, 20);
    private Hotel hotel;


    @Before
    public void setUp() {
        this.hotel = new Hotel(CODE, "Hollywood");
        new Room(this.hotel, "001", Type.SINGLE);
        new Room(this.hotel, "002", Type.DOUBLE);
        new Room(this.hotel, "003", Type.SINGLE);
    }

    @Test
    public void success() {
        Set<String> references = Hotel.bulkBooking(3, this.arrival,
                this.departure);

        Assert.assertEquals(3, references.size());
        for (String ref : references)
            Assert.assertTrue(ref.startsWith(CODE));
    }

    @Test(expected = HotelException.class)
    public void zeroBookings() {
        Hotel.bulkBooking(0, this.arrival, this.departure);
    }

    @Test(expected = HotelException.class)
    public void nullArrival() {
        Hotel.bulkBooking(1, null, this.departure);
    }

    @Test(expected = HotelException.class)
    public void nullDeparture() {
        Hotel.bulkBooking(1, this.arrival, null);
    }

    @Test(expected = HotelException.class)
    public void lessThanOneFullDay() {
        Hotel.bulkBooking(1, this.arrival, this.arrival);
    }

    @Test(expected = HotelException.class)
    public void notEnoughRooms() {
        Hotel.bulkBooking(4, this.arrival, this.departure);
    }

    @After
    public void tearDown() {
        Hotel.hotels.clear();
    }
}
