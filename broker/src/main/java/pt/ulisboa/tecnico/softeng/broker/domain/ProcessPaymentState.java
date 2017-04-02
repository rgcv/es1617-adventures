package pt.ulisboa.tecnico.softeng.broker.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ulisboa.tecnico.softeng.bank.dataobjects.BankOperationData;
import pt.ulisboa.tecnico.softeng.bank.exception.BankException;
import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State;
import pt.ulisboa.tecnico.softeng.broker.exception.RemoteAccessException;
import pt.ulisboa.tecnico.softeng.broker.interfaces.BankInterface;


public class ProcessPaymentState extends AdventureState {
	public static final int MAX_REMOTE_ERRORS = 3;
	
	private static Logger logger = LoggerFactory.getLogger(ProcessPaymentState.class);
	
	@Override
	public State getState() {
		return State.PROCESS_PAYMENT;
	}

	@Override
	public void process(Adventure adventure) {
		logger.debug("process");

		try {
			String pc = BankInterface.processPayment(adventure.getIBAN(), adventure.getAmount());
			adventure.setPaymentConfirmation(pc);
			adventure.setState(State.RESERVE_ACTIVITY);
			
			this.resetNumOfRemoteErrors();
			
			BankOperationData operation = BankInterface.getOperationData(pc);
			System.out.println("Payment: " + operation.getReference());
			System.out.println("Type: " + operation.getType());
			System.out.println("Value: " + operation.getValue());
			
		} catch (BankException be) {
			adventure.setState(State.CANCELLED);
			return;
		} catch (RemoteAccessException rae) {
			this.incNumOfRemoteErrors();
			if(this.getNumOfRemoteErrors() == MAX_REMOTE_ERRORS)
				adventure.setState(State.CANCELLED);
			return;
		}
		
	}
	
	

}
