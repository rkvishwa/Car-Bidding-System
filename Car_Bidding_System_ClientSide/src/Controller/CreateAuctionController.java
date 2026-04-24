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
            view.renderCars(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleCreate() {
        view.createBtn.setOnAction(e -> {
            String selected = view.carList.getValue();
            if (selected == null) {
                view.message.setTextFill(Color.RED);
                view.message.setText("Please select a car");
                return;
            }

            try {
                // Extract carId from "Title (Brand Model) | ID:carId"
                String carId = selected.split("ID:")[1].trim();
                
                String res = AuctionService.createAuction(carId, "0");
                if (res.startsWith("SUCCESS")) {
                    view.message.setTextFill(Color.GREEN);
                    view.message.setText("Submitted for admin approval!");
                    view.createBtn.setDisable(true);
                    EventBus.publish(new Event("AUCTION_CREATED", carId));
                } else {
                    view.message.setTextFill(Color.RED);
                    view.message.setText("Failed to create auction.");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                view.message.setTextFill(Color.RED);
                view.message.setText("Server error");
            }
        });
    }
}