package UI;

import Client.ClientManager;
import Controller.Navigation;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    public static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        Navigation.setStage(primaryStage);

        try {
			ClientManager.connect("localhost", 5000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        // 🔥 GLOBAL EVENT PIPELINE
        ClientManager.addListener(msg -> {

            String[] parts = msg.split(":", 2);

            String type = parts[0];
            String data = (parts.length > 1) ? parts[1] : "";

            Event.EventBus.publish(new Event.Event(type, data));
        });

        Navigation.goToLogin();

        primaryStage.setTitle("Car Auction System");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}