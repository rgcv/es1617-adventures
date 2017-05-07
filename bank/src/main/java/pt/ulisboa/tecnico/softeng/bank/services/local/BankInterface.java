package pt.ulisboa.tecnico.softeng.bank.services.local;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;
import pt.ulisboa.tecnico.softeng.bank.domain.Account;
import pt.ulisboa.tecnico.softeng.bank.domain.Bank;
import pt.ulisboa.tecnico.softeng.bank.domain.Client;
import pt.ulisboa.tecnico.softeng.bank.domain.Operation;
import pt.ulisboa.tecnico.softeng.bank.exception.BankException;
import pt.ulisboa.tecnico.softeng.bank.services.local.dataobjects.AccountData;
import pt.ulisboa.tecnico.softeng.bank.services.local.dataobjects.BankData;
import pt.ulisboa.tecnico.softeng.bank.services.local.dataobjects.BankData.CopyDepth;
import pt.ulisboa.tecnico.softeng.bank.services.local.dataobjects.BankOperationData;
import pt.ulisboa.tecnico.softeng.bank.services.local.dataobjects.ClientData;

public class BankInterface {

	@Atomic(mode = TxMode.WRITE)
	public static void createBank(BankData bankData) {
		new Bank(bankData.getName(), bankData.getCode());
	}
	
	@Atomic(mode = TxMode.READ)
	public static BankData getBankDataByCode(String bankCode, CopyDepth depth){
		Bank bank = getBankByCode(bankCode);
		if(bank != null){
			return new BankData(bank, depth);
		}
		else{
			return null;
		}
	}
	
	@Atomic(mode = TxMode.READ)
	public static ClientData getClientDataById(String bankCode, String clientID, ClientData.CopyDepth depth){
		Bank bank = getBankByCode(bankCode);
		if(bank == null) return null;
		Client client = getClientbyId(bank, clientID);
		if(client != null){
			return new ClientData(client, depth);
		}
		return null;
	}

	@Atomic(mode = TxMode.READ)
	public static List<BankData> getBanks() {
		List<BankData> banks = new ArrayList<>();
		for (Bank bank : FenixFramework.getDomainRoot().getBankSet()) {
			banks.add(new BankData(bank, CopyDepth.SHALLOW));
		}
		return banks;
	}

	@Atomic(mode = TxMode.WRITE)
	public static void createClient(String bankCode, ClientData clientData) {
		Bank bank = getBankByCode(bankCode);
		if(bank != null) {
			new Client(bank, clientData.getName());
		}
		else {
			throw new BankException("Bank does not exist");
		} 
	}
	
	@Atomic(mode = TxMode.WRITE)
	public static void createAccount(String bankCode, AccountData accountData) {
		Bank bank = getBankByCode(bankCode);
		
		if(bank != null){
			Client client = getClientbyId(bank,accountData.getClientID());
			
			if(client != null) 
				new Account(bank, client);
			else 
				throw new BankException("Client does not exist");
		}
		else {
			throw new BankException("Bank does not exist");
		}
		
	}


	@Atomic(mode = TxMode.WRITE)
	public static String processPayment(String IBAN, int amount) {
		for (Bank bank : FenixFramework.getDomainRoot().getBankSet()) {
			if (bank.getAccount(IBAN) != null) {
				return bank.getAccount(IBAN).withdraw(amount);
			}
		}
		throw new BankException();
	}
	
	@Atomic(mode = TxMode.WRITE)
	public static String processPayment(String bankCode, String IBAN, Double amount, Operation.Type type) {
		
		Bank bank = getBankByCode(bankCode);
		if (bank == null) {
			throw new BankException("Bank does not exist");
		}
		else if (amount == null) {
			throw new BankException("Empty amount");
		}
		else if (bank.getAccount(IBAN) != null) {
			if(type.equals(Operation.Type.DEPOSIT)) {
				return bank.getAccount(IBAN).deposit(amount.intValue());
			}
			else if(type.equals(Operation.Type.WITHDRAW)) {
				return bank.getAccount(IBAN).withdraw(amount.intValue());
			}
			else {
				throw new BankException("Operation not permited");
			}
		}
		throw new BankException("Account doesn't exist");
	}
	@Atomic(mode = TxMode.WRITE)
	public static String transferMoney(String bankCode, String senderIBAN, String receiverIBAN, Double amount) {
		Bank bank = getBankByCode(bankCode);
		Account senderAccount = null;
		Account receiverAccount = null;
		if (bank == null) {
			throw new BankException("Bank does not exist");
		}
		else if (amount == null) {
			throw new BankException("Empty amount");
		}
		else if (senderIBAN == null || senderIBAN.equals("")) {
			throw new BankException("Sender IBAN is null");
		}
		else if (receiverIBAN == null || receiverIBAN.equals("")) {
			throw new BankException("Receiver IBAN is null");
		}
		else if(senderIBAN.equals(receiverIBAN)) {
			throw new BankException("IBANs can't be the same");
		}
		else if ((senderAccount = bank.getAccount(senderIBAN)) != null 
				&& (receiverAccount = bank.getAccount(receiverIBAN)) != null) {
			senderAccount.withdraw(amount.intValue());
			return receiverAccount.deposit(amount.intValue());
		}
		else {
			throw new BankException("Account doesn't exist (" + (senderAccount == null ? senderIBAN : receiverIBAN) +")");
		}
	}

	@Atomic(mode = TxMode.WRITE)
	public static String cancelPayment(String paymentConfirmation) {
		Operation operation = getOperationByReference(paymentConfirmation);
		if (operation != null) {
			return operation.revert();
		}
		throw new BankException();
	}

	@Atomic(mode = TxMode.READ)
	public static BankOperationData getOperationData(String reference) {
		Operation operation = getOperationByReference(reference);
		if (operation != null) {
			return new BankOperationData(operation);
		}
		throw new BankException();
	}

	private static Operation getOperationByReference(String reference) {
		for (Bank bank : FenixFramework.getDomainRoot().getBankSet()) {
			Operation operation = bank.getOperation(reference);
			if (operation != null) {
				return operation;
			}
		}
		return null;
	}
	
	private static Bank getBankByCode(String code) {
		for (Bank bank : FenixFramework.getDomainRoot().getBankSet()) {
			if (bank.getCode().equals(code)) {
				return bank;
			}
		}
		return null;
	}
	
	private static Client getClientbyId(Bank bank, String ID) {
		if(bank != null){
			for (Client client : bank.getClientSet()) {
				if(client.getID().equals(ID)) {
					return client;
				}
			}
		}
		return null;
	}

	
}
