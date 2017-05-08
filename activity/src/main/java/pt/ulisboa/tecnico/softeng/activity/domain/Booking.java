package pt.ulisboa.tecnico.softeng.activity.domain;

import org.joda.time.LocalDate;

import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException;

public class Booking extends Booking_Base {
    private static int counter = 0;

    public Booking(ActivityProvider provider, ActivityOffer offer) {
        checkArguments(provider, offer);

        setReference(provider.getCode() + Integer.toString(++Booking.counter));

        setOffer(offer);
    }
    
    public void delete() {
    	setOffer(null);
    	
    	deleteDomainObject();
    }

    private void checkArguments(ActivityProvider provider, ActivityOffer offer) {
        if (provider == null || offer == null) {
            throw new ActivityException();
        }
    }

    public String cancel() {
        setCancellation("CANCEL" + getReference());
        setCancellationDate(new LocalDate());
        return getCancellation();
    }

    public boolean isCancelled() {
        return getCancellation() != null;
    }
}
