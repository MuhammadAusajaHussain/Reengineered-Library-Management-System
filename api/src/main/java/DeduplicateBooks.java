import java.sql.*;
import java.util.*;

public class DeduplicateBooks {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:derby:Database/lmsdb";
        Connection conn = DriverManager.getConnection(url);
        conn.setAutoCommit(false);

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT isbn, title, author, COUNT(*) as cnt FROM book GROUP BY isbn, title, author HAVING COUNT(*) > 1");
            
            List<String[]> duplicates = new ArrayList<>();
            while (rs.next()) {
                duplicates.add(new String[]{rs.getString("isbn"), rs.getString("title"), rs.getString("author")});
            }
            rs.close();

            for (String[] dup : duplicates) {
                String isbn = dup[0];
                String title = dup[1];
                String author = dup[2];

                System.out.println("Merging duplicates for: " + title + " by " + author + " (ISBN: " + isbn + ")");

                PreparedStatement ps = conn.prepareStatement("SELECT id, total_copies, available_copies FROM book WHERE (isbn = ? OR (isbn IS NULL AND ? IS NULL)) AND title = ? AND author = ? ORDER BY id");
                ps.setString(1, isbn);
                ps.setString(2, isbn);
                ps.setString(3, title);
                ps.setString(4, author);
                ResultSet rs2 = ps.executeQuery();

                int targetId = -1;
                int totalCopiesSum = 0;
                int availableCopiesSum = 0;
                List<Integer> sourceIds = new ArrayList<>();

                while (rs2.next()) {
                    int id = rs2.getInt("id");
                    int total = rs2.getInt("total_copies");
                    int available = rs2.getInt("available_copies");

                    if (targetId == -1) {
                        targetId = id;
                    } else {
                        sourceIds.add(id);
                    }
                    totalCopiesSum += total;
                    availableCopiesSum += available;
                }
                rs2.close();

                if (targetId != -1 && !sourceIds.isEmpty()) {
                    // Update target with summed copies
                    PreparedStatement updateTarget = conn.prepareStatement("UPDATE book SET total_copies = ?, available_copies = ? WHERE id = ?");
                    updateTarget.setInt(1, totalCopiesSum);
                    updateTarget.setInt(2, availableCopiesSum);
                    updateTarget.setInt(3, targetId);
                    updateTarget.executeUpdate();

                    for (int srcId : sourceIds) {
                        // Move loans
                        PreparedStatement moveLoans = conn.prepareStatement("UPDATE loan SET book_id = ? WHERE book_id = ?");
                        moveLoans.setInt(1, targetId);
                        moveLoans.setInt(2, srcId);
                        moveLoans.executeUpdate();

                        // Move holds
                        PreparedStatement moveHolds = conn.prepareStatement("UPDATE hold_request SET book_id = ? WHERE book_id = ?");
                        moveHolds.setInt(1, targetId);
                        moveHolds.setInt(2, srcId);
                        moveHolds.executeUpdate();

                        // Delete source book
                        PreparedStatement deleteSource = conn.prepareStatement("DELETE FROM book WHERE id = ?");
                        deleteSource.setInt(1, srcId);
                        deleteSource.executeUpdate();
                    }
                }
            }

            conn.commit();
            System.out.println("Deduplication complete.");
        } catch (Exception e) {
            conn.rollback();
            e.printStackTrace();
        } finally {
            conn.close();
        }
    }
}
