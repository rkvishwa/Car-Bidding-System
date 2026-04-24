package Controller;

import Client.ClientManager;
import Event.AdminSession;
import UI.*;
import javafx.scene.paint.Color;

public class LoginController {

    private LoginScreen view;

    public LoginController(LoginScreen view) {
        this.view = view;
        handleActions();
    }

    private void handleActions() {

    	view.loginBtn.setOnAction(e -> {

    	    try {

    	        String req = "LOGIN:"
    	                + view.emailField.getText() + ":"
    	                + view.passwordField.getText();

    	        String resp = ClientManager.send(req);

    	        if (resp.equals("PENDING")) {
    	            view.message.setTextFill(Color.ORANGE);
    	            view.message.setText("Account pending admin approval!");
    	            return;
    	        }
    	        
    	        if (resp.equals("BANNED")) {
    	            view.message.setTextFill(Color.RED);
    	            view.message.setText("This account has been banned!");
    	            return;
    	        }

    	        if (!resp.equals("FAILED")) {
    	            view.message.setTextFill(Color.GREEN);
    	            view.message.setText("Login success!");

    	            String[] data = resp.split(":");
    	            String userId = data[0];
    	            String role = data[1];
    	            String name = data.length > 2 ? data[2] : userId;
    	            
    	            if (role.equals("ADMIN")) {
    	                AdminSession.set(userId, role, name);
    	            }
    	            System.out.println("ADMIN SESSION = " + AdminSession.getAdminId());
    	            Navigation.goToDashboard(userId, role, name);

    	        } else {
    	            view.message.setTextFill(Color.RED);
    	            view.message.setText("Invalid login!");
    	        }

    	    } catch (Exception ex) {
    	        ex.printStackTrace();
    	        view.message.setTextFill(Color.RED);
    	        view.message.setText("Server error");
    	    }
    	});

        view.signupBtn.setOnAction(e -> {
            Navigation.goToSignUp(); // 🔥 clean navigation
        });

        view.forgotPasswordBtn.setOnAction(e -> {
            javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog();
            dialog.setTitle("Forgot Password");
            dialog.setHeaderText("Reset your password");
            dialog.setContentText("Please enter your email:");
            
            dialog.showAndWait().ifPresent(email -> {
                try {
                    String res = Service.UserService.forgotPassword(email);
                    if (res.startsWith("SUCCESS")) {
                        String newPass = res.split(":")[1];
                        view.message.setTextFill(Color.GREEN);
                        view.message.setText("Temp Password: " + newPass);
                    } else if (res.equals("FAILED:AccountDeleted")) {
                        view.message.setTextFill(Color.RED);
                        view.message.setText("Blocked! Account deleted after 3 attempts.");
                    } else {
                        view.message.setTextFill(Color.RED);
                        view.message.setText("Error: Email not found or reset failed.");
                    }
                } catch (Exception ex) { ex.printStackTrace(); }
            });
        });
    }
}