package pt.ulisboa.tecnico.softeng.broker.domain;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Injectable;
import mockit.Mocked;
import mockit.StrictExpectations;
import mockit.integration.junit4.JMockit;
import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException;
import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State;
import pt.ulisboa.tecnico.softeng.broker.exception.RemoteAccessException;
import pt.ulisboa.tecnico.softeng.broker.interfaces.ActivityInterface;

@RunWith(JMockit.class)
public class ReserveActivityStateProcessMethodTest {
    private static final int AGE = 20;
    private static final String IBAN = "BK01987654321";
    private static final int AMOUNT = 300;
	private static final String ACTIVITY_CONFIRMATION = "ActivityConfirmation";
    private final LocalDate begin = new LocalDate(2016, 12, 19);
    private final LocalDate end = new LocalDate(2016, 12, 21);

    private Adventure adventure;

    @Injectable
    private Broker broker;

    @Before
    public void setUp() {
        this.adventure = new Adventure(this.broker, this.begin, this.end, AGE, IBAN, AMOUNT);
        this.adventure.setState(Adventure.State.RESERVE_ACTIVITY);
    }
    
    @Test
    public void reserveActivityDifferentDates(@Mocked final ActivityInterface activityInterface) {
    	new StrictExpectations() {
    		{
    			ActivityInterface.reserveActivity(begin, end, AGE);
				this.result = ACTIVITY_CONFIRMATION;
    		}
    	};
    	
    	this.adventure.process();
    	
    	Assert.assertEquals(ACTIVITY_CONFIRMATION, this.adventure.getActivityConfirmation());
        Assert.assertEquals(State.BOOK_ROOM, this.adventure.getState());
    }
    
    @Test
    public void reserveActivityEqualDates(@Mocked final ActivityInterface activityInterface) {
        LocalDate date2 = new LocalDate(2016, 12, 19);
    	Adventure adventure2 = new Adventure(this.broker, date2, date2, AGE, IBAN, AMOUNT);
    	adventure2.setState(Adventure.State.RESERVE_ACTIVITY);
    	new StrictExpectations() {
    		{
    			ActivityInterface.reserveActivity(date2, date2, AGE);
				this.result = ACTIVITY_CONFIRMATION;
    		}
    	};
    	
    	adventure2.process();
    	
    	Assert.assertEquals(ACTIVITY_CONFIRMATION, adventure2.getActivityConfirmation());
        Assert.assertEquals(State.CONFIRMED, adventure2.getState());
    }
    
    @Test
    public void reserveActivityActivityException(@Mocked final ActivityInterface activityInterface) {
    	new StrictExpectations() {
    		{
    			ActivityInterface.reserveActivity(begin, end, AGE);
    			this.result = new ActivityException();
    		}
    	};
    	
    	this.adventure.process();
    	
    	Assert.assertEquals(State.UNDO, this.adventure.getState());
    }
    
    @Test
    public void reserveActivityRemoteAccessException(@Mocked final ActivityInterface activityInterface) {
    	new StrictExpectations() {
    		{
    			ActivityInterface.reserveActivity(begin, end, AGE);
    			this.times = ReserveActivityState.MAX_REMOTE_ERRORS;
    			this.result = new RemoteAccessException();
    		}
    	};
    	
    	for (int i = 0; i < ReserveActivityState.MAX_REMOTE_ERRORS; i++) {
    		this.adventure.process();
    	}
    	
    	Assert.assertEquals(State.UNDO, this.adventure.getState());
    }
    
    
}
