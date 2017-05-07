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
import pt.ulisboa.tecnico.softeng.activity.services.local.dataobjects.ActivityProviderData.CopyDepth;
import pt.ulisboa.tecnico.softeng.activity.services.local.dataobjects.ActivityProviderData;

@Controller

@RequestMapping(value = "/activityProviders/{activityProviderCode}/activities")
public class ActivityController {
	private static Logger logger = LoggerFactory.getLogger(ActivityController.class);
   
    @RequestMapping(method = RequestMethod.GET)
    public String activityForm(Model model, @PathVariable String activityProviderCode) {
    	logger.info("activityForm code:{}", activityProviderCode);

        ActivityProviderData activityProviderData = ActivityInterface.getActivityProviderDataByCode(activityProviderCode, 
        		CopyDepth.ACTIVITIES);

        if (activityProviderData == null) {
        	model.addAttribute("error", "Error: doesnt exist an activity provider with the code " + activityProviderCode);
            model.addAttribute("activity", new ActivityData());
            model.addAttribute("activityProviders", ActivityInterface.getAllActivityProvidersData());
            return "activityProviders";
        } else {
            model.addAttribute("activity", new ActivityData());
            model.addAttribute("activityProvider", activityProviderData);
            return "activities";
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    public String submitActivity(Model model, @PathVariable String activityProviderCode, 
    		@ModelAttribute ActivityData activityData) {
        logger.info("submitActivity activityProviderCode:{}, name:{}, minAge:{}, maxAge:{}, capacity:{}", 
        		activityProviderCode, activityData.getName(), activityData.getMinAge(), activityData.getMaxAge(), 
        		activityData.getCapacity());

        try {
            ActivityInterface.createActivity(activityProviderCode, activityData);
            
        } catch (ActivityException ae){
            model.addAttribute("error", "Error: it wasnt possible to create the activity");
            model.addAttribute("activity", activityData);
            model.addAttribute("activityProvider", ActivityInterface.getActivityProviderDataByCode(activityProviderCode, 
            		CopyDepth.ACTIVITIES));
            return "activities";
        }
        return "redirect:/activityProviders/" + activityProviderCode + "/activities";
    }
}