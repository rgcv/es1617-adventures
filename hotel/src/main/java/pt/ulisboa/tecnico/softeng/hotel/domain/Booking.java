package pt.ulisboa.tecnico.softeng.hotel.domain;

import org.joda.time.LocalDate;

import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException;

public class Booking {
	private static int counter = 0;

	private final String reference;
	private final LocalDate arrival;
	private final LocalDate departure;

	Booking(Hotel hotel, LocalDate arrival, LocalDate departure) throws HotelException {
		if(hotel == null || arrival == null || departure == null || arrival.isAfter(departure)) {
			throw new HotelException();
		}
		
		this.reference = hotel.getCode() + Integer.toString(++Booking.counter);
		this.arrival = arrival;
		this.departure = departure;
	}

	public String getReference() {
		return this.reference;
	}

	public LocalDate getArrival() {
		return this.arrival;
	}

	public LocalDate getDeparture() {
		return this.departure;
	}

	public boolean conflict(LocalDate arrival, LocalDate departure) throws HotelException {
		// Please consider code readability and return true whenever there's a conflict.
		// Exceptions should be used whenever there's is an inconsistency or a faulty situation.
		
		//if(arrival == null || departure == null
		//	|| (arrival.isAfter(this.arrival) || arrival.isEqual(this.arrival)) && (arrival.isBefore(this.departure) || arrival.isEqual(this.departure))
		//	|| (departure.isAfter(this.arrival) || departure.isEqual(this.arrival)) && (departure.isBefore(this.departure) || departure.isEqual(this.departure))
		//	|| arrival.isBefore(this.arrival) && departure.isAfter(this.departure)) {
		//	throw new HotelException();
		//}
		
		if (arrival == null || departure == null || departure.isBefore(arrival)) {
			throw new HotelException();
		}
		
		if ((arrival.equals(this.arrival) || arrival.isAfter(this.arrival)) && arrival.isBefore(this.departure)) {
			return true;
		}
		
		if ((departure.equals(this.departure) || departure.isBefore(this.departure)) && departure.isAfter(this.arrival)) {
			return true;
		}
		
		if ((arrival.isBefore(this.arrival) && departure.isAfter(this.departure))) {
			return true;
		}

		return false;
	}

	public static int getCounter() {
		return Booking.counter;
	}
	
	public static void resetCounter() {
		Booking.counter = 0;
	}
}
