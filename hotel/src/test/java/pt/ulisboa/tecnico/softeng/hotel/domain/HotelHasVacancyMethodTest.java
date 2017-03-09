package pt.ulisboa.tecnico.softeng.hotel.domain;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pt.ulisboa.tecnico.softeng.hotel.domain.Room.Type;
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException;

public class HotelHasVacancyMethodTest {
	private Hotel hotel;

	@Before
	public void setUp() {
		this.hotel = new Hotel("XPTO123", "Paris");
		new Room(this.hotel, "01", Type.DOUBLE);
	}

	@Test
	public void hasVacancy() {
		LocalDate arrival = new LocalDate(2016, 12, 19);
		LocalDate departure = new LocalDate(2016, 12, 21);

		Room room = this.hotel.hasVacancy(Type.DOUBLE, arrival, departure);

		Assert.assertEquals("01", room.getNumber());
	}
	
	@Test(expected = HotelException.class)
	public void wrongType(){
		LocalDate arrival = new LocalDate(2016, 12, 19);
		LocalDate departure = new LocalDate(2016, 12, 21);
		
		this.hotel.hasVacancy(Type.SINGLE, arrival, departure);
	}
	@Test(expected = HotelException.class)
	public void switchDate(){
		LocalDate arrival = new LocalDate(2016, 12, 21);
		LocalDate departure = new LocalDate(2016, 12, 19);
		this.hotel.hasVacancy(Type.DOUBLE, arrival, departure);			
	}
	
	@Test(expected = HotelException.class)
	public void wrongDate(){
		LocalDate arrival = new LocalDate(2016, 12, 19);
		LocalDate departure = new LocalDate(2016, 12, 21);
		Room room = this.hotel.hasVacancy(Type.DOUBLE, arrival, departure);
		room.reserve(Type.DOUBLE, arrival, departure);		
		arrival = new LocalDate(2016, 12, 18);
		departure = new LocalDate(2016, 12, 22);
		this.hotel.hasVacancy(Type.DOUBLE, arrival, departure);
	}
	
	@After
	public void tearDown() {
		Hotel.hotels.clear();
	}

}
