package Service;

import Client.ClientManager;

public class AuditService {

    public static String getLogs() throws Exception {
        return ClientManager.send("GET_AUDIT_LOGS");
    }
}