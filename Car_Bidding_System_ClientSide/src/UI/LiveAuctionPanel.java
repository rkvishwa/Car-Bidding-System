package UI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class LiveAuctionPanel {

    private String auctionId;
    private String userId;

    public Label currentBidLabel = new Label("Loading...");
    public VBox bidList = new VBox(8);

    public TextField bidField = new TextField();
    public Button bidBtn = new Button("🔨 Place Bid");
    public Button backBtn = new Button("← Back to Auctions");

    private Label msg = new Label();
    private ImageView image = new ImageView();
    private Label titleLabel = new Label();
    private Label detailsLabel = new Label();
    private Label statusLabel = new Label();

    public LiveAuctionPanel(String auctionId, String userId) {
        this.auctionId = auctionId;
        this.userId = userId;
    }

    public VBox getView() {

        VBox root = new VBox(0);
        root.setStyle("-fx-background-color: transparent;");

        // ===== BACK BUTTON =====
        backBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #1976D2;" +
            "-fx-font-size: 13px;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 10 20;"
        );

        // ===== MAIN CONTENT =====
        HBox mainContent = new HBox(25);
        mainContent.setPadding(new Insets(10, 25, 25, 25));
        HBox.setHgrow(mainContent, Priority.ALWAYS);

        // ===== LEFT: Car Info =====
        VBox leftSide = new VBox(15);
        leftSide.setPrefWidth(450);
        leftSide.setMaxWidth(500);

        // Car Image Card
        VBox imageCard = new VBox(0);
        imageCard.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 16;" +
            "-fx-border-radius: 16;"
        );
        DropShadow imgShadow = new DropShadow();
        imgShadow.setColor(Color.rgb(0, 0, 0, 0.04));
        imgShadow.setRadius(15);
        imgShadow.setOffsetY(5);
        imageCard.setEffect(imgShadow);

        image.setFitWidth(450);
        image.setFitHeight(280);
        image.setPreserveRatio(false);
        image.setStyle("-fx-background-radius: 16 16 0 0;");

        StackPane imgStack = new StackPane(image);
        imgStack.setStyle("-fx-background-color: #e0e0e0; -fx-background-radius: 16 16 0 0;");
        imgStack.setMinHeight(280);

        // Status badge on image
        statusLabel.setText("🔴 LIVE");
        statusLabel.setStyle(
            "-fx-background-color: #4CAF50; -fx-text-fill: white;" +
            "-fx-background-radius: 20; -fx-font-weight: bold;" +
            "-fx-padding: 5 14; -fx-font-size: 12px;"
        );
        StackPane imageOverlay = new StackPane(imgStack, statusLabel);
        StackPane.setAlignment(statusLabel, Pos.TOP_LEFT);
        StackPane.setMargin(statusLabel, new Insets(12, 0, 0, 12));

        // Car details below image
        VBox carDetails = new VBox(6);
        carDetails.setPadding(new Insets(16));

        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.web("#111827"));

        detailsLabel.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 13px;");

        carDetails.getChildren().addAll(titleLabel, detailsLabel);
        imageCard.getChildren().addAll(imageOverlay, carDetails);

        leftSide.getChildren().add(imageCard);

        // ===== RIGHT: Bidding Area =====
        VBox rightSide = new VBox(15);
        HBox.setHgrow(rightSide, Priority.ALWAYS);

        // Current Bid Card
        VBox bidCard = new VBox(10);
        bidCard.setPadding(new Insets(20));
        bidCard.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 16;"
        );
        DropShadow bidShadow = new DropShadow();
        bidShadow.setColor(Color.rgb(0, 0, 0, 0.04));
        bidShadow.setRadius(15);
        bidShadow.setOffsetY(5);
        bidCard.setEffect(bidShadow);

        Label bidTitle = new Label("Current Highest Bid");
        bidTitle.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 13px;");

        currentBidLabel.setFont(Font.font("System", FontWeight.BOLD, 28));
        currentBidLabel.setTextFill(Color.web("#2563EB"));

        bidCard.getChildren().addAll(bidTitle, currentBidLabel);

        // Place Bid Card
        VBox placeBidCard = new VBox(12);
        placeBidCard.setPadding(new Insets(20));
        placeBidCard.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 16;"
        );
        placeBidCard.setEffect(bidShadow);

        Label placeBidTitle = new Label("Place Your Bid");
        placeBidTitle.setFont(Font.font("System", FontWeight.BOLD, 16));

        bidField.setPromptText("Enter amount higher than current bid...");
        bidField.setStyle(
            "-fx-background-radius: 8;" +
            "-fx-border-radius: 8;" +
            "-fx-border-color: #E5E7EB;" +
            "-fx-background-color: #F9FAFB;" +
            "-fx-padding: 12;" +
            "-fx-font-size: 14px;"
        );

        bidBtn.setStyle(
            "-fx-background-color: #16A34A;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 15px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 12 30;" +
            "-fx-cursor: hand;"
        );
        bidBtn.setMaxWidth(Double.MAX_VALUE);

        msg.setStyle("-fx-font-size: 13px; -fx-padding: 5 0 0 0;");

        placeBidCard.getChildren().addAll(placeBidTitle, bidField, bidBtn, msg);

        // Live Bids Feed
        VBox bidsFeedCard = new VBox(10);
        bidsFeedCard.setPadding(new Insets(20));
        bidsFeedCard.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 16;"
        );
        bidsFeedCard.setEffect(bidShadow);

        Label feedTitle = new Label("📜 Live Bid Feed");
        feedTitle.setFont(Font.font("System", FontWeight.BOLD, 16));

        ScrollPane scroll = new ScrollPane(bidList);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(200);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        bidsFeedCard.getChildren().addAll(feedTitle, scroll);
        VBox.setVgrow(bidsFeedCard, Priority.ALWAYS);

        rightSide.getChildren().addAll(bidCard, placeBidCard, bidsFeedCard);

        mainContent.getChildren().addAll(leftSide, rightSide);

        root.getChildren().addAll(backBtn, mainContent);
        VBox.setVgrow(mainContent, Priority.ALWAYS);

        return root;
    }

    // Controller methods
    public void updateBid(String amount) {
        currentBidLabel.setText("💰 " + amount + " MMK");

        // Add to live feed
        HBox bidEntry = new HBox(10);
        bidEntry.setPadding(new Insets(8, 12, 8, 12));
        bidEntry.setStyle(
            "-fx-background-color: #E3F2FD;" +
            "-fx-background-radius: 8;"
        );
        bidEntry.setAlignment(Pos.CENTER_LEFT);

        Label bidIcon = new Label("🔥");
        Label bidText = new Label(amount + " MMK");
        bidText.setFont(Font.font("System", FontWeight.BOLD, 13));
        bidText.setTextFill(Color.web("#1565C0"));

        bidEntry.getChildren().addAll(bidIcon, bidText);
        bidList.getChildren().add(0, bidEntry);
    }

    public void showMessage(String text) {
        msg.setText(text);
        msg.setTextFill(Color.web("#D32F2F"));
    }

    public void showSuccess(String text) {
        msg.setText(text);
        msg.setTextFill(Color.web("#4CAF50"));
    }

    public void loadData(String res) {

        if (res == null || res.equals("NOT_FOUND")) return;

        String[] d = res.split("\\|");

        // d[0]=auctionId, d[1]=carId, d[2]=title, d[3]=brand, d[4]=model,
        // d[5]=currentBid, d[6]=image, d[7]=status

        titleLabel.setText(d[2]);
        detailsLabel.setText(d[3] + " • " + d[4]);
        currentBidLabel.setText("💰 " + d[5] + " MMK");

        if (d.length > 7 && "CLOSED".equals(d[7])) {
            statusLabel.setText("CLOSED");
            statusLabel.setStyle(
                "-fx-background-color: #9E9E9E; -fx-text-fill: white;" +
                "-fx-background-radius: 20; -fx-font-weight: bold;" +
                "-fx-padding: 5 14; -fx-font-size: 12px;"
            );
            bidBtn.setDisable(true);
            bidField.setDisable(true);
        }

        try {
            if (d[6] != null && !d[6].isEmpty() && !d[6].equals("null")) {
                image.setImage(new Image(d[6], 450, 280, false, true, true));
            }
        } catch (Exception e) {
            // no image
        }
    }

    public void loadBids(String res) {

        bidList.getChildren().clear();

        if (res == null || res.trim().isEmpty()) {
            Label noBids = new Label("No bids yet. Be the first!");
            noBids.setStyle("-fx-text-fill: #999; -fx-padding: 10;");
            bidList.getChildren().add(noBids);
            return;
        }

        for (String row : res.split("\n")) {
            if (row.trim().isEmpty()) continue;

            String[] d = row.split("\\|");

            HBox bidEntry = new HBox(10);
            bidEntry.setPadding(new Insets(8, 12, 8, 12));
            bidEntry.setStyle(
                "-fx-background-color: #F5F5F5;" +
                "-fx-background-radius: 8;"
            );
            bidEntry.setAlignment(Pos.CENTER_LEFT);

            Label userIcon = new Label("👤");
            Label bidInfo = new Label(d[0] + "  →  " + d[1] + " MMK");
            bidInfo.setStyle("-fx-font-size: 13px;");

            Label time = new Label(d.length > 2 ? d[2] : "");
            time.setStyle("-fx-text-fill: #999; -fx-font-size: 11px;");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            bidEntry.getChildren().addAll(userIcon, bidInfo, spacer, time);
            bidList.getChildren().add(bidEntry);
        }
    }
}