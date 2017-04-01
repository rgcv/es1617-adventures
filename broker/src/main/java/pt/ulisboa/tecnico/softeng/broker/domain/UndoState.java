package pt.ulisboa.tecnico.softeng.broker.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State;
import pt.ulisboa.tecnico.softeng.broker.exception.RemoteAccessException;
import pt.ulisboa.tecnico.softeng.broker.interfaces.ActivityInterface;
import pt.ulisboa.tecnico.softeng.broker.interfaces.BankInterface;
import pt.ulisboa.tecnico.softeng.broker.interfaces.HotelInterface;
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException;

public class UndoState extends AdventureState {
    public static final int MAX_ERRORS = 5;
    public static final int MAX_REMOTE_ERRORS = 20;

    private static Logger logger = LoggerFactory.getLogger(UndoState.class);


    @Override
    public State getState() {
        return State.UNDO;
    }

    @Override
    public void process(Adventure adventure) {
    	String paymentCancellation = "";
    	String activityCancellation = "";
    	String roomCancellation = "";
    	
		if (adventure.cancelPayment()) {
			try {
				paymentCancellation = BankInterface.cancelPayment(adventure.getPaymentConfirmation());
			} catch (HotelException | RemoteAccessException ex) {
				return;
			}
		}

		if (adventure.cancelActivity()) {
			try {
				activityCancellation = ActivityInterface.cancelReservation(adventure.getActivityConfirmation());
			} catch (HotelException | RemoteAccessException ex) {
				return;
			}
		}

		if (adventure.cancelRoom()) {
			try {
				roomCancellation = HotelInterface.cancelBooking(adventure.getRoomConfirmation());
			} catch (HotelException | RemoteAccessException ex) {
				return;
			}
		}

		if (paymentCancellation == "" && activityCancellation == "" && roomCancellation == "") {
			adventure.setState(State.CANCELLED);
		}       
    }
}