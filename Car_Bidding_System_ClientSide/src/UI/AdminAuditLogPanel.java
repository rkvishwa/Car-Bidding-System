package UI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class AdminAuditLogPanel {

    private VBox list = new VBox(8);
    private String allData = ""; // store for filtering

    public VBox getView() {

        VBox root = new VBox(18);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: transparent;");

        // ===== HEADER =====
        Label title = new Label("📜 Audit Logs");
        title.setFont(Font.font("System", FontWeight.BOLD, 26));
        title.setTextFill(Color.web("#1a1a2e"));

        Label subtitle = new Label("Track all administrative actions and changes");
        subtitle.setStyle("-fx-text-fill: #888; -fx-font-size: 13px;");

        VBox header = new VBox(4, title, subtitle);

        // ===== FILTER BAR =====
        HBox filterBar = new HBox(10);
        filterBar.setAlignment(Pos.CENTER_LEFT);
        filterBar.setPadding(new Insets(8, 14, 8, 14));
        filterBar.setStyle(
            "-fx-background-color: white; -fx-background-radius: 12;"
        );

        DropShadow filterShadow = new DropShadow();
        filterShadow.setColor(Color.rgb(0, 0, 0, 0.04));
        filterShadow.setRadius(15);
        filterShadow.setOffsetY(3);
        filterBar.setEffect(filterShadow);

        Label filterLabel = new Label("🔍 Filter:");
        filterLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");

        TextField filterField = new TextField();
        filterField.setPromptText("Search by Admin ID, Action, or Target ID...");
        filterField.setPrefWidth(350);
        filterField.setStyle(
            "-fx-background-radius: 8; -fx-border-radius: 8; -fx-background-color: #F9FAFB;" +
            "-fx-border-color: #E5E7EB; -fx-padding: 8 12; -fx-font-size: 12px;"
        );
        HBox.setHgrow(filterField, Priority.ALWAYS);

        Button filterBtn = new Button("Filter");
        filterBtn.setStyle(
            "-fx-background-color: #2563EB;" +
            "-fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold;" +
            "-fx-background-radius: 8; -fx-padding: 8 18; -fx-cursor: hand;"
        );

        Button clearBtn = new Button("Clear");
        clearBtn.setStyle(
            "-fx-background-color: #F5F5F5;" +
            "-fx-text-fill: #666; -fx-font-size: 12px;" +
            "-fx-background-radius: 8; -fx-padding: 8 14; -fx-cursor: hand;"
        );

        filterBtn.setOnAction(e -> {
            String query = filterField.getText().trim().toLowerCase();
            filterLogs(query);
        });

        clearBtn.setOnAction(e -> {
            filterField.clear();
            filterLogs("");
        });

        // Also filter on enter key
        filterField.setOnAction(e -> {
            String query = filterField.getText().trim().toLowerCase();
            filterLogs(query);
        });

        // Filter in real-time as user types
        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterLogs(newValue.trim().toLowerCase());
        });

        filterBar.getChildren().addAll(filterLabel, filterField, filterBtn, clearBtn);

        // ===== TABLE HEADER =====
        HBox tableHeader = new HBox();
        tableHeader.setPadding(new Insets(10, 20, 10, 20));
        tableHeader.setStyle(
            "-fx-background-color: #1a1a2e; -fx-background-radius: 10 10 0 0;"
        );

        Label h1 = createHeaderLabel("Log ID", 120);
        Label h2 = createHeaderLabel("Admin ID", 100);
        Label h3 = createHeaderLabel("Action", 180);
        Label h4 = createHeaderLabel("Target ID", 120);
        Label h5 = createHeaderLabel("Date", 160);

        tableHeader.getChildren().addAll(h1, h2, h3, h4, h5);

        VBox tableBox = new VBox(0, tableHeader, list);

        ScrollPane scroll = new ScrollPane(tableBox);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        root.getChildren().addAll(header, filterBar, scroll);

        return root;
    }

    private Label createHeaderLabel(String text, double width) {
        Label l = new Label(text);
        l.setPrefWidth(width);
        l.setMinWidth(width);
        l.setFont(Font.font("System", FontWeight.BOLD, 12));
        l.setTextFill(Color.web("#b0b0c8"));
        return l;
    }

    public void render(String res) {
        allData = res;
        filterLogs("");
    }

    private void filterLogs(String query) {

        list.getChildren().clear();

        if (allData == null || allData.trim().isEmpty()) {
            VBox emptyState = new VBox(10);
            emptyState.setAlignment(Pos.CENTER);
            emptyState.setPadding(new Insets(40));

            Label emptyIcon = new Label("📋");
            emptyIcon.setFont(Font.font(40));

            Label empty = new Label("No audit logs found");
            empty.setStyle("-fx-text-fill: #999; -fx-font-size: 16px;");

            emptyState.getChildren().addAll(emptyIcon, empty);
            list.getChildren().add(emptyState);
            return;
        }

        boolean alt = false;
        int count = 0;

        for (String row : allData.split("\n")) {
            if (row.trim().isEmpty()) continue;

            String[] d = row.split("\\|");
            if (d.length < 5) continue;

            // Filter check
            if (!query.isEmpty()) {
                String combined = (d[0] + d[1] + d[2] + d[3] + d[4]).toLowerCase();
                if (!combined.contains(query)) continue;
            }

            list.getChildren().add(createRow(d, alt));
            alt = !alt;
            count++;
        }

        if (count == 0 && !query.isEmpty()) {
            Label noResults = new Label("No logs matching '" + query + "'");
            noResults.setStyle("-fx-text-fill: #999; -fx-font-size: 13px; -fx-padding: 20;");
            list.getChildren().add(noResults);
        }
    }

    private HBox createRow(String[] d, boolean alt) {

        HBox row = new HBox();
        row.setPadding(new Insets(10, 20, 10, 20));
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle(
            "-fx-background-color: " + (alt ? "#f8f9fc" : "white") + ";"
        );

        // Hover effect
        row.setOnMouseEntered(e -> row.setStyle(
            "-fx-background-color: #E3F2FD;"
        ));
        row.setOnMouseExited(e -> row.setStyle(
            "-fx-background-color: " + (alt ? "#f8f9fc" : "white") + ";"
        ));

        Label l1 = createCellLabel(d[0], 120, "#666");
        Label l2 = createCellLabel(d[1], 100, "#333");
        Label l3 = createActionLabel(d[2], 180);
        Label l4 = createCellLabel(d[3], 120, "#666");
        Label l5 = createCellLabel(d[4], 160, "#999");

        row.getChildren().addAll(l1, l2, l3, l4, l5);

        return row;
    }

    private Label createCellLabel(String text, double width, String color) {
        Label l = new Label(text);
        l.setPrefWidth(width);
        l.setMinWidth(width);
        l.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 12px;");
        return l;
    }

    private Label createActionLabel(String action, double width) {
        Label l = new Label(action);
        l.setPrefWidth(width);
        l.setMinWidth(width);
        l.setFont(Font.font("System", FontWeight.BOLD, 12));

        // Color by action type
        String color;
        if (action.contains("APPROVE")) {
            color = "#2E7D32";
        } else if (action.contains("REJECT") || action.contains("BAN")) {
            color = "#C62828";
        } else if (action.contains("ADMIN") || action.contains("PROMOTE")) {
            color = "#7B1FA2";
        } else if (action.contains("STOP") || action.contains("CLOSE")) {
            color = "#E65100";
        } else {
            color = "#1565C0";
        }

        l.setTextFill(Color.web(color));
        return l;
    }
}