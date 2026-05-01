package UI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.function.Consumer;

public class AdminUserPanel {

    private VBox pendingList = new VBox(10);
    private VBox list = new VBox(10);
    private Consumer<String> onBan;
    private Consumer<String> onMakeAdmin;
    private Consumer<String> onApprove;
    private Consumer<String> onReject;

    public VBox getView() {

        VBox root = new VBox(20);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: transparent;");

        // ===== HEADER =====
        HBox headerRow = new HBox(15);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("👤 User Management");
        title.setFont(Font.font("System", FontWeight.BOLD, 26));
        title.setTextFill(Color.web("#1a1a2e"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label countLabel = new Label();
        countLabel.setId("userCount");
        countLabel.setStyle(
            "-fx-text-fill: #888; -fx-font-size: 12px;" +
            "-fx-background-color: #e8e8e8; -fx-background-radius: 12;" +
            "-fx-padding: 4 12;"
        );

        headerRow.getChildren().addAll(title, spacer, countLabel);

        Label subtitle = new Label("Manage user accounts — Ban users or promote to admin");
        subtitle.setStyle("-fx-text-fill: #888; -fx-font-size: 13px;");

        VBox header = new VBox(4, headerRow, subtitle);

        // ===== PENDING SECTION =====
        Label pendingTitle = new Label("⏳ Pending Registrations");
        pendingTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        pendingTitle.setTextFill(Color.web("#E65100"));
        pendingTitle.setPadding(new Insets(10, 0, 0, 0));

        ScrollPane pendingScroll = new ScrollPane(pendingList);
        pendingScroll.setFitToWidth(true);
        pendingScroll.setPrefHeight(200);
        pendingScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        pendingScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        // ===== ALL USERS SECTION =====
        Label allTitle = new Label("👤 All Users");
        allTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        allTitle.setTextFill(Color.web("#1a1a2e"));
        allTitle.setPadding(new Insets(10, 0, 0, 0));

        ScrollPane scroll = new ScrollPane(list);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        root.getChildren().addAll(header, pendingTitle, pendingScroll, new Separator(), allTitle, scroll);

        return root;
    }

    public void render(String res) {

        list.getChildren().clear();

        if (res == null || res.trim().isEmpty()) {
            Label empty = new Label("No users found");
            empty.setStyle("-fx-text-fill: #999; -fx-font-size: 14px; -fx-padding: 20;");
            list.getChildren().add(empty);
            return;
        }

        for (String row : res.split("\n")) {
            if (row.trim().isEmpty()) continue;

            String[] d = row.split("\\|");
            if (d.length < 3) continue;
            
            String status = d.length > 3 ? d[3] : "ACTIVE";

            list.getChildren().add(createCard(d[0], d[1], d[2], status));
        }
    }

    public void renderPending(String res) {
        pendingList.getChildren().clear();

        if (res == null || res.trim().isEmpty()) {
            Label empty = new Label("No pending registrations");
            empty.setStyle("-fx-text-fill: #999; -fx-font-size: 13px; -fx-padding: 10;");
            pendingList.getChildren().add(empty);
            return;
        }

        for (String row : res.split("\n")) {
            if (row.trim().isEmpty()) continue;
            String[] d = row.split("\\|");
            if (d.length < 3) continue;
            pendingList.getChildren().add(createPendingCard(d[0], d[1], d[2]));
        }
    }

    private HBox createPendingCard(String userId, String name, String role) {
        HBox card = new HBox(16);
        card.setPadding(new Insets(10, 15, 10, 15));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: #FFF3E0; -fx-background-radius: 12;");

        VBox info = new VBox(2);
        HBox.setHgrow(info, Priority.ALWAYS);
        Label n = new Label(name); n.setStyle("-fx-font-weight: bold;");
        Label r = new Label(role); r.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");
        info.getChildren().addAll(n, r);

        Button approveBtn = new Button("✅ Approve");
        approveBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 11px; -fx-background-radius: 8;");
        approveBtn.setOnAction(e -> { if(onApprove != null) onApprove.accept(userId); });

        Button rejectBtn = new Button("❌ Reject");
        rejectBtn.setStyle("-fx-background-color: #D32F2F; -fx-text-fill: white; -fx-font-size: 11px; -fx-background-radius: 8;");
        rejectBtn.setOnAction(e -> { if(onReject != null) onReject.accept(userId); });

        card.getChildren().addAll(info, approveBtn, rejectBtn);
        return card;
    }

    private HBox createCard(String userId, String name, String role, String status) {

        HBox card = new HBox(16);
        card.setPadding(new Insets(14, 20, 14, 20));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 16;"
        );

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.04));
        shadow.setRadius(15);
        shadow.setOffsetY(5);
        card.setEffect(shadow);

        // ===== AVATAR =====
        String avatarEmoji;
        String avatarBg;
        switch (role) {
            case "ADMIN":
                avatarEmoji = "👑";
                avatarBg = "#F3E5F5";
                break;
            case "SELLER":
                avatarEmoji = "🏪";
                avatarBg = "#FFF3E0";
                break;
            default:
                avatarEmoji = "🛒";
                avatarBg = "#E3F2FD";
        }

        Label avatar = new Label(avatarEmoji);
        avatar.setFont(Font.font(24));
        avatar.setAlignment(Pos.CENTER);

        StackPane avatarCircle = new StackPane(avatar);
        avatarCircle.setMinSize(48, 48);
        avatarCircle.setMaxSize(48, 48);
        avatarCircle.setStyle(
            "-fx-background-color: " + avatarBg + ";" +
            "-fx-background-radius: 24;"
        );

        // ===== INFO =====
        VBox info = new VBox(3);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label nameLabel = new Label(name);
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 15));
        nameLabel.setTextFill(Color.BLACK);
        nameLabel.setStyle("-fx-text-fill: black; -fx-font-size: 16px;");
//        nameLabel.setTextFill(Color.web("#111827"));

        // Role badge
        Label roleBadge = new Label(role);
        roleBadge.setPadding(new Insets(3, 10, 3, 10));
        roleBadge.setFont(Font.font("System", FontWeight.BOLD, 10));

        String roleColor;
        String roleBgColor;
        switch (role) {
            case "ADMIN":
                roleColor = "#7B1FA2";
                roleBgColor = "#F3E5F5";
                break;
            case "SELLER":
                roleColor = "#E65100";
                roleBgColor = "#FFF3E0";
                break;
            default:
                roleColor = "#1565C0";
                roleBgColor = "#E3F2FD";
        }
        roleBadge.setStyle(
            "-fx-background-color: " + roleBgColor + ";" +
            "-fx-text-fill: " + roleColor + ";" +
            "-fx-background-radius: 12;"
        );
        
        HBox badges = new HBox(8, roleBadge);
        
        if ("BANNED".equals(status)) {
            Label bannedBadge = new Label("BANNED");
            bannedBadge.setPadding(new Insets(3, 10, 3, 10));
            bannedBadge.setFont(Font.font("System", FontWeight.BOLD, 10));
            bannedBadge.setStyle(
                "-fx-background-color: #FFEBEE;" +
                "-fx-text-fill: #D32F2F;" +
                "-fx-background-radius: 12;"
            );
            badges.getChildren().add(bannedBadge);
            card.setOpacity(0.5);
        }

        info.getChildren().addAll(nameLabel, badges);

        // ===== ACTIONS =====
        HBox actions = new HBox(8);
        actions.setAlignment(Pos.CENTER_RIGHT);

        if (!role.equals("ADMIN")) {
            Button makeAdminBtn = new Button("👑 Make Admin");
            makeAdminBtn.setStyle(
                "-fx-background-color: #7B1FA2;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 7 14;" +
                "-fx-cursor: hand;"
            );
            makeAdminBtn.setOnAction(e -> {
                if (onMakeAdmin != null) {
                    onMakeAdmin.accept(userId);
                }
            });
            actions.getChildren().add(makeAdminBtn);
        }

        Button banBtn = new Button("🚫 Ban");
        banBtn.setStyle(
            "-fx-background-color: #D32F2F;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 11px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 7 14;" +
            "-fx-cursor: hand;"
        );
        banBtn.setOnAction(e -> {
            if (onBan != null) {
                onBan.accept(userId);
                card.setDisable(true);
                card.setOpacity(0.5);
                banBtn.setText("Banned");
                banBtn.setDisable(true);
            }
        });

        // Don't allow banning admins or already banned users
        if (role.equals("ADMIN") || "BANNED".equals(status)) {
            banBtn.setDisable(true);
            banBtn.setStyle(
                "-fx-background-color: #e0e0e0;" +
                "-fx-text-fill: #999;" +
                "-fx-font-size: 11px;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 7 14;"
            );
            if ("BANNED".equals(status)) {
            	banBtn.setText("Banned");
            }
        }

        actions.getChildren().add(banBtn);

        card.getChildren().addAll(avatarCircle, info, actions);

        return card;
    }

    public void onBan(Consumer<String> action) {
        this.onBan = action;
    }

    public void onMakeAdmin(Consumer<String> action) {
        this.onMakeAdmin = action;
    }

    public void onApprove(Consumer<String> action) {
        this.onApprove = action;
    }

    public void onReject(Consumer<String> action) {
        this.onReject = action;
    }
}