package pt.ulisboa.tecnico.softeng.activity.presentation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException;
import pt.ulisboa.tecnico.softeng.activity.services.local.ActivityInterface;
import pt.ulisboa.tecnico.softeng.activity.services.local.dataobjects.ActivityData;
import pt.ulisboa.tecnico.softeng.activity.services.local.dataobjects.ActivityOfferData;
import pt.ulisboa.tecnico.softeng.activity.services.local.dataobjects.ActivityProviderData;

@Controller
@RequestMapping(value = "/activityProviders/{activityProviderCode}/activities/{activityCode}/activityOffers") // FIXME avoid camel case in urls
public class ActivityOfferController {
	private static Logger logger = LoggerFactory.getLogger(ActivityOfferController.class);

	@RequestMapping(method = RequestMethod.POST)
	public String activityOfferSubmit(Model model, @PathVariable("activityProviderCode") String activityProviderCode, @PathVariable("activityCode") String activityCode, @ModelAttribute ActivityOfferData activityOfferData) {
		logger.info("submitActivityOffer activityProviderCode:{}, activityCode:{}, begin:{}, end:{} ", activityProviderCode, activityCode, activityOfferData.getBegin(), activityOfferData.getEnd());

		try {
			ActivityInterface.createActivityOffer(activityProviderCode, activityCode, activityOfferData);
		}
		catch(final ActivityException ae) {
			final String errorMessage = "An error has occured while creating the activity offer: " + ae.getMessage();

			model.addAttribute("error", errorMessage);
			model.addAttribute("activityOffer", activityOfferData);
			model.addAttribute("activity", ActivityInterface.getActivityDataByCode(activityProviderCode, activityCode, ActivityData.CopyDepth.ACTIVITYOFFER));
			model.addAttribute("activityProvider", ActivityInterface.getActivityProviderDataByCode(activityProviderCode, ActivityProviderData.CopyDepth.SHALLOW));
			return "activityOffers";
		}

		return "redirect:/activityProviders/" + activityProviderCode + "/activities/" + activityCode + "/activityOffers";
	}

	@RequestMapping(method = RequestMethod.GET)
	public String showActivityOffers(Model model, @PathVariable("activityProviderCode") String activityProviderCode, @PathVariable("activityCode") String activityCode) {
		logger.info("showActivityOffers activityProviderCode:{} activityCode:{}", activityProviderCode, activityCode);

		final ActivityProviderData activityProviderData = ActivityInterface.getActivityProviderDataByCode(activityProviderCode, ActivityProviderData.CopyDepth.ACTIVITIES);
		final ActivityData activityData = ActivityInterface.getActivityDataByCode(activityProviderCode, activityCode, ActivityData.CopyDepth.ACTIVITYOFFER);

		if(activityProviderData == null) {
			model.addAttribute("error", "Error: An activity provider with code " + activityProviderCode + " does not exist!");
			model.addAttribute("activityProvider", new ActivityProviderData());
			model.addAttribute("activityProviders", ActivityInterface.getAllActivityProvidersData());
			return "activityProviders";
		}
		else if(activityData == null) {
			model.addAttribute("error", "Error: An activity with code " + activityCode + " does not exist!");
			model.addAttribute("activity", new ActivityData());
			model.addAttribute("activityProvider", activityProviderData);
			return "activities";
		}
		else {
			model.addAttribute("activityOffer", new ActivityOfferData());
			model.addAttribute("activity", activityData);
			model.addAttribute("activityProvider", activityProviderData);
			return "activityOffers";
		}
	}
}
