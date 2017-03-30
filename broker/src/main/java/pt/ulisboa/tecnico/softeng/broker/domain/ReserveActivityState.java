package pt.ulisboa.tecnico.softeng.broker.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ulisboa.tecnico.softeng.activity.dataobjects.ActivityReservationData;
import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException;
import pt.ulisboa.tecnico.softeng.bank.dataobjects.BankOperationData;
import pt.ulisboa.tecnico.softeng.bank.exception.BankException;
import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State;
import pt.ulisboa.tecnico.softeng.broker.exception.RemoteAccessException;
import pt.ulisboa.tecnico.softeng.broker.interfaces.ActivityInterface;
import pt.ulisboa.tecnico.softeng.broker.interfaces.BankInterface;
import pt.ulisboa.tecnico.softeng.broker.interfaces.HotelInterface;
import pt.ulisboa.tecnico.softeng.hotel.dataobjects.RoomBookingData;
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException;

public class ReserveActivityState extends AdventureState {
	public static final int MAX_REMOTE_ERRORS = 5;
	private static Logger logger = LoggerFactory.getLogger(ReserveActivityState.class);

	@Override
	public State getState() {
		return State.RESERVE_ACTIVITY;
	}

	@Override
	public void process(Adventure adventure) {
		logger.debug("process");
		
		try {
			String ac = ActivityInterface.reserveActivity(adventure.getBegin(), adventure.getEnd(), adventure.getAge());
			adventure.setActivityConfirmation(ac);
			if (adventure.getBegin().equals(adventure.getEnd())) {
				adventure.setState(State.CONFIRMED);
			}
			else {
				adventure.setState(State.BOOK_ROOM);
			}
			this.resetNumOfRemoteErrors();
		} catch(ActivityException ae) {
			adventure.setState(State.UNDO);
			return;
		} catch(RemoteAccessException rae) {
			this.incNumOfRemoteErrors();
			if(this.getNumOfRemoteErrors() == MAX_REMOTE_ERRORS) {
				adventure.setState(State.UNDO);
			}
			return;
		}
		
	}

}
