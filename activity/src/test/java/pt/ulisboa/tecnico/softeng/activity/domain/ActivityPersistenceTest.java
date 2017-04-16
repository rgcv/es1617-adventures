package pt.ulisboa.tecnico.softeng.activity.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Test;

import org.joda.time.LocalDate;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;

public class ActivityPersistenceTest {

    private static final String PROVIDER_CODE = "APR001";
    private static final String PROVIDER_NAME = "Best Provider";

    private static final int MIN_AGE = 20;
    private static final int MAX_AGE = 42;
    private static final int CAPACITY = 23;

    private static final String ACTIVITY_NAME = "Dank activity name";

    private final LocalDate begin = new LocalDate(2016, 12, 19);
    private final LocalDate end = new LocalDate(2016, 12, 21);


    @Test
    public void success() {
        atomicProcess();
        atomicAssert();
    }

    @Atomic(mode = TxMode.WRITE)
    private void atomicProcess() {
        ActivityProvider provider = new ActivityProvider(PROVIDER_CODE, PROVIDER_NAME);
        Activity activity = new Activity(provider, ACTIVITY_NAME, MIN_AGE, MAX_AGE, CAPACITY);
        ActivityOffer offer = new ActivityOffer(activity, this.begin, this.end);
        new Booking(provider, offer);
    }

    @Atomic(mode = TxMode.READ)
    private void atomicAssert() {

        assertNotNull(FenixFramework.getDomainRoot().getProviderSet());
        assertEquals(1, FenixFramework.getDomainRoot().getProviderSet().size());


    	/* Test ActivityProvider */

        Set<ActivityProvider> providers = FenixFramework.getDomainRoot().getProviderSet();
        ActivityProvider provider = new ArrayList<>(providers).get(0);

        assertEquals(PROVIDER_CODE, provider.getCode());
        assertEquals(PROVIDER_NAME, provider.getName());

        assertNotNull(provider.getActivitySet());
        assertEquals(1, provider.getActivitySet().size());


        /* Test Activity */

        List<Activity> activities = new ArrayList<>(provider.getActivitySet());
        Activity activity = activities.get(0);

        assertNotNull(activity.getCode());
        assertEquals(ACTIVITY_NAME, activity.getName());
        assertEquals(MIN_AGE, activity.getMinAge());
        assertEquals(MAX_AGE, activity.getMaxAge());
        assertEquals(CAPACITY, activity.getCapacity());

        assertNotNull(activity.getOfferSet());
        assertEquals(1, activity.getOfferSet().size());


        /* Test Activity Offer */

        List<ActivityOffer> offers = new ArrayList<>(activity.getOfferSet());
        ActivityOffer offer = offers.get(0);

        assertEquals(this.begin, offer.getBegin());
        assertEquals(this.end, offer.getEnd());
        assertEquals(CAPACITY, offer.getCapacity());

        assertNotNull(offer.getBookingSet());
        assertEquals(1, offer.getBookingSet().size());


        /* Test Booking */

        List<Booking> bookings = new ArrayList<>(offer.getBookingSet());
        Booking booking = bookings.get(0);

        assertEquals(offer, booking.getOffer());

        assertNotNull(booking.getReference());
        assertNull(booking.getCancellation());
        assertNull(booking.getCancellationDate());

    }

    @After
    @Atomic(mode = TxMode.WRITE)
    public void tearDown() {
        Set<ActivityProvider> providers = FenixFramework.getDomainRoot().getProviderSet();

        for (ActivityProvider provider : providers) {
            provider.delete();
        }
    }
}
