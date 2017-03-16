package pt.ulisboa.tecnico.softeng.activity.domain;

import java.util.HashSet;
import java.util.Set;

import org.joda.time.LocalDate;

import pt.ulisboa.tecnico.softeng.activity.domain.exception.ActivityException;

public class Activity {
	private static int counter = 0;

	private final String name;
	private final String code;
	private final int minAge;
	private final int maxAge;
	private final int capacity;
	private final Set<ActivityOffer> offers = new HashSet<>();

	public Activity(ActivityProvider provider, String name, int minAge, int maxAge, int capacity) {
		
		checkMinAge(minAge);
		checkMaxAge(maxAge);
		checkCapacity(capacity);
		checkAgeDifference(minAge, maxAge);
		
		this.code = provider.getCode() + Integer.toString(++Activity.counter);
		this.name = name;
		this.minAge = minAge;
		this.maxAge = maxAge;
		this.capacity = capacity;

		provider.addActivity(this);
	}
	
	private void checkMinAge(int minAge) {
		if(minAge < 18) {
			throw new ActivityException();
		}
	}
	
	private void checkMaxAge(int maxAge) {
		if(maxAge >= 100) {
			throw new ActivityException();
		}
	}
	
	private void checkCapacity(int capacity) {
		if(capacity <= 0) {
			throw new ActivityException();
		}
	}
	
	private void checkAgeDifference(int minAge, int maxAge) {
		if(minAge > maxAge) {
			throw new ActivityException();
		}
	}
	
	String getName() {
		return this.name;
	}

	String getCode() {
		return this.code;
	}

	int getMinAge() {
		return this.minAge;
	}

	int getMaxAge() {
		return this.maxAge;
	}

	int getCapacity() {
		return this.capacity;
	}

	int getNumberOfOffers() {
		return this.offers.size();
	}

	void addOffer(ActivityOffer offer) {
		this.offers.add(offer);
	}

	Set<ActivityOffer> getOffers(LocalDate begin, LocalDate end, int age) {
		if (this.offers.isEmpty()) {
			throw new ActivityException();
		}
		Set<ActivityOffer> result = new HashSet<>();
		for (ActivityOffer offer : this.offers) {
			if (matchAge(age) && offer.available(begin, end)) {
				result.add(offer);
			}
		}
		return result;
	}

	boolean matchAge(int age) {
		return age >= this.minAge && age <= this.maxAge;
	}

}
