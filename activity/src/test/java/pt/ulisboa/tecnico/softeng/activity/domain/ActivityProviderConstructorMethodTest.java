package pt.ulisboa.tecnico.softeng.activity.domain;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import pt.ulisboa.tecnico.softeng.activity.domain.exception.ActivityException;

public class ActivityProviderConstructorMethodTest {

	@Test
	public void success() {
		ActivityProvider provider = new ActivityProvider("XtremX", "Adventure++");

		Assert.assertEquals("Adventure++", provider.getName());
		Assert.assertTrue(provider.getCode().length() == ActivityProvider.CODE_SIZE);
		Assert.assertEquals(1, ActivityProvider.providers.size());
		Assert.assertEquals(0, provider.getNumberOfActivities());
	}
	
	@Test(expected = ActivityException.class)
	public void uniqueCode() {
		new ActivityProvider("XtremX", "Adventure++");
		new ActivityProvider("XtremX", "Adventurepp");
	}
	
	@Test(expected = ActivityException.class)
	public void uniqueName() {
		new ActivityProvider("XtremX", "Adventure++");
		new ActivityProvider("Xtreme", "Adventure++");}
	
	@Test(expected = ActivityException.class)
	public void shortCode() {
		new ActivityProvider("Xtrm", "Adventures");
	}
	
	@Test(expected = ActivityException.class)
	public void longCode() {
		new ActivityProvider("ExtremeEx", "Adventures");}

	@After
	public void tearDown() {
		ActivityProvider.providers.clear();
	}

}
