package pt.ulisboa.tecnico.softeng.activity.domain;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;

import pt.ist.fenixframework.FenixFramework;
import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException;

public class ActivityProvider extends ActivityProvider_Base {
    static final int CODE_SIZE = 6;

    public ActivityProvider(String code, String name) {
        checkArguments(code, name);

        setCode(code);
        setName(name);

        FenixFramework.getDomainRoot().addActivityProvider(this);
    }

    public void delete() {
        setRoot(null);

        for (Activity activity : getActivitySet()) {
            activity.delete();
        }

        deleteDomainObject();
    }

    private void checkArguments(String code, String name) {
        if (code == null || name == null || code.trim().equals("") || name.trim().equals("")) {
            throw new ActivityException("Invalid or empty activity provider name or code.");
        }

        if (code.length() != ActivityProvider.CODE_SIZE) {
            throw new ActivityException("Code's size must be " + CODE_SIZE);
        }

        for (ActivityProvider activityProvider : FenixFramework.getDomainRoot().getActivityProviderSet()) {
            if (activityProvider.getCode().equals(code) || activityProvider.getName().equals(name)) {
                throw new ActivityException("Activity provider with code '" + code + "' already exists");
            }
        }
    }

    @Override
    public int getCounter() {
        int counter = super.getCounter() + 1;
        setCounter(counter);
        return counter;
    }

    public List<ActivityOffer> findOffer(LocalDate begin, LocalDate end, int age) {
        List<ActivityOffer> result = new ArrayList<>();
        for (Activity activity : getActivitySet()) {
            result.addAll(activity.getOffers(begin, end, age));
        }
        return result;
    }

    public Booking getBooking(String reference) {
        for (Activity activity : getActivitySet()) {
            Booking booking = activity.getBooking(reference);
            if (booking != null) {
                return booking;
            }
        }
        return null;
    }

}
