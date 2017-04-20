package pt.ulisboa.tecnico.softeng.hotel.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Set;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ulisboa.tecnico.softeng.hotel.domain.Room.Type;
import pt.ist.fenixframework.FenixFramework;

public class HotelPersistenceTest {

    private static final String HOTEL_CODE = "XPTO123";
    private static final String HOTEL_NAME = "Lisboa";
    private static LocalDate ARRIVAL_DATE;
    private static LocalDate DEPARTURE_DATE;

    @Test
    public void success() {
        atomicProcess();
        atomicAssert();
    }

    @Atomic(mode = TxMode.WRITE)
    private void atomicProcess() {
        Hotel hotel = new Hotel(HOTEL_CODE, HOTEL_NAME);
        new Room(hotel, "01" , Type.DOUBLE);
        new Booking(hotel, DEPARTURE_DATE = new LocalDate(2016, 12, 19), ARRIVAL_DATE =  new LocalDate(2016, 12, 21));
    }

    @Atomic(mode = TxMode.READ)
    private void atomicAssert() {
    	/* Test Hotel */
    	Set<Hotel> hotels = FenixFramework.getDomainRoot().getHotelSet();
    	assertNotNull(FenixFramework.getDomainRoot().getHotelSet());        		
        assertEquals(1, FenixFramework.getDomainRoot().getHotelSet().size());

        Hotel hotel = new ArrayList<>(hotels).get(0);
        assertEquals(HOTEL_CODE, hotel.getCode());
        assertEquals(HOTEL_NAME, hotel.getName());
        
        /* Test Room */
    	Set<Room> rooms = hotel.getRoomSet();
        assertNotNull(rooms);
        assertEquals(1, rooms.size());
        Room room = new ArrayList<>(rooms).get(0);
        assertEquals(hotel, room.getHotel());
        assertEquals("01", room.getNumber());
        assertEquals(Type.DOUBLE, room.getType());
        assertEquals(1, room.getBookingSet().size());
        
        /* Test Booking */
        Set<Booking> bookings = room.getBookingSet();
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        Booking booking = new ArrayList<>(bookings).get(0);
        Assert.assertTrue(booking.getReference().startsWith(HOTEL_CODE));
        Assert.assertTrue(booking.getReference().length() > Hotel.CODE_SIZE);
        assertEquals(ARRIVAL_DATE, booking.getArrival());
        assertEquals(DEPARTURE_DATE, booking.getDeparture());
        
    }

    @After
    @Atomic(mode = TxMode.WRITE)
    public void tearDown() {
        Set<Hotel> hotels = FenixFramework.getDomainRoot().getHotelSet();

        for (Hotel hotel : hotels) {
            hotel.delete();
        }
    }
}