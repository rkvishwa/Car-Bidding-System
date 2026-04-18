package UI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Dashboard {

    public Button auctionBtn = new Button("📦 Auctions");
    public Button bidHistoryBtn = new Button("📜 Bid History");
    public Button watchlistBtn = new Button("⭐ Watchlist");
    public Button notificationBtn = new Button("🔔 Notifications");
    public Button adminBtn = new Button("⚙ Admin");
    public Button myCarsBtn = new Button("🚗 My Cars");
    public Button myAuctionsBtn = new Button("🔨 My Auctions");
    public Button createAuctionBtn = new Button("➕ Create Auction");
    public Button addCarBtn = new Button("➕ Add Car");
    public Button pendingCarsBtn = new Button("🛠 Pending Cars");
    public Button adminAuctionBtn = new Button("📦 Auctions");
    public Button adminUsersBtn = new Button("👤 Users Acc");
    public Button auditLogBtn = new Button("📜 Audit Logs");
    public Button logoutBtn = new Button("🚪 Logout");
    
    public VBox contentArea = new VBox();
    private Button selectedButton;
    private Scene scene;
    
    private final String DEFAULT_STYLE =
    	    "-fx-background-color: transparent;" +
    	    "-fx-text-fill: #b0b0c8;" +
    	    "-fx-font-size: 14px;" +
    	    "-fx-alignment: CENTER-LEFT;" +
    	    "-fx-padding: 10 15;" +
    	    "-fx-background-radius: 8;" +
    	    "-fx-font-weight: normal;";

    private final String HOVER_STYLE =
    	    "-fx-background-color: rgba(255,255,255,0.08);" +
    	    "-fx-text-fill: white;" +
    	    "-fx-font-size: 14px;" +
    	    "-fx-alignment: CENTER-LEFT;" +
    	    "-fx-padding: 10 15;" +
    	    "-fx-background-radius: 8;" +
    	    "-fx-font-weight: normal;";

    private final String ACTIVE_STYLE =
    	    "-fx-background-color: #2563EB;" +
    	    "-fx-text-fill: white;" +
    	    "-fx-font-size: 14px;" +
    	    "-fx-alignment: CENTER-LEFT;" +
    	    "-fx-padding: 10 15;" +
    	    "-fx-background-radius: 8;" +
    	    "-fx-font-weight: bold;";

    public Dashboard(String role, String userId) {
    	
    	System.out.println("STEP 2: Dashboard UI created");
    	System.out.println("Dashboard UI created");

        // ===== SIDEBAR =====
        VBox sidebar = new VBox(4);
        sidebar.setStyle(
            "-fx-background-color: #0F172A;" +
            "-fx-padding: 20 12 20 12;"
        );
        sidebar.setPrefWidth(230);

        // STYLE BUTTONS
        styleBtn(auctionBtn);
        styleBtn(bidHistoryBtn);
        styleBtn(watchlistBtn);
        styleBtn(notificationBtn);
        styleBtn(adminBtn);
        styleBtn(myCarsBtn);
        styleBtn(myAuctionsBtn);
        styleBtn(createAuctionBtn);
        styleBtn(addCarBtn);
        styleBtn(pendingCarsBtn);
        styleBtn(adminAuctionBtn);
        styleBtn(adminUsersBtn);
        styleBtn(auditLogBtn);

        // Style logout button differently
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setPrefHeight(40);
        logoutBtn.setStyle(
            "-fx-background-color: rgba(211, 47, 47, 0.15);" +
            "-fx-text-fill: #ff6b6b;" +
            "-fx-font-size: 13px;" +
            "-fx-alignment: CENTER-LEFT;" +
            "-fx-padding: 10 15;" +
            "-fx-background-radius: 8;"
        );
        logoutBtn.setOnMouseEntered(e -> logoutBtn.setStyle(
            "-fx-background-color: rgba(211, 47, 47, 0.3);" +
            "-fx-text-fill: #ff8a80;" +
            "-fx-font-size: 13px;" +
            "-fx-alignment: CENTER-LEFT;" +
            "-fx-padding: 10 15;" +
            "-fx-background-radius: 8;"
        ));
        logoutBtn.setOnMouseExited(e -> logoutBtn.setStyle(
            "-fx-background-color: rgba(211, 47, 47, 0.15);" +
            "-fx-text-fill: #ff6b6b;" +
            "-fx-font-size: 13px;" +
            "-fx-alignment: CENTER-LEFT;" +
            "-fx-padding: 10 15;" +
            "-fx-background-radius: 8;"
        ));

        // Separator line
        Separator sep1 = new Separator();
        sep1.setStyle("-fx-background-color: rgba(255,255,255,0.08);");
        sep1.setPadding(new Insets(6, 0, 6, 0));

        Separator sep2 = new Separator();
        sep2.setStyle("-fx-background-color: rgba(255,255,255,0.08);");
        sep2.setPadding(new Insets(6, 0, 6, 0));

        // Spacer to push logout to bottom
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Section labels
        Label menuLabel = createSectionLabel("MENU");

     // ===== ROLE BASED MENU =====
        if (role.equals("BUYER")) {

            sidebar.getChildren().addAll(
                createUserBox(userId, role),
                sep1,
                menuLabel,
                auctionBtn,
                bidHistoryBtn,
                watchlistBtn,
                sep2,
                notificationBtn,
                spacer,
                logoutBtn
            );

        } else if (role.equals("SELLER")) {

            Label carLabel = createSectionLabel("CARS");
            Label auctionLabel = createSectionLabel("AUCTIONS");

            sidebar.getChildren().addAll(
                createUserBox(userId, role),
                sep1,
                carLabel,
                addCarBtn,
                myCarsBtn,
                auctionLabel,
                myAuctionsBtn,
                createAuctionBtn,
                sep2,
                notificationBtn,
                spacer,
                logoutBtn
            );

        } else if (role.equals("ADMIN")) {

            Label adminLabel = createSectionLabel("ADMINISTRATION");

            sidebar.getChildren().addAll(
                createUserBox(userId, role),
                sep1,
                adminLabel,
                pendingCarsBtn,
                adminAuctionBtn,
                adminUsersBtn,
                auditLogBtn, 
                sep2,
                notificationBtn,
                spacer,
                logoutBtn
            );
        }

        // ===== CONTENT =====
        contentArea.setStyle("-fx-background-color: #F8F9FA; -fx-padding: 0;");
        contentArea.setSpacing(0);

        HBox root = new HBox(sidebar, contentArea);

	     // FIX: content area grows
	     HBox.setHgrow(contentArea, Priority.ALWAYS);
	     contentArea.setMaxWidth(Double.MAX_VALUE);
	
	     scene = new Scene(root, 1200, 700);
    }

    private Label createSectionLabel(String text) {
        Label label = new Label(text);
        label.setStyle(
            "-fx-text-fill: #5a5a7a;" +
            "-fx-font-size: 10px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 12 0 4 16;"
        );
        label.setFont(Font.font("System", FontWeight.BOLD, 10));
        return label;
    }

    // MODERN BUTTON STYLE
    private void styleBtn(Button btn) {

        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPrefHeight(40);

        btn.setStyle(DEFAULT_STYLE);

        btn.setOnMouseEntered(e -> {
            if (btn != selectedButton) {
                btn.setStyle(HOVER_STYLE);
            }
        });

        btn.setOnMouseExited(e -> {
            if (btn != selectedButton) {
                btn.setStyle(DEFAULT_STYLE);
            }
        });
    }
    
    public void resetButtons() {
    	Button[] all = {
    		    auctionBtn,
    		    bidHistoryBtn,
    		    watchlistBtn,
    		    notificationBtn,
    		    adminBtn,
    		    myCarsBtn,
    		    myAuctionsBtn,
    		    createAuctionBtn,
    		    addCarBtn,
    		    pendingCarsBtn,
    		    adminAuctionBtn,
    		    adminUsersBtn,
    		    auditLogBtn
    		};

        for (Button b : all) {
        	  b.setStyle(DEFAULT_STYLE);
        }
    }
    
    public void setActiveButton(Button btn) {

        resetButtons();

        btn.setStyle(ACTIVE_STYLE);

        selectedButton = btn;
    }
    
    private VBox createUserBox(String userId, String role) {

        ImageView logo = new ImageView();
        try {
            logo.setImage(new Image("file:image/carLogo.png"));
        } catch (Exception e) {
            // no logo available
        }
        logo.setFitHeight(45);
        logo.setPreserveRatio(true);

        Label userLabel = new Label(userId);
        userLabel.setStyle(
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;"
        );

        Label roleLabel = new Label(role);
        
        String roleColor;
        String roleBg;
        switch (role) {
            case "ADMIN": 
                roleColor = "#BB86FC"; 
                roleBg = "rgba(187,134,252,0.15)";
                break;
            case "SELLER": 
                roleColor = "#FF9800"; 
                roleBg = "rgba(255,152,0,0.15)";
                break;
            default: 
                roleColor = "#03DAC6"; 
                roleBg = "rgba(3,218,198,0.15)";
        }
        roleLabel.setStyle(
            "-fx-text-fill:" + roleColor + ";" +
            "-fx-font-size: 11px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-color:" + roleBg + ";" +
            "-fx-background-radius: 12;" +
            "-fx-padding: 3 10;"
        );

        VBox userBox = new VBox(6, logo, userLabel, roleLabel);
        userBox.setAlignment(Pos.CENTER);
        userBox.setStyle("-fx-padding: 10 0 10 0;");

        return userBox;
    }

    public Scene getScene() {
        return scene;
    }
}