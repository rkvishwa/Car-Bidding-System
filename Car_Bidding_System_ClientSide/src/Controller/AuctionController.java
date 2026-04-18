package Controller;

import Event.Event;
import Event.EventBus;
import Service.AuctionService;
import UI.AuctionListPanel;
import javafx.application.Platform;

import java.util.function.Consumer;

public class AuctionController {

    private AuctionListPanel view;
    private String userId;
    private Consumer<Event> eventListener;

    public AuctionController(AuctionListPanel view, String userId) {
        this.view = view;
        this.userId = userId;

        load();

        // Store reference so it can be cleaned up
        eventListener = event -> {
            if (event.type.equals("NEW_BID") || event.type.equals("AUCTION_CLOSED") ||
                event.type.equals("AUCTION_STARTED")) {
                Platform.runLater(() -> refresh());
            }
        };

        EventBus.subscribe(eventListener);
    }

    private void load() {
        refresh();
    }

    private void refresh() {
        try {
            String res = AuctionService.getAuctions();
            Platform.runLater(() -> view.render(res));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cleanup() {
        if (eventListener != null) {
            EventBus.unsubscribe(eventListener);
        }
    }
}