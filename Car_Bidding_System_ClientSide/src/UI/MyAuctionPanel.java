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

import java.util.function.Consumer;

public class MyAuctionPanel {

    private VBox liveSection = new VBox(12);
    private VBox closedSection = new VBox(12);
    private Consumer<String> onClose;

    public void setOnClose(Consumer<String> onClose) {
        this.onClose = onClose;
    }

    public VBox getView() {

        VBox root = new VBox(20);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: #f0f2f5;");

        // ===== HEADER =====
        Label title = new Label("🔨 My Auctions");
        title.setFont(Font.font("System", FontWeight.BOLD, 26));
        title.setTextFill(Color.web("#1a1a2e"));

        Label subtitle = new Label("Your active and completed auctions");
        subtitle.setStyle("-fx-text-fill: #888; -fx-font-size: 13px;");

        VBox header = new VBox(4, title, subtitle);

        // ===== LIVE SECTION =====
        Label liveTitle = new Label("🔴 Currently Live");
        liveTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        liveTitle.setTextFill(Color.web("#2E7D32"));
        liveTitle.setPadding(new Insets(5, 0, 0, 0));

        // ===== CLOSED SECTION =====
        Label closedTitle = new Label("📋 Completed Auctions");
        closedTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        closedTitle.setTextFill(Color.web("#757575"));
        closedTitle.setPadding(new Insets(10, 0, 0, 0));

        Separator sep = new Separator();
        sep.setPadding(new Insets(5, 0, 5, 0));

        VBox content = new VBox(10);
        content.getChildren().addAll(liveTitle, liveSection, sep, closedTitle, closedSection);

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        root.getChildren().addAll(header, scroll);
        return root;
    }

    // Called by controller with detailed data
    public void render(String res) {

        liveSection.getChildren().clear();
        closedSection.getChildren().clear();

        if (res == null || res.trim().isEmpty()) {
            VBox emptyState = new VBox(10);
            emptyState.setAlignment(Pos.CENTER);
            emptyState.setPadding(new Insets(40));

            Label emptyIcon = new Label("🔨");
            emptyIcon.setFont(Font.font(40));

            Label empty = new Label("No auctions found");
            empty.setStyle("-fx-text-fill: #999; -fx-font-size: 16px;");

            Label hint = new Label("Create one from 'Create Auction'!");
            hint.setStyle("-fx-text-fill: #bbb; -fx-font-size: 13px;");

            emptyState.getChildren().addAll(emptyIcon, empty, hint);
            liveSection.getChildren().add(emptyState);
            return;
        }

        boolean hasLive = false;
        boolean hasClosed = false;

        for (String row : res.split("\n")) {

            if (row.trim().isEmpty()) continue;

            String[] d = row.split("\\|");

            if (d.length < 9) continue;

            // d[0]=auctionId, d[1]=title, d[2]=brand, d[3]=model,
            // d[4]=image, d[5]=currentBid, d[6]=minBid, d[7]=status, d[8]=winnerId

            String status = d[7];
            HBox card = createCard(d[0], d[1], d[2], d[3], d[4], d[5], d[6], d[7], d[8]);

            if ("ACTIVE".equals(status)) {
                liveSection.getChildren().add(card);
                hasLive = true;
            } else {
                closedSection.getChildren().add(card);
                hasClosed = true;
            }
        }

        if (!hasLive) {
            Label noLive = new Label("No live auctions right now");
            noLive.setStyle("-fx-text-fill: #999; -fx-font-size: 13px; -fx-padding: 10;");
            liveSection.getChildren().add(noLive);
        }

        if (!hasClosed) {
            Label noClosed = new Label("No completed auctions yet");
            noClosed.setStyle("-fx-text-fill: #999; -fx-font-size: 13px; -fx-padding: 10;");
            closedSection.getChildren().add(noClosed);
        }
    }

    private HBox createCard(String auctionId, String title, String brand, String model,
                            String imagePath, String currentBid, String minBid,
                            String status, String winnerId) {

        boolean isLive = "ACTIVE".equals(status);

        HBox card = new HBox(18);
        card.setPadding(new Insets(16, 20, 16, 20));
        card.setAlignment(Pos.CENTER_LEFT);
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
        img.setFitWidth(140);
        img.setFitHeight(90);
        img.setPreserveRatio(false);

        try {
            if (imagePath != null && !imagePath.isEmpty() && !imagePath.equals("null")) {
                img.setImage(new Image(imagePath, 140, 90, false, true, true));
            }
        } catch (Exception e) {
            // no image
        }

        StackPane imgBox = new StackPane(img);
        imgBox.setStyle("-fx-background-color: #e8e8e8; -fx-background-radius: 10;");
        imgBox.setMinSize(140, 90);
        imgBox.setMaxSize(140, 90);

        // ===== INFO =====
        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label name = new Label(title);
        name.setFont(Font.font("System", FontWeight.BOLD, 16));
        name.setTextFill(Color.web("#111827"));

        Label details = new Label(brand + " • " + model);
        details.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 12px;");

        Label bidInfo = new Label("💰 Current: " + currentBid + " MMK  |  Min: " + minBid + " MMK");
        bidInfo.setStyle("-fx-text-fill: #2563EB; -fx-font-weight: bold; -fx-font-size: 13px;");

        info.getChildren().addAll(name, details, bidInfo);

        // Winner info for closed auctions
        if ("CLOSED".equals(status)) {
            if (winnerId != null && !winnerId.equals("N/A") && !winnerId.isEmpty()) {
                HBox winnerBox = new HBox(6);
                winnerBox.setAlignment(Pos.CENTER_LEFT);
                winnerBox.setPadding(new Insets(4, 10, 4, 10));
                winnerBox.setStyle(
                    "-fx-background-color: #E8F5E9; -fx-background-radius: 8;"
                );

                Label winnerIcon = new Label("🏆");
                Label winnerLabel = new Label("Winner: " + winnerId + " — Won at " + currentBid + " MMK");
                winnerLabel.setStyle(
                    "-fx-text-fill: #2E7D32; -fx-font-weight: bold; -fx-font-size: 12px;"
                );

                winnerBox.getChildren().addAll(winnerIcon, winnerLabel);
                info.getChildren().add(winnerBox);
            } else {
                Label noWinner = new Label("No bids received");
                noWinner.setStyle("-fx-text-fill: #999; -fx-font-size: 12px;");
                info.getChildren().add(noWinner);
            }
        }

        // ===== RIGHT SIDE =====
        VBox rightSide = new VBox(8);
        rightSide.setAlignment(Pos.CENTER_RIGHT);

        // Status badge
        Label statusBadge = new Label(status);
        statusBadge.setPadding(new Insets(4, 14, 4, 14));
        statusBadge.setFont(Font.font("System", FontWeight.BOLD, 11));

        if (isLive) {
            statusBadge.setText("🔴 LIVE");
            statusBadge.setStyle(
                "-fx-background-color: #4CAF50; -fx-text-fill: white;" +
                "-fx-background-radius: 20;"
            );

            // Close button for active auctions
            Button closeBtn = new Button("🔒 Close Auction");
            closeBtn.setStyle(
                "-fx-background-color: #D32F2F;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 8 14;" +
                "-fx-cursor: hand;"
            );

            closeBtn.setOnAction(e -> {
                if (onClose != null) {
                    onClose.accept(auctionId);
                    card.setDisable(true);
                    card.setOpacity(0.6);
                }
            });

            rightSide.getChildren().addAll(statusBadge, closeBtn);

        } else {
            statusBadge.setText("CLOSED");
            statusBadge.setStyle(
                "-fx-background-color: #F5F5F5; -fx-text-fill: #757575;" +
                "-fx-background-radius: 20;"
            );
            rightSide.getChildren().add(statusBadge);
        }

        card.getChildren().addAll(imgBox, info, rightSide);

        return card;
    }
}