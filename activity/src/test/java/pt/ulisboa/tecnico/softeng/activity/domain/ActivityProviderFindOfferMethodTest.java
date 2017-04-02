package pt.ulisboa.tecnico.softeng.activity.domain;

import java.util.Set;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pt.ulisboa.tecnico.softeng.activity.domain.exception.ActivityException;

public class ActivityProviderFindOfferMethodTest {
	private ActivityProvider provider;
	private ActivityOffer offer;

	@Before
	public void setUp() {
		this.provider = new ActivityProvider("XtremX", "ExtremeAdventure");
		Activity activity = new Activity(this.provider, "Bush Walking", 18, 80, 25);

		LocalDate begin = new LocalDate(2016, 12, 19);
		LocalDate end = new LocalDate(2016, 12, 21);
		this.offer = new ActivityOffer(activity, begin, end);
	}

	@Test
	public void success() {
		LocalDate begin = new LocalDate(2016, 12, 19);
		LocalDate end = new LocalDate(2016, 12, 21);

		Set<ActivityOffer> offers = this.provider.findOffer(begin, end, 40);

		Assert.assertEquals(1, offers.size());
		Assert.assertTrue(offers.contains(this.offer));
	}
	
	// failure: nullBeginDate
	// failure: nullEndDate
	
	// failure: illegalAge

	@Test(expected = ActivityException.class)
	public void emptyActivitySet() {
		ActivityProvider provider = new ActivityProvider("XtremE", "ExtremeAdventures");
		
		LocalDate begin = new LocalDate(2016, 12, 19);
		LocalDate end = new LocalDate(2016, 12, 21);

		provider.findOffer(begin, end, 40);
	}
	
	@Test(expected = ActivityException.class)
	public void emptyOfferSet() {
		ActivityProvider provider = new ActivityProvider("XtremS", "ExtremeAdventurez");
		new Activity(provider, "Bush Running", 18, 80, 25);
		
		LocalDate begin = new LocalDate(2016, 12, 19);
		LocalDate end = new LocalDate(2016, 12, 21);

		provider.findOffer(begin, end, 40);		
	}
	
	@Test
	public void twoOffersInActivity() {
		ActivityProvider provider = new ActivityProvider("XtremA", "ExtremeAdventure2");
		Activity activity = new Activity(provider, "Rock Climbing", 18, 80, 25);

		LocalDate begin = new LocalDate(2016, 12, 19);
		LocalDate end = new LocalDate(2016, 12, 21);
		ActivityOffer offer1 = new ActivityOffer(activity, begin, end);
		ActivityOffer offer2 = new ActivityOffer(activity, begin, end);

		Set<ActivityOffer> offers = provider.findOffer(begin, end, 40);

		Assert.assertEquals(2, offers.size());
		Assert.assertTrue(offers.contains(offer1));
		Assert.assertTrue(offers.contains(offer2));		
	}
	
	@Test
	public void twoOffersInActivities() {
		ActivityProvider provider = new ActivityProvider("XtremB", "ExtremeAdventure3");
		Activity activity1 = new Activity(provider, "Rock Surfing", 18, 80, 25);
		Activity activity2 = new Activity(provider, "Rock Diving", 18, 80, 25);

		LocalDate begin = new LocalDate(2016, 12, 19);
		LocalDate end = new LocalDate(2016, 12, 21);
		ActivityOffer offer1 = new ActivityOffer(activity1, begin, end);
		ActivityOffer offer2 = new ActivityOffer(activity2, begin, end);

		Set<ActivityOffer> offers = provider.findOffer(begin, end, 40);

		Assert.assertEquals(2, offers.size());
		Assert.assertTrue(offers.contains(offer1));
		Assert.assertTrue(offers.contains(offer2));
	}
	
	@After
	public void tearDown() {
		ActivityProvider.providers.clear();
	}

}
