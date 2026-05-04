import java.sql.*;

public class ListBooks {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:derby:Database/lmsdb";
        Connection conn = DriverManager.getConnection(url);
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM book");
        System.out.println("ID | ISBN | Title | Author | Total | Available");
        while (rs.next()) {
            System.out.printf("%d | %s | %s | %s | %d | %d\n",
                rs.getInt("id"), rs.getString("isbn"), rs.getString("title"),
                rs.getString("author"), rs.getInt("total_copies"), rs.getInt("available_copies"));
        }
        conn.close();
    }
}
