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

    private FlowPane pendingGrid = new FlowPane();
    private FlowPane approvedGrid = new FlowPane();
    private FlowPane rejectedGrid = new FlowPane();
    private FlowPane deletedGrid = new FlowPane();
    private Consumer<String> onApprove;
    private Consumer<String> onReject;
    private Consumer<String> onPermanentDelete;
    
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

        countLabel.setStyle(
            "-fx-text-fill: #E65100; -fx-font-size: 12px; -fx-font-weight: bold;" +
            "-fx-background-color: #FFF3E0; -fx-background-radius: 12;" +
            "-fx-padding: 4 12;"
        );

        headerRow.getChildren().addAll(title, spacer, countLabel);

        Label subtitle = new Label("Review and approve or reject car listings");
        subtitle.setStyle("-fx-text-fill: #888; -fx-font-size: 13px;");

        VBox header = new VBox(4, headerRow, subtitle);

        // ===== PENDING SECTION =====
        Label pendingTitle = new Label("⏳ Awaiting Review");
        pendingTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        pendingTitle.setTextFill(Color.web("#E65100"));
        pendingTitle.setStyle("-fx-text-fill: #E65100; -fx-font-weight: bold;");
        pendingTitle.setPadding(new Insets(5, 0, 0, 0));

        pendingGrid.setHgap(18);
        pendingGrid.setVgap(18);
        pendingGrid.setPadding(new Insets(8, 0, 0, 0));
        
        // ===== APPROVED SECTION =====
        Label approvedTitle = new Label("✅ Approved Cars");
        approvedTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        approvedTitle.setTextFill(Color.web("#2E7D32"));
        approvedTitle.setStyle("-fx-text-fill: #2E7D32; -fx-font-weight: bold;");
        approvedTitle.setPadding(new Insets(10, 0, 0, 0));

        approvedGrid.setHgap(18);
        approvedGrid.setVgap(18);
        approvedGrid.setPadding(new Insets(8, 0, 0, 0));
        
        // ===== REJECTED SECTION =====
        Label rejectedTitle = new Label("❌ Rejected Cars");
        rejectedTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        rejectedTitle.setTextFill(Color.web("#C62828"));
        rejectedTitle.setStyle("-fx-text-fill: #C62828; -fx-font-weight: bold;");
        rejectedTitle.setPadding(new Insets(10, 0, 0, 0));

        rejectedGrid.setHgap(18);
        rejectedGrid.setVgap(18);
        rejectedGrid.setPadding(new Insets(8, 0, 0, 0));

        // ===== DELETED BY SELLER SECTION =====
        Label deletedTitle = new Label("🗑️ Cancelled by Seller");
        deletedTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        deletedTitle.setTextFill(Color.web("#757575"));
        deletedTitle.setStyle("-fx-text-fill: #757575; -fx-font-weight: bold;");
        deletedTitle.setPadding(new Insets(10, 0, 0, 0));

        deletedGrid.setHgap(18);
        deletedGrid.setVgap(18);
        deletedGrid.setPadding(new Insets(8, 0, 0, 0));

        VBox content = new VBox(15);
        content.getChildren().addAll(
            pendingTitle, pendingGrid, new Separator(),
            approvedTitle, approvedGrid, new Separator(),
            rejectedTitle, rejectedGrid, new Separator(),
            deletedTitle, deletedGrid
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

        pendingGrid.getChildren().clear();
        deletedGrid.getChildren().clear();

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
            pendingGrid.getChildren().add(emptyState);
            return;
        }

        int pendingCount = 0;
        int deletedCount = 0;

        for (String row : res.split("\n")) {
            if (row.trim().isEmpty()) continue;

            String[] d = row.split("\\|");
            if (d.length < 7) continue;

            // d[0]=carId, d[1]=sellerId, d[2]=title, d[3]=brand, d[4]=model,
            // d[5]=year, d[6]=price, d[7]=description, d[8]=image, d[9]=createdAt
            // Note: status is included in the query now, we need to check it
            // The getPendingCars query returns status column — let's check d[10] if present
            // Actually the query includes status in the SELECT but the string builder
            // doesn't append it. Let me check... 
            // Looking at CarDAO.getPendingCars: it selects status but doesn't include it in sb.
            // We need to add it. For now, let's check the count of fields.
            
            // Since getPendingCars now returns PENDING and DELETED_BY_SELLER,
            // we need status info. Let me check what getPendingCars returns...
            // It returns: car_id|seller_id|title|brand|model|year|price|desc|image|created_at
            // We need to differentiate. Let me add status to the response.
            // For now, use a workaround - check via a separate field.

            // After the CarDAO fix below, d will have status info
            // But currently, getPendingCars doesn't include status in the output.
            // We'll handle this after updating CarDAO.

            pendingGrid.getChildren().add(createCard(d, "PENDING"));
            pendingCount++;
        }
        
        countLabel.setText(pendingCount + " pending, " + deletedCount + " deleted");
    }

    public void renderWithStatus(String res) {
        pendingGrid.getChildren().clear();
        approvedGrid.getChildren().clear();
        rejectedGrid.getChildren().clear();
        deletedGrid.getChildren().clear();

        if (res == null || res.trim().isEmpty()) {
            countLabel.setText("0 pending");
            Label empty = new Label("No pending cars to review ✅");
            empty.setStyle("-fx-text-fill: #999; -fx-font-size: 14px; -fx-padding: 30;");
            pendingGrid.getChildren().add(empty);
            return;
        }

        int pendingCount = 0;
        int deletedCount = 0;

        for (String row : res.split("\n")) {
            if (row.trim().isEmpty()) continue;

            String[] d = row.split("\\|");
            if (d.length < 7) continue;

            // d[0]=carId, d[1]=sellerId, d[2]=title, d[3]=brand, d[4]=model,
            // d[5]=year, d[6]=price, d[7]=description, d[8]=image, d[9]=createdAt, d[10]=status
            String status = d.length > 10 ? d[10] : "PENDING";

            if ("DELETED_BY_SELLER".equals(status)) {
                deletedGrid.getChildren().add(createCard(d, status));
                deletedCount++;
            } else if ("APPROVED".equals(status)) {
                approvedGrid.getChildren().add(createCard(d, status));
            } else if ("REJECTED".equals(status)) {
                rejectedGrid.getChildren().add(createCard(d, status));
            } else {
                pendingGrid.getChildren().add(createCard(d, status));
                pendingCount++;
            }
        }

        if (pendingCount == 0) {
            Label empty = new Label("No pending cars ✅");
            empty.setStyle("-fx-text-fill: #999; -fx-font-size: 13px; -fx-padding: 15;");
            pendingGrid.getChildren().add(empty);
        }
        if (deletedCount == 0) {
            Label empty = new Label("No cancelled cars");
            empty.setStyle("-fx-text-fill: #999; -fx-font-size: 13px; -fx-padding: 15;");
            deletedGrid.getChildren().add(empty);
        }

        countLabel.setText(pendingCount + " pending, " + deletedCount + " cancelled");
    }

    private VBox createCard(String[] d, String status) {

        VBox card = new VBox(0);
        card.setPrefWidth(300);
        card.setMaxWidth(300);
        
        boolean isDeleted = "DELETED_BY_SELLER".equals(status);
        String borderColor = "#FFF3E0";
        if (isDeleted) borderColor = "#E0E0E0";
        else if ("APPROVED".equals(status)) borderColor = "#C8E6C9";
        else if ("REJECTED".equals(status)) borderColor = "#FFCDD2";
        
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 16;" +
            "-fx-border-color: " + borderColor + "; -fx-border-radius: 16; -fx-border-width: 2;"
        );

        if (isDeleted) {
            card.setOpacity(0.8);
        }

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

        // Badge
        Label badge;
        if (isDeleted) {
            badge = new Label("🗑️ CANCELLED");
            badge.setStyle(
                "-fx-background-color: #F5F5F5; -fx-text-fill: #757575;" +
                "-fx-background-radius: 12; -fx-font-size: 10px; -fx-font-weight: bold;" +
                "-fx-padding: 4 10;"
            );
        } else if ("APPROVED".equals(status)) {
            badge = new Label("✅ APPROVED");
            badge.setStyle(
                "-fx-background-color: #E8F5E9; -fx-text-fill: #2E7D32;" +
                "-fx-background-radius: 12; -fx-font-size: 10px; -fx-font-weight: bold;" +
                "-fx-padding: 4 10;"
            );
        } else if ("REJECTED".equals(status)) {
            badge = new Label("❌ REJECTED");
            badge.setStyle(
                "-fx-background-color: #FFEBEE; -fx-text-fill: #C62828;" +
                "-fx-background-radius: 12; -fx-font-size: 10px; -fx-font-weight: bold;" +
                "-fx-padding: 4 10;"
            );
        } else {
            badge = new Label("⏳ PENDING REVIEW");
            badge.setStyle(
                "-fx-background-color: #FFF3E0; -fx-text-fill: #E65100;" +
                "-fx-background-radius: 12; -fx-font-size: 10px; -fx-font-weight: bold;" +
                "-fx-padding: 4 10;"
            );
        }

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

        Label priceLabel = new Label("💰 " + formatPrice(d[6]) + " MMK");
        priceLabel.setStyle("-fx-text-fill: #1976D2; -fx-font-weight: bold; -fx-font-size: 14px;");

        Label sellerLabel = new Label("👤 Seller: " + d[1]);
        sellerLabel.setStyle("-fx-text-fill: #999; -fx-font-size: 11px;");

        // Description
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

        if (isDeleted) {
            // Only show permanent delete button for cancelled cars
            Button permDeleteBtn = new Button("🗑️ Permanently Delete");
            permDeleteBtn.setStyle(
                "-fx-background-color: #B71C1C;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 8 18;" +
                "-fx-cursor: hand;"
            );
            permDeleteBtn.setOnAction(e -> {
                if (onPermanentDelete != null) {
                    onPermanentDelete.accept(d[0]);
                }
            });
            actions.getChildren().add(permDeleteBtn);
        } else if ("PENDING".equals(status)) {
            // Normal approve/reject buttons for pending cars
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
        }

        details.getChildren().addAll(name, info, priceLabel, sellerLabel, descLabel, dateLabel, actions);
        card.getChildren().addAll(imgStack, details);

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

    public void onApprove(Consumer<String> action) {
        this.onApprove = action;
    }

    public void onReject(Consumer<String> action) {
        this.onReject = action;
    }

    public void onPermanentDelete(Consumer<String> action) {
        this.onPermanentDelete = action;
    }
}