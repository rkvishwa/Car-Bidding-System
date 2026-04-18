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

import Service.WatchlistService;

public class WatchlistPanel {

    private String userId;
    private VBox list = new VBox(12);

    public WatchlistPanel(String userId) {
        this.userId = userId;
    }

    public Node getView() {

        VBox root = new VBox(20);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: #f0f2f5;");

        // ===== HEADER =====
        HBox headerRow = new HBox(15);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("⭐ My Watchlist");
        title.setFont(Font.font("System", FontWeight.BOLD, 26));
        title.setTextFill(Color.web("#1a1a2e"));

        Region hSpacer = new Region();
        HBox.setHgrow(hSpacer, Priority.ALWAYS);

        Label countLabel = new Label();
        countLabel.setStyle(
            "-fx-text-fill: #888; -fx-font-size: 13px;" +
            "-fx-background-color: #e8e8e8; -fx-background-radius: 12;" +
            "-fx-padding: 4 12;"
        );

        headerRow.getChildren().addAll(title, hSpacer, countLabel);

        Label subtitle = new Label("Auctions you're tracking • Get notified of bid activity");
        subtitle.setStyle("-fx-text-fill: #888; -fx-font-size: 13px;");

        VBox header = new VBox(4, headerRow, subtitle);

        // ===== LOAD DATA =====
        loadData(countLabel);

        ScrollPane scroll = new ScrollPane(list);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        root.getChildren().addAll(header, scroll);

        return root;
    }

    private void loadData(Label countLabel) {

        list.getChildren().clear();

        try {
            String res = WatchlistService.getWatchlist(userId);

            if (res == null || res.trim().isEmpty()) {
                VBox emptyState = new VBox(10);
                emptyState.setAlignment(Pos.CENTER);
                emptyState.setPadding(new Insets(60));

                Label emptyIcon = new Label("⭐");
                emptyIcon.setFont(Font.font(48));

                Label empty = new Label("Your watchlist is empty");
                empty.setStyle("-fx-text-fill: #999; -fx-font-size: 16px;");

                Label hint = new Label("Add auctions from the Auctions panel to track them here!");
                hint.setStyle("-fx-text-fill: #bbb; -fx-font-size: 13px;");
                hint.setWrapText(true);

                emptyState.getChildren().addAll(emptyIcon, empty, hint);
                list.getChildren().add(emptyState);
                countLabel.setText("0 items");
                return;
            }

            int count = 0;
            for (String row : res.split("\n")) {
                if (row.trim().isEmpty()) continue;

                String[] d = row.split("\\|");
                if (d.length < 7) continue;

                // d[0]=auctionId, d[1]=title, d[2]=brand, d[3]=model,
                // d[4]=image, d[5]=currentBid, d[6]=status
                list.getChildren().add(
                    createCard(d[0], d[1], d[2], d[3], d[4], d[5], d[6])
                );
                count++;
            }

            countLabel.setText(count + " item" + (count != 1 ? "s" : ""));

        } catch (Exception e) {
            e.printStackTrace();
            Label error = new Label("⚠ Failed to load watchlist");
            error.setStyle("-fx-text-fill: #D32F2F; -fx-font-size: 14px;");
            list.getChildren().add(error);
        }
    }

    private HBox createCard(String auctionId, String title, String brand,
                            String model, String imagePath, String bid, String status) {

        HBox card = new HBox(16);
        card.setPadding(new Insets(14, 18, 14, 18));
        card.setAlignment(Pos.CENTER_LEFT);

        boolean isLive = "ACTIVE".equals(status);

        card.setStyle(
            "-fx-background-color: " + (isLive ? "#F1F8E9" : "white") + ";" +
            "-fx-background-radius: 16;" +
            (isLive ? "-fx-border-color: #C5E1A5; -fx-border-radius: 16; -fx-border-width: 1;" : "")
        );

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.04));
        shadow.setRadius(15);
        shadow.setOffsetY(5);
        card.setEffect(shadow);

        // ===== IMAGE =====
        ImageView img = new ImageView();
        img.setFitWidth(120);
        img.setFitHeight(80);
        img.setPreserveRatio(false);

        try {
            if (imagePath != null && !imagePath.isEmpty() && !imagePath.equals("null")) {
                img.setImage(new Image(imagePath, 120, 80, false, true, true));
            }
        } catch (Exception e) {
            // no image
        }

        StackPane imgBox = new StackPane(img);
        imgBox.setStyle("-fx-background-color: #e8e8e8; -fx-background-radius: 10;");
        imgBox.setMinSize(120, 80);
        imgBox.setMaxSize(120, 80);

        // ===== INFO =====
        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label name = new Label(title);
        name.setFont(Font.font("System", FontWeight.BOLD, 15));
        name.setTextFill(Color.web("#111827"));

        Label details = new Label(brand + " • " + model);
        details.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 12px;");

        Label bidLbl = new Label("💰 " + bid + " MMK");
        bidLbl.setFont(Font.font("System", FontWeight.BOLD, 14));
        bidLbl.setTextFill(Color.web("#1976D2"));

        info.getChildren().addAll(name, details, bidLbl);

        // ===== RIGHT SIDE =====
        VBox rightSide = new VBox(8);
        rightSide.setAlignment(Pos.CENTER_RIGHT);

        // Status
        Label statusBadge = new Label(status);
        statusBadge.setPadding(new Insets(4, 12, 4, 12));
        statusBadge.setFont(Font.font("System", FontWeight.BOLD, 11));

        if (isLive) {
            statusBadge.setText("🔴 LIVE");
            statusBadge.setStyle(
                "-fx-background-color: #E8F5E9; -fx-text-fill: #2E7D32;" +
                "-fx-background-radius: 12;"
            );
        } else {
            statusBadge.setStyle(
                "-fx-background-color: #F5F5F5; -fx-text-fill: #757575;" +
                "-fx-background-radius: 12;"
            );
        }

        // View Button
        Button viewBtn = new Button("🔨 View Auction");
        viewBtn.setStyle(
            "-fx-background-color: #1976D2;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 11px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 7 14;" +
            "-fx-cursor: hand;"
        );
        viewBtn.setOnAction(e -> {
            Controller.Navigation.goToLiveAuction(auctionId, userId);
        });

        if (!isLive) {
            viewBtn.setDisable(true);
            viewBtn.setStyle(
                "-fx-background-color: #e0e0e0;" +
                "-fx-text-fill: #999;" +
                "-fx-font-size: 11px;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 7 14;"
            );
        }

        // Remove Button
        Button removeBtn = new Button("✕ Remove");
        removeBtn.setStyle(
            "-fx-background-color: #FFEBEE;" +
            "-fx-text-fill: #C62828;" +
            "-fx-font-size: 11px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 7 12;" +
            "-fx-cursor: hand;"
        );
        removeBtn.setOnAction(e -> {
            try {
                WatchlistService.removeFromWatchlist(userId, auctionId);
                list.getChildren().remove(card);

                if (list.getChildren().isEmpty()) {
                    VBox emptyState = new VBox(10);
                    emptyState.setAlignment(Pos.CENTER);
                    emptyState.setPadding(new Insets(60));

                    Label emptyIcon = new Label("⭐");
                    emptyIcon.setFont(Font.font(48));

                    Label empty = new Label("Your watchlist is empty");
                    empty.setStyle("-fx-text-fill: #999; -fx-font-size: 16px;");

                    emptyState.getChildren().addAll(emptyIcon, empty);
                    list.getChildren().add(emptyState);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        HBox actionBtns = new HBox(6, viewBtn, removeBtn);

        rightSide.getChildren().addAll(statusBadge, actionBtns);

        card.getChildren().addAll(imgBox, info, rightSide);

        return card;
    }
}