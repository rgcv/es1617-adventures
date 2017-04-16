package pt.ulisboa.tecnico.softeng.activity.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Test;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;

public class ActivityPersistenceTest {

    private static final String PROVIDER_CODE = "APR001";
    private static final String PROVIDER_NAME = "Best Provider";
    
    private static final int  ACTIVITY_MIN_AGE = 20;
    private static final int  ACTIVITY_MAX_AGE = 42;
    private static final int ACTIVITY_CAPACITY = 23;
    private static final String  ACTIVITY_NAME = "Dank activity name";
    
    private final LocalDate begin = new LocalDate(2016, 12, 19);
    private final LocalDate end = new LocalDate(2016, 12, 21);
    

    @Test
    public void success() {
        atomicProcess();
        atomicAssert();
    }

    @Atomic(mode = TxMode.WRITE)
    private void atomicProcess() {
        ActivityProvider ap = 
        		new ActivityProvider(PROVIDER_CODE, PROVIDER_NAME);
        
        Activity act = new Activity(ap, ACTIVITY_NAME, ACTIVITY_MIN_AGE, 
        								ACTIVITY_MAX_AGE, ACTIVITY_CAPACITY);
        
        new ActivityOffer(act, begin, end);
    }	

    @Atomic(mode = TxMode.READ)
    private void atomicAssert() {
    	
    	/* Test ActivityProvider */
    	
        Set<ActivityProvider> providers =
                FenixFramework.getDomainRoot().getProviderSet();
        assertEquals(1, providers.size());

        ActivityProvider provider = new ArrayList<>(providers).get(0);

        assertEquals(PROVIDER_CODE, provider.getCode());
        assertEquals(PROVIDER_NAME, provider.getName());
        
        assertNotNull(provider.getActivitySet());
        
        /* Test Activity */
        
        List<Activity> activities = 
        		new ArrayList<>(provider.getActivitySet());
        
        assertEquals(1, activities.size());
        
        Activity activity = activities.get(0);
        
        assertNotNull(activity.getCode());
        assertEquals(ACTIVITY_NAME, activity.getName());
        assertEquals(ACTIVITY_MIN_AGE, activity.getMinAge());
        assertEquals(ACTIVITY_MAX_AGE, activity.getMaxAge());
        assertEquals(ACTIVITY_CAPACITY, activity.getCapacity());
        
        assertNotNull(activity.getOfferSet());
        
        /* Test Activity Offer */
        
        List<ActivityOffer> offers = 
        		new ArrayList<>(activity.getOfferSet());
       
        assertEquals(1, offers.size());
        
        ActivityOffer offer = offers.get(0);
        
        assertEquals(begin, offer.getBegin());
        assertEquals(end, offer.getEnd());
        assertEquals(ACTIVITY_CAPACITY, offer.getCapacity());
        
        
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
