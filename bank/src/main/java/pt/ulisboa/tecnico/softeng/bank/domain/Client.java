package pt.ulisboa.tecnico.softeng.bank.domain;

import pt.ulisboa.tecnico.softeng.bank.exception.BankException;

public class Client {
	private static int counter = 0;

	private final String name;
	private final String ID;

	public Client(Bank bank, String name) {
		this.ID = Integer.toString(++Client.counter);
		checkName(name);
		checkBank(bank);
		this.name = name;

		bank.addClient(this);
	}

	public String getName() {
		return this.name;
	}

	public String getID() {
		return this.ID;
	}

	private void checkBank(Bank bank) {
		if (bank == null) {
			throw new BankException();
		}
	}

	private void checkName(String name) {
		if (name == null || name.trim().length() == 0) {
			throw new BankException();
		}
	}
}