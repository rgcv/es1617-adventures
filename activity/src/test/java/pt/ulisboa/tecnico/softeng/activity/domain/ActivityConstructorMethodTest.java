package pt.ulisboa.tecnico.softeng.activity.domain;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pt.ulisboa.tecnico.softeng.activity.domain.exception.ActivityException;

public class ActivityConstructorMethodTest {
	private ActivityProvider provider;

	@Before
	public void setUp() {
		this.provider = new ActivityProvider("XtremX", "ExtremeAdventure");
	}

	@Test
	public void success() {
		Activity activity = new Activity(this.provider, "Bush Walking", 18, 80, 25);

		Assert.assertTrue(activity.getCode().startsWith(this.provider.getCode()));
		Assert.assertTrue(activity.getCode().length() > ActivityProvider.CODE_SIZE);
		Assert.assertEquals("Bush Walking", activity.getName());
		Assert.assertEquals(18, activity.getMinAge());
		Assert.assertEquals(80, activity.getMaxAge());
		Assert.assertEquals(25, activity.getCapacity());
		Assert.assertEquals(0, activity.getNumberOfOffers());
		Assert.assertEquals(1, this.provider.getNumberOfActivities());
	}
	
	@Test (expected = ActivityException.class)
	public void maxAge() {
		new Activity(this.provider, "Atividade", 18, 101, 1);
	}
	
	@Test (expected = ActivityException.class)
	public void minAge() {
		new Activity(this.provider, "Atividade", 17, 23, 1);
	}
	
	@Test (expected = ActivityException.class)
	public void ageDifference() {
		new Activity(this.provider, "Atividade", 23, 18, 1);
	}
	
	@Test (expected = ActivityException.class)
	public void minCapacity() {
		new Activity(this.provider, "Atividade", 19, 23, 0);
	}
	
	@After
	public void tearDown() {
		ActivityProvider.providers.clear();
	}

}
