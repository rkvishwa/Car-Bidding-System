package UI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import Service.BidHistoryService;

public class BidHistoryPanel {

    private String userId;
    private VBox list = new VBox(12);

    public BidHistoryPanel(String userId) {
        this.userId = userId;
    }

    public Node getView() {

        VBox root = new VBox(20);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: transparent;");

        // ===== HEADER =====
        Label title = new Label("📜 Bid History");
        title.setFont(Font.font("System", FontWeight.BOLD, 26));
        title.setTextFill(Color.web("#1a1a2e"));

        Label subtitle = new Label("Your past bids and auction results");
        subtitle.setStyle("-fx-text-fill: #888; -fx-font-size: 13px;");

        VBox header = new VBox(4, title, subtitle);

        // ===== STATS BAR =====
        HBox statsBar = createStatsBar();

        // ===== LOAD DATA =====
        loadData();

        ScrollPane scroll = new ScrollPane(list);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        root.getChildren().addAll(header, statsBar, scroll);

        return root;
    }

    private HBox createStatsBar() {
        HBox bar = new HBox(16);
        bar.setPadding(new Insets(0));
        bar.setAlignment(Pos.CENTER_LEFT);

        // Stats will be populated after data loads
        return bar;
    }

    private void loadData() {

        list.getChildren().clear();

        try {
            String res = BidHistoryService.getBidHistory(userId);

            if (res == null || res.trim().isEmpty()) {
                VBox emptyState = new VBox(10);
                emptyState.setAlignment(Pos.CENTER);
                emptyState.setPadding(new Insets(60));

                Label emptyIcon = new Label("📋");
                emptyIcon.setFont(Font.font(48));

                Label empty = new Label("No bids placed yet");
                empty.setStyle("-fx-text-fill: #999; -fx-font-size: 16px;");

                Label hint = new Label("Browse auctions and place your first bid!");
                hint.setStyle("-fx-text-fill: #bbb; -fx-font-size: 13px;");

                emptyState.getChildren().addAll(emptyIcon, empty, hint);
                list.getChildren().add(emptyState);
                return;
            }

            for (String row : res.split("\n")) {
                if (row.trim().isEmpty()) continue;

                String[] d = row.split("\\|");
                if (d.length < 9) continue;

                // d[0]=bidId, d[1]=auctionId, d[2]=title, d[3]=brand, d[4]=model
                // d[5]=image, d[6]=bidAmount, d[7]=bidTime, d[8]=result, d[9]=currentBid

                list.getChildren().add(
                    createCard(d[2], d[3], d[4], d[5], d[6], d[7], d[8],
                              d.length > 9 ? d[9] : d[6])
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
            Label error = new Label("⚠ Failed to load bid history");
            error.setStyle("-fx-text-fill: #D32F2F; -fx-font-size: 14px;");
            list.getChildren().add(error);
        }
    }

    private HBox createCard(String carTitle, String brand, String model,
                            String imagePath, String bidAmount, String bidTime,
                            String result, String currentBid) {

        HBox card = new HBox(16);
        card.setPadding(new Insets(16, 20, 16, 20));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 16;"
        );

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.04));
        shadow.setRadius(15);
        shadow.setOffsetY(5);
        card.setEffect(shadow);

        // ===== RESULT ICON =====
        Label icon = new Label();
        icon.setFont(Font.font(28));
        icon.setMinWidth(45);
        icon.setAlignment(Pos.CENTER);

        String iconBg;
        switch (result) {
            case "WON":
                icon.setText("🏆");
                iconBg = "#E8F5E9";
                break;
            case "LOST":
                icon.setText("💔");
                iconBg = "#FFEBEE";
                break;
            case "ACTIVE":
                icon.setText("⏳");
                iconBg = "#E3F2FD";
                break;
            default:
                icon.setText("📋");
                iconBg = "#F5F5F5";
        }

        StackPane iconCircle = new StackPane(icon);
        iconCircle.setMinSize(52, 52);
        iconCircle.setMaxSize(52, 52);
        iconCircle.setStyle(
            "-fx-background-color: " + iconBg + ";" +
            "-fx-background-radius: 26;"
        );

        // ===== CAR IMAGE THUMBNAIL =====
        ImageView img = new ImageView();
        img.setFitWidth(90);
        img.setFitHeight(60);
        img.setPreserveRatio(false);

        try {
            if (imagePath != null && !imagePath.isEmpty() && !imagePath.equals("null")) {
                img.setImage(new Image(imagePath, 90, 60, false, true, true));
            }
        } catch (Exception e) {
            // no image
        }

        StackPane imgBox = new StackPane(img);
        imgBox.setStyle("-fx-background-color: #e8e8e8; -fx-background-radius: 8;");
        imgBox.setMinSize(90, 60);
        imgBox.setMaxSize(90, 60);

        // ===== INFO =====
        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label name = new Label(carTitle);
        name.setFont(Font.font("System", FontWeight.BOLD, 15));
        name.setTextFill(Color.web("#111827"));

        Label details = new Label(brand + " • " + model);
        details.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 12px;");

        Label time = new Label("📅 " + bidTime);
        time.setStyle("-fx-text-fill: #aaa; -fx-font-size: 11px;");

        info.getChildren().addAll(name, details, time);

        // ===== BID + RESULT =====
        VBox rightSide = new VBox(5);
        rightSide.setAlignment(Pos.CENTER_RIGHT);

        Label bidLbl = new Label("Your Bid: " + formatPrice(bidAmount) + " MMK");
        bidLbl.setFont(Font.font("System", FontWeight.BOLD, 14));
        bidLbl.setTextFill(Color.web("#2563EB"));

        Label currentLbl = new Label("Final: " + formatPrice(currentBid) + " MMK");
        currentLbl.setStyle("-fx-text-fill: #888; -fx-font-size: 11px;");

        Label resultBadge = new Label(result);
        resultBadge.setPadding(new Insets(4, 12, 4, 12));
        resultBadge.setFont(Font.font("System", FontWeight.BOLD, 11));

        switch (result) {
            case "WON":
                resultBadge.setText("🏆 WON");
                resultBadge.setStyle(
                    "-fx-background-color: #E8F5E9; -fx-text-fill: #2E7D32;" +
                    "-fx-background-radius: 12;"
                );
                break;
            case "LOST":
                resultBadge.setText("💔 LOST");
                resultBadge.setStyle(
                    "-fx-background-color: #FFEBEE; -fx-text-fill: #C62828;" +
                    "-fx-background-radius: 12;"
                );
                break;
            case "ACTIVE":
                resultBadge.setText("⏳ ACTIVE");
                resultBadge.setStyle(
                    "-fx-background-color: #E3F2FD; -fx-text-fill: #1565C0;" +
                    "-fx-background-radius: 12;"
                );
                break;
        }

        rightSide.getChildren().addAll(bidLbl, currentLbl, resultBadge);

        card.getChildren().addAll(iconCircle, imgBox, info, rightSide);

        return card;
    }

    private String formatPrice(String priceStr) {
        try {
            double price = Double.parseDouble(priceStr);
            return String.format("%,.0f", price);
        } catch (Exception e) {
            return priceStr;
        }
    }
}