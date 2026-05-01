package UI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class LiveAuctionPanel {

    public Button backBtn = new Button("←");
    public Label timerLabel = new Label("00:00");
    public Button bidBtn = new Button("Place Bid");
    public TextField bidAmount = new TextField();
    public VBox bidControls = new VBox(10);
    public Label winnerOverlay = new Label();
    
    public Label carTitle = new Label();
    public Label carBrandModel = new Label();
    public Label currentBidLabel = new Label("0");
    public ImageView carImage = new ImageView();
    public VBox bidHistory = new VBox(5);
    private Label msg = new Label();

    private String auctionId;
    private String userId;
    private String role;

    public LiveAuctionPanel(String auctionId, String userId, String role) {
        this.auctionId = auctionId;
        this.userId = userId;
        this.role = role;
    }

    public Parent getView() {

        VBox root = new VBox(20);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: #f3f4f6;");

        // ===== HEADER =====
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        backBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-font-size: 18px;");

        VBox titleBox = new VBox(2);
        Label title = new Label("🔴 Live Auction");
        title.setFont(Font.font("System", FontWeight.BOLD, 22));
        titleBox.getChildren().addAll(title);
        
        Region s1 = new Region(); HBox.setHgrow(s1, Priority.ALWAYS);
        
        timerLabel.setFont(Font.font("System", FontWeight.BOLD, 36));
        timerLabel.setTextFill(Color.web("#D32F2F"));
        timerLabel.setStyle("-fx-background-color: #FFEBEE; -fx-padding: 5 20; -fx-background-radius: 10;");

        header.getChildren().addAll(backBtn, titleBox, s1, timerLabel);

        // ===== MAIN CONTENT =====
        HBox mainContent = new HBox(30);
        VBox.setVgrow(mainContent, Priority.ALWAYS);

        // LEFT SIDE: CAR DETAILS
        VBox leftSide = new VBox(20);
        HBox.setHgrow(leftSide, Priority.ALWAYS);

        carImage.setFitWidth(500);
        carImage.setFitHeight(300);
        carImage.setPreserveRatio(true);
        
        StackPane imgFrame = new StackPane(carImage);
        imgFrame.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-padding: 10;");
        imgFrame.setEffect(new DropShadow(10, Color.rgb(0,0,0,0.1)));

        VBox infoBox = new VBox(10);
        infoBox.setPadding(new Insets(20));
        infoBox.setStyle("-fx-background-color: white; -fx-background-radius: 16;");
        
        carTitle.setFont(Font.font("System", FontWeight.BOLD, 24));
        carBrandModel.setFont(Font.font("System", 16));
        carBrandModel.setTextFill(Color.web("#666"));
        
        infoBox.getChildren().addAll(carTitle, carBrandModel);

        leftSide.getChildren().addAll(imgFrame, infoBox);

        // RIGHT SIDE: BIDDING
        VBox rightSide = new VBox(20);
        rightSide.setPrefWidth(350);

        // Current Bid Card
        VBox priceCard = new VBox(10);
        priceCard.setPadding(new Insets(20));
        priceCard.setAlignment(Pos.CENTER);
        priceCard.setStyle("-fx-background-color: #1a1a2e; -fx-background-radius: 16;");
        
        Label currentBidTitle = new Label("CURRENT HIGHEST BID");
        currentBidTitle.setTextFill(Color.web("#888"));
        currentBidTitle.setFont(Font.font("System", FontWeight.BOLD, 12));
        
        currentBidLabel.setFont(Font.font("System", FontWeight.BOLD, 32));
        currentBidLabel.setTextFill(Color.WHITE);
        
        Label mmk = new Label("MMK");
        mmk.setTextFill(Color.web("#888"));
        
        priceCard.getChildren().addAll(currentBidTitle, currentBidLabel, mmk);

        // Bidding Controls
        bidControls.setPadding(new Insets(20));
        bidControls.setStyle("-fx-background-color: white; -fx-background-radius: 16;");
        bidControls.setEffect(new DropShadow(10, Color.rgb(0,0,0,0.05)));

        if (!"BUYER".equals(role)) {
            Label watchOnly = new Label("👀 Watch Only Mode");
            watchOnly.setStyle("-fx-background-color: #E3F2FD; -fx-text-fill: #1565C0; -fx-font-weight: bold; -fx-padding: 15; -fx-background-radius: 10;");
            watchOnly.setMaxWidth(Double.MAX_VALUE);
            watchOnly.setAlignment(Pos.CENTER);
            bidControls.getChildren().add(watchOnly);
        } else {
            Label bidTitle = new Label("Place Your Bid");
            bidTitle.setFont(Font.font("System", FontWeight.BOLD, 16));

            bidAmount.setPromptText("Enter amount (MMK)");
            bidAmount.setStyle("-fx-padding: 12; -fx-background-radius: 10; -fx-border-color: #ddd;");

            bidBtn.setStyle("-fx-background-color: #2563EB; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 14; -fx-background-radius: 10; -fx-cursor: hand;");
            bidBtn.setMaxWidth(Double.MAX_VALUE);

            bidControls.getChildren().addAll(bidTitle, bidAmount, bidBtn);
        }

        // Bid History
        VBox historyBox = new VBox(10);
        historyBox.setPadding(new Insets(20));
        historyBox.setStyle("-fx-background-color: white; -fx-background-radius: 16;");
        VBox.setVgrow(historyBox, Priority.ALWAYS);
        
        Label historyTitle = new Label("Bid History");
        historyTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        ScrollPane historyScroll = new ScrollPane(bidHistory);
        historyScroll.setFitToWidth(true);
        historyScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        
        historyBox.getChildren().addAll(historyTitle, historyScroll);

        rightSide.getChildren().addAll(priceCard, bidControls, historyBox);

        mainContent.getChildren().addAll(leftSide, rightSide);

        root.getChildren().addAll(header, mainContent);

        // Winner Overlay
        winnerOverlay.setVisible(false);
        winnerOverlay.setStyle("-fx-background-color: rgba(0,0,0,0.85); -fx-text-fill: #FFD700; -fx-font-size: 28px; -fx-font-weight: bold; -fx-padding: 50; -fx-background-radius: 20; -fx-text-alignment: center;");
        winnerOverlay.setAlignment(Pos.CENTER);
        winnerOverlay.setWrapText(true);

        StackPane rootWrap = new StackPane(root, winnerOverlay);
        return rootWrap;
    }

    public void renderAuction(String data) {
        String[] d = data.split("\\|");
        if (d.length < 10) return;
        
        // d[0]=auctionId, d[1]=carId, d[2]=title, d[3]=brand, d[4]=model,
        // d[5]=currentBid, d[6]=image, d[7]=status, d[8]=year, d[9]=priceStart, d[10]=winnerId
        
        carTitle.setText(d[2]);
        carBrandModel.setText(d[3] + " " + d[4] + " (" + d[8] + ") • Original Price: " + formatPrice(d[9]) + " MMK");
        currentBidLabel.setText(formatPrice(d[5]));
//        try {
//            if (d[6] != null && !d[6].equals("null")) {
//                carImage.setImage(new Image(d[6], 500, 300, false, true, true));
//            }
//        } catch (Exception e) {}
        try {
            String url = cleanImageUrl(d[6]);

            if (url == null || url.trim().isEmpty() || url.equals("null")) {
                return;
            }

            // FIX COMMON DATA ISSUES
            url = url.replace("https //", "https://");
            url = url.replace("http //", "http://");
            url = url.trim();

            // FINAL VALIDATION (STRICT)
            if (!url.contains("://")) {
                url = "file:" + url;
            }

            carImage.setImage(new Image(url, 500, 300, false, true, true));

        } catch (Exception e) {
            System.out.println("Image skipped: " + d[6]);
        }
    }

    public void updateBids(String res) {
        bidHistory.getChildren().clear();
        if (res == null || res.isEmpty()) return;
        for (String row : res.split("\n")) {
            String[] d = row.split("\\|");
            if (d.length < 3) continue;
            HBox bidRow = new HBox(10);
            
            // d[0]=buyer_id, d[1]=bid_amount, d[2]=bid_time
            Label b = new Label("👤 " + d[0] + " bid " + formatPrice(d[1]) + " MMK (" + d[2].substring(0, Math.min(d[2].length(), 16)) + ")");
            b.setStyle("-fx-font-size: 12px;");
            bidRow.getChildren().add(b);
            bidHistory.getChildren().add(bidRow);
        }
    }
    
    private String formatPrice(String priceStr) {
        try {
            double price = Double.parseDouble(priceStr);
            return String.format("%,.0f", price);
        } catch (Exception e) {
            return priceStr;
        }
    }
    
    private String cleanImageUrl(String url) {
        try {
            if (url == null) return null;

            if (url.contains("google.com/url")) {
                String decoded = java.net.URLDecoder.decode(url, "UTF-8");

                String[] parts = decoded.split("url=");
                if (parts.length < 2) return null;

                String real = parts[1];

                int end = real.indexOf("&");
                if (end != -1) {
                    real = real.substring(0, end);
                }

                return real;
            }

            return url;

        } catch (Exception e) {
            return url;
        }
    }
    
    public void showMessage(String text) {
        msg.setText(text);
        msg.setTextFill(Color.web("#D32F2F"));
    }

    public void showSuccess(String text) {
        msg.setText(text);
        msg.setTextFill(Color.web("#4CAF50"));
    }

    public void showWinner(String winnerName, double amount) {
        winnerOverlay.setText("🏆 WINNER 🏆\n\n" + winnerName + "\n\nWon at " + formatPrice(String.valueOf(amount)) + " MMK");
        winnerOverlay.setVisible(true);
        bidControls.setDisable(true);
    }
}