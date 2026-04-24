package Controller;


import UI.*;
import javafx.application.Platform;

import java.util.function.Consumer;

import Event.AdminSession;
import Event.Event;
import Event.EventBus;

public class DashboardController {

    private Dashboard view;
    private String role;
    private String userId;

    private String currentPage = "AUCTIONS";
    private Consumer<Event> eventSubscriber;

    private final String DEFAULT_STYLE =
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #e0e0e0;" +
            "-fx-font-size: 14px;" +
            "-fx-alignment: CENTER-LEFT;" +
            "-fx-padding: 10 15;" +
            "-fx-background-radius: 8;";

    private final String ACTIVE_STYLE =
            "-fx-background-color: #4CAF50;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-alignment: CENTER-LEFT;" +
            "-fx-padding: 10 15;" +
            "-fx-background-radius: 8;";

    public DashboardController(Dashboard view, String role, String userId, String name) {
        this.view = view;
        this.role = role;
        this.userId = userId;

        System.out.println("DashboardController started");

        // Store dashboard reference for in-panel navigation
        Navigation.setDashboard(view);

        handleActions();
        loadDefaultScreen();
        setupEventBus();
    }

    // =========================
    // DEFAULT SCREEN
    // =========================
    private void loadDefaultScreen() {

        if (role.equals("BUYER")) {
            setActive(view.auctionBtn);
            refreshAuctionList();

        } else if (role.equals("SELLER")) {
            setActive(view.addCarBtn);
            openAddCar();

        } else if (role.equals("ADMIN")) {
            setActive(view.pendingCarsBtn);
            refreshPendingCars();
        }
    }

    // =========================
    // EVENT BUS (GLOBAL LISTENER)
    // =========================
    private void setupEventBus() {

        // Use a stored reference so we can unsubscribe later
        eventSubscriber = event -> {

            Platform.runLater(() -> {

                // ================= BUYER =================
                if (role.equals("BUYER")) {

                    if (event.type.equals("NEW_BID") || event.type.equals("AUCTION_CLOSED") ||
                        event.type.equals("AUCTION_STARTED")) {
                        if (currentPage.equals("AUCTIONS")) {
                            refreshAuctionList();
                        }
                    }

                    if (event.type.equals("NOTIFY")) {
                        System.out.println("BUYER NOTIFY: " + event.data);
                    }
                }

                // ================= SELLER =================
                if (role.equals("SELLER")) {

                    if (event.type.equals("CAR_ADDED")) {
                        if (currentPage.equals("MY_CARS")) {
                            refreshMyCars();
                        }
                    }

                    if (event.type.equals("AUCTION_CREATED") || event.type.equals("AUCTION_STARTED")) {
                        if (currentPage.equals("MY_AUCTIONS")) {
                            refreshMyAuctions();
                        }
                    }

                    if (event.type.equals("CAR_APPROVED") || event.type.equals("CAR_REJECTED")) {
                        if (currentPage.equals("MY_CARS")) {
                            refreshMyCars();
                        }
                    }

                    if (event.type.equals("NOTIFY")) {
                        System.out.println("SELLER NOTIFY: " + event.data);
                    }
                }

                // ================= ADMIN =================
                if (role.equals("ADMIN")) {

                    if (event.type.equals("USER_DELETED") ||
                        event.type.equals("USER_BANNED") ||
                        event.type.equals("ROLE_CHANGED")) {

                        if (currentPage.equals("ADMIN_USERS")) {
                            refreshAdminUsers();
                        }
                    }

                    if (event.type.equals("CAR_APPROVED") ||
                        event.type.equals("CAR_REJECTED")) {

                        if (currentPage.equals("PENDING_CARS")) {
                            refreshPendingCars();
                        }
                    }

                    if (event.type.equals("AUCTION_CLOSED") ||
                        event.type.equals("AUCTION_STARTED")) {

                        if (currentPage.equals("ADMIN_AUCTIONS")) {
                            refreshAdminAuctions();
                        }
                    }

                    // audit log live update
                    if (event.type.equals("AUDIT_LOG")) {

                        if (currentPage.equals("ADMIN_AUDIT")) {
                            refreshAuditLogs();
                        }
                    }
                }
            });
        };

        EventBus.subscribe(eventSubscriber);
    }

    // =========================
    // CLEANUP
    // =========================
    public void cleanup() {
        if (eventSubscriber != null) {
            EventBus.unsubscribe(eventSubscriber);
        }
    }

    // =========================
    // NAVIGATION ACTIONS
    // =========================
    private void handleActions() {

        // ===== BUYER =====
        view.auctionBtn.setOnAction(e -> {
            currentPage = "AUCTIONS";
            view.setActiveButton(view.auctionBtn);
            refreshAuctionList();
        });

        view.bidHistoryBtn.setOnAction(e -> {
            currentPage = "HISTORY";
            view.setActiveButton(view.bidHistoryBtn);
            view.contentArea.getChildren().setAll(new BidHistoryPanel(userId).getView());
        });

        view.watchlistBtn.setOnAction(e -> {
            currentPage = "WATCHLIST";
            view.setActiveButton(view.watchlistBtn);
            view.contentArea.getChildren().setAll(new WatchlistPanel(userId).getView());
        });

        view.notificationBtn.setOnAction(e -> {
            currentPage = "NOTIFICATIONS";
            view.setActiveButton(view.notificationBtn);
            view.contentArea.getChildren().setAll(new NotificationsPanel(userId).getView());
        });

        // ===== SELLER =====
        view.addCarBtn.setOnAction(e -> {
            currentPage = "ADD_CAR";
            view.setActiveButton(view.addCarBtn);
            openAddCar();
        });

        view.myCarsBtn.setOnAction(e -> {
            currentPage = "MY_CARS";
            view.setActiveButton(view.myCarsBtn);
            refreshMyCars();
        });

        view.myAuctionsBtn.setOnAction(e -> {
            currentPage = "MY_AUCTIONS";
            view.setActiveButton(view.myAuctionsBtn);
            refreshMyAuctions();
        });

        view.createAuctionBtn.setOnAction(e -> {
            currentPage = "CREATE_AUCTION";
            view.setActiveButton(view.createAuctionBtn);

            CreateAuctionPanel panel = new CreateAuctionPanel(userId);
            new CreateAuctionController(panel, userId);

            view.contentArea.getChildren().setAll(panel.getView());
        });

        // ===== ADMIN =====
        view.pendingCarsBtn.setOnAction(e -> {
            currentPage = "PENDING_CARS";
            view.setActiveButton(view.pendingCarsBtn);
            refreshPendingCars();
        });

        view.adminAuctionBtn.setOnAction(e -> {
            currentPage = "ADMIN_AUCTIONS";
            view.setActiveButton(view.adminAuctionBtn);
            refreshAdminAuctions();
        });

        view.adminUsersBtn.setOnAction(e -> {
            currentPage = "ADMIN_USERS";
            view.setActiveButton(view.adminUsersBtn);

            AdminUserPanel panel = new AdminUserPanel();
            AdminController.handleUsers(panel);

            view.contentArea.getChildren().setAll(panel.getView());
        });
        
        view.auditLogBtn.setOnAction(e -> {
            currentPage = "ADMIN_AUDIT";
            view.setActiveButton(view.auditLogBtn);

            AdminAuditLogPanel panel = new AdminAuditLogPanel();
            AdminController.handleAuditLogs(panel);

            view.contentArea.getChildren().setAll(panel.getView());
        });

        // ===== LOGOUT =====
        view.logoutBtn.setOnAction(e -> {
            cleanup();
            AdminSession.set(null, null);
            Navigation.goToLogin();
        });
    }

    // =========================
    // REFRESH METHODS
    // =========================

    private void refreshAuctionList() {
        AuctionListPanel panel = new AuctionListPanel(userId);
        new AuctionController(panel, userId);
        view.contentArea.getChildren().setAll(panel.getView());
    }

    private void refreshMyCars() {
        view.contentArea.getChildren().setAll(
                new MyCarsPanel(userId).getView()
        );
    }

    private void refreshMyAuctions() {
    	MyAuctionPanel panel = new MyAuctionPanel();
    	new MyAuctionController(panel, userId);

    	view.contentArea.getChildren().setAll(panel.getView());
    }

    private void refreshPendingCars() {
        AdminPendingCarsPanel panel = new AdminPendingCarsPanel();
        AdminController.handleCars(panel);
        view.contentArea.getChildren().setAll(panel.getView());
    }

    private void refreshAdminAuctions() {
        AdminAuctionPanel panel = new AdminAuctionPanel();
        AdminController.handleAuctions(panel);
        view.contentArea.getChildren().setAll(panel.getView());
    }
    
    private void refreshAuditLogs() {
        AdminAuditLogPanel panel = new AdminAuditLogPanel();
        AdminController.handleAuditLogs(panel);
        view.contentArea.getChildren().setAll(panel.getView());
    }

    private void refreshAdminUsers() {
        AdminUserPanel panel = new AdminUserPanel();
        AdminController.handleUsers(panel);
        view.contentArea.getChildren().setAll(panel.getView());
    }

    private void openAddCar() {
        AddCarPanel panel = new AddCarPanel(userId);
        new AddCarController(panel);
        view.contentArea.getChildren().setAll(panel.getView());
    }

    // =========================
    // BUTTON STYLE
    // =========================
    private void setActive(javafx.scene.control.Button activeBtn) {

        javafx.scene.control.Button[] allBtns = {
                view.auctionBtn,
                view.bidHistoryBtn,
                view.watchlistBtn,
                view.notificationBtn,
                view.adminBtn,
                view.myCarsBtn,
                view.myAuctionsBtn,
                view.addCarBtn,
                view.createAuctionBtn,
                view.pendingCarsBtn,
                view.adminAuctionBtn,
                view.adminUsersBtn,
                view.auditLogBtn
        };

        for (javafx.scene.control.Button btn : allBtns) {
            btn.setStyle(DEFAULT_STYLE);
        }

        activeBtn.setStyle(ACTIVE_STYLE);
    }
}