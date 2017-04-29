package pt.ulisboa.tecnico.softeng.bank.presentation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import pt.ulisboa.tecnico.softeng.bank.services.local.BankInterface;
import pt.ulisboa.tecnico.softeng.bank.services.local.dataobjects.BankData;
import pt.ulisboa.tecnico.softeng.bank.services.local.dataobjects.BankData.CopyDepth;
import pt.ulisboa.tecnico.softeng.bank.services.local.dataobjects.BankOperationData;

@Controller
@RequestMapping(value = "/banks/{bankCode}/operations")

public class OperationController {
	private static Logger logger = LoggerFactory.getLogger(OperationController.class);

	@RequestMapping(method = RequestMethod.GET)
	public String operationForm(Model model, @PathVariable String bankCode) {
		logger.info("operationForm code: {}", bankCode);

		BankData bankData = BankInterface.getBankDataByCode(bankCode, CopyDepth.OPERATIONS);

		if (bankData == null) {
			model.addAttribute("error", "Error: it does not exist a bank with the code " + bankCode);
			model.addAttribute("bank", new BankData());
			model.addAttribute("banks", BankInterface.getBanks());
			return "banks";
		} else {
			model.addAttribute("operation", new BankOperationData());
			model.addAttribute("bank", bankData);
			return "operations";
		}
	}
}
