package Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import DB.DBConnection;

public class IDGenerator {

    public static String generateID(String prefix, String table, String column) {
        String newId = prefix + "0001";

        String sql = "SELECT " + column + " FROM " + table + " ORDER BY " + column + " DESC LIMIT 1";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                String lastId = rs.getString(1); // e.g. BID0005
                int num = Integer.parseInt(lastId.substring(3)); // 5
                num++;
                newId = String.format("%s%04d", prefix, num); // BID0006
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return newId;
    }
}
