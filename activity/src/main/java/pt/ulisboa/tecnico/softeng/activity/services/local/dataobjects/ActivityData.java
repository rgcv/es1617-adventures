package pt.ulisboa.tecnico.softeng.activity.services.local.dataobjects;

/*import java.util.ArrayList;
import java.util.List;*/

import pt.ulisboa.tecnico.softeng.activity.domain.Activity;
//import pt.ulisboa.tecnico.softeng.activity.domain.ActivityOffer;

public class ActivityData {
	public static enum CopyDepth {
		SHALLOW, ACTIVITYOFFER
	}

	private String name;
	private int min_age;
	private int max_age;
	private int capacity;
	private String code;
	//private List<ActivityOffer> activityOffer = new ArrayList<>();

	public ActivityData() {}

	public ActivityData(Activity activity, CopyDepth depth) {
				
		name = activity.getName();
		min_age = activity.getMinAge();
		max_age = activity.getMaxAge();
		capacity = activity.getCapacity();
		code = activity.getCode();

		switch(depth) {
			case ACTIVITYOFFER:
				/*for(final ActivityOffer activityOffer : activity.getActivityOfferSet()) {
					activityOffer.add(new ActivityOfferData(activityOffer));
			}
			break;*/
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
	
	public int getMinAge() {
		return min_age;
	}
	
	public void setMinAge(int min_age) {
		this.min_age = min_age;
	}
	
	public int getMaxAge() {
		return max_age;
	}
	
	public void setMaxAge(int max_age) {
		this.max_age = max_age;
	}

	public int getCapacity() {
		return capacity;
	}
	
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
	
	public String getCode(){
		return code;
	}
	
    /*public List<ActivityOffer> getOffers() {
        return activityOffer;
    }

    public void setOffers(List<ActivityOffer> activityOffer) {
        this.activityOffer = activityOffer;
    }*/
}
