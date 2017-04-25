package pt.ulisboa.tecnico.softeng.hotel.services.local.dataobjects;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.softeng.hotel.domain.Hotel;
import pt.ulisboa.tecnico.softeng.hotel.domain.Room;

public class HotelData {
    private String code;
    private String name;
    private List<Room> rooms = new ArrayList<>();

    public HotelData() {
    }

    public HotelData(Hotel hotel, CopyDepth depth) {
        this.code = hotel.getCode();
        this.name = hotel.getName();

        switch (depth) {
            case ROOMS:
                //TODO: Add RoomData objects to rooms
            case SHALLOW:
            default:
                break;
        }
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    public static enum CopyDepth {
        SHALLOW, ROOMS
    }
}
