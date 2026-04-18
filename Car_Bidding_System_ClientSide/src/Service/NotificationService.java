package Service;

import Client.ClientManager;

public class NotificationService {

    public static String getNotifications(String userId) throws Exception {
        return ClientManager.send("GET_NOTIFICATIONS:" + userId);
    }

    public static String markRead(String notifId) throws Exception {
        return ClientManager.send("MARK_NOTIFICATION_READ:" + notifId);
    }

    public static String markAllRead(String userId) throws Exception {
        return ClientManager.send("MARK_ALL_NOTIFICATIONS_READ:" + userId);
    }
}
