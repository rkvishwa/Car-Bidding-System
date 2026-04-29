package UI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.function.Consumer;

public class AdminAuctionPanel {

    private VBox pendingSection = new VBox(12);
    private VBox liveSection = new VBox(12);
    private VBox closedSection = new VBox(12);
    private Consumer<String> onClose;
    private Consumer<String[]> onApprove; // auctionId, minBid, duration
    private Consumer<String> onReject;
    private Consumer<String> onConfirmWinner;

    public VBox getView() {

        VBox root = new VBox(20);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: transparent;");

        // ===== HEADER =====
        Label title = new Label("🔨 Auction Management");
        title.setFont(Font.font("System", FontWeight.BOLD, 26));
        title.setTextFill(Color.web("#1a1a2e"));

        Label subtitle = new Label("Review pending auctions and manage live ones");
        subtitle.setStyle("-fx-text-fill: #888; -fx-font-size: 13px;");

        VBox header = new VBox(4, title, subtitle);

        // ===== PENDING SECTION =====
        Label pendingTitle = new Label("⏳ Pending Auctions");
        pendingTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        pendingTitle.setTextFill(Color.web("#E65100"));

        // ===== LIVE SECTION =====
        Label liveTitle = new Label("🔴 Live Auctions");
        liveTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        liveTitle.setTextFill(Color.web("#2E7D32"));

        // ===== CLOSED SECTION =====
        Label closedTitle = new Label("📋 Completed Auctions");
        closedTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        closedTitle.setTextFill(Color.web("#757575"));

        VBox content = new VBox(15);
        content.getChildren().addAll(
            pendingTitle, pendingSection, new Separator(),
            liveTitle, liveSection, new Separator(),
            closedTitle, closedSection
        );

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

        for (String row : res.split("\n")) {
            if (row.trim().isEmpty()) continue;
            String[] d = row.split("\\|");
            if (d.length < 8) continue;

            // d[0]=auctionId, d[1]=title, d[2]=brand, d[3]=model,
            // d[4]=minBid, d[5]=status, d[6]=image, d[7]=winnerId, d[8]=sellerId, d[9]=winnerConfirmed
            
            String status = d[5];
            int winnerConfirmed = d.length > 9 ? Integer.parseInt(d[9]) : 0;
            
            HBox card = createCard(d[0], d[1], d[2], d[3], d[4], d[5], d[6], d[7], winnerConfirmed);

            if ("ACTIVE".equals(status)) {
                liveSection.getChildren().add(card);
            } else if ("CLOSED".equals(status)) {
                closedSection.getChildren().add(card);
            }
        }
    }

    public void renderPending(String res) {
        pendingSection.getChildren().clear();
        if (res == null || res.trim().isEmpty()) {
            Label empty = new Label("No pending auctions");
            empty.setStyle("-fx-text-fill: #999; -fx-font-size: 13px; -fx-padding: 10;");
            pendingSection.getChildren().add(empty);
            return;
        }
        for (String row : res.split("\n")) {
            if (row.trim().isEmpty()) continue;
            String[] d = row.split("\\|");
            if (d.length < 5) continue;
            // d[0]=auctionId, d[1]=title, d[2]=brand, d[3]=model, d[4]=image, d[5]=sellerId
            pendingSection.getChildren().add(createPendingCard(d[0], d[1], d[2], d[3], d[4]));
        }
    }

    private HBox createPendingCard(String id, String title, String brand, String model, String image) {
        HBox card = new HBox(15);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color: #FFF3E0; -fx-background-radius: 12;");
        card.setAlignment(Pos.CENTER_LEFT);

        VBox info = new VBox(2);
        HBox.setHgrow(info, Priority.ALWAYS);
        Label t = new Label(title); t.setStyle("-fx-font-weight: bold;");
        Label m = new Label(brand + " " + model); m.setStyle("-fx-font-size: 11px;");
        info.getChildren().addAll(t, m);

        TextField minBidField = new TextField(); minBidField.setPromptText("Min Bid"); minBidField.setPrefWidth(80);
        TextField durationField = new TextField(); durationField.setPromptText("Mins"); durationField.setPrefWidth(60);
        TextField startTimeField = new TextField();startTimeField.setPromptText("Start: yyyy-MM-dd HH:mm");startTimeField.setPrefWidth(140);

        Button approveBtn = new Button("✅ Approve");
        approveBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        approveBtn.setOnAction(e -> {
            if (onApprove != null) onApprove.accept(new String[]{id, minBidField.getText(), durationField.getText(),startTimeField.getText()});
        });

        card.getChildren().addAll(info, minBidField, durationField, approveBtn);
        return card;
    }

    private HBox createCard(String auctionId, String title, String brand, String model,
                            String minBid, String status, String imagePath, String winnerId, int winnerConfirmed) {

        HBox card = new HBox(18);
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

        // ===== IMAGE =====
        ImageView img = new ImageView();
        img.setFitWidth(120);
        img.setFitHeight(80);
        img.setPreserveRatio(false);

        try {
            if (imagePath != null && !imagePath.isEmpty() && !imagePath.equals("null")) {
                img.setImage(new Image(imagePath, 120, 80, false, true, true));
            }
        } catch (Exception e) {}

        StackPane imgBox = new StackPane(img);
        imgBox.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 10;");

        // ===== INFO =====
        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label name = new Label(title);
        name.setFont(Font.font("System", FontWeight.BOLD, 16));

        Label details = new Label(brand + " • " + model);
        details.setStyle("-fx-text-fill: #888; -fx-font-size: 12px;");

        Label bidInfo = new Label("💰 Min Bid: " + formatPrice(minBid) + " MMK");
        bidInfo.setStyle("-fx-text-fill: #1976D2; -fx-font-weight: bold;");

        info.getChildren().addAll(name, details, bidInfo);

        // ===== RIGHT SIDE =====
        VBox rightSide = new VBox(8);
        rightSide.setAlignment(Pos.CENTER_RIGHT);

        Label statusBadge = new Label(status);
        statusBadge.setPadding(new Insets(4, 12, 4, 12));
        statusBadge.setFont(Font.font("System", FontWeight.BOLD, 11));

        if ("ACTIVE".equals(status)) {
            statusBadge.setStyle("-fx-background-color: #E8F5E9; -fx-text-fill: #2E7D32; -fx-background-radius: 20;");

            Button stopBtn = new Button("🛑 Stop");
            stopBtn.setStyle("-fx-background-color: #D32F2F; -fx-text-fill: white; -fx-font-size: 11px; -fx-background-radius: 8;");
            stopBtn.setOnAction(e -> { if (onClose != null) onClose.accept(auctionId); });

            Button watchBtn = new Button("👀 Watch");
            watchBtn.setStyle("-fx-background-color: #1976D2; -fx-text-fill: white; -fx-font-size: 11px; -fx-background-radius: 8;");
            watchBtn.setOnAction(e -> {
                Controller.Navigation.goToLiveAuction(auctionId, Event.AdminSession.getAdminId(),"ADMIN","ADMIN_DASHBOARD");
            });

            rightSide.getChildren().addAll(statusBadge, stopBtn, watchBtn);
        } else {
            statusBadge.setStyle("-fx-background-color: #F5F5F5; -fx-text-fill: #757575; -fx-background-radius: 20;");
            
            if (winnerConfirmed == 0 && winnerId != null && !winnerId.equals("N/A")) {
                Button confirmBtn = new Button("🏆 Confirm Winner");
                confirmBtn.setStyle("-fx-background-color: #7B1FA2; -fx-text-fill: white; -fx-font-size: 11px; -fx-background-radius: 8;");
                confirmBtn.setOnAction(e -> { if(onConfirmWinner != null) onConfirmWinner.accept(auctionId); });
                rightSide.getChildren().addAll(statusBadge, confirmBtn);
            } else {
                rightSide.getChildren().add(statusBadge);
            }
        }

        card.getChildren().addAll(imgBox, info, rightSide);
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

    public void onClose(Consumer<String> action) {
        this.onClose = action;
    }

    public void onApprove(Consumer<String[]> action) {
        this.onApprove = action;
    }

    public void onReject(Consumer<String> action) {
        this.onReject = action;
    }

    public void onConfirmWinner(Consumer<String> action) {
        this.onConfirmWinner = action;
    }
}