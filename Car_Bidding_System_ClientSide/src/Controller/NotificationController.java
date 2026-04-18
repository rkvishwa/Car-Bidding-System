package Controller;

import Event.EventBus;
import UI.NotificationsPanel;
import javafx.application.Platform;

public class NotificationController {

    private NotificationsPanel view;

    public NotificationController(NotificationsPanel view) {
        this.view = view;

        EventBus.subscribe(event -> {

            if (!event.type.equals("NOTIFY")) return;

            Platform.runLater(() -> {
                view.addNotification(event.data.toString());
            });
        });
    }
}