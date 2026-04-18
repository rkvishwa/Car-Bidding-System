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
    	        String res = CarService.addCar(
    	                data.sellerId,
    	                data.title,
    	                data.brand,
    	                data.model,
    	                data.year,
    	                data.price,
    	                data.description,
    	                data.image
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