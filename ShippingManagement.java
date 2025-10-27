import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ShippingManagement extends JFrame {

    private Connection conn;
    private JTable table;
    private DefaultTableModel model;

    private JComboBox<String> cmbProvider;
    private JRadioButton rbFixed, rbPerItem, rbByWeight;
    private JRadioButton rbNormal, rbCOD;
    private JTextField txtFee;
    private ButtonGroup groupCalc, groupPayment;

    public ShippingManagement() {
        setTitle("จัดการช่องทางการส่งสินค้า");
        setSize(750, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        Font thaiFont = new Font("Tahoma", Font.PLAIN, 14);

        // ตารางแสดงข้อมูล
        model = new DefaultTableModel(new String[]{
                "ผู้ให้บริการ", "รูปแบบคำนวณ", "วิธีเก็บเงิน", "ค่าส่ง"
        }, 0);
        table = new JTable(model);
        table.setFont(thaiFont);
        table.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 14));
        table.setRowHeight(25);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // ฟอร์มข้อมูล
        TitledBorder border = BorderFactory.createTitledBorder("ข้อมูลช่องทางการส่ง");
        border.setTitleFont(new Font("Tahoma", Font.BOLD, 16));

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(border);
        formPanel.setBackground(new Color(245, 245, 245));

        JLabel lblProvider = new JLabel("ผู้ให้บริการส่งสินค้า:");
        lblProvider.setFont(thaiFont);
        cmbProvider = new JComboBox<>(new String[]{
                "Kerry Express", "Flash Express", "J&T Express"
        });
        cmbProvider.setFont(thaiFont);

        JLabel lblCalc = new JLabel("รูปแบบการคำนวณ:");
        lblCalc.setFont(thaiFont);
        rbFixed = new JRadioButton("คิดราคาแบบคงที่");
        rbPerItem = new JRadioButton("คิดตามจำนวนสินค้า");
        rbByWeight = new JRadioButton("คิดตามน้ำหนักพัสดุ");
        rbFixed.setFont(thaiFont);
        rbPerItem.setFont(thaiFont);
        rbByWeight.setFont(thaiFont);
        groupCalc = new ButtonGroup();
        groupCalc.add(rbFixed);
        groupCalc.add(rbPerItem);
        groupCalc.add(rbByWeight);
        JPanel calcPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        calcPanel.setBackground(new Color(245, 245, 245));
        calcPanel.add(rbFixed);
        calcPanel.add(rbPerItem);
        calcPanel.add(rbByWeight);

        JLabel lblPayment = new JLabel("การเก็บเงินค่าสินค้า:");
        lblPayment.setFont(thaiFont);
        rbNormal = new JRadioButton("เก็บเงินปกติ");
        rbCOD = new JRadioButton("เก็บเงินปลายทาง");
        rbNormal.setFont(thaiFont);
        rbCOD.setFont(thaiFont);
        groupPayment = new ButtonGroup();
        groupPayment.add(rbNormal);
        groupPayment.add(rbCOD);
        JPanel paymentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        paymentPanel.setBackground(new Color(245, 245, 245));
        paymentPanel.add(rbNormal);
        paymentPanel.add(rbCOD);

        JLabel lblFee = new JLabel("ค่าส่งสินค้า:");
        lblFee.setFont(thaiFont);
        txtFee = new JTextField();
        txtFee.setFont(thaiFont);

        formPanel.add(lblProvider);
        formPanel.add(cmbProvider);
        formPanel.add(lblCalc);
        formPanel.add(calcPanel);
        formPanel.add(lblPayment);
        formPanel.add(paymentPanel);
        formPanel.add(lblFee);
        formPanel.add(txtFee);

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

        // เชื่อมฐานข้อมูล MySQL
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/shoestore?useUnicode=true&characterEncoding=utf8",
                    "root", ""
            );
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "เชื่อมต่อฐานข้อมูลไม่สำเร็จ: " + e.getMessage());
            return;
        }

        loadData();

        // การทำงานปุ่ม
        btnAdd.addActionListener(e -> addShipping());
        btnEdit.addActionListener(e -> editShipping());
        btnDelete.addActionListener(e -> deleteShipping());
        btnBack.addActionListener(e -> {
            new AdminMenu().setVisible(true); // เปิดหน้า AdminMenu
            dispose(); // ปิดหน้าต่าง ShippingManagement
        });

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                cmbProvider.setSelectedItem(model.getValueAt(row, 0));
                String calc = model.getValueAt(row, 1).toString();
                rbFixed.setSelected(calc.equals("คิดราคาแบบคงที่"));
                rbPerItem.setSelected(calc.equals("คิดตามจำนวนสินค้า"));
                rbByWeight.setSelected(calc.equals("คิดตามน้ำหนักพัสดุ"));
                String payment = model.getValueAt(row, 2).toString();
                rbNormal.setSelected(payment.equals("เก็บเงินปกติ"));
                rbCOD.setSelected(payment.equals("เก็บเงินปลายทาง"));
                txtFee.setText(model.getValueAt(row, 3).toString());
            }
        });
    }

    private void loadData() {
        model.setRowCount(0);
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM shipping_method");
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("provider"),
                        rs.getString("calc_type"),
                        rs.getString("payment_type"),
                        rs.getDouble("fee")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "ไม่สามารถโหลดข้อมูลได้: " + e.getMessage());
        }
    }

    private void addShipping() {
        try {
            String provider = cmbProvider.getSelectedItem().toString();
            String calc = rbFixed.isSelected() ? "คิดราคาแบบคงที่" :
                    rbPerItem.isSelected() ? "คิดตามจำนวนสินค้า" :
                            rbByWeight.isSelected() ? "คิดตามน้ำหนักพัสดุ" : "";
            String payment = rbNormal.isSelected() ? "เก็บเงินปกติ" :
                    rbCOD.isSelected() ? "เก็บเงินปลายทาง" : "";
            String feeText = txtFee.getText().trim();
            if (provider.isEmpty() || calc.isEmpty() || payment.isEmpty() || feeText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "กรุณากรอกข้อมูลให้ครบ", "แจ้งเตือน", JOptionPane.WARNING_MESSAGE);
                return;
            }
            double fee = Double.parseDouble(feeText);
            PreparedStatement pst = conn.prepareStatement(
                    "INSERT INTO shipping_method(provider, calc_type, payment_type, fee) VALUES (?, ?, ?, ?)"
            );
            pst.setString(1, provider);
            pst.setString(2, calc);
            pst.setString(3, payment);
            pst.setDouble(4, fee);
            pst.executeUpdate();
            loadData();
            clearForm();
            JOptionPane.showMessageDialog(this, "เพิ่มช่องทางการส่งเรียบร้อย", "สำเร็จ", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "เกิดข้อผิดพลาด: " + e.getMessage(), "ข้อผิดพลาด", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editShipping() {
        try {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "กรุณาเลือกช่องทางที่ต้องการแก้ไข", "แจ้งเตือน", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String provider = cmbProvider.getSelectedItem().toString();
            String calc = rbFixed.isSelected() ? "คิดราคาแบบคงที่" :
                    rbPerItem.isSelected() ? "คิดตามจำนวนสินค้า" :
                            rbByWeight.isSelected() ? "คิดตามน้ำหนักพัสดุ" : "";
            String payment = rbNormal.isSelected() ? "เก็บเงินปกติ" :
                    rbCOD.isSelected() ? "เก็บเงินปลายทาง" : "";
            double fee = Double.parseDouble(txtFee.getText().trim());

            String oldProvider = table.getValueAt(row, 0).toString();
            String oldCalc = table.getValueAt(row, 1).toString();
            String oldPayment = table.getValueAt(row, 2).toString();

            PreparedStatement pst = conn.prepareStatement(
                    "UPDATE shipping_method SET provider=?, calc_type=?, payment_type=?, fee=? " +
                            "WHERE provider=? AND calc_type=? AND payment_type=?"
            );
            pst.setString(1, provider);
            pst.setString(2, calc);
            pst.setString(3, payment);
            pst.setDouble(4, fee);
            pst.setString(5, oldProvider);
            pst.setString(6, oldCalc);
            pst.setString(7, oldPayment);
            pst.executeUpdate();
            loadData();
            clearForm();
            JOptionPane.showMessageDialog(this, "แก้ไขช่องทางการส่งเรียบร้อย", "สำเร็จ", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "เกิดข้อผิดพลาด: " + e.getMessage(), "ข้อผิดพลาด", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteShipping() {
        try {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "กรุณาเลือกช่องทางที่ต้องการลบ", "แจ้งเตือน", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "คุณต้องการลบช่องทางนี้หรือไม่?", "ยืนยัน", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                String provider = table.getValueAt(row, 0).toString();
                String calc = table.getValueAt(row, 1).toString();
                String payment = table.getValueAt(row, 2).toString();
                PreparedStatement pst = conn.prepareStatement(
                        "DELETE FROM shipping_method WHERE provider=? AND calc_type=? AND payment_type=?"
                );
                pst.setString(1, provider);
                pst.setString(2, calc);
                pst.setString(3, payment);
                pst.executeUpdate();
                loadData();
                clearForm();
                JOptionPane.showMessageDialog(this, "ลบช่องทางการส่งเรียบร้อย", "สำเร็จ", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "เกิดข้อผิดพลาด: " + e.getMessage(), "ข้อผิดพลาด", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        cmbProvider.setSelectedIndex(0);
        groupCalc.clearSelection();
        groupPayment.clearSelection(); 
        txtFee.setText("");
    }

    public static void main(String[] args) {
        Font thaiFont = new Font("Tahoma", Font.PLAIN, 14);
        UIManager.put("OptionPane.messageFont", thaiFont);
        UIManager.put("OptionPane.buttonFont", thaiFont);

        SwingUtilities.invokeLater(() -> new ShippingManagement().setVisible(true));
    }
}
