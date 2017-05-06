package pt.ulisboa.tecnico.softeng.hotel.services.local.dataobjects;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.softeng.hotel.domain.Booking;
import pt.ulisboa.tecnico.softeng.hotel.domain.Room;
import pt.ulisboa.tecnico.softeng.hotel.domain.Room.Type;

public class RoomData {
	public static enum CopyDepth {
		SHALLOW, BOOKINGS
	}
	
	private String hotelCode;
    private String hotelName;
    private String number;
    private Type type;

	private List<RoomBookingData> bookings = new ArrayList<>();

	public RoomData() {
	}

	public RoomData(Room room, CopyDepth depth) {
		this.hotelCode = room.getHotel().getCode();
		this.hotelName = room.getHotel().getName();
		this.number = room.getNumber();
		this.type = room.getType();

		switch (depth) {
			case BOOKINGS:
				for(Booking booking : room.getBookingSet()) {
					bookings.add(new RoomBookingData(room, booking));
				}
			case SHALLOW:
			default:
				break;
		}
	}

	public String getHotelCode() {
		return hotelCode;
	}

	public void setHotelCode(String code) {
		hotelCode = code;
	}

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
	
	public List<RoomBookingData> getBookings() {
		return bookings;
	}
	
	public void setBookings(List<RoomBookingData> bookings) {
		this.bookings = bookings;
	}
}
