package pt.ulisboa.tecnico.softeng.activity.services.local;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.joda.time.LocalDate;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;
import pt.ulisboa.tecnico.softeng.activity.domain.Activity;
import pt.ulisboa.tecnico.softeng.activity.domain.ActivityOffer;
import pt.ulisboa.tecnico.softeng.activity.domain.ActivityProvider;
import pt.ulisboa.tecnico.softeng.activity.domain.Booking;
import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException;
import pt.ulisboa.tecnico.softeng.activity.services.local.dataobjects.ActivityData;
import pt.ulisboa.tecnico.softeng.activity.services.local.dataobjects.ActivityOfferData;
import pt.ulisboa.tecnico.softeng.activity.services.local.dataobjects.ActivityProviderData;
import pt.ulisboa.tecnico.softeng.activity.services.local.dataobjects.ActivityProviderData.CopyDepth;
import pt.ulisboa.tecnico.softeng.activity.services.local.dataobjects.ActivityReservationData;

public class ActivityInterface {
	
    @Atomic(mode = TxMode.WRITE)
    public static void createActivity(String activityProviderCode, ActivityData activityData) {
        new Activity(getActivityProviderByCode(activityProviderCode), activityData.getName(), activityData.getMinAge(),
        		 activityData.getMaxAge(), activityData.getCapacity());
    }

	@Atomic(mode = TxMode.WRITE)
	public static String cancelReservation(String reference) {
		final Booking booking = getBookingByReference(reference);
		if(booking != null) {
			return booking.cancel();
		}
		throw new ActivityException();
	}

	@Atomic(mode = TxMode.WRITE)
	public static void createActivityOffer(String activityProviderCode, String activityCode, ActivityOfferData activityOfferData) {
		new ActivityOffer(getActivityByCode(activityProviderCode, activityCode), activityOfferData.getBegin(), activityOfferData.getEnd());
	}
	

	@Atomic(mode = TxMode.WRITE)
	public static void createActivityProvider(ActivityProviderData activityProviderData) {
		new ActivityProvider(activityProviderData.getCode(), activityProviderData.getName());
	}


	
	
	private static ActivityProvider getActivityProviderByCode(String code) {
		for(final ActivityProvider activityProvider : FenixFramework.getDomainRoot().getActivityProviderSet()) {
			if(activityProvider.getCode().equals(code)) {
				return activityProvider;
			}
		}

		return null;
	}

	@Atomic(mode = TxMode.READ)
	public static ActivityProviderData getActivityProviderDataByCode(String activityProviderCode, ActivityProviderData.CopyDepth depth) {
		final ActivityProvider activityProvider = getActivityProviderByCode(activityProviderCode);

		if(activityProvider != null) {
			return new ActivityProviderData(activityProvider, depth);
		}
		
		return null;
	}
	
	@Atomic(mode = TxMode.READ)
	public static List<ActivityOfferData> getActivityOfferDatasByCodeAndDate(
			String providerCode, String activityCode, LocalDate begin, 
			LocalDate end, ActivityOfferData.CopyDepth depth) {
	List<ActivityOffer> offers = getActivityOffersByCodeAndDates(
			providerCode, activityCode, begin, end);
	if(offers != null) {
		List<ActivityOfferData> offerDatas = new ArrayList<>();
		
		for(ActivityOffer offer : offers) {
			offerDatas.add(new ActivityOfferData(offer, depth));
		}
		return offerDatas;
	}
	return null;
}
	
	@Atomic(mode = TxMode.READ)
	public static List<ActivityOffer> getActivityOffersByCodeAndDates(
			String providerCode, String activityCode, LocalDate begin, LocalDate end) {
		Activity activity = getActivityByCode(providerCode, activityCode);
		if(activity != null) {
			List<ActivityOffer> offers = new ArrayList<>();
			
			for (ActivityOffer offer : activity.getActivityOfferSet()) {
				if(offer.getBegin().equals(begin) && offer.getEnd().equals(end))
					offers.add(offer);
			}
			return offers;
		}
		return null;
	}
		
	@Atomic(mode = TxMode.READ)
	public static List<ActivityProviderData> getActivityProviders() {
		final List<ActivityProviderData> activityProviders = new ArrayList<>();

		for(final ActivityProvider activityProvider : FenixFramework.getDomainRoot().getActivityProviderSet()) {
			activityProviders.add(new ActivityProviderData(activityProvider, CopyDepth.SHALLOW));
		}

		return activityProviders;
	}


	@Atomic(mode = TxMode.READ)
	public static ActivityReservationData getActivityReservationData(String reference) {
		for(final ActivityProvider provider : FenixFramework.getDomainRoot().getActivityProviderSet()) {
			for(final Activity activity : provider.getActivitySet()) {
				for(final ActivityOffer offer : activity.getActivityOfferSet()) {
					final Booking booking = offer.getBooking(reference);
					if(booking != null) {
						return new ActivityReservationData(provider, offer, booking);
					}
				}
			}
		}
		throw new ActivityException();
	}

	private static Booking getBookingByReference(String reference) {
		for(final ActivityProvider provider : FenixFramework.getDomainRoot().getActivityProviderSet()) {
			final Booking booking = provider.getBooking(reference);
			if(booking != null) {
				return booking;
			}
		}
		return null;
	}

	@Atomic(mode = TxMode.WRITE)
	public static String reserveActivity(LocalDate begin, LocalDate end, int age) {
		List<ActivityOffer> offers;
		for(final ActivityProvider provider : FenixFramework.getDomainRoot().getActivityProviderSet()) {
			offers = provider.findOffer(begin, end, age);
			if(!offers.isEmpty()) {
				return new Booking(offers.get(0)).getReference();
			}
		}
		throw new ActivityException();
	}

	@Atomic(mode = TxMode.READ)
	public static ActivityData getActivityDataByCode(String activityProviderCode, String activityCode, ActivityData.CopyDepth depth) {
		Activity activity = getActivityByCode(activityProviderCode, activityCode);
		
		if(activity != null) {
			return new ActivityData(activity, depth);
		}
		
		return null;
	}
	
	@Atomic(mode = TxMode.READ)
	public static Activity getActivityByCode(String activityProviderCode, String activityCode) {
		ActivityProvider activityProvider = getActivityProviderByCode(activityProviderCode);
		
		if(activityProvider != null) {
			Set<Activity> activities = activityProvider.getActivitySet();

			for(Activity activity : activities) {
				if(activity.getCode().equals(activityCode)) {
					return activity;
				}
			}
		}
		
		return null;
	}
}
