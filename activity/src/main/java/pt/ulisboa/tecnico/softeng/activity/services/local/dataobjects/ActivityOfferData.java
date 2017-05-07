package pt.ulisboa.tecnico.softeng.activity.services.local.dataobjects;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

import pt.ulisboa.tecnico.softeng.activity.domain.ActivityOffer;
import pt.ulisboa.tecnico.softeng.activity.domain.Booking;
import pt.ulisboa.tecnico.softeng.activity.services.local.ActivityInterface;

public class ActivityOfferData {
	public static enum CopyDepth {
		SHALLOW, ACTIVITY_RESERVATIONS
	}
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate begin;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate end;
	
	List<ActivityReservationData> reservations = new ArrayList<ActivityReservationData>();

	public ActivityOfferData() {}

	public ActivityOfferData(ActivityOffer activityOffer, CopyDepth depth) {
		begin = activityOffer.getBegin();
		end = activityOffer.getEnd();
		
		switch(depth) {
			case ACTIVITY_RESERVATIONS:
				for(Booking booking : activityOffer.getBookingSet()) {
		            reservations.add(ActivityInterface.getActivityReservationData(booking.getReference()));
		        }
				break;
			case SHALLOW:
			default:
				break;
		}
	}

	public LocalDate getBegin() {
		return begin;
	}

	public LocalDate getEnd() {
		return end;
	}

	public void setBegin(LocalDate begin) {
		this.begin = begin;
	}

	public void setEnd(LocalDate end) {
		this.end = end;
	}
	
	public List<ActivityReservationData> getReservations() {
		return reservations;
	}	
	
	public void setReservations(List<ActivityReservationData> reservations) {
		this.reservations = reservations;
	}
}
