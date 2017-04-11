package pt.ulisboa.tecnico.softeng.hotel.domain;

import java.util.HashSet;
import java.util.Set;

import org.joda.time.LocalDate;

import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException;

public class Room extends Room_Base {
    public static enum Type {
        SINGLE, DOUBLE
    }

    private final Set<Booking> bookings = new HashSet<>();

    public Room(Hotel hotel, String number, Type type) {
        checkArguments(hotel, number, type);

        setHotel(hotel);
        setNumber(number);
        setType(type);

        getHotel().addRoom(this);
    }

    private void checkArguments(Hotel hotel, String number, Type type) {
        if (hotel == null || number == null || number.trim().length() == 0 || type == null) {
            throw new HotelException();
        }

        if (!number.matches("\\d*")) {
            throw new HotelException();
        }
    }


    int getNumberOfBookings() {
        return this.bookings.size();
    }

    boolean isFree(Type type, LocalDate arrival, LocalDate departure) {
        if (!type.equals(getType())) {
            return false;
        }

        for (Booking booking : this.bookings) {
            if (booking.conflict(arrival, departure)) {
                return false;
            }
        }

        return true;
    }
    
    public void delete() {
        setHotel(null);
        deleteDomainObject();
    }

    public Booking reserve(Type type, LocalDate arrival, LocalDate departure) {
        if (type == null || arrival == null || departure == null) {
            throw new HotelException();
        }

        if (!isFree(type, arrival, departure)) {
            throw new HotelException();
        }

        Booking booking = new Booking(getHotel(), arrival, departure);
        this.bookings.add(booking);

        return booking;
    }

    public Booking getBooking(String reference) {
        for (Booking booking : this.bookings) {
            if (booking.getReference().equals(reference)
                    || (booking.isCancelled() && booking.getCancellation().equals(reference))) {
                return booking;
            }
        }
        return null;
    }

}
