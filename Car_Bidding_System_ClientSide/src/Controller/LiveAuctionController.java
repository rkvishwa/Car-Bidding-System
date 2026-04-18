package Controller;

import Event.Event;
import Event.EventBus;
import Service.AuctionService;
import UI.LiveAuctionPanel;

import java.util.function.Consumer;

public class LiveAuctionController {

    private Consumer<Event> eventListener;

    public LiveAuctionController(LiveAuctionPanel view, String auctionId, String userId) {

        loadData(view, auctionId);
        handleBid(view, auctionId, userId);
        listen(view, auctionId);
    }

    private void loadData(LiveAuctionPanel view, String auctionId) {
        try {
            String res = AuctionService.getAuction(auctionId);
            view.loadData(res);

            String bids = AuctionService.getBids(auctionId);
            view.loadBids(bids);

        } catch (Exception e) {
            e.printStackTrace();
            view.showMessage("Failed to load auction data");
        }
    }

    private void handleBid(LiveAuctionPanel view, String auctionId, String userId) {
        view.bidBtn.setOnAction(e -> {
            try {
                String bidText = view.bidField.getText().trim();

                if (bidText.isEmpty()) {
                    view.showMessage("Please enter a bid amount");
                    return;
                }

                double amount;
                try {
                    amount = Double.parseDouble(bidText);
                } catch (NumberFormatException ex) {
                    view.showMessage("Invalid bid amount — must be a number");
                    return;
                }

                if (amount <= 0) {
                    view.showMessage("Bid must be a positive number");
                    return;
                }

                String res = AuctionService.placeBid(auctionId, userId, amount);

                if (res != null && res.startsWith("SUCCESS")) {
                    view.showSuccess("✅ Bid placed: " + amount + " MMK");
                    view.bidField.clear();
                } else {
                    view.showMessage("❌ Bid failed — must be higher than current bid");
                }

            } catch (Exception ex) {
                view.showMessage("Server error — please try again");
                ex.printStackTrace();
            }
        });
    }

    private void listen(LiveAuctionPanel view, String auctionId) {

        eventListener = event -> {

            if (event.type.equals("NEW_BID")) {

                String eventData = event.data.toString();
                String[] parts = eventData.split(":");

                if (parts.length < 2) return;

                String eventAuctionId = parts[0];
                String newBid = parts[1];

                if (!eventAuctionId.equals(auctionId)) return;

                javafx.application.Platform.runLater(() -> {
                    view.updateBid(newBid);
                });
            }

            if (event.type.equals("AUCTION_CLOSED")) {

                String closedId = event.data.toString();

                if (closedId.equals(auctionId)) {
                    javafx.application.Platform.runLater(() -> {
                        view.showMessage("🔒 This auction has been closed!");
                        view.bidBtn.setDisable(true);
                        view.bidField.setDisable(true);
                    });
                }
            }
        };

        EventBus.subscribe(eventListener);
    }

    public void cleanup() {
        if (eventListener != null) {
            EventBus.unsubscribe(eventListener);
        }
    }
}