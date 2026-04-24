package UI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.Year;
import java.util.function.Consumer;

public class AddCarPanel {

    private String sellerId;

    private TextField titleField = new TextField();
    private TextField brandField = new TextField();
    private TextField modelField = new TextField();
    private TextField yearField = new TextField();
    private TextField priceField = new TextField();
    private TextField descField = new TextField();
    private TextField imageField = new TextField();

    private Button addBtn = new Button("🚗 Add Car");
    private Label msg = new Label();

    private Consumer<AddCarData> onAddCar;

    public AddCarPanel(String sellerId) {
        this.sellerId = sellerId;
    }

    public VBox getView() {

        VBox root = new VBox(20);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: transparent;");
        root.setAlignment(Pos.TOP_CENTER);

        // ===== HEADER =====
        Label title = new Label("➕ Add New Car");
        title.setFont(Font.font("System", FontWeight.BOLD, 24));
        title.setTextFill(Color.web("#1a1a2e"));

        Label subtitle = new Label("Fill in the details to list your car");
        subtitle.setStyle("-fx-text-fill: #666; -fx-font-size: 13px;");

        VBox header = new VBox(4, title, subtitle);
        header.setAlignment(Pos.CENTER);

        // ===== FORM CARD =====
        VBox formCard = new VBox(14);
        formCard.setPadding(new Insets(30));
        formCard.setMaxWidth(500);
        formCard.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 16;"
        );

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.04));
        shadow.setRadius(15);
        shadow.setOffsetY(5);
        formCard.setEffect(shadow);

        // Style inputs
        String inputStyle =
                "-fx-background-radius: 8;" +
                "-fx-border-radius: 8;" +
                "-fx-border-color: #E5E7EB;" +
                "-fx-background-color: #F9FAFB;" +
                "-fx-padding: 10 14;" +
                "-fx-font-size: 13px;";

        titleField.setPromptText("Car Title (e.g. 2024 BMW M3)");
        titleField.setStyle(inputStyle);

        brandField.setPromptText("Brand (e.g. BMW)");
        brandField.setStyle(inputStyle);

        modelField.setPromptText("Model (e.g. M3 Competition)");
        modelField.setStyle(inputStyle);

        yearField.setPromptText("Year (e.g. 2024)");
        yearField.setStyle(inputStyle);

        priceField.setPromptText("Starting Price (e.g. 1230k = 1,230,000 MMK)");
        priceField.setStyle(inputStyle);

        descField.setPromptText("Description");
        descField.setStyle(inputStyle);

        imageField.setPromptText("Image URL (https://...)");
        imageField.setStyle(inputStyle);

        addBtn.setStyle(
                "-fx-background-color: #16A34A;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 12 30;" +
                "-fx-cursor: hand;"
        );

        addBtn.setMaxWidth(Double.MAX_VALUE);

        addBtn.setOnAction(e -> {
            if (!validate()) return;

            if (onAddCar != null) {
                onAddCar.accept(new AddCarData(
                        sellerId,
                        titleField.getText(),
                        brandField.getText(),
                        modelField.getText(),
                        yearField.getText(),
                        parsePrice(priceField.getText()),
                        descField.getText(),
                        imageField.getText()
                ));
            }
        });

        msg.setStyle("-fx-font-size: 13px; -fx-padding: 5 0 0 0;");

        formCard.getChildren().addAll(
                createFieldLabel("Car Title"),
                titleField,
                createFieldLabel("Brand"),
                brandField,
                createFieldLabel("Model"),
                modelField,
                createFieldLabel("Year"),
                yearField,
                createFieldLabel("Starting Price (MMK)"),
                priceField,
                createFieldLabel("Description"),
                descField,
                createFieldLabel("Image URL"),
                imageField,
                addBtn,
                msg
        );

        ScrollPane scroll = new ScrollPane(formCard);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        VBox.setVgrow(scroll, Priority.ALWAYS);

        VBox centered = new VBox(formCard);
        centered.setAlignment(Pos.TOP_CENTER);

        root.getChildren().addAll(header, scroll);

        return root;
    }

    private Label createFieldLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("System", FontWeight.BOLD, 12));
        label.setTextFill(Color.web("#555"));
        return label;
    }

    public void onAddCar(Consumer<AddCarData> action) {
        this.onAddCar = action;
    }

    public void showMessage(String m) {
        msg.setText(m);
        msg.setTextFill(Color.web("#4CAF50"));
    }

    public void clear() {
        titleField.clear();
        brandField.clear();
        modelField.clear();
        yearField.clear();
        priceField.clear();
        descField.clear();
        imageField.clear();
        msg.setText("");
    }

    private boolean validate() {

        if (titleField.getText().isBlank()) return error("Title is required");
        if (brandField.getText().isBlank()) return error("Brand is required");
        if (modelField.getText().isBlank()) return error("Model is required");

        try {
            int y = Integer.parseInt(yearField.getText());
            int now = Year.now().getValue();
            if (y < 1900 || y > now + 1)
                return error("Invalid year (1900-" + (now + 1) + ")");
        } catch (Exception e) {
            return error("Year must be a number");
        }

        try {
            double p = Double.parseDouble(parsePrice(priceField.getText()));
            if (p <= 0) return error("Price must be positive");
        } catch (Exception e) {
            return error("Price must be a valid number (e.g. 1230k or 1230000)");
        }

        return true;
    }

    private String parsePrice(String price) {
        String p = price.trim().toLowerCase();
        if (p.endsWith("k")) {
            try {
                double val = Double.parseDouble(p.substring(0, p.length() - 1));
                return String.format("%.0f", val * 1000);
            } catch (Exception e) {
                return price;
            }
        }
        return price;
    }

    private boolean error(String msgText) {
        msg.setText("⚠ " + msgText);
        msg.setTextFill(Color.web("#D32F2F"));
        return false;
    }
}