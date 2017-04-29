package pt.ulisboa.tecnico.softeng.bank.services.local.dataobjects;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.softeng.bank.domain.Account;
import pt.ulisboa.tecnico.softeng.bank.domain.Bank;
import pt.ulisboa.tecnico.softeng.bank.domain.Client;
import pt.ulisboa.tecnico.softeng.bank.domain.Operation;

public class BankData {
	public static enum CopyDepth {
		SHALLOW, ACCOUNTS, CLIENTS, OPERATIONS
	}

	private String name;
	private String code;
	// private int counter;

	private List<Account> accounts = new ArrayList<>();
	private List<ClientData> clients = new ArrayList<>();
	private List<BankOperationData> operations = new ArrayList<>();

	public BankData() {
	}

	public BankData(Bank bank, CopyDepth depth) {
		this.name = bank.getName();
		this.code = bank.getCode();
		// this.counter = bank.getCounter();

		switch (depth) {
		case OPERATIONS:
			for(Operation operation : bank.getOperationSet()){
				this.operations.add(new BankOperationData(operation));
			}
			break;
		case CLIENTS:
			for(Client client : bank.getClientSet()){
				this.clients.add(new ClientData(client, ClientData.CopyDepth.SHALLOW));
			}
			break;
		case ACCOUNTS:
		case SHALLOW:
		default:
			break;
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	/*
	 * public int getCounter() { return counter; }
	 * 
	 * public void setCounter(int counter) { this.counter = counter; }
	 */

	public List<Account> getAccounts() {
		return accounts;
	}

	public void setAccounts(List<Account> accounts) {
		this.accounts = accounts;
	}

	public List<ClientData> getClients() {
		return clients;
	}

	public void setClients(List<ClientData> clients) {
		this.clients = clients;
	}

	public List<BankOperationData> getOperations() {
		return operations;
	}

	public void setOperations(List<BankOperationData> operations) {
		this.operations = operations;
	}
}
