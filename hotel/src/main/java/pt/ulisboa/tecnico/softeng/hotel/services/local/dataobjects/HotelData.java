package pt.ulisboa.tecnico.softeng.hotel.services.local.dataobjects;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import pt.ulisboa.tecnico.softeng.hotel.domain.Hotel;
import pt.ulisboa.tecnico.softeng.hotel.domain.Room;

public class HotelData {
    public static enum CopyDepth {
        SHALLOW, ROOMS
    }

    private String code;
    private String name;
    private List<RoomData> rooms = new ArrayList<>();

    public HotelData() {
    }

    public HotelData(Hotel hotel, CopyDepth depth) {
        this.code = hotel.getCode();
        this.name = hotel.getName();

        switch (depth) {
            case ROOMS:
                this.rooms = hotel.getRoomSet().stream()
                    .map(room -> new RoomData(room, RoomData.CopyDepth.SHALLOW))
                        .collect(Collectors.toList());
                break;
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

    public List<RoomData> getRooms() {
        return rooms;
    }

    public void setRooms(List<RoomData> rooms) {
        this.rooms = rooms;
    }
}
