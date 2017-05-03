package pt.ulisboa.tecnico.softeng.hotel.services.local.dataobjects;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.softeng.hotel.domain.Room;
import pt.ulisboa.tecnico.softeng.hotel.services.local.dataobjects.HotelData.CopyDepth;

public class RoomData {
	private HotelData hotel;
	private String number;
	private Room.Type type;
	
	private List<RoomBookingData> bookings = new ArrayList<>();
	
	public RoomData() {
	}
	
	public RoomData(Room room) {
		this.hotel = new HotelData(room.getHotel(), CopyDepth.SHALLOW);
		this.number = room.getNumber();
		this.type = room.getType();
	}
	
	public HotelData getHotel() {
		return hotel;
	}
	
	public void setHotel(HotelData hotel) {
		this.hotel = hotel;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public Room.Type getType() {
		return type;
	}

	public void setType(Room.Type type) {
		this.type = type;
	}
	
	public List<RoomBookingData> getBookings() {
		return bookings;
	}
	
	public void setBookings(List<RoomBookingData> bookings) {
		this.bookings = bookings;
	}
}
