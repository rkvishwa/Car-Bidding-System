package Controller;

import Service.CarService;
import Event.Event;
import Event.EventBus;
import Service.AuctionService;
import UI.CreateAuctionPanel;

public class CreateAuctionController {

    private CreateAuctionPanel view;
    private String sellerId;

    public CreateAuctionController(CreateAuctionPanel view, String sellerId) {
        this.view = view;
        this.sellerId = sellerId;

        load();
        handleCreate();
    }

    private void load() {
        try {
            String res = CarService.getApprovedCars(sellerId);
            view.render(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleCreate() {

    	view.onCreateAuction((carId, bid) -> {

    	    try {
    	        AuctionService.createAuction(carId, bid);

    	        EventBus.publish(new Event("AUCTION_CREATED", carId));

    	    } catch (Exception e) {
    	        e.printStackTrace();
    	        EventBus.publish(new Event("NOTIFY", "Auction creation failed"));
    	    }
    	});
    }
}