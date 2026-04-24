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

public class AdminAuctionPanel {

    private VBox liveSection = new VBox(12);
    private VBox closedSection = new VBox(12);
    private Consumer<String> onClose;

    public VBox getView() {

        VBox root = new VBox(20);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: transparent;");

        // ===== HEADER =====
        Label title = new Label("📦 All Auctions");
        title.setFont(Font.font("System", FontWeight.BOLD, 26));
        title.setTextFill(Color.web("#1a1a2e"));

        Label subtitle = new Label("Monitor and manage all auctions — Stop live auctions if needed");
        subtitle.setStyle("-fx-text-fill: #888; -fx-font-size: 13px;");

        VBox header = new VBox(4, title, subtitle);

        // ===== LIVE =====
        Label liveTitle = new Label("🔴 Live Auctions");
        liveTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        liveTitle.setTextFill(Color.web("#2E7D32"));

        // ===== CLOSED =====
        Label closedTitle = new Label("📋 Closed Auctions");
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

    public void render(String res) {

        liveSection.getChildren().clear();
        closedSection.getChildren().clear();

        if (res == null || res.trim().isEmpty()) {
            Label empty = new Label("No auctions found");
            empty.setStyle("-fx-text-fill: #999; -fx-font-size: 14px; -fx-padding: 20;");
            liveSection.getChildren().add(empty);
            return;
        }

        boolean hasLive = false;
        boolean hasClosed = false;

        for (String row : res.split("\n")) {
            if (row.trim().isEmpty()) continue;

            String[] d = row.split("\\|");
            if (d.length < 10) continue;

            // d[0]=auctionId, d[1]=title, d[2]=brand, d[3]=model,
            // d[4]=image, d[5]=currentBid, d[6]=minBid, d[7]=status,
            // d[8]=winnerId, d[9]=sellerId

            String status = d[7];
            HBox card = createCard(d);

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

    private HBox createCard(String[] d) {

        boolean isLive = "ACTIVE".equals(d[7]);

        HBox card = new HBox(16);
        card.setPadding(new Insets(16, 20, 16, 20));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle(
            "-fx-background-color: " + (isLive ? "#FFF8E1" : "white") + ";" +
            "-fx-background-radius: 16;" +
            (isLive ? "-fx-border-color: #FFE082; -fx-border-radius: 16; -fx-border-width: 1;" : "")
        );

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.04));
        shadow.setRadius(15);
        shadow.setOffsetY(5);
        card.setEffect(shadow);

        // ===== IMAGE =====
        ImageView img = new ImageView();
        img.setFitWidth(130);
        img.setFitHeight(85);
        img.setPreserveRatio(false);

        try {
            if (d[4] != null && !d[4].isEmpty() && !d[4].equals("null")) {
                img.setImage(new Image(d[4], 130, 85, false, true, true));
            }
        } catch (Exception e) {
            // no image
        }

        StackPane imgBox = new StackPane(img);
        imgBox.setStyle("-fx-background-color: #e8e8e8; -fx-background-radius: 10;");
        imgBox.setMinSize(130, 85);
        imgBox.setMaxSize(130, 85);

        // ===== INFO =====
        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label name = new Label(d[1]);
        name.setFont(Font.font("System", FontWeight.BOLD, 15));
        name.setTextFill(Color.web("#111827"));

        Label details = new Label(d[2] + " • " + d[3]);
        details.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 12px;");

        Label bidInfo = new Label("💰 Current: " + d[5] + " MMK  |  Min: " + d[6] + " MMK");
        bidInfo.setStyle("-fx-text-fill: #2563EB; -fx-font-weight: bold; -fx-font-size: 12px;");

        Label sellerInfo = new Label("👤 Seller: " + d[9]);
        sellerInfo.setStyle("-fx-text-fill: #999; -fx-font-size: 11px;");

        info.getChildren().addAll(name, details, bidInfo, sellerInfo);

        // Winner for closed
        if (!isLive && d[8] != null && !d[8].equals("N/A")) {
            Label winner = new Label("🏆 Winner: " + d[8]);
            winner.setStyle("-fx-text-fill: #2E7D32; -fx-font-weight: bold; -fx-font-size: 12px;");
            info.getChildren().add(winner);
        }

        // ===== RIGHT SIDE =====
        VBox rightSide = new VBox(6);
        rightSide.setAlignment(Pos.CENTER_RIGHT);

        Label statusBadge = new Label(d[7]);
        statusBadge.setPadding(new Insets(4, 14, 4, 14));
        statusBadge.setFont(Font.font("System", FontWeight.BOLD, 11));

        if (isLive) {
            statusBadge.setText("🔴 LIVE");
            statusBadge.setStyle(
                "-fx-background-color: #4CAF50; -fx-text-fill: white;" +
                "-fx-background-radius: 20;"
            );

            Button stopBtn = new Button("🛑 Stop Auction");
            stopBtn.setStyle(
                "-fx-background-color: #D32F2F;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 8 14;" +
                "-fx-cursor: hand;"
            );

            stopBtn.setOnAction(e -> {
                if (onClose != null) {
                    onClose.accept(d[0]);
                    card.setDisable(true);
                    card.setOpacity(0.6);
                }
            });

            rightSide.getChildren().addAll(statusBadge, stopBtn);
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

    public void onClose(Consumer<String> action) {
        this.onClose = action;
    }
}