package pt.ulisboa.tecnico.softeng.hotel.domain;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Set;

import org.junit.After;
import org.junit.Test;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;

public class HotelPersistenceTest {

    private static final String HOTEL_CODE = "XPTO123";
    private static final String HOTEL_NAME = "Lisboa";

    @Test
    public void success() {
        atomicProcess();
        atomicAssert();
    }

    @Atomic(mode = TxMode.WRITE)
    private void atomicProcess() {
        new Hotel(HOTEL_CODE, HOTEL_NAME);
    }

    @Atomic(mode = TxMode.READ)
    private void atomicAssert() {
        Set<Hotel> hotels =
        		FenixFramework.getDomainRoot().getHotelSet();
        assertEquals(1, hotels.size());

        Hotel hotel = new ArrayList<>(hotels).get(0);

        assertEquals(HOTEL_CODE, hotel.getCode());
        assertEquals(HOTEL_NAME, hotel.getName());
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