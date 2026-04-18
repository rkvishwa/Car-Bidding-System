package UI;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.image.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.util.function.BiConsumer;

public class CreateAuctionPanel {

    private VBox root = new VBox(20);
    private FlowPane grid = new FlowPane();

    private BiConsumer<String, String> onCreateAuction;
    private Label statusMsg = new Label();

    public CreateAuctionPanel(String sellerId) {

        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: #f0f2f5;");

        // ===== HEADER =====
        Label title = new Label("➕ Create Auction");
        title.setFont(Font.font("System", FontWeight.BOLD, 26));
        title.setTextFill(Color.web("#1a1a2e"));

        Label subtitle = new Label("Select an approved car to start a live auction");
        subtitle.setStyle("-fx-text-fill: #888; -fx-font-size: 13px;");

        VBox header = new VBox(4, title, subtitle);

        statusMsg.setStyle("-fx-font-size: 13px;");

        grid.setHgap(18);
        grid.setVgap(18);
        grid.setPadding(new Insets(10, 0, 0, 0));

        ScrollPane scroll = new ScrollPane(grid);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        root.getChildren().addAll(header, statusMsg, scroll);
    }

    public VBox getView() {
        return root;
    }

    public void render(String res) {

        grid.getChildren().clear();

        if (res == null || res.trim().isEmpty()) {
            VBox emptyState = new VBox(10);
            emptyState.setAlignment(Pos.CENTER);
            emptyState.setPadding(new Insets(60));

            Label emptyIcon = new Label("🔨");
            emptyIcon.setFont(Font.font(48));

            Label empty = new Label("No approved cars available");
            empty.setStyle("-fx-text-fill: #999; -fx-font-size: 16px;");

            Label hint = new Label("Add cars and get them approved by admin first!");
            hint.setStyle("-fx-text-fill: #bbb; -fx-font-size: 13px;");
            hint.setWrapText(true);

            emptyState.getChildren().addAll(emptyIcon, empty, hint);
            grid.getChildren().add(emptyState);
            return;
        }

        for (String row : res.split("\n")) {

            if (row == null || row.trim().isEmpty()) continue;

            if (!row.contains("|")) {
                System.out.println("Skipping invalid row: " + row);
                continue;
            }

            String[] d = row.split("\\|");

            if (d.length < 5) {
                System.out.println("Invalid format row: " + row);
                continue;
            }

            // d[0]=carId, d[1]=title, d[2]=brand, d[3]=model, d[4]=image,
            // d[5]=year (optional), d[6]=price (optional)
            grid.getChildren().add(createCard(d));
        }
    }

    private VBox createCard(String[] d) {

        VBox card = new VBox(0);
        card.setPrefWidth(280);
        card.setMaxWidth(280);
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 16;"
        );

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.04));
        shadow.setRadius(15);
        shadow.setOffsetY(5);
        card.setEffect(shadow);

        // Hover
        card.setOnMouseEntered(e -> card.setStyle(
            "-fx-background-color: white; -fx-background-radius: 16;" +
            "-fx-scale-x: 1.02; -fx-scale-y: 1.02;"
        ));
        card.setOnMouseExited(e -> card.setStyle(
            "-fx-background-color: white; -fx-background-radius: 16;"
        ));

        // ===== IMAGE =====
        ImageView img = new ImageView();
        img.setFitWidth(280);
        img.setFitHeight(160);
        img.setPreserveRatio(false);

        try {
            if (d[4] != null && !d[4].isEmpty() && !d[4].equals("null")) {
                img.setImage(new Image(d[4], 280, 160, false, true, true));
            }
        } catch (Exception e) {
            // no image
        }

        StackPane imgContainer = new StackPane(img);
        imgContainer.setStyle("-fx-background-color: #e8e8e8; -fx-background-radius: 16 16 0 0;");
        imgContainer.setMinHeight(160);

        // Approved badge
        Label badge = new Label("✅ Approved");
        badge.setStyle(
            "-fx-background-color: #4CAF50; -fx-text-fill: white;" +
            "-fx-background-radius: 12; -fx-font-size: 10px; -fx-font-weight: bold;" +
            "-fx-padding: 4 10;"
        );
        StackPane imgStack = new StackPane(imgContainer, badge);
        StackPane.setAlignment(badge, Pos.TOP_RIGHT);
        StackPane.setMargin(badge, new Insets(10, 10, 0, 0));

        // ===== DETAILS =====
        VBox details = new VBox(8);
        details.setPadding(new Insets(14, 16, 16, 16));

        Label name = new Label(d[1]);
        name.setFont(Font.font("System", FontWeight.BOLD, 16));
        name.setTextFill(Color.web("#1a1a2e"));

        Label info = new Label(d[2] + " • " + d[3]);
        info.setStyle("-fx-text-fill: #888; -fx-font-size: 12px;");

        details.getChildren().addAll(name, info);

        // Additional info if available
        if (d.length > 5) {
            Label yearPrice = new Label(
                (d.length > 5 ? "Year: " + d[5] : "") +
                (d.length > 6 ? "  |  Price: " + d[6] + " MMK" : "")
            );
            yearPrice.setStyle("-fx-text-fill: #666; -fx-font-size: 11px;");
            details.getChildren().add(yearPrice);
        }

        // ===== BID INPUT =====
        TextField bidField = new TextField();
        bidField.setPromptText("Minimum Bid (MMK)");
        bidField.setStyle(
            "-fx-background-radius: 8;" +
            "-fx-border-radius: 8;" +
            "-fx-border-color: #ddd;" +
            "-fx-padding: 10;" +
            "-fx-font-size: 12px;"
        );

        Button btn = new Button("🔨 Start Auction");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle(
            "-fx-background-color: #4CAF50;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 10;" +
            "-fx-cursor: hand;"
        );

        Label cardMsg = new Label();
        cardMsg.setStyle("-fx-font-size: 11px;");

        btn.setOnAction(e -> {
            String bidText = bidField.getText().trim();
            if (bidText.isEmpty()) {
                cardMsg.setText("⚠ Please enter minimum bid");
                cardMsg.setTextFill(Color.web("#D32F2F"));
                return;
            }
            try {
                Double.parseDouble(bidText);
            } catch (NumberFormatException ex) {
                cardMsg.setText("⚠ Invalid bid amount");
                cardMsg.setTextFill(Color.web("#D32F2F"));
                return;
            }

            if (onCreateAuction != null) {
                onCreateAuction.accept(d[0], bidText);
                card.setDisable(true);
                card.setOpacity(0.6);
                cardMsg.setText("✅ Auction Created!");
                cardMsg.setTextFill(Color.web("#4CAF50"));
            }
        });

        details.getChildren().addAll(bidField, btn, cardMsg);
        card.getChildren().addAll(imgStack, details);

        return card;
    }

    public void onCreateAuction(BiConsumer<String, String> action) {
        this.onCreateAuction = action;
    }

    public void renderSuccess(String carId) {
        statusMsg.setText("✅ Auction created successfully!");
        statusMsg.setTextFill(Color.web("#4CAF50"));
    }

    public void showError(String msg) {
        statusMsg.setText("⚠ " + msg);
        statusMsg.setTextFill(Color.web("#D32F2F"));
    }
}