package pt.ulisboa.tecnico.softeng.broker.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State;
import pt.ulisboa.tecnico.softeng.broker.exception.RemoteAccessException;
import pt.ulisboa.tecnico.softeng.broker.interfaces.HotelInterface;
import pt.ulisboa.tecnico.softeng.hotel.domain.Room;
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException;

public class BookRoomState extends AdventureState {
	
	public static final int MAX_REMOTE_ERRORS = 10;
	private static Logger logger = LoggerFactory.getLogger(BookRoomState.class);
	
	@Override
	public State getState() {
		return State.BOOK_ROOM;
	}

	@Override
	public void process(Adventure adventure) {
		logger.debug("process");

		try {
			String rc = HotelInterface.reserveRoom(Room.Type.SINGLE, adventure.getBegin(), adventure.getEnd());
			adventure.setRoomConfirmation(rc);	
			adventure.setState(State.CONFIRMED);
			this.resetNumOfRemoteErrors();
			
		} catch (HotelException he) {
			adventure.setState(State.UNDO);
			return;
			
		} catch (RemoteAccessException rae) {
			this.incNumOfRemoteErrors();
			if(this.getNumOfRemoteErrors() == MAX_REMOTE_ERRORS){
				adventure.setState(State.UNDO);
			}
			return;
		}
	}
}