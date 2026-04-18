package Controller;

import Client.Client;
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

    	        if (!resp.equals("FAILED")) {

    	            view.message.setTextFill(Color.GREEN);
    	            view.message.setText("Login success!");

    	            String[] data = resp.split(":");
    	            String userId = data[0];
    	            String role = data[1];
    	            
    	            if (role.equals("ADMIN")) {
    	                AdminSession.set(userId,role);
    	            }
    	            System.out.println("ADMIN SESSION = " + AdminSession.getAdminId());
    	            Navigation.goToDashboard(userId, role);

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
    }
}