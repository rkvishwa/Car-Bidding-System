package UI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class SignUpScreen {

    public TextField nameField = new TextField();
    public TextField emailField = new TextField();
    public TextField phoneField = new TextField();

    public PasswordField passwordField = new PasswordField();
    public PasswordField confirmPasswordField = new PasswordField();

    public ComboBox<String> roleBox = new ComboBox<>();

    public Button registerBtn = new Button("Register");
    public Button backBtn = new Button("← Back to Login");

    public Label title = new Label("CREATE ACCOUNT");
    public Label message = new Label();

    private Scene scene;

    public SignUpScreen() {

        // ===== BACKGROUND IMAGE =====
    	Image bg = new Image("file:image/silverCar.png");
    	ImageView bgImage = new ImageView(bg);
        bgImage.setPreserveRatio(true);
        bgImage.setSmooth(true);
        bgImage.setOpacity(1.0);

        // ===== GRADIENT OVERLAY =====
        Pane gradientOverlay = new Pane();
        gradientOverlay.setStyle(
            "-fx-background-color: linear-gradient(to right, rgba(20,30,48,0.85), rgba(36,59,85,0.85));"
        );

        // ===== ROOT =====
        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));

        // ===== CARD =====
        VBox card = new VBox(12);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(28));
        card.setPrefWidth(420);
        card.setMaxWidth(420);
        card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 18;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 20, 0, 0, 8);"
        );

        ImageView logo = new ImageView();
        try {
            logo.setImage(new Image("file:image/carLogo.png"));
        } catch (Exception e) {
            // no logo
        }
        logo.setFitWidth(70);
        logo.setPreserveRatio(true);

        title.setFont(Font.font("System", FontWeight.BOLD, 20));
        title.setTextFill(Color.web("#1a1a2e"));

        Label subtitle = new Label("Fill in your details to get started");
        subtitle.setStyle("-fx-text-fill: #888; -fx-font-size: 12px;");

        // ===== INPUTS =====
        String inputStyle =
            "-fx-background-radius: 10;" +
            "-fx-border-radius: 10;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-padding: 10 14;" +
            "-fx-font-size: 13px;";

        nameField.setPromptText("Full Name");
        nameField.setStyle(inputStyle);
        nameField.setMaxWidth(300);

        emailField.setPromptText("Email Address");
        emailField.setStyle(inputStyle);
        emailField.setMaxWidth(300);

        phoneField.setPromptText("Phone Number");
        phoneField.setStyle(inputStyle);
        phoneField.setMaxWidth(300);

        passwordField.setPromptText("Password");
        passwordField.setStyle(inputStyle);
        passwordField.setMaxWidth(300);

        confirmPasswordField.setPromptText("Confirm Password");
        confirmPasswordField.setStyle(inputStyle);
        confirmPasswordField.setMaxWidth(300);

        roleBox.getItems().addAll("BUYER", "SELLER");
        roleBox.setPromptText("Select Role");
        roleBox.setMaxWidth(300);
        roleBox.setStyle(inputStyle);

        // ===== BUTTONS =====
        registerBtn.setStyle(
                "-fx-background-color: linear-gradient(to right, #43A047, #388E3C);" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 10;" +
                "-fx-padding: 12 0;" +
                "-fx-cursor: hand;"
        );
        registerBtn.setMaxWidth(300);

        backBtn.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: #1976D2;" +
                "-fx-font-size: 12px;" +
                "-fx-cursor: hand;"
        );

        message.setTextFill(Color.RED);
        message.setFont(Font.font("System", FontWeight.NORMAL, 12));

        card.getChildren().addAll(
                logo,
                title,
                subtitle,
                nameField,
                emailField,
                phoneField,
                passwordField,
                confirmPasswordField,
                roleBox,
                message,
                registerBtn,
                backBtn
        );

        root.getChildren().add(card);

        // ===== STACKPANE =====
        StackPane stack = new StackPane();
        stack.getChildren().addAll(bgImage, gradientOverlay, root);
        StackPane.setAlignment(root, Pos.CENTER);

        double width = Math.min(bg.getWidth(), 1100);
        double height = Math.min(bg.getHeight(), 700);

        scene = new Scene(stack, width, height);
    }

    public Scene getScene() {
        return scene;
    }
}