import javax.swing.*;
import java.awt.*;

public class AdminMenu extends JFrame {

    public AdminMenu() {
        setTitle("ระบบผู้ดูแลร้านขายรองเท้า");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(new Color(245, 245, 245));
        setContentPane(contentPane);

        JLabel lblTitle = new JLabel("ระบบผู้ดูแลร้านขายรองเท้า", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Tahoma", Font.BOLD, 20));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        contentPane.add(lblTitle, BorderLayout.NORTH);

        JPanel panelButtons = new JPanel(new GridLayout(3, 2, 20, 20));
        panelButtons.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        panelButtons.setBackground(new Color(245, 245, 245));
        contentPane.add(panelButtons, BorderLayout.CENTER);

        JButton btnCategory = new JButton("จัดการหมวดหมู่สินค้า");
        JButton btnProduct = new JButton("จัดการสินค้า");
        JButton btnPayment = new JButton("จัดการช่องทางชำระเงิน");
        JButton btnShipping = new JButton("จัดการช่องทางส่ง");
        JButton btnDelivery = new JButton("จัดการการจัดส่ง");
        JButton btnReview = new JButton("จัดการรีวิวและคะแนน");

        JButton[] buttons = {btnCategory, btnProduct, btnPayment, btnShipping, btnDelivery, btnReview};
        for (JButton btn : buttons) {
            btn.setFont(new Font("Tahoma", Font.BOLD, 14));
            btn.setBackground(new Color(100, 149, 237));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            panelButtons.add(btn);
        }

        JButton btnLogout = new JButton("ออกจากระบบ");
        btnLogout.setFont(new Font("Tahoma", Font.PLAIN, 14));
        btnLogout.setBackground(Color.LIGHT_GRAY);
        btnLogout.setFocusPainted(false);
        contentPane.add(btnLogout, BorderLayout.SOUTH);

        // การทำงานปุ่ม
        btnCategory.addActionListener(e -> {
            dispose();
            new CategoryManagement().setVisible(true);
        });
        btnProduct.addActionListener(e -> {
            dispose();
            new ProductManagement().setVisible(true);
        });
        btnPayment.addActionListener(e -> {
            dispose();
            new PaymentManagement().setVisible(true);
        });
       btnShipping.addActionListener(e -> {
           dispose();
            new ShippingManagement().setVisible(true);
        });
        /*btnDelivery.addActionListener(e -> {
            dispose();
            new DeliveryManagement().setVisible(true);
        });
        btnReview.addActionListener(e -> {
            dispose();
            new ReviewManagement().setVisible(true);
        });
        btnLogout.addActionListener(e -> {
            dispose();
            new AdminLogin().setVisible(true);
        });*/
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminMenu().setVisible(true));
    }
}
