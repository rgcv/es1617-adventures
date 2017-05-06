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
	private List<ActivityData> activities = new ArrayList<>();

	public ActivityProviderData() {}

	public ActivityProviderData(ActivityProvider activityProvider, CopyDepth depth) {
		name = activityProvider.getName();
		code = activityProvider.getCode();

		switch(depth) {
			case ACTIVITIES:
				for(final Activity activity : activityProvider.getActivitySet()) {
					this.activities.add(new ActivityData(activity, ActivityData.CopyDepth.SHALLOW));
				}
			case SHALLOW:
			default:
				break;
		}

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}

	public List<ActivityData> getActivities() {
		return this.activities;
	}

	public void setActivities(List<ActivityData> activities) {
		this.activities = activities;
	}
}
