import java.sql.Connection;
import java.sql.DriverManager;

public class TestConnection {
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/shoestore?useSSL=false&serverTimezone=UTC",
                "root",   // ชื่อผู้ใช้ MySQL ของคุณ
                ""        // ถ้ามีรหัสผ่านให้ใส่
            );
            System.out.println("✅ เชื่อมต่อฐานข้อมูลสำเร็จ!");
            conn.close();
        } catch (Exception e) {
            System.out.println("❌ เชื่อมต่อไม่สำเร็จ: " + e.getMessage());
        }
    }
}
