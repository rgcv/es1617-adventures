package pt.ulisboa.tecnico.softeng.hotel.services.local.dataobjects;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.softeng.hotel.domain.Room;

public class RoomData {
	public static enum CopyDepth {
		SHALLOW, BOOKINGS
	}

	private String number;
	private String type;
	
	private List<RoomBookingData> bookings = new ArrayList<>();
	
	public RoomData() {
	}
	
	public RoomData(Room room, CopyDepth depth) {
		this.number = room.getNumber();
		this.type = room.getType().name();
		
		switch (depth) {
			case BOOKINGS:
				//TODO: Add RoomBookingData objects to bookings
			case SHALLOW:
			default:
				break;
		}
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public List<RoomBookingData> getBookings() {
		return bookings;
	}
	
	public void setBookings(List<RoomBookingData> bookings) {
		this.bookings = bookings;
	}
}
