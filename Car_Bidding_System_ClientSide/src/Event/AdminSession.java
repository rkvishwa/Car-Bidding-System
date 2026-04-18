package Event;

public class AdminSession {

    private static String adminId;
    private static String role;

    public static void set(String id, String userRole) {
        adminId = id;
        role = userRole;
    }

    public static String getAdminId() {
        return adminId;
    }

    public static String getRole() {
        return role;
    }
}