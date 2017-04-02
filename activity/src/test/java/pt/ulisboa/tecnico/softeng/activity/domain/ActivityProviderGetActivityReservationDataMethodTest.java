package pt.ulisboa.tecnico.softeng.activity.domain;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Injectable;
import mockit.StrictExpectations;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import pt.ulisboa.tecnico.softeng.activity.dataobjects.ActivityReservationData;
import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException;

@RunWith(JMockit.class)
public class ActivityProviderGetActivityReservationDataMethodTest {
	private final String providerCode = "PROVID";
	private final String providerName = "providerName";

	private String bookingReference;
	private String bookingBadReference;
	private final String bookingNullReference = null;

	private final String activityName = "ActivityName";
	private String activityCode;
	private final int activityCapacity = 100;
	private final int activityMinAge = 20;
	private final int activityMaxAge = 60;
	
	private final LocalDate offerBegin = new LocalDate(2017, 03, 10);
	private final LocalDate offerEnd = new LocalDate(2017, 03, 20);

	private final String cancellation = null; // FIXME: cancellation string
	private final LocalDate cancellationDate = null; // FIXME: cancellation date

	@Tested ActivityProvider activityProvider;
	
	@Injectable Activity activity;
	@Injectable ActivityOffer activityOffer;
	@Injectable Booking booking;
	
	@Before
	public void setUp() {
		activityProvider = new ActivityProvider(providerCode, providerName);
		activity = new Activity(activityProvider, activityName, activityMinAge, activityMaxAge, activityCapacity);
		activityOffer = new ActivityOffer(activity, offerBegin, offerEnd);
		booking = new Booking(activityProvider, activityOffer);
		
		bookingReference = booking.getReference();
		int bookingCounter = Integer.parseInt(bookingReference.substring(bookingReference.length() - 1)) + 1;
		bookingBadReference = bookingReference.substring(0, bookingReference.length() - 1) + bookingCounter;
		
		activityCode = activity.getCode();
	}
	
	@Test
	public void success() {
		new StrictExpectations(activityProvider) {
			{
				ActivityProvider.getActivityReservationData(bookingReference);
				
				final ActivityReservationData data = new ActivityReservationData();
				data.setReference(bookingReference);
				data.setBegin(offerBegin);
				data.setEnd(offerEnd);
				data.setCode(activityCode);
				data.setName(activityName);
				data.setCancellation(cancellation);
				data.setCancellationDate(cancellationDate);
				result = data;
			}
		};

		final ActivityReservationData data = ActivityProvider.getActivityReservationData(bookingReference);

		Assert.assertNotNull(data);
		Assert.assertEquals(bookingReference, data.getReference());
		Assert.assertEquals(offerBegin, data.getBegin());
		Assert.assertEquals(offerEnd, data.getEnd());
		Assert.assertEquals(activityCode, data.getCode());
		Assert.assertEquals(activityName, data.getName());
		Assert.assertEquals(cancellation, data.getCancellation());
		Assert.assertEquals(cancellationDate, data.getCancellationDate());
	}
	
	@Test
	public void failureBadReference() {
		new StrictExpectations(activityProvider) {
			{
				ActivityProvider.getActivityReservationData(bookingBadReference);
				result = new ActivityException();
			}
		};

		ActivityReservationData data = null;
		try {
			data = ActivityProvider.getActivityReservationData(bookingBadReference);
		}
		catch(final ActivityException e) {
			Assert.assertNull(data);
		}
	}

	@Test
	public void failureNullReference() {
		new StrictExpectations(activityProvider) {
			{
				ActivityProvider.getActivityReservationData(bookingNullReference);
				result = new ActivityException();
			}
		};

		ActivityReservationData data = null;
		try {
			data = ActivityProvider.getActivityReservationData(bookingNullReference);
		}
		catch(final ActivityException e) {
			Assert.assertNull(data);
		}
	}
	
	@Test
	public void failureNoProviders() {
		new StrictExpectations(activityProvider) {
			{
				ActivityProvider.getActivityReservationData(bookingReference);
				result = new ActivityException();
			}
		};

		ActivityProvider.providers.clear();

		ActivityReservationData data = null;
		try {
			data = ActivityProvider.getActivityReservationData(bookingReference);
		}
		catch(final ActivityException e) {
			Assert.assertNull(data);
		}
	}
	
	@Test
	public void failureNoActivities() {
		new StrictExpectations(activityProvider) {
			{
				ActivityProvider.getActivityReservationData(bookingReference);
				result = new ActivityException();
			}
		};
		
		activityProvider.getActivities().clear();

		ActivityReservationData data = null;
		try {
			data = ActivityProvider.getActivityReservationData(bookingReference);
		}
		catch(final ActivityException e) {
			Assert.assertNull(data);
		}
	}
	
	@Test
	public void failureNoOffers() {
		new StrictExpectations(activityProvider) {
			{
				ActivityProvider.getActivityReservationData(bookingReference);
				result = new ActivityException();
			}
		};
		
		activity.getAllOffers().clear();

		ActivityReservationData data = null;
		try {
			data = ActivityProvider.getActivityReservationData(bookingReference);
		}
		catch(final ActivityException e) {
			Assert.assertNull(data);
		}
	}
	
	@Test
	public void failureNoBookings() {
		new StrictExpectations(activityProvider) {
			{
				ActivityProvider.getActivityReservationData(bookingReference);
				result = new ActivityException();
			}
		};
		
		activityOffer.getBookings().clear();

		ActivityReservationData data = null;
		try {
			data = ActivityProvider.getActivityReservationData(bookingReference);
		}
		catch(final ActivityException e) {
			Assert.assertNull(data);
		}
	}
	
	@After
	public void tearDown() {
		ActivityProvider.providers.clear();
		Activity.resetCounter();
		Booking.resetCounter();
	}
}
