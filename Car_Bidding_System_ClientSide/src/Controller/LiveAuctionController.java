package Controller;

import Event.EventBus;
import Event.Event;
import Service.AuctionService;
import UI.LiveAuctionPanel;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.util.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class LiveAuctionController {

    private LiveAuctionPanel view;
    private String auctionId;
    private String userId;
    private Timeline timeline;
    private Timeline countdownTimeline;

    public LiveAuctionController(LiveAuctionPanel view, String auctionId, String userId) {
        this.view = view;
        this.auctionId = auctionId;
        this.userId = userId;

        load();
        startPolling();
        startCountdown();
        handleBid();
    }

    private void load() {
        try {
            String res = AuctionService.getAuction(auctionId);
            view.renderAuction(res);
            view.updateBids(AuctionService.getBids(auctionId));
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void startPolling() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(2), e -> {
            load();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void startCountdown() {
        countdownTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            try {
                String endTimeStr = AuctionService.getAuctionEndTime(auctionId);
                if (endTimeStr.equals("NONE")) {
                    view.timerLabel.setText("PENDING");
                    return;
                }
                
                // Format: 2026-04-24 20:40:43.0
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
                LocalDateTime endTime = LocalDateTime.parse(endTimeStr, formatter);
                LocalDateTime now = LocalDateTime.now();
                
                long diff = ChronoUnit.SECONDS.between(now, endTime);
                
                if (diff <= 0) {
                    view.timerLabel.setText("CLOSED");
                    countdownTimeline.stop();
                    handleAuctionEnd();
                } else {
                    long mins = diff / 60;
                    long secs = diff % 60;
                    view.timerLabel.setText(String.format("%02d:%02d", mins, secs));
                }
            } catch (Exception ex) { 
                view.timerLabel.setText("00:00");
            }
        }));
        countdownTimeline.setCycleCount(Timeline.INDEFINITE);
        countdownTimeline.play();
    }

//    private void handleBid() {
//        view.bidBtn.setOnAction(e -> {
//            String amount = view.bidAmount.getText();
//            if (amount.isEmpty()) return;
//            try {
//                String res = AuctionService.placeBid(auctionId, userId, Double.parseDouble(amount));
//                if (res.startsWith("SUCCESS")) {
//                    load();
//                    view.bidAmount.clear();
//                }
//            } catch (Exception ex) { ex.printStackTrace(); }
//        });
//    }
    
    private void handleBid() {
        view.bidBtn.setOnAction(e -> {
            try {
                String bidText = view.bidAmount.getText().trim();

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
                    view.bidAmount.clear();
                } else {
                    view.showMessage("❌ Bid failed — must be higher than current bid");
                }

            } catch (Exception ex) {
                view.showMessage("Server error — please try again");
                ex.printStackTrace();
            }
        });
    }


    private void handleAuctionEnd() {
        try {
            String res = AuctionService.getAuction(auctionId);
            String[] d = res.split("\\|");
            if (d.length >= 8) {
                String winnerId = d[7];
                double amount = Double.parseDouble(d[4]);
                Platform.runLater(() -> view.showWinner(winnerId, amount));
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void cleanup() {
        if (timeline != null) timeline.stop();
        if (countdownTimeline != null) countdownTimeline.stop();
    }
}