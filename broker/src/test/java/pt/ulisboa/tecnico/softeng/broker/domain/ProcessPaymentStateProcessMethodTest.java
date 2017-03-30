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
import pt.ulisboa.tecnico.softeng.bank.exception.BankException;
import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State;
import pt.ulisboa.tecnico.softeng.broker.exception.RemoteAccessException;
import pt.ulisboa.tecnico.softeng.broker.interfaces.BankInterface;


@RunWith(JMockit.class)
public class ProcessPaymentStateProcessMethodTest {

	private static final String PAYMENT_CONFIRMATION = "PaymentConfirmation";
	
	private static final String IBAN = "BK01987654321";
	private static final int AMOUNT = 300;
	
	private final LocalDate begin = new LocalDate(2016, 12, 19);
	private final LocalDate end = new LocalDate(2016, 12, 21);

	private Adventure adventure;
	
	@Injectable
	private Broker broker;
	
	@Before
	public void setUp() {
		this.adventure = new Adventure(this.broker, this.begin, this.end, 20, IBAN, AMOUNT);
		
		this.adventure.setState(State.PROCESS_PAYMENT);
	}
	
	@Test
	public void success(@Mocked final BankInterface bankInterface) {
		new StrictExpectations() {
			{
				BankInterface.processPayment(IBAN, AMOUNT);
				this.result = PAYMENT_CONFIRMATION;
			}
		};
		
		this.adventure.process();
		
		Assert.assertEquals(Adventure.State.RESERVE_ACTIVITY, this.adventure.getState());
		Assert.assertEquals(PAYMENT_CONFIRMATION, this.adventure.getPaymentConfirmation());
	}
	
	@Test 
	public void oneBankException(@Mocked final BankInterface bankInterface) {
		new StrictExpectations() {
			{
				BankInterface.processPayment(IBAN, AMOUNT);
				this.result = new BankException();
			}
		};
		
		this.adventure.process();
		
		Assert.assertEquals(Adventure.State.CANCELLED, this.adventure.getState());
	}
	
	@Test
	public void oneRemoteException(@Mocked final BankInterface bankInterface) {
		new StrictExpectations() {
			{
				BankInterface.processPayment(IBAN, AMOUNT);
				this.result = new RemoteAccessException();
			}
		};
		
		this.adventure.process();
		
		Assert.assertEquals(Adventure.State.PROCESS_PAYMENT, this.adventure.getState());
	}
	
	@Test
	public void threeRemoteException(@Mocked final BankInterface bankInterface) {
		new StrictExpectations() {
			{
				BankInterface.processPayment(IBAN, AMOUNT);
				this.minTimes = 3;
				this.result = new RemoteAccessException();
			}
		};
		for(int i = 0; i < 3; i++)
			this.adventure.process();
		
		Assert.assertEquals(Adventure.State.CANCELLED, this.adventure.getState());
	}
	
	
	
	

}
