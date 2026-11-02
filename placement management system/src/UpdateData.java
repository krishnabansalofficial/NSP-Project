import java.sql.Connection;
import java.sql.PreparedStatement;

public class UpdateData {

    // 1. Update Student
    public static boolean updateStudent(String rollNo, String name, String branch, String cgpa) {
        String sql = "UPDATE students SET name = ?, branch = ?, cgpa = ? WHERE roll_no = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, branch);
            ps.setString(3, cgpa);
            ps.setString(4, rollNo);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 2. Update Company Drive
    public static boolean updateCompany(String driveId, String companyName, String jobRole, String pkg, String minCgpa, String date) {
        String sql = "UPDATE companies SET company_name = ?, job_role = ?, package_lpa = ?, min_cgpa = ?, drive_date = ? WHERE drive_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, companyName);
            ps.setString(2, jobRole);
            ps.setString(3, pkg);
            ps.setString(4, minCgpa);
            ps.setString(5, date);
            ps.setString(6, driveId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 3. Update Notice
    public static boolean updateNotice(String noticeId, String date, String details) {
        String sql = "UPDATE notices SET notice_date = ?, details = ? WHERE notice_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, date);
            ps.setString(2, details);
            ps.setString(3, noticeId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 4. Update Admin Password
    public static boolean updateAdminPassword(String newPassword) {
        return updateUserPassword("college@poornima.org", newPassword);
    }

    // 5. Update Any User Password (Admin or Student)
    public static boolean updateUserPassword(String email, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE email = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, newPassword);
            ps.setString(2, email);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
