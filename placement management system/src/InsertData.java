import java.sql.Connection;
import java.sql.PreparedStatement;

public class InsertData {

    // 1. Insert Student
    public static boolean insertStudent(String rollNo, String name, String branch, String cgpa, String status) {
        String sql = "INSERT INTO students (roll_no, name, branch, cgpa, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, rollNo);
            ps.setString(2, name);
            ps.setString(3, branch);
            ps.setString(4, cgpa);
            ps.setString(5, status);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 2. Insert Company Drive
    public static boolean insertCompany(String driveId, String companyName, String jobRole, String pkg, String minCgpa, String date, String status) {
        String sql = "INSERT INTO companies (drive_id, company_name, job_role, package_lpa, min_cgpa, drive_date, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, driveId);
            ps.setString(2, companyName);
            ps.setString(3, jobRole);
            ps.setString(4, pkg);
            ps.setString(5, minCgpa);
            ps.setString(6, date);
            ps.setString(7, status);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 3. Insert Notice
    public static boolean insertNotice(String noticeId, String date, String details) {
        String sql = "INSERT INTO notices (notice_id, notice_date, details) VALUES (?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, noticeId);
            ps.setString(2, date);
            ps.setString(3, details);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 4. Insert User
    public static boolean insertUser(String email, String password, String role) {
        String sql = "INSERT INTO users (email, password, role) VALUES (?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);
            ps.setString(3, role);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
