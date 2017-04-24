package pt.ulisboa.tecnico.softeng.activity.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.LocalDate;

import pt.ulisboa.tecnico.softeng.activity.dataobjects.ActivityReservationData;
import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException;

public class ActivityProvider {
	public static Set<ActivityProvider> providers = new HashSet<>();

	static final int CODE_SIZE = 6;

	private final String name;
	private final String code;
	private final Set<Activity> activities = new HashSet<>();

	public ActivityProvider(String code, String name) {
		checkArguments(code, name);

		this.code = code;
		this.name = name;

		ActivityProvider.providers.add(this);
	}

	private void checkArguments(String code, String name) {
		if (code == null || name == null || code.trim().equals("") || name.trim().equals("")) {
			throw new ActivityException();
		}

		if (code.length() != ActivityProvider.CODE_SIZE) {
			throw new ActivityException();
		}

		for (ActivityProvider activityProvider : providers) {
			if (activityProvider.getCode().equals(code) || activityProvider.getName().equals(name)) {
				throw new ActivityException();
			}
		}
	}

	String getName() {
		return this.name;
	}

	String getCode() {
		return this.code;
	}

	int getNumberOfActivities() {
		return this.activities.size();
	}

	void addActivity(Activity activity) {
		this.activities.add(activity);
	}
	
	Set<Activity> getActivities() {
		return activities;
	}

	public List<ActivityOffer> findOffer(LocalDate begin, LocalDate end, int age) {
		List<ActivityOffer> result = new ArrayList<>();
		for (Activity activity : this.activities) {
			result.addAll(activity.getOffers(begin, end, age));
		}
		return result;
	}

	public static String reserveActivity(LocalDate begin, LocalDate end, int age) {
		List<ActivityOffer> offers;
		for (ActivityProvider provider : ActivityProvider.providers) {
			offers = provider.findOffer(begin, end, age);
			if (!offers.isEmpty()) {
				return new Booking(provider, offers.get(0)).getReference();
			}
		}
		throw new ActivityException();
	}

	public static String cancelReservation(String activityConfirmation) {
		if (activityConfirmation == null || activityConfirmation.equals("") || activityConfirmation.endsWith(" ")) {
			throw new ActivityException();
		}
		String activityCancellation = null;
		Booking b;
		
		for (ActivityProvider ap : ActivityProvider.providers) {
			for (Activity activity : ap.getActivities()) {
				for (ActivityOffer offer : activity.getAllOffers()) {
					b = offer.findBooking(activityConfirmation);
					if (b == null) break;

					// FIXME PLEASE SEE MY COMMENT ON Hotel.cancel
					offer.getBookings().remove(b);
					activityCancellation = b.getReference() + "Cancelled";
					
					ActivityReservationData ard = new ActivityReservationData();
					ard.setCancellation(activityCancellation);
					ard.setCancellationDate(new LocalDate());
					return activityCancellation;
				}
			}
		}
		throw new ActivityException();
	}

	public static ActivityReservationData getActivityReservationData(String reference) throws ActivityException {
		if(reference == null || providers == null || providers.isEmpty()) { throw new ActivityException(); }
		
		for(ActivityProvider provider : providers) {
			Set<Activity> activities = provider.getActivities();
			
			for(Activity activity : activities) {
				ActivityOffer offer = activity.findOffer(reference);
				
				if(offer == null) { throw new ActivityException(); }

				//FIXME PLEASE SEE MY COMMENT ON Hotel.getOperationData
				ActivityReservationData data = new ActivityReservationData();
				data.setReference(reference); 
				data.setCode(activity.getCode());
				data.setName(activity.getName()); 
				data.setBegin(offer.getBegin());
				data.setEnd(offer.getEnd());
				data.setCancellation(null); //FIXME: cancellation string
				data.setCancellationDate(null); //FIXME: cancellation date
				data.setReference(reference);
				return data;
			}
		}
		
		throw new ActivityException();
	}
}
