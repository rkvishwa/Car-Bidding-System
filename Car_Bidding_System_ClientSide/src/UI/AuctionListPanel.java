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

public class AuctionListPanel {

    private FlowPane liveCards = new FlowPane();
    private FlowPane otherCards = new FlowPane();
    private String userId;
    private VBox root;

    public AuctionListPanel(String userId) {
        this.userId = userId;
    }

    public Node getView() {

        root = new VBox(20);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: transparent;");

        // ===== HEADER =====
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Label header = new Label("🏎  Auctions");
        header.setFont(Font.font("System", FontWeight.BOLD, 26));
        header.setTextFill(Color.web("#1a1a2e"));

        Label subtitle = new Label("Browse and bid on available cars");
        subtitle.setStyle("-fx-text-fill: #888; -fx-font-size: 13px; -fx-padding: 8 0 0 15;");

        headerBox.getChildren().addAll(header, subtitle);

        // ===== LIVE SECTION =====
        Label liveTitle = new Label("🔴 Live Auctions");
        liveTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        liveTitle.setTextFill(Color.web("#2E7D32"));
        liveTitle.setPadding(new Insets(5, 0, 0, 0));

        liveCards.setHgap(18);
        liveCards.setVgap(18);
        liveCards.setPadding(new Insets(8, 0, 0, 0));

        // ===== UPCOMING/CLOSED SECTION =====
        Label otherTitle = new Label("📋 Upcoming & Closed Auctions");
        otherTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        otherTitle.setTextFill(Color.web("#757575"));
        otherTitle.setPadding(new Insets(10, 0, 0, 0));

        otherCards.setHgap(18);
        otherCards.setVgap(18);
        otherCards.setPadding(new Insets(8, 0, 0, 0));

        Separator sep = new Separator();
        sep.setPadding(new Insets(5, 0, 5, 0));

        VBox content = new VBox(10);
        content.getChildren().addAll(liveTitle, liveCards, sep, otherTitle, otherCards);

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        root.getChildren().addAll(headerBox, scroll);

        return root;
    }

    // Controller calls this to render auctions
    public void render(String response) {

        liveCards.getChildren().clear();
        otherCards.getChildren().clear();

        if (response == null || response.trim().isEmpty()) {
            Label empty = new Label("No auctions available right now");
            empty.setStyle("-fx-text-fill: #999; -fx-font-size: 16px; -fx-padding: 40;");
            liveCards.getChildren().add(empty);
            return;
        }

        boolean hasLive = false;
        boolean hasOther = false;

        for (String a : response.split("\n")) {

            if (a.trim().isEmpty()) continue;

            String[] d = a.split("\\|");

            if (d.length < 7) continue;

            // d[0]=auctionId, d[1]=title, d[2]=brand, d[3]=model,
            // d[4]=currentBid, d[5]=status, d[6]=image

            VBox card = createCard(d[0], d[1], d[2], d[3], d[4], d[5], d[6]);

            if ("ACTIVE".equals(d[5])) {
                liveCards.getChildren().add(card);
                hasLive = true;
            } else {
                otherCards.getChildren().add(card);
                hasOther = true;
            }
        }

        if (!hasLive) {
            Label noLive = new Label("No live auctions right now. Check back soon!");
            noLive.setStyle("-fx-text-fill: #999; -fx-font-size: 14px; -fx-padding: 20;");
            noLive.setWrapText(true);
            liveCards.getChildren().add(noLive);
        }

        if (!hasOther) {
            Label noOther = new Label("No other auctions at the moment");
            noOther.setStyle("-fx-text-fill: #999; -fx-font-size: 14px; -fx-padding: 20;");
            otherCards.getChildren().add(noOther);
        }
    }

    private VBox createCard(String id, String title, String brand, String model,
                            String bid, String status, String imagePath) {

        VBox card = new VBox(0);
        card.setPrefWidth(280);
        card.setMaxWidth(280);
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 16;" +
            "-fx-border-radius: 16;"
        );

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.04));
        shadow.setRadius(15);
        shadow.setOffsetY(5);
        card.setEffect(shadow);

        // Hover effect
        card.setOnMouseEntered(e -> {
            card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 16;" +
                "-fx-border-radius: 16;" +
                "-fx-scale-x: 1.02; -fx-scale-y: 1.02;"
            );
        });
        card.setOnMouseExited(e -> {
            card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 16;" +
                "-fx-border-radius: 16;"
            );
        });

        // ===== IMAGE =====
        ImageView img = new ImageView();
        img.setFitWidth(280);
        img.setFitHeight(160);
        img.setPreserveRatio(false);

        try {
            if (imagePath != null && !imagePath.isEmpty() && !imagePath.equals("null")) {
                img.setImage(new Image(imagePath, 280, 160, false, true, true));
            }
        } catch (Exception e) {
            // fallback - no image
        }

        // Image container with rounded top
        StackPane imgContainer = new StackPane(img);
        imgContainer.setStyle(
            "-fx-background-radius: 16 16 0 0;" +
            "-fx-background-color: #e8e8e8;"
        );
        imgContainer.setMinHeight(160);
        imgContainer.setMaxHeight(160);

        // ===== STATUS BADGE =====
        Label statusBadge = new Label(status);
        statusBadge.setPadding(new Insets(4, 12, 4, 12));
        statusBadge.setFont(Font.font("System", FontWeight.BOLD, 11));

        if ("ACTIVE".equals(status)) {
            statusBadge.setStyle(
                "-fx-background-color: #4CAF50; -fx-text-fill: white;" +
                "-fx-background-radius: 20; -fx-font-weight: bold;"
            );
            statusBadge.setText("🔴 LIVE");
        } else if ("CLOSED".equals(status)) {
            statusBadge.setStyle(
                "-fx-background-color: #9E9E9E; -fx-text-fill: white;" +
                "-fx-background-radius: 20; -fx-font-weight: bold;"
            );
            statusBadge.setText("CLOSED");
        } else {
            statusBadge.setStyle(
                "-fx-background-color: #FF9800; -fx-text-fill: white;" +
                "-fx-background-radius: 20; -fx-font-weight: bold;"
            );
            statusBadge.setText("⏳ UPCOMING");
        }

        StackPane imageStack = new StackPane(imgContainer, statusBadge);
        StackPane.setAlignment(statusBadge, Pos.TOP_RIGHT);
        StackPane.setMargin(statusBadge, new Insets(10, 10, 0, 0));

        // ===== INFO =====
        VBox info = new VBox(6);
        info.setPadding(new Insets(14, 16, 16, 16));

        Label name = new Label(title);
        name.setFont(Font.font("System", FontWeight.BOLD, 16));
        name.setTextFill(Color.web("#111827"));

        Label details = new Label(brand + " • " + model);
        details.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 12px;");

        // Bid amount
        Label bidLbl = new Label("💰 " + bid + " MMK");
        bidLbl.setFont(Font.font("System", FontWeight.BOLD, 15));
        bidLbl.setTextFill(Color.web("#1976D2"));
        bidLbl.setId("bid_" + id);

        // ===== BUTTONS =====
        HBox buttons = new HBox(8);
        buttons.setAlignment(Pos.CENTER_LEFT);
        buttons.setPadding(new Insets(8, 0, 0, 0));

        Button openBtn = new Button("View Auction");
        openBtn.setStyle(
            "-fx-background-color: #1976D2;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 8 18;" +
            "-fx-cursor: hand;"
        );

        openBtn.setOnAction(e -> {
            Controller.Navigation.goToLiveAuction(id, userId, "AUCTION_LIST");
        });

        // Hover
        openBtn.setOnMouseEntered(e -> openBtn.setStyle(
            "-fx-background-color: #1565C0;" +
            "-fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold;" +
            "-fx-background-radius: 8; -fx-padding: 8 18; -fx-cursor: hand;"
        ));
        openBtn.setOnMouseExited(e -> openBtn.setStyle(
            "-fx-background-color: #1976D2;" +
            "-fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold;" +
            "-fx-background-radius: 8; -fx-padding: 8 18; -fx-cursor: hand;"
        ));

        Button watchBtn = new Button("⭐ Watch");
        watchBtn.setStyle(
            "-fx-background-color: #FFF3E0;" +
            "-fx-text-fill: #E65100;" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 8 14;" +
            "-fx-cursor: hand;"
        );

        watchBtn.setOnAction(e -> {
            try {
                String res = WatchlistService.addToWatchlist(userId, id);
                if (res.equals("SUCCESS")) {
                    watchBtn.setText("✅ Watching");
                    watchBtn.setStyle(
                        "-fx-background-color: #E8F5E9;" +
                        "-fx-text-fill: #2E7D32;" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8 14;" +
                        "-fx-cursor: hand;"
                    );
                    watchBtn.setDisable(true);
                } else {
                    watchBtn.setText("✅ Already Watching");
                    watchBtn.setDisable(true);
                    watchBtn.setStyle(
                        "-fx-background-color: #E8F5E9;" +
                        "-fx-text-fill: #2E7D32;" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8 14;"
                    );
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // Show buttons based on status
        if ("ACTIVE".equals(status)) {
            buttons.getChildren().addAll(openBtn, watchBtn);
        } else {
            buttons.getChildren().add(watchBtn);
        }

        info.getChildren().addAll(name, details, bidLbl, buttons);
        card.getChildren().addAll(imageStack, info);

        return card;
    }
}