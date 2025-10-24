import java.awt.EventQueue;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.sql.*;

public class AdminLogin extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private Connection conn;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                AdminLogin frame = new AdminLogin();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public AdminLogin() {
        setTitle("Admin Login - Shoe Store");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 400, 300);
        setLocationRelativeTo(null);

        contentPane = new JPanel();
        contentPane.setBackground(new Color(245, 245, 245));
        contentPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblTitle = new JLabel("AdminLogin");
        lblTitle.setFont(new Font("Tahoma", Font.BOLD, 18));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setBounds(40, 10, 300, 30);
        contentPane.add(lblTitle);

        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblUsername.setBounds(50, 70, 80, 25);
        contentPane.add(lblUsername);

        txtUsername = new JTextField();
        txtUsername.setBounds(140, 70, 180, 25);
        contentPane.add(txtUsername);

        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblPassword.setBounds(50, 110, 80, 25);
        contentPane.add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(140, 110, 180, 25);
        contentPane.add(txtPassword);

        JButton btnLogin = new JButton("LOGIN");
        btnLogin.setFont(new Font("Tahoma", Font.BOLD, 14));
        btnLogin.setBackground(new Color(100, 149, 237));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setBounds(140, 160, 120, 35);
        contentPane.add(btnLogin);

        JLabel lblStatus = new JLabel("");
        lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
        lblStatus.setForeground(Color.RED);
        lblStatus.setBounds(50, 210, 280, 25);
        contentPane.add(lblStatus);

        connectDatabase();

        btnLogin.addActionListener(e -> {
            String username = txtUsername.getText();
            String password = String.valueOf(txtPassword.getPassword());
            if (checkLogin(username, password)) {
                JOptionPane.showMessageDialog(this, "Login Success!");
                dispose();
                new AdminMenu().setVisible(true);
            } else {
                lblStatus.setText("Incorrect Username or Password ");
            }
        });
    }

    private void connectDatabase() {
        try {
           
            Class.forName("com.mysql.cj.jdbc.Driver");

            String url = "jdbc:mysql://localhost:3306/shoestore?useSSL=false&serverTimezone=UTC";
            String user = "root";       
            String pass = "";            

            conn = DriverManager.getConnection(url, user, pass);
            System.out.println("เชื่อมต่อฐานข้อมูลสำเร็จ");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "เชื่อมต่อฐานข้อมูลไม่สำเร็จ: " + e.getMessage());
        }
    }

    private boolean checkLogin(String username, String password) {
        try {
            String sql = "SELECT * FROM admin WHERE username=? AND password=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, username);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
