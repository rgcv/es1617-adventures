package pt.ulisboa.tecnico.softeng.bank.presentation;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import pt.ulisboa.tecnico.softeng.bank.domain.Operation;
import pt.ulisboa.tecnico.softeng.bank.exception.BankException;
import pt.ulisboa.tecnico.softeng.bank.services.local.BankInterface;
import pt.ulisboa.tecnico.softeng.bank.services.local.dataobjects.AccountData;
import pt.ulisboa.tecnico.softeng.bank.services.local.dataobjects.BankData;
import pt.ulisboa.tecnico.softeng.bank.services.local.dataobjects.BankData.CopyDepth;
import pt.ulisboa.tecnico.softeng.bank.services.local.dataobjects.BankOperationData;
import pt.ulisboa.tecnico.softeng.bank.services.local.dataobjects.ClientData;

@Controller
@RequestMapping(value = "/banks/{bankCode}/accounts")

public class AccountController {
	private static Logger logger = LoggerFactory.getLogger(AccountController.class);

	@RequestMapping(method = RequestMethod.GET)
	public String AccountForm(Model model, @PathVariable String bankCode) {
		logger.info("accountForm bankCode: {}", bankCode);

		BankData bankData = BankInterface.getBankDataByCode(bankCode, CopyDepth.ACCOUNTS);

		if (bankData == null) {
			model.addAttribute("error", "Error: it does not exist a bank with the code " + bankCode);
			model.addAttribute("bank", new BankData());
			model.addAttribute("banks", BankInterface.getBanks());
			return "banks";
		} else {
			model.addAttribute("account", new AccountData());
			model.addAttribute("bank", bankData);
			return "accounts";
		}
	}
	
	@RequestMapping(method = RequestMethod.GET, value="/client/{clientID}")
	public String AccountFormByClient(Model model, @PathVariable String bankCode, @PathVariable String clientID) {
		logger.info("accountFormByClient bankCode: {} clientID: {}", bankCode, clientID);
		
		BankData bankData = BankInterface.getBankDataByCode(bankCode, CopyDepth.CLIENTS);
		if(bankData == null) {
			model.addAttribute("error", "Error: it does not exist a bank with the code " + bankCode);
			model.addAttribute("bank", new BankData());
			model.addAttribute("banks", BankInterface.getBanks());
			return "banks";
		}
		
		ClientData clientData = BankInterface.getClientDataById(bankCode, clientID, ClientData.CopyDepth.ACCOUNTS);
		if(clientData == null) {
			model.addAttribute("error", "Error: it does not exist a client with the ID " + clientID);
			model.addAttribute("client", new ClientData());
			model.addAttribute("bank", bankData);
			return "clients";
		}
		else {
			model.addAttribute("account", new AccountData());
			model.addAttribute("client", clientData);
			model.addAttribute("bank", bankData);
			return "accountsByClient";
		}
		
	}

	@RequestMapping(method = RequestMethod.POST, value={"","/client/{clientID}"})
	public String AccountSubmit(Model model, @PathVariable String bankCode,@PathVariable Optional<String> clientID, @ModelAttribute AccountData accountData) {
		logger.info("accountSubmit bankCode:{}, clientID:{}", bankCode, accountData.getClientID());

		try {
			BankInterface.createAccount(bankCode, accountData);
		} catch (BankException be) {
			String errorMessage = "Couldn't create account: " + be.getMessage();

			logger.error(errorMessage);
			model.addAttribute("error", errorMessage);
			model.addAttribute("account", accountData);
			model.addAttribute("bank", BankInterface.getBankDataByCode(bankCode, CopyDepth.ACCOUNTS));
			
			return (clientID.isPresent() ? "accountsById" : "accounts");
		}
		
		return "redirect:/banks/" + bankCode + "/accounts" + 
			(clientID.isPresent() ? "/client/" + clientID.get() : "");
	}
	
	@RequestMapping(method = RequestMethod.POST, value={"/{IBAN}/do", "/clients/client/{clientID}/{IBAN}/do"})
	public String DoOperation(Model model, @PathVariable String bankCode,
			@PathVariable Optional<String> clientID, @PathVariable String IBAN, 
			@RequestParam(value="value", required=true) Double value,
			@RequestParam(value="operationType", required=true) String operation, 
			@ModelAttribute AccountData accountData) {
		
		logger.info("doOperation bankCode:{}, accountIBAN:{}, type:{}, value:{}", bankCode, IBAN, operation, value);
		
		String operationType = operation;
		
		BankData bankData = BankInterface.getBankDataByCode(bankCode, CopyDepth.ACCOUNTS);
		
		if(bankData == null) {
			model.addAttribute("error", "It does not exist a bank with the code " + bankCode);
			model.addAttribute("bank", new BankData());
			model.addAttribute("banks", BankInterface.getBanks());
			return "banks";
		}
		
		else if(operationType == null) {
			String errorMessage = "Operation type not specified";
			logger.error(errorMessage);
			model.addAttribute("error", errorMessage);
			model.addAttribute("account", accountData);
			model.addAttribute("bank", bankData);
			return "accounts";
		}
		else {
			try {
				BankInterface.processPayment(bankCode, IBAN, value, 
						(operationType.equals("Withdrawal") ?
								Operation.Type.WITHDRAW : Operation.Type.DEPOSIT));
				
			} catch(BankException be) {
				
				String errorMessage = "Couldn't execute " + operationType + " : " + be.getMessage();
				logger.error(errorMessage);
				model.addAttribute("error", errorMessage);
				
				model.addAttribute("account", accountData);
				model.addAttribute("operation", new BankOperationData());
				model.addAttribute("bank", bankData);
				
				if(clientID.isPresent()) {
					ClientData clientData = BankInterface.getClientDataById(bankCode, clientID.get(), ClientData.CopyDepth.ACCOUNTS);
					if(clientData == null){
						model.addAttribute("error", "It does not exist a client with the ID " + clientID);
						model.addAttribute("client", new ClientData());
						return "clients";
					}
					else {
						model.addAttribute("client", clientData);
						
						return "accountsByClient";
					}
				}
				else {
					return "accounts";
				}
				
			}
			return "redirect:/banks/" + bankCode + "/accounts" + 
				(clientID.isPresent() ? "/client/" + clientID.get() : "");		
		}
	}
	
	@RequestMapping(method = RequestMethod.POST, value="/{fromIBAN}/transfer") 
		public String TransferMoney(Model model, @PathVariable String bankCode, @PathVariable String fromIBAN, 
				@ModelAttribute AccountData accountData,
				@RequestParam(value="toIBAN", required=true) String toIBAN,
				@RequestParam(value="value", required=true) Double value) {
		
		logger.info("transfer bankCode:{}, fromIBAN:{}, toIBAN:{}, value:{}", bankCode, fromIBAN, toIBAN, value);
		
		BankData bankData = BankInterface.getBankDataByCode(bankCode, CopyDepth.ACCOUNTS);
		
		if(bankData == null) {
			model.addAttribute("error", "Error: it does not exist a bank with the code " + bankCode);
			model.addAttribute("bank", new BankData());
			model.addAttribute("banks", BankInterface.getBanks());
			return "banks";
		}
		else {
			try {
				BankInterface.transferMoney(bankCode, fromIBAN, toIBAN, value);
			} catch (BankException be) {
				String errorMessage = "Couln't transfer money from " + fromIBAN + " to " + toIBAN + ": " + be.getMessage();
				logger.error(errorMessage);
				model.addAttribute("error", errorMessage);
				model.addAttribute("account", accountData);
				model.addAttribute("operation", new BankOperationData());
				model.addAttribute("bank", bankData);
				return "accounts";
			}
		}
		
		return "redirect:/banks/" + bankCode + "/accounts";
	}

	
}
