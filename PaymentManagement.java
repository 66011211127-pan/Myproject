import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class PaymentManagement extends JFrame {

    private Connection conn;
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtBank, txtAccountName, txtAccountNumber;

    public PaymentManagement() {
        setTitle("จัดการช่องทางการชำระเงิน");
        setSize(700, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        Font thaiFont = new Font("Tahoma", Font.PLAIN, 14);

        // ตาราง
        model = new DefaultTableModel(new String[]{"ธนาคาร", "ชื่อบัญชี", "เลขบัญชี"}, 0);
        table = new JTable(model);
        table.setFont(thaiFont);
        table.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 14));
        table.setRowHeight(25);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // ฟอร์มข้อมูล
        TitledBorder border = BorderFactory.createTitledBorder("ข้อมูลช่องทางชำระเงิน");
        border.setTitleFont(new Font("Tahoma", Font.BOLD, 16));

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(border);
        formPanel.setBackground(new Color(245, 245, 245));

        JLabel lblBank = new JLabel("ธนาคาร:");
        JLabel lblAccountName = new JLabel("ชื่อบัญชี:");
        JLabel lblAccountNumber = new JLabel("เลขบัญชี:");

        lblBank.setFont(thaiFont);
        lblAccountName.setFont(thaiFont);
        lblAccountNumber.setFont(thaiFont);

        txtBank = new JTextField();
        txtAccountName = new JTextField();
        txtAccountNumber = new JTextField();
        txtBank.setFont(thaiFont);
        txtAccountName.setFont(thaiFont);
        txtAccountNumber.setFont(thaiFont);

        formPanel.add(lblBank);
        formPanel.add(txtBank);
        formPanel.add(lblAccountName);
        formPanel.add(txtAccountName);
        formPanel.add(lblAccountNumber);
        formPanel.add(txtAccountNumber);

        add(formPanel, BorderLayout.NORTH);

        // ปุ่มคำสั่ง
        JPanel buttonPanel = new JPanel();
        JButton btnAdd = new JButton("เพิ่ม");
        JButton btnEdit = new JButton("แก้ไข");
        JButton btnDelete = new JButton("ลบ");
        JButton btnBack = new JButton("กลับเมนูหลัก");

        btnAdd.setFont(thaiFont);
        btnEdit.setFont(thaiFont);
        btnDelete.setFont(thaiFont);
        btnBack.setFont(thaiFont);

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnBack);
        add(buttonPanel, BorderLayout.SOUTH);

        // เชื่อมฐานข้อมูล
        try {
            conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/shoestore?useUnicode=true&characterEncoding=utf8",
                "root", ""
            );
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "ไม่สามารถเชื่อมต่อฐานข้อมูลได้\n" + e.getMessage(), "ข้อผิดพลาด", JOptionPane.ERROR_MESSAGE);
            return;
        }

        loadData();

        // การทำงานปุ่ม
        btnAdd.addActionListener(e -> addPayment());
        btnEdit.addActionListener(e -> editPayment());
        btnDelete.addActionListener(e -> deletePayment());
        btnBack.addActionListener(e -> {
            dispose();
            new AdminMenu().setVisible(true);
        });

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                txtBank.setText(model.getValueAt(row, 0).toString());
                txtAccountName.setText(model.getValueAt(row, 1).toString());
                txtAccountNumber.setText(model.getValueAt(row, 2).toString());
            }
        });
    }

    private void loadData() {
        model.setRowCount(0);
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM payment_method");
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("bank"),
                        rs.getString("account_name"),
                        rs.getString("account_number")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "ไม่สามารถโหลดข้อมูลได้\n" + e.getMessage(), "ข้อผิดพลาด", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addPayment() {
        try {
            String bank = txtBank.getText().trim();
            String accountName = txtAccountName.getText().trim();
            String accountNumber = txtAccountNumber.getText().trim();
            if (bank.isEmpty() || accountName.isEmpty() || accountNumber.isEmpty()) {
                JOptionPane.showMessageDialog(this, "กรุณากรอกข้อมูลให้ครบ", "แจ้งเตือน", JOptionPane.WARNING_MESSAGE);
                return;
            }
            PreparedStatement pst = conn.prepareStatement(
                    "INSERT INTO payment_method(bank, account_name, account_number) VALUES (?, ?, ?)"
            );
            pst.setString(1, bank);
            pst.setString(2, accountName);
            pst.setString(3, accountNumber);
            pst.executeUpdate();
            loadData();
            clearForm();
            JOptionPane.showMessageDialog(this, "เพิ่มช่องทางชำระเงินเรียบร้อย", "สำเร็จ", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "เกิดข้อผิดพลาด: " + e.getMessage(), "ข้อผิดพลาด", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editPayment() {
        try {
            int row = table.getSelectedRow();
            if (row == -1) {
                UIManager.put("OptionPane.messageFont", new Font("Tahoma", Font.PLAIN, 14));
                UIManager.put("OptionPane.buttonFont", new Font("Tahoma", Font.PLAIN, 14));
                JOptionPane.showMessageDialog(this, "กรุณาเลือกช่องทางที่ต้องการแก้ไข", "แจ้งเตือน", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String bank = txtBank.getText().trim();
            String accountName = txtAccountName.getText().trim();
            String accountNumber = txtAccountNumber.getText().trim();
            PreparedStatement pst = conn.prepareStatement(
                    "UPDATE payment_method SET bank=?, account_name=?, account_number=? WHERE account_number=?"
            );
            pst.setString(1, bank);
            pst.setString(2, accountName);
            pst.setString(3, accountNumber);
            pst.setString(4, accountNumber);
            pst.executeUpdate();
            loadData();
            clearForm();
            UIManager.put("OptionPane.messageFont", new Font("Tahoma", Font.PLAIN, 14));
            UIManager.put("OptionPane.buttonFont", new Font("Tahoma", Font.PLAIN, 14));
            JOptionPane.showMessageDialog(this, "แก้ไขช่องทางชำระเงินเรียบร้อย", "สำเร็จ", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            UIManager.put("OptionPane.messageFont", new Font("Tahoma", Font.PLAIN, 14));
            UIManager.put("OptionPane.buttonFont", new Font("Tahoma", Font.PLAIN, 14));
            JOptionPane.showMessageDialog(this, "เกิดข้อผิดพลาด: " + e.getMessage(), "ข้อผิดพลาด", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void deletePayment() {
        try {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "กรุณาเลือกช่องทางที่ต้องการลบ", "แจ้งเตือน", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String accountNumber = model.getValueAt(row, 2).toString();
            int confirm = JOptionPane.showConfirmDialog(this, "คุณต้องการลบช่องทางนี้หรือไม่?", "ยืนยัน", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                PreparedStatement pst = conn.prepareStatement("DELETE FROM payment_method WHERE account_number=?");
                pst.setString(1, accountNumber);
                pst.executeUpdate();
                loadData();
                clearForm();
                JOptionPane.showMessageDialog(this, "ลบช่องทางชำระเงินเรียบร้อย", "สำเร็จ", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "เกิดข้อผิดพลาด: " + e.getMessage(), "ข้อผิดพลาด", JOptionPane.ERROR_MESSAGE);
        }
    }
    

    private void clearForm() {
        txtBank.setText("");
        txtAccountName.setText("");
        txtAccountNumber.setText("");
    }
}
