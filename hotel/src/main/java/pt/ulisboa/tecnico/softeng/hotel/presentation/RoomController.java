package pt.ulisboa.tecnico.softeng.hotel.presentation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException;
import pt.ulisboa.tecnico.softeng.hotel.services.local.HotelInterface;
import pt.ulisboa.tecnico.softeng.hotel.services.local.dataobjects.HotelData;
import pt.ulisboa.tecnico.softeng.hotel.services.local.dataobjects.HotelData.CopyDepth;
import pt.ulisboa.tecnico.softeng.hotel.services.local.dataobjects.RoomData;

@Controller
@RequestMapping(value = "/hotels/{hotelCode}/rooms")
public class RoomController {
	private static Logger logger = LoggerFactory.getLogger(RoomController.class);
	
	@RequestMapping(method = RequestMethod.GET)
	public String roomForm(Model model, @PathVariable String hotelCode) {
		logger.debug("roomForm");
		
		HotelData hotelData = HotelInterface.getHotelDataByCode(hotelCode, CopyDepth.ROOMS);
		
		if (hotelData == null) {
			model.addAttribute("error", "Error: Hotel with code " + hotelCode + " does not exist");
			model.addAttribute("hotel", new HotelData());
			model.addAttribute("hotels", HotelInterface.getHotels());
			return "hotels";
		} else {
			model.addAttribute("room", new RoomData());
			model.addAttribute("hotel", hotelData);
			return "rooms";
		}
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String submitRoom(Model model, @PathVariable String hotelCode, @ModelAttribute RoomData roomData) {
		logger.debug("submitRoom: hotelCode '{}'; number '{}'; type '{}'.", 
						hotelCode, roomData.getNumber(), roomData.getType());
		
		try {
			logger.info("Creating room...");
			HotelInterface.createRoom(roomData, hotelCode);
			logger.info("Room successfully created!");
		} catch (HotelException he){
			String errorMessage = "Couldn't create room: " + he.getMessage();
			
			logger.error(errorMessage);
			model.addAttribute("error", errorMessage);
			model.addAttribute("room", roomData);
			model.addAttribute("hotel", HotelInterface.getHotelDataByCode(hotelCode, CopyDepth.ROOMS));
			
			return "rooms";
		}
		return "redirect:/hotels/" + hotelCode + "/rooms";
	}
}