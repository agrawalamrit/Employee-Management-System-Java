import java.io.FileInputStream;
import java.sql.*;
import java.util.Properties;
import java.util.Scanner;

public class Employee {

    public static void main(String[] args) {
        try {
            Properties props = new Properties();
            FileInputStream fis = new FileInputStream("config.properties");
            props.load(fis);

            String dbUrl = props.getProperty("db.url");
            String dbUser = props.getProperty("db.user");
            String dbPassword = props.getProperty("db.password");

            try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                 Scanner sc = new Scanner(System.in)) {

                Class.forName("com.mysql.cj.jdbc.Driver");

                while (true) {
                    System.out.print("\n===== Employee Management System =====\n" +
                                            "1) Add New Employee\n" +
                                            "2) View All Employees\n" +
                                            "3) Search Employee\n" +
                                            "4) Update Employee Details\n" +
                                            "5) Delete Employee\n" +
                                            "6) Exit" +
                                            "\nEnter choice: ");
                    int mm_choice = sc.nextInt();
                    System.out.println();

                    String sql;
                    PreparedStatement stmt;
                    ResultSet rs;

                    switch (mm_choice) {
                        case 1:
                            sc.nextLine();
                            System.out.print("Enter Employee's First name: ");
                            String fname = sc.nextLine();
                            fname = fname.substring(0, 1).toUpperCase() + fname.substring(1).toLowerCase();

                            System.out.print("Enter Employee's Last name: ");
                            String lname = sc.nextLine();
                            lname = lname.substring(0, 1).toUpperCase() + lname.substring(1).toLowerCase();

                            System.out.print("Enter Employee's Email id: ");
                            String email_id = sc.nextLine();

                            System.out.print("Enter Employee's Phone Number: ");
                            String ph_no = sc.nextLine();

                            System.out.print("Enter Employee's Salary: ");
                            double sal = Double.parseDouble(sc.nextLine());

                            sql = "INSERT INTO employees (fname,lname,email,phone,salary) VALUES (?,?,?,?,?)";
                            stmt = conn.prepareStatement(sql);
                            stmt.setString(1, fname);
                            stmt.setString(2, lname);
                            stmt.setString(3, email_id);
                            stmt.setString(4, ph_no);
                            stmt.setDouble(5, sal);

                            int rows = stmt.executeUpdate();
                            if (rows > 0)
                                System.out.println("Employee details added to the DataBase successfully.");
                            break;

                        case 2:
                            sql = "SELECT * FROM employees";
                            stmt = conn.prepareStatement(sql);
                            rs = stmt.executeQuery();

                            String viewFormat = "%-5s | %-20s | %-25s | %-15s | %10s\n";

                            System.out.printf(viewFormat, "ID", "Name", "Email Id", "Phone Number", "Salary");
                            System.out.println("-".repeat(90));

                            while (rs.next()) {
                                int id = rs.getInt("id");
                                String fullName = rs.getString("fname") + " " + rs.getString("lname");

                                System.out.printf(viewFormat, id, fullName, rs.getString("email"), rs.getString("phone"), rs.getString("salary"));
                            }
                            break;

                        case 3:
                            System.out.print("Enter Employee ID to search: ");
                            int searchId = sc.nextInt();
                            sql = "SELECT * FROM employees WHERE id = ?";
                            stmt = conn.prepareStatement(sql);
                            stmt.setInt(1, searchId);
                            rs = stmt.executeQuery();

                            if (rs.next()) {
                                System.out.println("\n--- Employee Found ---");
                                String searchFormat = "%-20s: %s\n";
                                System.out.printf(searchFormat, "ID", rs.getInt("id"));
                                System.out.printf(searchFormat, "First Name", rs.getString("fname"));
                                System.out.printf(searchFormat, "Last Name", rs.getString("lname"));
                                System.out.printf(searchFormat, "Email", rs.getString("email"));
                                System.out.printf(searchFormat, "Phone", rs.getString("phone"));
                                System.out.printf(searchFormat, "Salary", rs.getDouble("salary"));
                                System.out.println("----------------------");
                            } else {
                                System.out.println("Employee with ID " + searchId + " not found.");
                            }
                            break;

                        case 4:
                            System.out.print("Enter Employee ID to update: ");
                            int updateId = sc.nextInt();
                            sc.nextLine();

                            sql = "SELECT * FROM employees WHERE id = ?";
                            stmt = conn.prepareStatement(sql);
                            stmt.setInt(1, updateId);
                            rs = stmt.executeQuery();

                            if (rs.next()) {
                                String oldFname = rs.getString("fname");
                                String oldLname = rs.getString("lname");
                                String oldEmail = rs.getString("email");
                                String oldPhone = rs.getString("phone");
                                double oldSalary = rs.getDouble("salary");

                                System.out.print("Enter new First Name (current: " + oldFname + ") or press Enter to keep: ");
                                String newFname = sc.nextLine();

                                System.out.print("Enter new Last Name (current: " + oldLname + ") or press Enter to keep: ");
                                String newLname = sc.nextLine();

                                System.out.print("Enter new Email (current: " + oldEmail + ") or press Enter to keep: ");
                                String newEmail = sc.nextLine();

                                System.out.print("Enter new Phone Number (current: " + oldPhone + ") or press Enter to keep: ");
                                String newPhone = sc.nextLine();

                                System.out.print("Enter new Salary (current: " + oldSalary + ") or press Enter to keep: ");
                                String newSalaryStr = sc.nextLine();

                                String finalFname = newFname.isEmpty() ? oldFname : newFname;
                                String finalLname = newLname.isEmpty() ? oldLname : newLname;
                                String finalEmail = newEmail.isEmpty() ? oldEmail : newEmail;
                                String finalPhone = newPhone.isEmpty() ? oldPhone : newPhone;
                                double finalSalary = newSalaryStr.isEmpty() ? oldSalary : Double.parseDouble(newSalaryStr);

                                sql = "UPDATE employees SET fname = ?, lname = ?, email = ?, phone = ?, salary = ? WHERE id = ?";
                                stmt = conn.prepareStatement(sql);
                                stmt.setString(1, finalFname);
                                stmt.setString(2, finalLname);
                                stmt.setString(3, finalEmail);
                                stmt.setString(4, finalPhone);
                                stmt.setDouble(5, finalSalary);
                                stmt.setInt(6, updateId);

                                int rowsAffected = stmt.executeUpdate();
                                if (rowsAffected > 0) {
                                    System.out.println("Employee details updated successfully.");
                                } else {
                                    System.out.println("Update failed or no new data was provided.");
                                }
                            } else {
                                System.out.println("Employee with ID " + updateId + " not found.");
                            }
                            break;

                        case 5:
                            System.out.print("Enter Employee ID to delete: ");
                            int deleteId = sc.nextInt();

                            sql = "DELETE FROM employees WHERE id = ?";
                            stmt = conn.prepareStatement(sql);
                            stmt.setInt(1, deleteId);

                            int rowsAffected = stmt.executeUpdate();
                            if (rowsAffected > 0) {
                                System.out.println("Employee with ID " + deleteId + " was deleted successfully.");
                            } else {
                                System.out.println("Employee with ID " + deleteId + " not found.");
                            }
                            break;

                        case 6:
                            System.out.println("Exiting application.");
                            return;

                        default:
                            System.out.println("Invalid choice. Please enter a number between 1 and 6.");
                            break;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}