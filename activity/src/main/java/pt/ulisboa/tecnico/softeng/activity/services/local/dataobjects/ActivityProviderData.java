package pt.ulisboa.tecnico.softeng.activity.services.local.dataobjects;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.softeng.activity.domain.Activity;
import pt.ulisboa.tecnico.softeng.activity.domain.ActivityProvider;

public class ActivityProviderData {
	public static enum CopyDepth {
		SHALLOW, ACTIVITIES
	}

	private String name;
	private String code;
	//TODO: Wait on class ActivityData 
	//private List<ActivityData> activities = new ArrayList<>();

	public ActivityProviderData() {}

	public ActivityProviderData(ActivityProvider activityProvider, CopyDepth depth) {
		name = activityProvider.getName();
		code = activityProvider.getCode();

		switch(depth) {
			case ACTIVITIES:
				//TODO: Wait on class ActivityData 
				/*for(final Activity activity : activityProvider.getActivitySet()) {
					activities.add(new ActivityData(activity));
				}
				break;*/
			case SHALLOW:
				break;
			default:
				break;
		}

	}

	//TODO: Wait on class ActivityData 
	/*public List<ActivityData> getAdventures() {
		return activities;
	}*/

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	//TODO: Wait on class ActivityData 
	/*public void setActivities(List<ActivityData> activities) {
		this.activities = activities;
	}*/

	public void setCode(String code) {
		this.code = code;
	}

	public void setName(String name) {
		this.name = name;
	}
}
