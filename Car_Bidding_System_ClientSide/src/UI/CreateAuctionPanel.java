package UI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class CreateAuctionPanel {

    public ComboBox<String> carList = new ComboBox<>();
    public Button createBtn = new Button("Submit for Auction");
    public Label message = new Label();

    public VBox getView() {

        VBox root = new VBox(20);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: transparent;");

        // ===== HEADER =====
        Label title = new Label("🚀 Create Auction");
        title.setFont(Font.font("System", FontWeight.BOLD, 26));
        title.setTextFill(Color.web("#1a1a2e"));

        Label subtitle = new Label("Submit your car for admin approval");
        subtitle.setStyle("-fx-text-fill: #888; -fx-font-size: 13px;");

        VBox header = new VBox(4, title, subtitle);

        // ===== FORM CARD =====
        VBox form = new VBox(15);
        form.setPadding(new Insets(30));
        form.setMaxWidth(500);
        form.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 16;"
        );

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.05));
        shadow.setRadius(20);
        shadow.setOffsetY(10);
        form.setEffect(shadow);

        String inputStyle =
            "-fx-background-radius: 10;" +
            "-fx-border-radius: 10;" +
            "-fx-border-color: #E5E7EB;" +
            "-fx-background-color: #F9FAFB;" +
            "-fx-padding: 12 15;" +
            "-fx-font-size: 14px;";

        // ===== CAR SELECT =====
        Label carLabel = new Label("Select Your Car");
        carLabel.setStyle("-fx-text-fill: #374151; -fx-font-weight: bold;");

        carList.setPromptText("Choose a car from your inventory");
        carList.setMaxWidth(Double.MAX_VALUE);
        carList.setStyle(inputStyle);

        // ===== CREATE BUTTON =====
        createBtn.setMaxWidth(Double.MAX_VALUE);
        createBtn.setStyle(
            "-fx-background-color: #2563EB;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 15px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 10;" +
            "-fx-padding: 14 0;" +
            "-fx-cursor: hand;"
        );

        // Hover effect
        createBtn.setOnMouseEntered(e -> createBtn.setStyle(
            "-fx-background-color: #1D4ED8;" +
            "-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 14 0; -fx-cursor: hand;"
        ));
        createBtn.setOnMouseExited(e -> createBtn.setStyle(
            "-fx-background-color: #2563EB;" +
            "-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 14 0; -fx-cursor: hand;"
        ));

        message.setWrapText(true);
        message.setFont(Font.font(13));

        form.getChildren().addAll(
                carLabel, carList,
                new Label(""), // spacer
                createBtn,
                message
        );

        VBox center = new VBox(form);
        center.setAlignment(Pos.CENTER);
        VBox.setVgrow(center, Priority.ALWAYS);

        root.getChildren().addAll(header, center);

        return root;
    }

    public void renderCars(String res) {
        carList.getItems().clear();
        if (res == null || res.trim().isEmpty()) {
            carList.setPromptText("No cars available");
            createBtn.setDisable(true);
            return;
        }

        for (String row : res.split("\n")) {
            if (row.trim().isEmpty()) continue;
            String[] d = row.split("\\|");
            // d[0]=carId, d[1]=title, d[2]=brand, d[3]=model
            carList.getItems().add(d[1] + " (" + d[2] + " " + d[3] + ") | ID:" + d[0]);
        }
        createBtn.setDisable(false);
    }
}