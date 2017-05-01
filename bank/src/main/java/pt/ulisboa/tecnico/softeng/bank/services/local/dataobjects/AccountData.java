package pt.ulisboa.tecnico.softeng.bank.services.local.dataobjects;

import pt.ulisboa.tecnico.softeng.bank.domain.Account;

public class AccountData {
	
	private String IBAN;
	private String clientID;
    private int balance;

    public AccountData() {
    }

    public AccountData(Account account) {
    	this.IBAN = account.getIBAN();
    	this.balance = account.getBalance();
    	this.clientID = account.getClient().getID();
    }

	public String getIBAN() {
		return IBAN;
	}

	public void setIBAN(String IBAN) {
		this.IBAN = IBAN;
	}

	public int getBalance() {
		return balance;
	}

	public void setId(int balance) {
		this.balance = balance;
	}
	
	public String getClientID() {
		return clientID;
	}

	public void setClientID(String clientID) {
		this.clientID = clientID;
	}
}