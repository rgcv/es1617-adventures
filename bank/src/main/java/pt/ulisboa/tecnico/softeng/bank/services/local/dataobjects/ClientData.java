package pt.ulisboa.tecnico.softeng.bank.services.local.dataobjects;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.softeng.bank.domain.Account;
import pt.ulisboa.tecnico.softeng.bank.domain.Client;

public class ClientData {
	public static enum CopyDepth{
		SHALLOW, ACCOUNTS
	}
	
    private String name;
    private String id;
    
    private List<Account> accounts = new ArrayList<>();

    public ClientData() {
    }

    public ClientData(Client client, CopyDepth depth) {
    	this.name = client.getName();
    	this.id = client.getID();
    	
    	switch (depth){
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Account> getAccounts() {
		return accounts;
	}

	public void setAccounts(List<Account> accounts) {
		this.accounts = accounts;
	}
}