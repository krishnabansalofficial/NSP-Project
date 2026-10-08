import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class insert {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter the Name: ");
        String name = sc.nextLine();

        System.out.print("Enter Password: ");
        String password = sc.nextLine();

        String url = "jdbc:mysql://localhost:3306/krishna";
        String user = "root";
        String dbPassword = "Krishna@01Mysql";

        String sql = "INSERT INTO student(name, password) VALUES (?, ?)";

        try (
                Connection con = DriverManager.getConnection(url, user, dbPassword);
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setString(1, name);
            ps.setString(2, password);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Data inserted successfully!");
            } else {
                System.out.println("Not inserted");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        sc.close();
    }
}