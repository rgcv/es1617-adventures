package pt.ulisboa.tecnico.softeng.bank.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Set;

import org.junit.After;
import org.junit.Test;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;
import pt.ulisboa.tecnico.softeng.bank.domain.Operation.Type;

public class BankPersistenceTest {
    private static final String BANK_CODE = "BK01";
    private static final String BANK_NAME = "Money";
    private static final String CLIENT_NAME = "IST Alameda";
    private static final int AMOUNT = 1065;

    @Test
    public void success() {
        atomicProcess();
        atomicAssert();
    }

    @Atomic(mode = TxMode.WRITE)
    public void atomicProcess() {
        Bank bank = new Bank(BANK_NAME, BANK_CODE);
        Client client = new Client(bank, CLIENT_NAME);
        Account account = new Account(bank, client);
        new Operation(Type.DEPOSIT, account, AMOUNT);
    }

    @Atomic(mode = TxMode.READ)
    public void atomicAssert() {
    	
    	assertNotNull(FenixFramework.getDomainRoot().getBankSet());
    	assertEquals(1, FenixFramework.getDomainRoot().getBankSet().size());
    	
    	/* Test Bank */
    	
    	Set<Bank> banks = FenixFramework.getDomainRoot().getBankSet();
    	Bank bank = new ArrayList<>(banks).get(0);
    	
    	assertEquals(BANK_NAME, bank.getName());
    	assertEquals(BANK_CODE, bank.getCode());
    	
    	assertNotNull(bank.getClientSet());
    	assertEquals(1, bank.getClientSet().size());
    	
    	/* Test Client */
    	
    	Set<Client> clients = bank.getClientSet();
    	Client client = new ArrayList<>(clients).get(0);
    	
    	assertEquals(CLIENT_NAME, client.getName());
    	assertEquals(bank, client.getBank());
    	
    	assertNotNull(client.getAccountSet());
    	assertEquals(1, client.getAccountSet().size());
    	    	
     	/* Test Account */
    	
    	Set<Account> accounts = client.getAccountSet();
    	Account account = new ArrayList<>(accounts).get(0);
    	
    	assertEquals(bank, account.getBank());
    	assertEquals(client, account.getClient());
    	
    	assertNotNull(account.getOperationSet());
    	assertEquals(1, account.getOperationSet().size());
    	
    	/* Test Operation */
    	
    	Set<Operation> operations = account.getOperationSet();
    	Operation operation = new ArrayList<>(operations).get(0);
    	
    	assertEquals(Type.DEPOSIT, operation.getType());
    	assertEquals(account, operation.getAccount());
    	assertEquals(AMOUNT, operation.getValue());
    	
    }

    @After
    @Atomic(mode = TxMode.WRITE)
    public void tearDown() {
        for (Bank bank : FenixFramework.getDomainRoot().getBankSet()) {
            bank.delete();
        }
    }

}
