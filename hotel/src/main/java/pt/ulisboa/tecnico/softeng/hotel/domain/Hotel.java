package pt.ulisboa.tecnico.softeng.hotel.domain;

import java.util.HashSet;
import java.util.Set;

import org.joda.time.LocalDate;

import pt.ulisboa.tecnico.softeng.hotel.dataobjects.RoomBookingData;
import pt.ulisboa.tecnico.softeng.hotel.domain.Room.Type;
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException;

public class Hotel {
	public static Set<Hotel> hotels = new HashSet<>();

	public static final int CODE_SIZE = 7;

	private final String code;
	private final String name;
	private final Set<Room> rooms = new HashSet<>();

	public Hotel(String code, String name) {
		checkArguments(code, name);

		this.code = code;
		this.name = name;
		Hotel.hotels.add(this);
	}

	private void checkArguments(String code, String name) {
		if (code == null || name == null || code.trim().length() == 0 || name.trim().length() == 0) {
			throw new HotelException();
		}

		if (code.length() != Hotel.CODE_SIZE) {
			throw new HotelException();
		}

		for (Hotel hotel : hotels) {
			if (hotel.getCode().equals(code)) {
				throw new HotelException();
			}
		}
	}

	public Room hasVacancy(Room.Type type, LocalDate arrival, LocalDate departure) {
		if (type == null || arrival == null || departure == null) {
			throw new HotelException();
		}

		for (Room room : this.rooms) {
			if (room.isFree(type, arrival, departure)) {
				return room;
			}
		}
		return null;
	}

	String getCode() {
		return this.code;
	}

	String getName() {
		return this.name;
	}
	
	public Set<Room> getRooms() {
		return rooms;
	}

	void addRoom(Room room) {
		if (hasRoom(room.getNumber())) {
			throw new HotelException();
		}

		this.rooms.add(room);
	}

	int getNumberOfRooms() {
		return this.rooms.size();
	}

	public boolean hasRoom(String number) {
		for (Room room : this.rooms) {
			if (room.getNumber().equals(number)) {
				return true;
			}
		}
		return false;
	}

	public static String reserveRoom(Room.Type type, LocalDate arrival, LocalDate departure) {
		for (Hotel hotel : Hotel.hotels) {
			Room room = hotel.hasVacancy(type, arrival, departure);
			if (room != null) {
				return room.reserve(type, arrival, departure).getReference();
			}
		}
		throw new HotelException();
	}

	public static String cancelBooking(String roomConfirmation) {
		if(roomConfirmation == null || roomConfirmation.equals("") || roomConfirmation.endsWith(" ")){
			throw new HotelException();
		}
		String roomCancellation = null;
		for (Hotel hotel : Hotel.hotels){
			for(Room room : hotel.rooms ){
				for(Booking booking : room.getBookings()){
					if(booking.getReference().equals(roomConfirmation)){
						// FIXME YOU'RE NOT SUPPOSED TO REMOVE THE BOOKING... JUST SET IT'S CANCELLATION REFERENCE AND DATE
						// FIXME ADDITIONALLY YOU NEED TO FIX THE METHODS THAT CHECK FOR REFERENCES TO COMPARE WITH CANCELLATION REFERENCE
						//       AND METHODS THAT COUNT THE AMOUNT OF BOOKINGS, ETC.
						room.getBookings().remove(booking); // FIXME DELETE THIS
						// FIXME SETTING THE CANCELLATION REFERENCE AND DATE SHOULD BE DONE IN A METHOD cancel IN Booking
						roomCancellation = booking.getReference() + "Cancelled";
						// FIXME SET THE CANCELLATION DATE
						RoomBookingData rbd = new RoomBookingData(); // FIXME DELETE THIS
						rbd.setCancellation(roomCancellation); // FIXME DELETE THIS
						rbd.setCancellationDate(new LocalDate()); //FIXME DELETE THIS
						return roomCancellation;
					}
				}
			}
		}
		throw new HotelException();
	}

	public static RoomBookingData getRoomBookingData(String reference) {
		if (reference == null || reference.trim().equals("")) throw new HotelException();

		for (Hotel hotel : Hotel.hotels) {
			for (Room room : hotel.getRooms()) {
				// FIXME CREATE A METHOD getBooking in ROOM
				for (Booking booking : room.getBookings()) {
					if (booking.getReference().equals(reference)) {
						RoomBookingData rbd = new RoomBookingData();
						// FIXME THIS SHOULD BE IN CONSTRUCTOR OF RoomBookingData
						rbd.setReference(reference);
						rbd.setHotelName(hotel.getName());
						rbd.setHotelCode(hotel.getCode());
						rbd.setRoomNumber(room.getNumber());
						rbd.setRoomType(room.getType().name());
						rbd.setArrival(booking.getArrival());
						rbd.setDeparture(booking.getDeparture());
						
						rbd.setCancellation(booking.getCancellation());
						rbd.setCancellationDate(booking.getCancellationDate());
						return rbd;
					}
				}
			}
		}
		throw new HotelException();
	}

	public static Set<String> bulkBooking(int number, LocalDate arrival,
			LocalDate departure) throws HotelException {
		if (number < 1 || arrival == null || departure == null)
			throw new HotelException();

		if (departure.isBefore(arrival.plusDays(1)))
			throw new HotelException();

		// FIXME THIS FOR SHOULD BE IN A DIFFERENT METHOD (IN ORDER TO AVOID MEGAMOTHS)
		Set<Room> rooms = new HashSet<>();
		for (Hotel hotel : Hotel.hotels) {
			for (Room room : hotel.rooms) {
				for (Type type : Type.values()) {
					if (room.isFree(type, arrival, departure))
						rooms.add(room);
				}
			}
		}

		if (rooms.size() < number)
			throw new HotelException();

		Set<String> references = new HashSet<>(number);
		for (Room room : rooms) {
			Booking booking = room.reserve(room.getType(), arrival, departure);
			references.add(booking.getReference());
			--number;

			if (number == 0) break;
		}

        return references;
	}

}
