package pt.ulisboa.tecnico.softeng.activity.domain;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException;

public class ActivityCancelReservationMethodTest {
	
	private static final int MIN_AGE = 18;
	private static final int MAX_AGE = 40;
	private static final int CAPACITY = 5;
	private static final LocalDate begin = new LocalDate(2017,07,24);
	private static final LocalDate end = new LocalDate(2017,07,27);
	private ActivityProvider provider;
	private ActivityOffer offer;
	private String activityConfirmation;

	@Before
	public void setUp() {
		this.provider = new ActivityProvider("XtremX", "ExtremeAdventure");
		Activity activity = new Activity(provider, "Bush Walking", MIN_AGE, MAX_AGE, CAPACITY);
		this.offer = new ActivityOffer(activity, begin, end);
		activityConfirmation = ActivityProvider.reserveActivity(begin, end, MIN_AGE);
	}

	@Test
	public void success() {
		String activityCancellation = ActivityProvider.cancelReservation(activityConfirmation);
		
		Assert.assertEquals("XtremX1Cancelled", activityCancellation);
		Assert.assertEquals(0, this.offer.getNumberOfBookings());
	}
	
	@Test(expected = ActivityException.class)
	public void nullArgument() {
		ActivityProvider.cancelReservation(null);
	}
	
	@Test(expected = ActivityException.class)
	public void emptyStringArg() {
		ActivityProvider.cancelReservation("");
	}
	
	@Test(expected = ActivityException.class)
	public void endsWithSpaceArg() {
		ActivityProvider.cancelReservation(" ");
	}
	
	@Test
	public void cancelSameBookingTwice() {
		ActivityProvider.cancelReservation(activityConfirmation);
		try {
			ActivityProvider.cancelReservation(activityConfirmation);
			Assert.fail();
		} catch (ActivityException ae) {
			Assert.assertEquals(0, this.offer.getNumberOfBookings());
		}
	}
	
	@Test(expected = ActivityException.class)
	public void cancelNonExistentBooking() {
		ActivityProvider.cancelReservation("XtremX4");
	}
	
	@After
	public void tearDown() {
		ActivityProvider.providers.clear();
	}
}