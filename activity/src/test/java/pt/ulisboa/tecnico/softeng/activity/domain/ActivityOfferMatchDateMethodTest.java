package pt.ulisboa.tecnico.softeng.activity.domain;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pt.ulisboa.tecnico.softeng.activity.domain.exception.ActivityException;

public class ActivityOfferMatchDateMethodTest {
	private ActivityOffer offer;

	@Before
	public void setUp() {
		ActivityProvider provider = new ActivityProvider("XtremX", "ExtremeAdventure");
		Activity activity = new Activity(provider, "Bush Walking", 18, 80, 3);

		LocalDate begin = new LocalDate(2016, 12, 19);
		LocalDate end = new LocalDate(2016, 12, 21);

		this.offer = new ActivityOffer(activity, begin, end);
	}

	@Test
	public void success() {
		Assert.assertTrue(this.offer.matchDate(new LocalDate(2016, 12, 19), new LocalDate(2016, 12, 21)));
	}
	
	@Test
	public void bothDatesDiff() {
		Assert.assertFalse(this.offer.matchDate(new LocalDate(2016,12,22), new LocalDate(2016,12,23)));
	}
	
	@Test
	public void beginDateDiff() {
		Assert.assertFalse(this.offer.matchDate(new LocalDate(2016,12,20), new LocalDate(2016,12,21)));
	}
	
	@Test
	public void endDateDiff() {
		Assert.assertFalse(this.offer.matchDate(new LocalDate(2016,12,19), new LocalDate(2016,12,22)));
	}
	
	@Test
	public void datesSwitched() {
		Assert.assertFalse(this.offer.matchDate(new LocalDate(2016, 12, 21), new LocalDate(2016, 12, 19)));
	}
	
	@Test (expected = ActivityException.class)
	public void invalidBeginDate() {
		Assert.assertFalse(this.offer.matchDate(null, new LocalDate(2016, 12, 21)));
	}
	
	@Test (expected = ActivityException.class)
	public void invalidEndDate() {
		Assert.assertFalse(this.offer.matchDate(new LocalDate(2016, 12, 19), null));
	}
	
	@After
	public void tearDown() {
		ActivityProvider.providers.clear();
	}

}
