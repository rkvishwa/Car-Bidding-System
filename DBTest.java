import DB.DBConnection;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

public class DBTest {
    public static void main(String[] args) {
        try (Connection con = DBConnection.getConnection()) {
            DatabaseMetaData dbm = con.getMetaData();
            ResultSet rs = dbm.getColumns(null, null, "admin_logs", null);
            while (rs.next()) {
                System.out.println(rs.getString("COLUMN_NAME") + " - " + rs.getString("TYPE_NAME") + " (" + rs.getInt("COLUMN_SIZE") + ")");
            }
            ResultSet fk = dbm.getImportedKeys(null, null, "admin_logs");
            while (fk.next()) {
                System.out.println("FK: " + fk.getString("FKCOLUMN_NAME") + " -> " + fk.getString("PKTABLE_NAME"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
