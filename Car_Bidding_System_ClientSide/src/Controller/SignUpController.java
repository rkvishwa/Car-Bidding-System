package Controller;

import Client.Client;
import UI.*;
import javafx.stage.Stage;
import javafx.scene.paint.Color;

public class SignUpController {

    private SignUpScreen view;
    private Stage stage;

    public SignUpController(SignUpScreen view, Stage stage) {
        this.view = view;
        this.stage = stage;

        handleActions();
    }

    private void handleActions() {

        // ✅ REGISTER BUTTON
        view.registerBtn.setOnAction(e -> {

            try (Client client = new Client("localhost", 5000)) {

                String req = "SIGNUP:"
                        + view.nameField.getText() + ":"
                        + view.emailField.getText() + ":"
                        + view.passwordField.getText() + ":"
                        + view.roleBox.getValue();

                String resp = client.sendRequest(req);

                if (resp.startsWith("SUCCESS")) {
                    view.message.setTextFill(Color.GREEN);
                    view.message.setText("Registration successful!");
                    System.out.println("Server response: " + resp);
                    Navigation.goToLogin();

                } else {
                    view.message.setTextFill(Color.RED);
                    view.message.setText("Sign-up failed!");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                view.message.setText("Server error");
            }
        });

        // ✅ BACK BUTTON
        view.backBtn.setOnAction(e -> {
        	System.out.println("Back button clicked!");
        	Navigation.goToLogin();
        });
    }
}