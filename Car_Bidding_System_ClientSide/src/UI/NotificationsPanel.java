package UI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import Service.NotificationService;

public class NotificationsPanel {

    private String userId;
    private VBox list = new VBox(10);

    public NotificationsPanel(String userId) {
        this.userId = userId;
    }

    public Node getView() {

        VBox root = new VBox(20);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: transparent;");

        // ===== HEADER =====
        HBox headerRow = new HBox(15);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("🔔 Notifications");
        title.setFont(Font.font("System", FontWeight.BOLD, 26));
        title.setTextFill(Color.web("#1a1a2e"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label countLabel = new Label();
        countLabel.setStyle(
            "-fx-text-fill: #888; -fx-font-size: 12px;" +
            "-fx-background-color: #e8e8e8; -fx-background-radius: 12;" +
            "-fx-padding: 4 12;"
        );

        Button markAllBtn = new Button("✓ Mark All Read");
        markAllBtn.setStyle(
            "-fx-background-color: #1976D2;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 8 16;" +
            "-fx-cursor: hand;"
        );
        markAllBtn.setOnAction(e -> {
            try {
                NotificationService.markAllRead(userId);
                loadData(countLabel); // refresh
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        headerRow.getChildren().addAll(title, spacer, countLabel, markAllBtn);

        // ===== LOAD DATA =====
        loadData(countLabel);

        ScrollPane scroll = new ScrollPane(list);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        root.getChildren().addAll(headerRow, scroll);

        return root;
    }

    private void loadData(Label countLabel) {

        list.getChildren().clear();

        try {
            String res = NotificationService.getNotifications(userId);

            if (res == null || res.trim().isEmpty()) {
                VBox emptyState = new VBox(10);
                emptyState.setAlignment(Pos.CENTER);
                emptyState.setPadding(new Insets(60));

                Label emptyIcon = new Label("🔔");
                emptyIcon.setFont(Font.font(48));
                emptyIcon.setStyle("-fx-text-fill: #999;"); // Fix text color for white themes

                Label empty = new Label("No notifications yet");
                empty.setStyle("-fx-text-fill: #999; -fx-font-size: 16px;");

                Label hint = new Label("You'll be notified about bids, auctions, and more");
                hint.setStyle("-fx-text-fill: #bbb; -fx-font-size: 13px;");

                emptyState.getChildren().addAll(emptyIcon, empty, hint);
                list.getChildren().add(emptyState);
                countLabel.setText("0");
                return;
            }

            int total = 0;
            int unread = 0;

            for (String row : res.split("\n")) {
                if (row.trim().isEmpty()) continue;

                String[] d = row.split("\\|");
                if (d.length < 5) continue;

                // d[0]=notifId, d[1]=message, d[2]=type, d[3]=status, d[4]=createdAt
                list.getChildren().add(
                    createCard(d[0], d[1], d[2], d[3], d[4])
                );
                total++;
                if ("UNREAD".equals(d[3])) unread++;
            }

            countLabel.setText(unread > 0 ? unread + " unread" : total + " total");
            if (unread > 0) {
                countLabel.setStyle(
                    "-fx-text-fill: white; -fx-font-size: 12px;" +
                    "-fx-background-color: #1976D2; -fx-background-radius: 12;" +
                    "-fx-padding: 4 12; -fx-font-weight: bold;"
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
            Label error = new Label("⚠ Failed to load notifications");
            error.setStyle("-fx-text-fill: #D32F2F; -fx-font-size: 14px;");
            list.getChildren().add(error);
        }
    }

    private HBox createCard(String notifId, String message, String type,
                            String status, String createdAt) {

        HBox card = new HBox(14);
        card.setPadding(new Insets(14, 18, 14, 18));
        card.setAlignment(Pos.CENTER_LEFT);

        boolean isUnread = "UNREAD".equals(status);

        card.setStyle(
            "-fx-background-color: " + (isUnread ? "#E8F4FD" : "white") + ";" +
            "-fx-background-radius: 16;" +
            (isUnread ? "-fx-border-color: #90CAF9; -fx-border-radius: 16; -fx-border-width: 1;" : "")
        );

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.04));
        shadow.setRadius(15);
        shadow.setOffsetY(3);
        card.setEffect(shadow);

        // ===== ICON =====
        Label icon = new Label(getIcon(type));
        icon.setFont(Font.font(22));
        icon.setMinWidth(36);
        icon.setAlignment(Pos.CENTER);
        icon.setStyle("-fx-text-fill: #333;"); // Ensure icon emoji is not painted white

        // ===== ICON CIRCLE =====
        StackPane iconCircle = new StackPane(icon);
        iconCircle.setMinSize(48, 48);
        iconCircle.setMaxSize(48, 48);
        iconCircle.setStyle(
            "-fx-background-color: " + getIconBg(type) + ";" +
            "-fx-background-radius: 24;"
        );

        // ===== MESSAGE =====
        VBox info = new VBox(3);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label msgLabel = new Label(message);
        msgLabel.setWrapText(true);
        msgLabel.setFont(Font.font("System", isUnread ? FontWeight.BOLD : FontWeight.NORMAL, 13));
        msgLabel.setTextFill(Color.web("#333"));

        Label dateLabel = new Label("📅 " + createdAt);
        dateLabel.setStyle("-fx-text-fill: #aaa; -fx-font-size: 11px;");

        // Type badge
        Label typeBadge = new Label(type);
        typeBadge.setPadding(new Insets(2, 8, 2, 8));
        typeBadge.setStyle(
            "-fx-background-color: " + getIconBg(type) + ";" +
            "-fx-text-fill: #666; -fx-font-size: 9px; -fx-font-weight: bold;" +
            "-fx-background-radius: 8;"
        );

        HBox dateRow = new HBox(8, dateLabel, typeBadge);
        dateRow.setAlignment(Pos.CENTER_LEFT);

        info.getChildren().addAll(msgLabel, dateRow);

        // ===== UNREAD DOT =====
        if (isUnread) {
            Label dot = new Label("●");
            dot.setTextFill(Color.web("#1976D2"));
            dot.setFont(Font.font(12));

            card.getChildren().addAll(iconCircle, info, dot);

            // Click to mark read
            card.setOnMouseClicked(e -> {
                try {
                    NotificationService.markRead(notifId);
                    card.setStyle(
                        "-fx-background-color: white;" +
                        "-fx-background-radius: 12;"
                    );
                    dot.setText("");
                    msgLabel.setFont(Font.font("System", FontWeight.NORMAL, 13));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            card.setStyle(card.getStyle() + "-fx-cursor: hand;");

        } else {
            card.getChildren().addAll(iconCircle, info);
        }

        return card;
    }

    private String getIcon(String type) {
        switch (type) {
            case "WIN": return "🏆";
            case "LOSE": return "💔";
            case "BID": return "💰";
            case "WATCHLIST": return "⭐";
            case "WATCHLIST_BID": return "📢";
            case "CAR_APPROVED": return "✅";
            case "CAR_REJECTED": return "❌";
            case "SOLD": return "🎉";
            case "AUCTION_START": return "🔨";
            default: return "🔔";
        }
    }

    private String getIconBg(String type) {
        switch (type) {
            case "WIN": return "#E8F5E9";
            case "LOSE": return "#FFEBEE";
            case "BID": return "#E3F2FD";
            case "WATCHLIST": return "#FFF3E0";
            case "WATCHLIST_BID": return "#F3E5F5";
            case "CAR_APPROVED": return "#E8F5E9";
            case "CAR_REJECTED": return "#FFEBEE";
            case "SOLD": return "#E8F5E9";
            case "AUCTION_START": return "#E3F2FD";
            default: return "#F5F5F5";
        }
    }

    // For real-time notification additions
    public void addNotification(String msg) {
        HBox card = createCard(
            "RT_" + System.currentTimeMillis(),
            msg, "INFO", "UNREAD",
            java.time.LocalDateTime.now().toString()
        );
        list.getChildren().add(0, card);
    }
}