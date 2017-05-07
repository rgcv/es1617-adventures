package pt.ulisboa.tecnico.softeng.activity.presentation;


import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.joda.time.format.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.joda.time.LocalDate;

import pt.ulisboa.tecnico.softeng.activity.services.local.ActivityInterface;
import pt.ulisboa.tecnico.softeng.activity.services.local.dataobjects.ActivityData;
import pt.ulisboa.tecnico.softeng.activity.services.local.dataobjects.ActivityOfferData;
import pt.ulisboa.tecnico.softeng.activity.services.local.dataobjects.ActivityProviderData;



@Controller
@RequestMapping(value = "/activityReservations/{activityProviderCode}/activities/{activityCode}/activityOffers/{begin}/{end}")
public class ActivityReservationDataController {


		private static Logger logger = LoggerFactory.getLogger(ActivityReservationDataController.class);



		@RequestMapping(method = RequestMethod.GET)
		public String showActivityOfferReservations(Model model,@PathVariable("activity") String activityCode,
				@PathVariable("provider") String activityProviderCode,
				@PathVariable("Begin") String begin, @PathVariable("End") String end) {
	
			logger.info("showActivityOfferReservations");
			
			final LocalDate beginDate = DateTimeFormat.forPattern("yyyy-MM-dd").parseLocalDate(begin);
			final LocalDate endDate = DateTimeFormat.forPattern("yyyy-MM-dd").parseLocalDate(end);
			
			final List<ActivityOfferData> activityOffers = 
					ActivityInterface.getActivityOfferDatasByCodeAndDate(activityProviderCode,
							activityCode, beginDate, endDate, ActivityOfferData.CopyDepth.ACTIVITY_RESERVATIONS);
			final ActivityData activityData = ActivityInterface.getActivityDataByCode(activityProviderCode, activityCode, ActivityData.CopyDepth.ACTIVITYOFFER);
			final ActivityProviderData activityProviderData = 
					ActivityInterface.getActivityProviderDataByCode(activityProviderCode, ActivityProviderData.CopyDepth.SHALLOW);
			if(activityProviderData == null) {
				model.addAttribute("error", "Error: An activity provider with code " + activityProviderCode + " does not exist!");
				model.addAttribute("activityProvider", new ActivityProviderData());
				model.addAttribute("activityProviders", ActivityInterface.getActivityProviders());
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