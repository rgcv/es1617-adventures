package pt.ulisboa.tecnico.softeng.hotel.domain;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pt.ulisboa.tecnico.softeng.hotel.domain.Room.Type;
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException;

public class RoomConstructorMethodTest {
	private Hotel hotel;

	@Before
	public void setUp() {
		this.hotel = new Hotel("XPTO123", "Lisboa");
	}

	@Test
	public void success() {
		Room room = new Room(this.hotel, "01", Type.DOUBLE);

		Assert.assertEquals(this.hotel, room.getHotel());
		Assert.assertEquals("01", room.getNumber());
		Assert.assertEquals(Type.DOUBLE, room.getType());
		Assert.assertEquals(1, this.hotel.getNumberOfRooms());
	}
	
	@Test
	public void noHotelRoom(){
		try{
			new Room(null, "02", Type.DOUBLE);
			Assert.fail();
		} catch (HotelException he){
			Assert.assertEquals(0, this.hotel.getNumberOfRooms());
		}	
	}
	
	@Test
	public void uniqueRoom(){
		try {
			new Room(this.hotel, "01", Type.DOUBLE);
			new Room(this.hotel, "01", Type.DOUBLE);
			Assert.fail();
		} catch (HotelException he) {
			Assert.assertEquals(1, this.hotel.getNumberOfRooms());
		}
	}	
	
	@Test
	public void notNumberRoom(){
		try{
			new Room(this.hotel, "AB", Type.DOUBLE);
			Assert.fail();
		} catch (HotelException he){
			Assert.assertEquals(0, this.hotel.getNumberOfRooms());
		}	
	}	
	
	@Test
	public void spacesNumberRoom(){
		try{
			new Room(this.hotel, " ", Type.DOUBLE); // This is number with spaces, not empty
			Assert.fail();
		} catch (HotelException he){
			Assert.assertEquals(0, this.hotel.getNumberOfRooms());
		}	
	}	
	
	// failure: emptyNumberRoom
	
	@Test
	public void noNumberRoom(){
		try{
			new Room(this.hotel, null, Type.DOUBLE);
			Assert.fail();
		} catch (HotelException he){
			Assert.assertEquals(0, this.hotel.getNumberOfRooms());
		}	
	}
	
	@Test
	public void noTypeRoom(){
		try{
			new Room(this.hotel, "03", null);
			Assert.fail();
		} catch (HotelException he){
			Assert.assertEquals(0, this.hotel.getNumberOfRooms());
		}	
	}
	
	@After
	public void tearDown() {
		Hotel.hotels.clear();
	}

}
