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

public class LoginScreen {

    public TextField emailField = new TextField();
    public PasswordField passwordField = new PasswordField();

    public Button loginBtn = new Button("Login");
    public Button signupBtn = new Button("Create Account");

    public Label title = new Label("CAR AUCTION SYSTEM");
    public Label subtitle = new Label("Login to continue");
    public Label message = new Label();

    private Scene scene;

    public LoginScreen() {

        // ===== BACKGROUND IMAGE =====
    	Image bg = new Image("file:image/redCar.png");

    	// ===== BACKGROUND IMAGE =====
    	ImageView bgImage = new ImageView(bg);
        bgImage.setPreserveRatio(true);
        bgImage.setSmooth(true);
        bgImage.setOpacity(1.0);

        // ===== GRADIENT OVERLAY =====
        Pane gradientOverlay = new Pane();
        gradientOverlay.setStyle(
            "-fx-background-color: rgba(20,30,48,0.85);"
        );

        // ===== ROOT =====
        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));

        // ===== CARD =====
        VBox card = new VBox(14);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(30));
        card.setPrefWidth(380);
        card.setMaxWidth(380);
        card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 16;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.04), 15, 0, 0, 5);"
        );

        // ===== LOGO =====
        ImageView logo = new ImageView();
        try {
            logo.setImage(new Image("file:image/carLogo.png"));
        } catch (Exception e) {
            // no logo
        }
        logo.setFitWidth(80);
        logo.setPreserveRatio(true);

        // ===== TITLE =====
        title.setFont(Font.font("System", FontWeight.BOLD, 22));
        title.setTextFill(Color.web("#111827"));

        subtitle.setTextFill(Color.web("#6B7280"));
        subtitle.setFont(Font.font("System", FontWeight.NORMAL, 13));

        // ===== INPUTS =====
        String inputStyle =
            "-fx-background-radius: 8;" +
            "-fx-border-radius: 8;" +
            "-fx-border-color: #E5E7EB;" +
            "-fx-background-color: #F9FAFB;" +
            "-fx-padding: 12 14;" +
            "-fx-font-size: 13px;";

        emailField.setPromptText("Email address");
        emailField.setStyle(inputStyle);
        emailField.setMaxWidth(300);

        passwordField.setPromptText("Password");
        passwordField.setStyle(inputStyle);
        passwordField.setMaxWidth(300);

        // ===== BUTTONS =====
        loginBtn.setStyle(
                "-fx-background-color: #2563EB;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 12 0;" +
                "-fx-cursor: hand;"
        );
        loginBtn.setMaxWidth(300);

        loginBtn.setOnMouseEntered(e -> loginBtn.setStyle(
                "-fx-background-color: #1D4ED8;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 12 0;" +
                "-fx-cursor: hand;"
        ));
        loginBtn.setOnMouseExited(e -> loginBtn.setStyle(
                "-fx-background-color: #2563EB;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 12 0;" +
                "-fx-cursor: hand;"
        ));

        signupBtn.setStyle(
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
                new Label(""),  // spacer
                emailField,
                passwordField,
                message,
                loginBtn,
                signupBtn
        );

        root.getChildren().add(card);

        // ===== STACKPANE FIX =====
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