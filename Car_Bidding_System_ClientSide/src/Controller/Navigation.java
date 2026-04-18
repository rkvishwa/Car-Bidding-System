package Controller;

import UI.Dashboard;
import UI.LiveAuctionPanel;
import UI.LoginScreen;
import UI.SignUpScreen;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Navigation {

    private static Stage stage;
    private static Dashboard dashboard;
    private static DashboardController dashboardController;
    
    public static void setStage(Stage s) {
        stage = s;
    }

    public static void setDashboard(Dashboard d) {
        dashboard = d;
    }

    public static void setDashboardController(DashboardController dc) {
        dashboardController = dc;
    }

    // ===== SCREENS =====

    public static void goToLogin() {
        // Cleanup previous dashboard if any
        if (dashboardController != null) {
            dashboardController.cleanup();
            dashboardController = null;
        }
        dashboard = null;

        LoginScreen view = new LoginScreen();
        new LoginController(view);
        stage.setScene(view.getScene());
    }

    public static void goToSignUp() {
        SignUpScreen view = new SignUpScreen();
        new SignUpController(view, stage);
        stage.setScene(view.getScene());
    }

    public static void goToDashboard(String userId, String role) {
    	System.out.println("STEP 1: Dashboard opening");

        System.out.println("Opening dashboard...");

        Dashboard view = new Dashboard(role, userId);
        DashboardController controller = new DashboardController(view, role, userId);

        // Store references for cleanup
        dashboardController = controller;

        stage.setScene(view.getScene());

        stage.setWidth(1200);
        stage.setHeight(700);
        stage.centerOnScreen();  
    }
    
    // Load live auction INSIDE dashboard content area (keeps sidebar visible)
    public static void goToLiveAuction(String auctionId, String userId) {

        if (dashboard != null) {
            // Load within dashboard
            LiveAuctionPanel view = new LiveAuctionPanel(auctionId, userId);
            LiveAuctionController controller = new LiveAuctionController(view, auctionId, userId);

            // Back button returns to auction list
            view.backBtn.setOnAction(e -> {
                // Cleanup live auction listener
                controller.cleanup();

                // Reload auctions
                UI.AuctionListPanel panel = new UI.AuctionListPanel(userId);
                new AuctionController(panel, userId);
                dashboard.contentArea.getChildren().setAll(panel.getView());
            });

            dashboard.contentArea.getChildren().setAll(view.getView());

        } else {
            // Fallback: new scene
            LiveAuctionPanel view = new LiveAuctionPanel(auctionId, userId);
            new LiveAuctionController(view, auctionId, userId);
            stage.setScene(new Scene(view.getView(), 1100, 700));
        }
    }
    
    
}