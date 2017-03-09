package pt.ulisboa.tecnico.softeng.hotel.domain;

import java.util.HashSet;
import java.util.Set;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException;

public class Room {
	private static Logger logger = LoggerFactory.getLogger(Room.class);

	public static enum Type {
		SINGLE, DOUBLE
	}
		
	private final Hotel hotel;
	private final String number;
	private final Type type;
	private final Set<Booking> bookings = new HashSet<>();

	public Room(Hotel hotel, String number, Type type) {
		this.hotel = hotel;
		checkInt(number);
		this.number = number;

		checkRoom(number);
		this.type = type;
		this.hotel.addRoom(this);
		
	}
		
	private void checkRoom(String number) {
		for (Room room : this.hotel.getRooms()) {
			if(number.equals(room.getNumber())) {
				throw new HotelException();
			}
		}
	}
	
	private void checkInt(String number){
		try { 
			Integer.parseInt(number); 
		} catch(NumberFormatException e) { 
			throw new HotelException(); 
		} catch(NullPointerException e) {
			throw new HotelException();
		}
	}
		
	Hotel getHotel() {
		return this.hotel;
	}

	String getNumber() {
		return this.number;
	}

	Type getType() {
		return this.type;
	}

	boolean isFree(Type type, LocalDate arrival, LocalDate departure) {
		if (!type.equals(this.type) || arrival.isAfter(departure)) {
			return false;
		}

		for (Booking booking : this.bookings) {
			if (booking.conflict(arrival, departure)) {
				return false;
			}
		}

		return true;
	}

	public Booking reserve(Type type, LocalDate arrival, LocalDate departure) {
		if (!isFree(type, arrival, departure)) {
			throw new HotelException();
		}
		
		Booking booking = new Booking(this.hotel, arrival, departure);
		this.bookings.add(booking);

		return booking;
	}

	public Set<Booking> getBookings() {
		return bookings;
	}

}
