package pt.ulisboa.tecnico.softeng.hotel.domain;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException;

import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException;

public class HotelConstructorTest {

	@Before
	public void setUp() {

	}

	@Test
	public void success() {
		Hotel hotel = new Hotel("XPTO123", "Londres");

		Assert.assertEquals("Londres", hotel.getName());
		Assert.assertTrue(hotel.getCode().length() == Hotel.CODE_SIZE);
		Assert.assertEquals(0, hotel.getNumberOfRooms());
		Assert.assertEquals(1, Hotel.hotels.size());
	}
	@Test(expected = HotelException.class)
	public void UniqueCode(){
		new Hotel("XPTO123", "Batalha");
		new Hotel("XPTO123", "Lindoso");
	}
	@Test(expected = HotelException.class)
	public void ShortCode(){
		new Hotel("XPTO12", "Mexico");
	}
	@Test(expected = HotelException.class)
	public void LongCode(){
		new Hotel("XPTO1234", "EUA");
	}
	

	@Test 
	public void nullCode(){
		try {
			new Hotel(null, "RNL");
		} catch (Exception e) {
			Assert.assertTrue(Hotel.hotels.isEmpty());
		}
	}
	@Test
	public void uniqueCode(){
		try {
			new Hotel("XPTO123", "Batalha");
			new Hotel("XPTO123", "Lindoso");
			Assert.fail();
		} catch (HotelException he) {
			Assert.assertEquals(1, Hotel.hotels.size());
		}
		
	}
	@Test
	public void shortCode(){
		try {
			new Hotel("XPTO12", "Mexico");
			Assert.fail();
		} catch(HotelException he) {
			Assert.assertTrue(Hotel.hotels.isEmpty());
		}
		
	}
	@Test
	public void longCode(){
		try {
			new Hotel("XPTO1234", "EUA");
			Assert.fail();
		} catch(HotelException he) {
			Assert.assertTrue(Hotel.hotels.isEmpty());
		}
		
	}

	@Test 
	public void nullName(){
		try {
			new Hotel("XPTO123", null);
		} catch (Exception e) {
			Assert.assertTrue(Hotel.hotels.isEmpty());
		}
	}

	@After
	public void tearDown() {
		Hotel.hotels.clear();
	}

}
