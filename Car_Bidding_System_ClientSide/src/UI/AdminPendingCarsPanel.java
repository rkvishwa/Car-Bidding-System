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

public class AdminPendingCarsPanel {

    private FlowPane grid = new FlowPane();
    private Consumer<String> onApprove;
    private Consumer<String> onReject;
    
    private Label countLabel = new Label();

    public VBox getView() {

        VBox root = new VBox(20);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: transparent;");

        // ===== HEADER =====
        HBox headerRow = new HBox(15);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("🛠 Pending Cars");
        title.setFont(Font.font("System", FontWeight.BOLD, 26));
        title.setTextFill(Color.web("#1a1a2e"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

//        Label countLabel = new Label();
//        countLabel.setId("pendingCount");
        countLabel.setStyle(
            "-fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold;" +
            "-fx-background-color: #FF9800; -fx-background-radius: 12;" +
            "-fx-padding: 4 12;"
        );

        headerRow.getChildren().addAll(title, spacer, countLabel);

        Label subtitle = new Label("Review and approve or reject car listings");
        subtitle.setStyle("-fx-text-fill: #888; -fx-font-size: 13px;");

        VBox header = new VBox(4, headerRow, subtitle);

        grid.setHgap(18);
        grid.setVgap(18);
        grid.setPadding(new Insets(8, 0, 0, 0));

        ScrollPane scroll = new ScrollPane(grid);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        root.getChildren().addAll(header, scroll);

        return root;
    }

    public void render(String res) {

        grid.getChildren().clear();

        // Update count label
//        Label countLabel = (Label) grid.getScene() != null ? null : null;

        if (res == null || res.trim().isEmpty()) {
        	
        	countLabel.setText("0 pending");
            VBox emptyState = new VBox(10);
            emptyState.setAlignment(Pos.CENTER);
            emptyState.setPadding(new Insets(60));

            Label emptyIcon = new Label("✅");
            emptyIcon.setFont(Font.font(48));

            Label empty = new Label("No pending cars to review");
            empty.setStyle("-fx-text-fill: #999; -fx-font-size: 16px;");

            Label hint = new Label("All caught up! New listings will appear here.");
            hint.setStyle("-fx-text-fill: #bbb; -fx-font-size: 13px;");

            emptyState.getChildren().addAll(emptyIcon, empty, hint);
            grid.getChildren().add(emptyState);
            return;
        }

        int count = 0;
        for (String row : res.split("\n")) {
            if (row.trim().isEmpty()) continue;

            String[] d = row.split("\\|");
            if (d.length < 7) continue;

            // d[0]=carId, d[1]=sellerId, d[2]=title, d[3]=brand, d[4]=model,
            // d[5]=year, d[6]=price, d[7]=description, d[8]=image, d[9]=createdAt

            grid.getChildren().add(createCard(d));
            count++;
        }
        
        countLabel.setText(count + " pending");
    }

    private VBox createCard(String[] d) {

        VBox card = new VBox(0);
        card.setPrefWidth(300);
        card.setMaxWidth(300);
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 16;" +
            "-fx-border-color: #FFF3E0; -fx-border-radius: 16; -fx-border-width: 2;"
        );

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.04));
        shadow.setRadius(15);
        shadow.setOffsetY(5);
        card.setEffect(shadow);

        // ===== IMAGE =====
        ImageView img = new ImageView();
        img.setFitWidth(300);
        img.setFitHeight(170);
        img.setPreserveRatio(false);

        try {
            if (d.length > 8 && d[8] != null && !d[8].isEmpty() && !d[8].equals("null")) {
                img.setImage(new Image(d[8], 300, 170, false, true, true));
            }
        } catch (Exception e) {
            // no image
        }

        StackPane imgContainer = new StackPane(img);
        imgContainer.setStyle("-fx-background-color: #e8e8e8; -fx-background-radius: 16 16 0 0;");
        imgContainer.setMinHeight(170);

        // Pending badge
        Label badge = new Label("⏳ PENDING REVIEW");
        badge.setStyle(
            "-fx-background-color: #FF9800; -fx-text-fill: white;" +
            "-fx-background-radius: 12; -fx-font-size: 10px; -fx-font-weight: bold;" +
            "-fx-padding: 4 10;"
        );
        StackPane imgStack = new StackPane(imgContainer, badge);
        StackPane.setAlignment(badge, Pos.TOP_RIGHT);
        StackPane.setMargin(badge, new Insets(10, 10, 0, 0));

        // ===== DETAILS =====
        VBox details = new VBox(6);
        details.setPadding(new Insets(14, 16, 16, 16));

        Label name = new Label(d[2]);
        name.setFont(Font.font("System", FontWeight.BOLD, 16));
        name.setTextFill(Color.web("#111827"));

        Label info = new Label(d[3] + " • " + d[4] + " • " + d[5]);
        info.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 12px;");

        Label priceLabel = new Label("💰 " + d[6] + " MMK");
        priceLabel.setStyle("-fx-text-fill: #1976D2; -fx-font-weight: bold; -fx-font-size: 14px;");

        Label sellerLabel = new Label("👤 Seller: " + d[1]);
        sellerLabel.setStyle("-fx-text-fill: #999; -fx-font-size: 11px;");

        // Description (truncated)
        String descText = d.length > 7 ? d[7] : "No description";
        if (descText.length() > 80) descText = descText.substring(0, 80) + "...";
        Label descLabel = new Label("📝 " + descText);
        descLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 11px;");
        descLabel.setWrapText(true);

        // Date
        String dateStr = d.length > 9 ? d[9] : "";
        Label dateLabel = new Label("📅 " + dateStr);
        dateLabel.setStyle("-fx-text-fill: #aaa; -fx-font-size: 10px;");

        // ===== ACTION BUTTONS =====
        HBox actions = new HBox(10);
        actions.setPadding(new Insets(8, 0, 0, 0));

        Button approveBtn = new Button("✅ Approve");
        approveBtn.setStyle(
            "-fx-background-color: #16A34A;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 8 18;" +
            "-fx-cursor: hand;"
        );
        approveBtn.setOnAction(e -> {
            if (onApprove != null) {
                onApprove.accept(d[0]);
            }
        });

        Button rejectBtn = new Button("❌ Reject");
        rejectBtn.setStyle(
            "-fx-background-color: #D32F2F;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 8 18;" +
            "-fx-cursor: hand;"
        );
        rejectBtn.setOnAction(e -> {
            if (onReject != null) {
                onReject.accept(d[0]);
            }
        });

        actions.getChildren().addAll(approveBtn, rejectBtn);

        details.getChildren().addAll(name, info, priceLabel, sellerLabel, descLabel, dateLabel, actions);
        card.getChildren().addAll(imgStack, details);

        return card;
    }

    public void onApprove(Consumer<String> action) {
        this.onApprove = action;
    }

    public void onReject(Consumer<String> action) {
        this.onReject = action;
    }
}