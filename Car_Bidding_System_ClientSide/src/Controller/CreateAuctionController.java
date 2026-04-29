package Controller;

import Service.CarService;
import Event.Event;
import Event.EventBus;
import Service.AuctionService;
import UI.CreateAuctionPanel;
import javafx.scene.paint.Color;

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

        view.onCreateAuction((carId, minBid) -> {

            try {
                String res = AuctionService.createAuction(carId, minBid);

                if (res.startsWith("SUCCESS")) {
                    view.renderSuccess(carId);
                    EventBus.publish(new Event("AUCTION_CREATED", carId));
                } else {
                    view.showError("Failed to create auction");
                }

            } catch (Exception e) {
                e.printStackTrace();
                view.showError("Server error");
            }
        });
    }
}