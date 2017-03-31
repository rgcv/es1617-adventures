package pt.ulisboa.tecnico.softeng.activity.domain;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import pt.ulisboa.tecnico.softeng.activity.dataobjects.ActivityReservationData;
import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException;

@RunWith(JMockit.class)
public class ActivityProviderGetActivityReservationDataMethodTest {
	private final String providerCode = "PROVID";

	private final String reference = providerCode + 1;
	private final String badReference = providerCode + 2;

	private final String activityName = "ActivityName";
	private final String activityCode = providerCode + 1;

	private final String cancellation = null; // FIXME: cancellation string
	private final LocalDate cancellationDate = null; // FIXME: cancellation date
	private final LocalDate begin = new LocalDate(2017, 03, 10);;
	private final LocalDate end = new LocalDate(2017, 03, 20);

	@Test
	public void failureBadReference(@Mocked ActivityProvider provider) {
		new Expectations() {
			{
				ActivityProvider.getActivityReservationData(badReference);
				result = new ActivityException();
			}
		};

		ActivityReservationData data = null;
		try {
			data = ActivityProvider.getActivityReservationData(badReference);
		}
		catch(final ActivityException e) {
			Assert.assertNull(data);
		}
	}

	@Test
	public void failureNoProviders(@Mocked ActivityProvider provider) {
		new Expectations() {
			{
				ActivityProvider.getActivityReservationData(badReference);
				result = new ActivityException();
			}
		};

		ActivityProvider.providers.clear();

		ActivityReservationData data = null;
		try {
			data = ActivityProvider.getActivityReservationData(badReference);
		}
		catch(final ActivityException e) {
			Assert.assertNull(data);
		}
	}

	@Test
	public void failureNullReference(@Mocked ActivityProvider provider) {
		new Expectations() {
			{
				ActivityProvider.getActivityReservationData(null);
				result = new ActivityException();
			}
		};

		ActivityReservationData data = null;
		try {
			data = ActivityProvider.getActivityReservationData(null);
		}
		catch(final ActivityException e) {
			Assert.assertNull(data);
		}
	}

	@Test
	public void success(@Mocked ActivityProvider provider) {
		new Expectations() {
			{
				ActivityProvider.getActivityReservationData(reference);
				final ActivityReservationData data = new ActivityReservationData();
				data.setReference(reference);
				data.setBegin(begin);
				data.setEnd(end);
				data.setCode(activityCode);
				data.setName(activityName);
				data.setCancellation(cancellation);
				data.setCancellationDate(cancellationDate);
				result = data;
			}
		};

		final ActivityReservationData data = ActivityProvider.getActivityReservationData(reference);

		Assert.assertNotNull(data);
		Assert.assertEquals(reference, data.getReference());
		Assert.assertEquals(begin, data.getBegin());
		Assert.assertEquals(end, data.getEnd());
		Assert.assertEquals(activityCode, data.getCode());
		Assert.assertEquals(activityName, data.getName());
		Assert.assertEquals(cancellation, data.getCancellation());
		Assert.assertEquals(cancellationDate, data.getCancellationDate());
	}
}
