package pt.ulisboa.tecnico.softeng.activity.domain;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ActivityMatchAgeMethodTest {

	private static final int CAPACITY = 3;
	private static final int MAX_AGE = 80;
	private static final int MIN_AGE = 18;
	private Activity activity;

	@Before
	public void setUp() {
		ActivityProvider provider = new ActivityProvider("XtremX", "ExtremeAdventure");
		this.activity = new Activity(provider, "Bush Walking", MIN_AGE, MAX_AGE, CAPACITY);
	}

	@Test
	public void successIn() {
		Assert.assertTrue(this.activity.matchAge(50));
	}
	
	@Test
	public void ageOverMax() {
		Assert.assertFalse(this.activity.matchAge(MAX_AGE + 1));
	}
	
	@Test
	public void ageUnderMin() {
		Assert.assertFalse(this.activity.matchAge(MIN_AGE - 1));
	}
	
	@Test
	public void minAge() {
		Assert.assertTrue(this.activity.matchAge(MIN_AGE));
	}
	
	@Test
	public void maxAge() {
		Assert.assertTrue(this.activity.matchAge(MAX_AGE));
	}

	@After
	public void tearDown() {
		ActivityProvider.providers.clear();
	}

}
