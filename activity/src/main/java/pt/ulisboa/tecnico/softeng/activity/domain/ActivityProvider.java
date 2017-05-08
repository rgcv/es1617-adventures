package pt.ulisboa.tecnico.softeng.activity.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.LocalDate;

import pt.ist.fenixframework.FenixFramework;
import pt.ulisboa.tecnico.softeng.activity.dataobjects.ActivityReservationData;
import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException;

public class ActivityProvider extends ActivityProvider_Base {
    static final int CODE_SIZE = 6;

    public ActivityProvider(String code, String name) {
        checkArguments(code, name);

        setCode(code);
        setName(name);

        FenixFramework.getDomainRoot().addProvider(this);
    }

    public void delete() {
        setRoot(null);

        for(Activity activity: getActivitySet()) {
            activity.delete();
        }

        deleteDomainObject();
    }

    private void checkArguments(String code, String name) {
        if (code == null || name == null || code.trim().equals("") || name.trim().equals("")) {
            throw new ActivityException();
        }

        if (code.length() != ActivityProvider.CODE_SIZE) {
            throw new ActivityException();
        }

        Set<ActivityProvider> providers = FenixFramework.getDomainRoot().getProviderSet();
        for (ActivityProvider activityProvider : providers) {
            if (activityProvider.getCode().equals(code) || activityProvider.getName().equals(name)) {
                throw new ActivityException();
            }
        }
    }

    public int getNumberOfActivities() {
        return getActivitySet().size();
    }
    
    
    public List<ActivityOffer> findOffer(LocalDate begin, LocalDate end, int age) {
        List<ActivityOffer> result = new ArrayList<>();
        for (Activity activity : getActivitySet()) {
            result.addAll(activity.getOffers(begin, end, age));
        }
        return result;
    }

    private Booking getBooking(String reference) {
        for (Activity activity : getActivitySet()) {
            Booking booking = activity.getBooking(reference);
            if (booking != null) {
                return booking;
            }
        }
        return null;
    }

    private static Booking getBookingByReference(String reference) {
        Set<ActivityProvider> providers = FenixFramework.getDomainRoot().getProviderSet();

        for (ActivityProvider provider : providers) {
            Booking booking = provider.getBooking(reference);
            if (booking != null) {
                return booking;
            }
        }
        return null;
    }

    public static String reserveActivity(LocalDate begin, LocalDate end, int age) {
        List<ActivityOffer> offers;
        Set<ActivityProvider> providers = FenixFramework.getDomainRoot().getProviderSet();

        for (ActivityProvider provider : providers) {
            offers = provider.findOffer(begin, end, age);
            if (!offers.isEmpty()) {
                return new Booking(provider, offers.get(0)).getReference();
            }
        }
        throw new ActivityException();
    }

    public static String cancelReservation(String reference) {
        Booking booking = getBookingByReference(reference);
        if (booking != null) {
            return booking.cancel();
        }
        throw new ActivityException();
    }

    public static ActivityReservationData getActivityReservationData(String reference) {
        Set<ActivityProvider> providers = FenixFramework.getDomainRoot().getProviderSet();

        for (ActivityProvider provider : providers) {
            for (Activity activity : provider.getActivitySet()) {
                for (ActivityOffer offer : activity.getOfferSet()) {
                    Booking booking = offer.getBooking(reference);
                    if (booking != null) {
                        return new ActivityReservationData(provider, offer, booking);
                    }
                }
            }
        }
        throw new ActivityException();
    }

}
