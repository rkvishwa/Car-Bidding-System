package Event;

public class AdminSession {

    private static String adminId;
    private static String role;
    private static String adminName;

    public static void set(String id, String userRole, String name) {
        adminId = id;
        role = userRole;
        adminName = name;
    }

    // Overload for logout (null, null)
    public static void set(String id, String userRole) {
        adminId = id;
        role = userRole;
        adminName = null;
    }

    public static String getAdminId() {
        return adminId;
    }

    public static String getRole() {
        return role;
    }

    public static String getAdminName() {
        return adminName;
    }
}