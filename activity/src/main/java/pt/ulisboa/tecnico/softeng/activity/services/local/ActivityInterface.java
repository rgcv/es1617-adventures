package pt.ulisboa.tecnico.softeng.activity.services.local;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;
import pt.ulisboa.tecnico.softeng.activity.domain.Activity;
import pt.ulisboa.tecnico.softeng.activity.domain.ActivityOffer;
import pt.ulisboa.tecnico.softeng.activity.domain.ActivityProvider;
import pt.ulisboa.tecnico.softeng.activity.domain.Booking;
import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException;
import pt.ulisboa.tecnico.softeng.activity.services.local.dataobjects.ActivityProviderData;
import pt.ulisboa.tecnico.softeng.activity.services.local.dataobjects.ActivityProviderData.CopyDepth;
import pt.ulisboa.tecnico.softeng.activity.services.local.dataobjects.ActivityReservationData;

public class ActivityInterface {

	@Atomic(mode = TxMode.WRITE)
	public static String cancelReservation(String reference) {
		final Booking booking = getBookingByReference(reference);
		if(booking != null) {
			return booking.cancel();
		}
		throw new ActivityException();
	}

	@Atomic(mode = TxMode.WRITE)
	public static void createActivityProvider(ActivityProviderData activityProviderData) {
		new ActivityProvider(activityProviderData.getCode(), activityProviderData.getName());
	}

	@Atomic(mode = TxMode.READ)
	public static List<ActivityProviderData> getActivityProviders() {
		final List<ActivityProviderData> brokers = new ArrayList<>();

		for(final ActivityProvider broker : FenixFramework.getDomainRoot().getActivityProviderSet()) {
			brokers.add(new ActivityProviderData(broker, CopyDepth.SHALLOW));
		}
		return brokers;
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

}
