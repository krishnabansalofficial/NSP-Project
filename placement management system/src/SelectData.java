import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.table.DefaultTableModel;

public class SelectData {

    // 1. Check Login Credentials (returns role: "ADMIN", "STUDENT", or null)
    public static String checkLogin(String email, String password) {
        String sql = "SELECT role FROM users WHERE email = ? AND password = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("role");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 2. Load all students into JTable DefaultTableModel
    public static void loadStudents(DefaultTableModel model) {
        model.setRowCount(0);
        String sql = "SELECT roll_no, name, branch, cgpa, status FROM students";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("roll_no"),
                    rs.getString("name"),
                    rs.getString("branch"),
                    rs.getString("cgpa"),
                    rs.getString("status"),
                    "" // Actions column
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 3. Load all placement company drives into DefaultTableModel
    public static void loadCompanies(DefaultTableModel model) {
        model.setRowCount(0);
        String sql = "SELECT drive_id, company_name, job_role, package_lpa, min_cgpa, drive_date, status FROM companies";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("drive_id"),
                    rs.getString("company_name"),
                    rs.getString("job_role"),
                    rs.getString("package_lpa"),
                    rs.getString("min_cgpa"),
                    rs.getString("drive_date"),
                    rs.getString("status"),
                    "" // Actions column
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 4. Load all notices into DefaultTableModel
    public static void loadNotices(DefaultTableModel model) {
        model.setRowCount(0);
        String sql = "SELECT notice_id, notice_date, details FROM notices";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("notice_id"),
                    rs.getString("notice_date"),
                    rs.getString("details"),
                    "" // Actions column
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 5. Get current Admin Password
    public static String getAdminPassword() {
        return getUserPassword("college@poornima.org");
    }

    // 6. Get Any User Password by Email
    public static String getUserPassword(String email) {
        String sql = "SELECT password FROM users WHERE email = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("password");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
