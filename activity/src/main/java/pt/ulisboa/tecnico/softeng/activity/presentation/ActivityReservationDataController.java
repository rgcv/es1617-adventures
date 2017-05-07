package pt.ulisboa.tecnico.softeng.activity.presentation;


import java.util.List;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import pt.ulisboa.tecnico.softeng.activity.services.local.ActivityInterface;
import pt.ulisboa.tecnico.softeng.activity.services.local.dataobjects.ActivityData;
import pt.ulisboa.tecnico.softeng.activity.services.local.dataobjects.ActivityOfferData;
import pt.ulisboa.tecnico.softeng.activity.services.local.dataobjects.ActivityProviderData;



@Controller
@RequestMapping(value = "/activityProviders/{activityProviderCode}/activities/{activityCode}/activityOffers/{begin}/{end}")
public class ActivityReservationDataController {
		private static Logger logger = LoggerFactory.getLogger(ActivityReservationDataController.class);

		@RequestMapping(method = RequestMethod.GET)
		public String showActivityOfferReservations(Model model, @PathVariable("activityProviderCode") String activityProviderCode, @PathVariable("activityCode") String activityCode, @PathVariable("begin") String begin, @PathVariable("end") String end) {
			logger.info("showActivityOfferReservations");
			
			final LocalDate beginDate = DateTimeFormat.forPattern("yyyy-MM-dd").parseLocalDate(begin);
			final LocalDate endDate = DateTimeFormat.forPattern("yyyy-MM-dd").parseLocalDate(end);

			final ActivityProviderData activityProviderData = ActivityInterface.getActivityProviderDataByCode(activityProviderCode, ActivityProviderData.CopyDepth.SHALLOW);
			final ActivityData activityData = ActivityInterface.getActivityDataByCode(activityProviderCode, activityCode, ActivityData.CopyDepth.ACTIVITYOFFER);
			final List<ActivityOfferData> activityOffers = ActivityInterface.getActivityOffersDataByCodeAndDates(activityProviderCode, activityCode, beginDate, endDate, ActivityOfferData.CopyDepth.ACTIVITY_RESERVATIONS);

			if(activityProviderData == null) {
				model.addAttribute("error", "Error: An activity provider with code " + activityProviderCode + " does not exist!");
				model.addAttribute("activityProvider", new ActivityProviderData());
				model.addAttribute("activityProviders", ActivityInterface.getAllActivityProvidersData());
				return "redirect:/activityProviders";
			}
			else if(activityData == null) {
				model.addAttribute("error", "Error: An activity with code " + activityCode + " does not exist!");
				model.addAttribute("activity", new ActivityData());
				model.addAttribute("activityProvider", activityProviderData);
				return "redirect:/activityProviders/" + activityProviderCode + "/activities";
			}
			else if(activityOffers == null) {
				model.addAttribute("error", "Error: Offer(s) between " + begin + "and" + end + " does not exist!");
				model.addAttribute("activityOffer", new ActivityOfferData());
				model.addAttribute("activity", activityData);
				model.addAttribute("activityProvider", activityProviderData);
				return "redirect:/activityProviders/" + activityProviderCode + "/activities" + activityCode + "/activityOffers";			
			}
			else {
				model.addAttribute("activityProvider", activityProviderData);
				model.addAttribute("activity", activityData);
				model.addAttribute("activityOffers", activityOffers);
				return "activityReservations";
			}
		}
}