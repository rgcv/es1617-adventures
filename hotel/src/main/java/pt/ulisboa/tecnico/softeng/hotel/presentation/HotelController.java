package pt.ulisboa.tecnico.softeng.hotel.presentation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException;
import pt.ulisboa.tecnico.softeng.hotel.services.local.HotelInterface;
import pt.ulisboa.tecnico.softeng.hotel.services.local.dataobjects.HotelData;

@Controller
@RequestMapping(value = "/hotels")
public class HotelController {
    private static Logger logger = LoggerFactory.getLogger(HotelController.class);

    @RequestMapping(method = RequestMethod.GET)
    public String hotelForm(Model model) {
        logger.debug("hotelForm");

        model.addAttribute("hotel", new HotelData());
        model.addAttribute("hotels", HotelInterface.getHotels());

        return "hotels";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String submitHotel(Model model, @ModelAttribute HotelData hotelData) {
        logger.debug("submitHotel: code '{}'; name '{}'.",
                hotelData.getCode(), hotelData.getName());
        try {
            logger.info("Creating hotel..");
            HotelInterface.createHotel(hotelData);
            logger.info("Hotel successfully created!");
        } catch (HotelException he) {
            String errorMessage = "Couldn't create hotel: " + he.getMessage();

            logger.error(errorMessage);
            model.addAttribute("error", errorMessage);
            model.addAttribute("hotel", hotelData);
            model.addAttribute("hotels", HotelInterface.getHotels());

            return "hotels";
        }

        return "redirect:/hotels";
    }
}
