package Controller;

import Service.AuctionService;
import UI.MyAuctionPanel;
import javafx.application.Platform;

public class MyAuctionController {

    private MyAuctionPanel view;
    private String userId;
    private String role;

    public MyAuctionController(MyAuctionPanel view, String userId, String role) {
        this.view = view;
        this.userId = userId;
        this.role = role;

        handleClose();
        load();
    }

    private void load() {
        try {
            String res = AuctionService.getMyAuctions(userId);

            Platform.runLater(() -> view.render(res));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleClose() {

        view.setOnClose(auctionId -> {
            try {
                AuctionService.closeAuction(auctionId, userId);

                // refresh after close
                load();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        view.setOnWatchLive(auctionId -> {
            Controller.Navigation.goToLiveAuction(auctionId, userId, role, "MY_AUCTIONS");
        });
    }
}