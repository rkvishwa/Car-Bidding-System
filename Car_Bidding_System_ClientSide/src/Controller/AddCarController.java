package Controller;

import Event.Event;
import Event.EventBus;
import Service.CarService;
import UI.AddCarPanel;
import UI.AddCarData;

public class AddCarController {

    public AddCarController(AddCarPanel view) {

    	view.onAddCar(data -> {

    	    try {
    	        // Sanitize inputs to prevent breaking the parsing logic
    	        String cleanTitle = data.title.replace("\n", " ").replace("|", " ").replace(":", " ");
    	        String cleanBrand = data.brand.replace("\n", " ").replace("|", " ").replace(":", " ");
    	        String cleanModel = data.model.replace("\n", " ").replace("|", " ").replace(":", " ");
    	        String cleanDesc = data.description.replace("\n", " ").replace("|", " ").replace(":", " ");
    	        String cleanImage = data.image.replace("\n", " ").replace("|", " ").replace(":", " ");
    	        
    	        // Parse price mapping 'k' to '000'
    	        String parsedPrice = data.price.toLowerCase().trim();
    	        if (parsedPrice.endsWith("k")) {
    	            parsedPrice = parsedPrice.replace("k", "000");
    	        }

    	        String res = CarService.addCar(
    	                data.sellerId,
    	                cleanTitle,
    	                cleanBrand,
    	                cleanModel,
    	                data.year,
    	                parsedPrice,
    	                cleanDesc,
    	                cleanImage
    	        );

    	        if (res != null && res.startsWith("SUCCESS")) {
    	            view.showMessage("✅ Car added successfully! It will be reviewed by admin.");
    	            view.clear();
    	            EventBus.publish(new Event("CAR_ADDED", res));
    	        } else {
    	            view.showMessage("❌ Failed to add car. Please try again.");
    	        }

    	    } catch (Exception e) {
    	        e.printStackTrace();
    	        view.showMessage("⚠ Server error. Please try again.");
    	        EventBus.publish(new Event("NOTIFY", "Failed to add car"));
    	    }
    	});
    }
}