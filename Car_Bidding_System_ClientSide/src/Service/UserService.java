package Service;

import Client.ClientManager;

public class UserService {

    public static String login(String email, String password) throws Exception {
        return ClientManager.send("LOGIN:" + email + ":" + password);
    }

    public static String signup(String name, String email, String password, String role) throws Exception {
        return ClientManager.send("SIGNUP:" + name + ":" + email + ":" + password + ":" + role);
    }

    public static String getUsers() throws Exception {
        return ClientManager.send("GET_USERS");
    }

    public static String deleteUser(String userId, String adminId) throws Exception {
        return ClientManager.send("DELETE_USER:" + userId + ":" + adminId);
    }

    public static String banUser(String userId, String adminId) throws Exception {
        return ClientManager.send("BAN_USER:" + userId + ":" + adminId);
    }

    public static String makeAdmin(String userId, String adminId) throws Exception {
        return ClientManager.send("MAKE_ADMIN:" + userId + ":" + adminId);
    }

    public static String forgotPassword(String email) throws Exception {
        return ClientManager.send("FORGOT_PASSWORD:" + email);
    }

    public static String getPendingUsers() throws Exception {
        return ClientManager.send("GET_PENDING_USERS");
    }

    public static String approveUser(String userId, String adminId) throws Exception {
        return ClientManager.send("APPROVE_USER:" + userId + ":" + adminId);
    }
}