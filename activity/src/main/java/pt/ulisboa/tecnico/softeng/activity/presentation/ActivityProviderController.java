package pt.ulisboa.tecnico.softeng.activity.presentation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException;
import pt.ulisboa.tecnico.softeng.activity.services.local.ActivityInterface;
import pt.ulisboa.tecnico.softeng.activity.services.local.dataobjects.ActivityProviderData;

@Controller
@RequestMapping(value = "/activityProviders") // FIXME avoid camel case in URLs
public class ActivityProviderController {
	private static Logger logger = LoggerFactory.getLogger(ActivityProviderController.class);

	@RequestMapping(method = RequestMethod.POST)
	public String activityProviderSubmit(Model model, @ModelAttribute ActivityProviderData activityProviderData) {
		logger.info("activityProviderSubmit name:{}, code:{}", activityProviderData.getName(), activityProviderData.getCode());

		try {
			ActivityInterface.createActivityProvider(activityProviderData);
		}
		catch(final ActivityException ae) {
			String errorMessage = "An error has occured while creating the activity provider: " + ae.getMessage();
			
			model.addAttribute("error", errorMessage);
			model.addAttribute("activityProvider", activityProviderData);
			model.addAttribute("activityProviders", ActivityInterface.getAllActivityProvidersData());
			return "activityProviders";
		}

		return "redirect:/activityProviders";
	}

	@RequestMapping(method = RequestMethod.GET)
	public String showActivityProviders(Model model) {
		logger.info("showActivityProviders");
		
		model.addAttribute("activityProvider", new ActivityProviderData());
		model.addAttribute("activityProviders", ActivityInterface.getAllActivityProvidersData());
		return "/activityProviders";
	}
}
