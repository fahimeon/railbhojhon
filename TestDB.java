import java.sql.*;

public class TestDB {
    public static void main(String[] args) throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:sqlite:railkhabar.db");
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("PRAGMA table_info(restaurant_owners);");
        boolean hasApproved = false;
        while(rs.next()) {
            String name = rs.getString("name");
            System.out.println("Column: " + name);
            if (name.equals("is_approved")) hasApproved = true;
        }
        if (!hasApproved) {
            System.out.println("MISSING is_approved COLUMN!");
        } else {
            System.out.println("is_approved column exists.");
        }
    }
}
