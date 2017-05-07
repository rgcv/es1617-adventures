package pt.ulisboa.tecnico.softeng.bank.domain;

import pt.ulisboa.tecnico.softeng.bank.exception.BankException;

public class Account extends Account_Base {

    public Account(Bank bank, Client client) {
        checkArguments(bank, client);

        setIBAN(bank.getCode() + Integer.toString(bank.getCounter()));
        setBalance(0);

        setClient(client);
        setBank(bank);
    }

    public void delete() {
        setBank(null);
        setClient(null);

        for (Operation operation : getOperationSet()) {
            operation.delete();
        }

        deleteDomainObject();
    }

    private void checkArguments(Bank bank, Client client) {
        if (bank == null || client == null) {
            throw new BankException("Bank does not exist");
        }

        if (!bank.getClientSet().contains(client)) {
            throw new BankException("Client does not exist");
        }

    }

    public String deposit(int amount) {
        if (amount <= 0) {
            throw new BankException("Amount must be positive");
        }

        setBalance(getBalance() + amount);

        return new Operation(Operation.Type.DEPOSIT, this, amount).getReference();
    }

    public String withdraw(int amount) {
        if (amount <= 0){
        	throw new BankException("Amount must be positive");
        }
        else if(amount > getBalance()){
            throw new BankException("Not enough money in the account");
        }

        setBalance(getBalance() - amount);

        return new Operation(Operation.Type.WITHDRAW, this, amount).getReference();
    }

}
