package pt.ulisboa.tecnico.softeng.broker.domain;

import java.util.HashSet;
import java.util.Set;

import org.joda.time.LocalDate;

import pt.ulisboa.tecnico.softeng.broker.exception.RemoteAccessException;
import pt.ulisboa.tecnico.softeng.broker.interfaces.HotelInterface;
import pt.ulisboa.tecnico.softeng.hotel.dataobjects.RoomBookingData;
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException;

public class BulkRoomBooking {
	private final Set<String> references = new HashSet<>();
	private final int number;
	private final LocalDate arrival;
	private final LocalDate departure;
	private boolean cancelled = false;
	
	private int numberOfRemoteErrors = 0;
	private int numberOfHotelExceptions = 0;
	
	public static final int MAX_HOTEL_EXCEPTIONS = 5;
	public static final int MAX_REMOTE_ERRORS = 5;

	public BulkRoomBooking(int number, LocalDate arrival, LocalDate departure) {
		this.number = number;
		this.arrival = arrival;
		this.departure = departure;
	}

	public Set<String> getReferences() {
		return this.references;
	}

	public int getNumber() {
		return this.number;
	}

	public LocalDate getArrival() {
		return this.arrival;
	}

	public LocalDate getDeparture() {
		return this.departure;
	}
	
	public void setStatus(boolean cancelled) {
		this.cancelled = cancelled;
	} 
	
	public boolean getStatus() {
		return this.cancelled;
	}
	
	public void addHotelException() {
		this.numberOfHotelExceptions++;
	}
	
	public int getHotelExceptions() {
		return this.numberOfHotelExceptions;
	}
	
	private void setHotelExceptions(int numExceptions) {
		this.numberOfHotelExceptions = numExceptions;
	}
	
	public void addRemoteError() {
		this.numberOfRemoteErrors++;
	}
	
	public int getRemoteErrors() {
		return this.numberOfRemoteErrors;
	}
	
	private void setRemoteErrors(int numErrors) {
		this.numberOfRemoteErrors = numErrors;
	}

	public void processBooking() {
		if(getStatus()) {
			return;
		}

		try {
			this.references.addAll(HotelInterface.bulkBooking(this.number, this.arrival, this.departure));
			setHotelExceptions(0);
			setRemoteErrors(0);
			return;
		} 
		catch(HotelException he) {
			addHotelException();
			 
			if(getHotelExceptions() == MAX_HOTEL_EXCEPTIONS) {
				setStatus(true);
			}
			 
			setRemoteErrors(0);
			return;
		} 
		catch(RemoteAccessException rae) {
			addRemoteError();
			
			if(getRemoteErrors() == MAX_REMOTE_ERRORS) {
				setStatus(true);
			}
			
			setHotelExceptions(0);
			return;
		}
	}

	public String getReference(String type) {
		if (this.cancelled) {
			return null;
		}

		for (String reference : this.references) {
			RoomBookingData data = null;
			try {
				data = HotelInterface.getRoomBookingData(reference);
				setRemoteErrors(0);
			} catch (HotelException he) {
				setRemoteErrors(0);
			} catch (RemoteAccessException rae) {
				addRemoteError();
				if (getRemoteErrors() == MAX_REMOTE_ERRORS) {
					setStatus(true);
				}
			}

			if (data != null && data.getRoomType().equals(type)) {
				this.references.remove(reference);
				return reference;
			}
		}
		return null;
	}
}
