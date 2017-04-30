package pt.ulisboa.tecnico.softeng.activity.services.local.dataobjects;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.softeng.activity.domain.Activity;
import pt.ulisboa.tecnico.softeng.activity.domain.ActivityOffer;

public class ActivityData {

	private String name;
	private int min_age;
	private int max_age;
	private int capacity;
	private List<ActivityOffer> act_offer = new ArrayList<>();

	public ActivityData() {}

	public ActivityData(Activity activity) {
		name = activity.getName();
		min_age = activity.getMinAge();
		max_age = activity.getMaxAge();
		capacity = activity.getCapacity();
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
	
    public List<ActivityOffer> getOffers() {
        return act_offer;
    }

    public void setRooms(List<ActivityOffer> act_offer) {
        this.act_offer = act_offer;
    }
}
