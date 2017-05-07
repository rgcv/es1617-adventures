package pt.ulisboa.tecnico.softeng.hotel.presentation;

import org.joda.time.LocalDate;
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
import pt.ulisboa.tecnico.softeng.hotel.services.local.dataobjects.RoomBookingData;
import pt.ulisboa.tecnico.softeng.hotel.services.local.dataobjects.RoomData;


@RequestMapping(value="/hotels/{hotelCode}/rooms/{roomNumber}/bookings")
@Controller
public class BookingController {
    private static Logger logger = LoggerFactory.getLogger(BookingController.class);
    
    @RequestMapping(method = RequestMethod.GET)
    public String bookingForm(Model model, @PathVariable String hotelCode, @PathVariable String roomNumber) {
    		//logger.info("Bookings of Hotel with code " + hotelCode + " and Room number " + roomNumber);
        	
    		RoomData room = HotelInterface.getHotelRoomByNumber(hotelCode, roomNumber);
    		
    		if (room == null) {
    			model.addAttribute("error", "Error: Room with number " + roomNumber + " does not exist on Hotel " + HotelInterface.getHotelDataByCode(hotelCode, HotelData.CopyDepth.SHALLOW).getName());
    			model.addAttribute("room", new RoomData());
    			model.addAttribute("hotel", HotelInterface.getHotelDataByCode(hotelCode, HotelData.CopyDepth.ROOMS));
    			return "rooms";
    		} else {
    			model.addAttribute("booking", new RoomBookingData());
    			model.addAttribute("room", room);
    			return "bookings";
    		}

    }
    
    @RequestMapping(method = RequestMethod.POST)
    public String bookingPost(Model model, @PathVariable String hotelCode, @PathVariable String roomNumber, @ModelAttribute RoomBookingData booking) {
    	try {
    		
    		//logger.info("Creating Booking with Arrival " + booking.getArrival() + " and Departure " + booking.getDeparture());
    		
    		booking.setHotelCode(hotelCode);
    		booking.setRoomNumber(roomNumber);

    		HotelInterface.addBooking(booking);
    		
    		//logger.info("Succeeded in creating Booking");
    		
    	} catch (HotelException he) {
    		logger.error("Received hotel exception " + he.getMessage());
    		
    		model.addAttribute("error", "Error: couldn't create the booking");
    		model.addAttribute("booking", booking);
    		model.addAttribute("room", HotelInterface.getHotelRoomByNumber(hotelCode, roomNumber));
    		
    		return "bookings";
    	}
    	
    	return "redirect:/hotels/" + hotelCode + "/rooms/" + roomNumber + "/bookings";
    }
    
    @RequestMapping(value="/{bookingReference}/cancel")
    public String bookingCancel(Model model, @PathVariable String bookingReference, @PathVariable String hotelCode, @PathVariable String roomNumber, @ModelAttribute RoomBookingData booking) {
    	try {
    		HotelInterface.cancelBooking(bookingReference);
    	} catch(HotelException he) {
    		logger.error("Received hotel exception " + he.getMessage());
    		
    		model.addAttribute("error", "Error: couldn't create the booking");
    		model.addAttribute("booking", booking);
    		model.addAttribute("room", HotelInterface.getHotelRoomByNumber(hotelCode, roomNumber));
    		
    		return "bookings";
    	}
    	
    	return "redirect:/hotels/" + hotelCode + "/rooms/" + roomNumber + "/bookings";
    }
    
    
}
