package UI;

import Client.ClientManager;
import Service.CarService;
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

public class MyCarsPanel {

    private String userId;
    private Consumer<String> onDelete;
    private Consumer<String> onUpdate;
    private FlowPane cards = new FlowPane();
    private Label countLabel = new Label();

    public MyCarsPanel(String userId) {
        this.userId = userId;
    }

    public VBox getView() {

        VBox root = new VBox(20);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: transparent;");

        // ===== HEADER =====
        HBox headerRow = new HBox(15);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("🚗 My Cars");
        title.setFont(Font.font("System", FontWeight.BOLD, 26));
        title.setTextFill(Color.web("#1a1a2e"));

        Region hSpacer = new Region();
        HBox.setHgrow(hSpacer, Priority.ALWAYS);

        countLabel.setStyle(
            "-fx-text-fill: #888; -fx-font-size: 13px;" +
            "-fx-background-color: #e8e8e8; -fx-background-radius: 12;" +
            "-fx-padding: 4 12;"
        );

        headerRow.getChildren().addAll(title, hSpacer, countLabel);

        Label subtitle = new Label("All your listed vehicles — Approved, Pending, and Rejected");
        subtitle.setStyle("-fx-text-fill: #888; -fx-font-size: 13px;");

        VBox header = new VBox(4, headerRow, subtitle);

        cards.setHgap(18);
        cards.setVgap(18);
        cards.setPadding(new Insets(8, 0, 0, 0));

        ScrollPane scroll = new ScrollPane(cards);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        root.getChildren().addAll(header, scroll);

        loadCars();

        return root;
    }

    public void loadCars() {
        cards.getChildren().clear();

        String res;
        try {
            res = ClientManager.send("GET_MY_CARS:" + userId);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        if (res == null || res.startsWith("ERROR") || res.trim().isEmpty()) {
            VBox emptyState = new VBox(10);
            emptyState.setAlignment(Pos.CENTER);
            emptyState.setPadding(new Insets(60));

            Label emptyIcon = new Label("🚗");
            emptyIcon.setFont(Font.font(48));

            Label empty = new Label("No cars added yet");
            empty.setStyle("-fx-text-fill: #999; -fx-font-size: 16px;");

            Label hint = new Label("Use 'Add Car' to list your first vehicle!");
            hint.setStyle("-fx-text-fill: #bbb; -fx-font-size: 13px;");

            emptyState.getChildren().addAll(emptyIcon, empty, hint);
            countLabel.setText("0 cars");
            cards.getChildren().add(emptyState);
            return;
        }

        int count = 0;
        for (String row : res.split("\n")) {
            row = row.trim();
            if (row.isEmpty()) continue;

            String[] d = row.split("\\|");
            if (d.length < 7) continue;

            String carId = d[0];
            String titleCar = d[1];
            String brand = d[2];
            String model = d[3];
            String imageUrl = d[4];
            String status = d[5];
            String createdAt = d[6];
            String year = d.length > 7 ? d[7] : "";
            String price = d.length > 8 ? d[8] : "";

            cards.getChildren().add(
                createCard(carId, titleCar, brand, model, imageUrl, status, createdAt, year, price)
            );
            count++;
        }

        countLabel.setText(count + " car" + (count != 1 ? "s" : ""));
    }

    private VBox createCard(String carId, String titleCar, String brand, String model,
                            String imageUrl, String status, String createdAt,
                            String year, String price) {

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

        // Hover
        card.setOnMouseEntered(e -> card.setStyle(
            "-fx-background-color: white; -fx-background-radius: 16;" +
            "-fx-border-radius: 16; -fx-scale-x: 1.02; -fx-scale-y: 1.02;"
        ));
        card.setOnMouseExited(e -> card.setStyle(
            "-fx-background-color: white; -fx-background-radius: 16; -fx-border-radius: 16;"
        ));

        // ===== IMAGE =====
        ImageView img = new ImageView();
        img.setFitWidth(280);
        img.setFitHeight(160);
        img.setPreserveRatio(false);

        try {
            if (imageUrl != null && !imageUrl.isEmpty() && !imageUrl.equals("null")) {
                img.setImage(new Image(imageUrl, 280, 160, false, true, true));
            }
        } catch (Exception e) {
            // no image
        }

        StackPane imgContainer = new StackPane(img);
        imgContainer.setStyle(
            "-fx-background-radius: 16 16 0 0;" +
            "-fx-background-color: #e8e8e8;"
        );
        imgContainer.setMinHeight(160);
        imgContainer.setMaxHeight(160);

        // Status badge on image
        Label statusLabel = new Label(status);
        statusLabel.setPadding(new Insets(4, 12, 4, 12));
        statusLabel.setFont(Font.font("System", FontWeight.BOLD, 11));

        switch (status) {
            case "APPROVED":
                statusLabel.setStyle(
                    "-fx-background-color: #E8F5E9; -fx-text-fill: #2E7D32;" +
                    "-fx-background-radius: 20;"
                );
                statusLabel.setText("✅ APPROVED");
                break;
            case "PENDING":
                statusLabel.setStyle(
                    "-fx-background-color: #FFF3E0; -fx-text-fill: #E65100;" +
                    "-fx-background-radius: 20;"
                );
                statusLabel.setText("⏳ PENDING");
                break;
            case "REJECTED":
                statusLabel.setStyle(
                    "-fx-background-color: #FFEBEE; -fx-text-fill: #C62828;" +
                    "-fx-background-radius: 20;"
                );
                statusLabel.setText("❌ REJECTED");
                break;
        }

        StackPane imageStack = new StackPane(imgContainer, statusLabel);
        StackPane.setAlignment(statusLabel, Pos.TOP_RIGHT);
        StackPane.setMargin(statusLabel, new Insets(10, 10, 0, 0));

        // ===== INFO =====
        VBox info = new VBox(5);
        info.setPadding(new Insets(14, 16, 16, 16));

        Label name = new Label(titleCar);
        name.setFont(Font.font("System", FontWeight.BOLD, 16));
        name.setTextFill(Color.web("#111827"));

        Label details = new Label(brand + " • " + model + (year.isEmpty() ? "" : " • " + year));
        details.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 12px;");

        info.getChildren().addAll(name, details);

        if (!price.isEmpty()) {
            String displayPrice = price;
            try {
                double p = Double.parseDouble(price);
                displayPrice = String.format("%,.0f", p);
            } catch (Exception e) {}
            
            Label priceLabel = new Label("💰 " + displayPrice + " MMK");
            priceLabel.setStyle("-fx-text-fill: #1976D2; -fx-font-weight: bold; -fx-font-size: 13px;");
            info.getChildren().add(priceLabel);
        }

        Label dateLabel = new Label("📅 " + createdAt);
        dateLabel.setStyle("-fx-text-fill: #aaa; -fx-font-size: 11px;");
        info.getChildren().add(dateLabel);

        // ===== PENDING ACTION BUTTONS =====
        if ("PENDING".equals(status)) {
            HBox actions = new HBox(8);
            actions.setPadding(new Insets(8, 0, 0, 0));

            Button updateBtn = new Button("✏️ Update");
            updateBtn.setStyle(
                "-fx-background-color: #1976D2;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 6 14;" +
                "-fx-cursor: hand;"
            );
            updateBtn.setOnAction(e -> {
                if (onUpdate != null) onUpdate.accept(carId);
            });

            Button deleteBtn = new Button("🗑️ Delete");
            deleteBtn.setStyle(
                "-fx-background-color: #D32F2F;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 6 14;" +
                "-fx-cursor: hand;"
            );
            deleteBtn.setOnAction(e -> {
                // Confirmation dialog
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Delete Car");
                alert.setHeaderText("Are you sure you want to delete this car?");
                alert.setContentText("This will remove the car from your listings.");
                alert.showAndWait().ifPresent(result -> {
                    if (result == ButtonType.OK) {
                        if (onDelete != null) onDelete.accept(carId);
                    }
                });
            });

            actions.getChildren().addAll(updateBtn, deleteBtn);
            info.getChildren().add(actions);
        }

        card.getChildren().addAll(imageStack, info);

        return card;
    }

    public void onDelete(Consumer<String> action) {
        this.onDelete = action;
    }

    public void onUpdate(Consumer<String> action) {
        this.onUpdate = action;
    }
}