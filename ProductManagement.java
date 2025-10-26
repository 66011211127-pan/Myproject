import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ProductManagement extends JFrame {

    private static final long serialVersionUID = 1L;
    private Connection conn;
    private JTable table;
    private DefaultTableModel model;

    private JTextField txtProductID, txtName, txtCategory, txtPrice, txtSize;
    private JTextArea txtDetails;

    public ProductManagement() {
        setTitle("ระบบจัดการสินค้า - ร้านขายรองเท้า");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // === เชื่อมต่อฐานข้อมูล ===
        try {
            conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/shoestore?useUnicode=true&characterEncoding=utf8",
                "root", ""
            );
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "เชื่อมต่อฐานข้อมูลไม่สำเร็จ: " + e.getMessage(),
                    "ข้อผิดพลาด", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // === ตั้งค่าฟอนต์ภาษาไทย ===
        Font thaiFont = new Font("TH Sarabun New", Font.PLAIN, 18);
        UIManager.put("Label.font", thaiFont);
        UIManager.put("Button.font", thaiFont);
        UIManager.put("Table.font", thaiFont);
        UIManager.put("TableHeader.font", thaiFont);
        UIManager.put("TextField.font", thaiFont);
        UIManager.put("TextArea.font", thaiFont);

        // === ตารางแสดงข้อมูลสินค้า ===
        model = new DefaultTableModel(new String[]{"รหัสสินค้า", "ชื่อสินค้า", "หมวดหมู่", "ราคา", "ขนาด", "รายละเอียด"}, 0);
        table = new JTable(model);
        table.setFont(thaiFont);
        table.getTableHeader().setFont(new Font("TH Sarabun New", Font.BOLD, 18));
        table.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // === ฟอร์มข้อมูลสินค้า ===
        TitledBorder border = BorderFactory.createTitledBorder("ข้อมูลสินค้า");
        border.setTitleFont(new Font("TH Sarabun New", Font.BOLD, 20));

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(border);
        formPanel.setBackground(new Color(245, 245, 245));

        JLabel lblID = new JLabel("รหัสสินค้า:");
        JLabel lblName = new JLabel("ชื่อสินค้า:");
        JLabel lblCategory = new JLabel("หมวดหมู่:");
        JLabel lblPrice = new JLabel("ราคา:");
        JLabel lblSize = new JLabel("ขนาด:");
        JLabel lblDetails = new JLabel("รายละเอียด:");

        lblID.setFont(thaiFont);
        lblName.setFont(thaiFont);
        lblCategory.setFont(thaiFont);
        lblPrice.setFont(thaiFont);
        lblSize.setFont(thaiFont);
        lblDetails.setFont(thaiFont);

        txtProductID = new JTextField();
        txtName = new JTextField();
        txtCategory = new JTextField();
        txtPrice = new JTextField();
        txtSize = new JTextField();
        txtDetails = new JTextArea(3, 20);
        txtDetails.setFont(thaiFont);

        formPanel.add(lblID);
        formPanel.add(txtProductID);
        formPanel.add(lblName);
        formPanel.add(txtName);
        formPanel.add(lblCategory);
        formPanel.add(txtCategory);
        formPanel.add(lblPrice);
        formPanel.add(txtPrice);
        formPanel.add(lblSize);
        formPanel.add(txtSize);
        formPanel.add(lblDetails);
        formPanel.add(new JScrollPane(txtDetails));

        add(formPanel, BorderLayout.NORTH);

        // === ปุ่มคำสั่ง ===
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(230, 230, 230));

        JButton btnAdd = new JButton("เพิ่มสินค้า");
        JButton btnEdit = new JButton("แก้ไขสินค้า");
        JButton btnDelete = new JButton("ลบสินค้า");
        JButton btnClear = new JButton("ล้างข้อมูล");
        JButton btnBack = new JButton("กลับเมนูหลัก");

        btnAdd.setFont(thaiFont);
        btnEdit.setFont(thaiFont);
        btnDelete.setFont(thaiFont);
        btnClear.setFont(thaiFont);
        btnBack.setFont(thaiFont);

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnBack);

        add(buttonPanel, BorderLayout.SOUTH);

        // === โหลดข้อมูลเริ่มต้น ===
        loadData();

        // === การทำงานของปุ่ม ===
        btnAdd.addActionListener(e -> addProduct());
        btnEdit.addActionListener(e -> editProduct());
        btnDelete.addActionListener(e -> deleteProduct());
        btnClear.addActionListener(e -> clearForm());
        btnBack.addActionListener(e -> {
            dispose();
            new AdminMenu().setVisible(true);
        });

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                txtProductID.setText(model.getValueAt(row, 0).toString());
                txtName.setText(model.getValueAt(row, 1).toString());
                txtCategory.setText(model.getValueAt(row, 2).toString());
                txtPrice.setText(model.getValueAt(row, 3).toString());
                txtSize.setText(model.getValueAt(row, 4).toString());
                txtDetails.setText(model.getValueAt(row, 5).toString());
            }
        });
    }

    // === โหลดข้อมูลจากฐานข้อมูล ===
    private void loadData() {
        model.setRowCount(0);
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM product");
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("product_id"),
                    rs.getString("name"),
                    rs.getString("category"),
                    rs.getDouble("price"),
                    rs.getString("size"),
                    rs.getString("details")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "ไม่สามารถโหลดข้อมูลได้: " + e.getMessage(),
                    "ข้อผิดพลาด", JOptionPane.ERROR_MESSAGE);
        }
    }

    // === เพิ่มสินค้า ===
    private void addProduct() {
        try {
            String id = txtProductID.getText().trim();
            String name = txtName.getText().trim();
            String category = txtCategory.getText().trim();
            String price = txtPrice.getText().trim();
            String size = txtSize.getText().trim();
            String details = txtDetails.getText().trim();

            if (id.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "กรุณากรอกรหัสสินค้าและชื่อสินค้า", "แจ้งเตือน",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String sql = "INSERT INTO product VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, id);
            pst.setString(2, name);
            pst.setString(3, category);
            pst.setString(4, price);
            pst.setString(5, size);
            pst.setString(6, details);
            pst.executeUpdate();

            loadData();
            clearForm();
            JOptionPane.showMessageDialog(this, "เพิ่มสินค้าสำเร็จ");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "เกิดข้อผิดพลาด: " + e.getMessage(),
                    "ข้อผิดพลาด", JOptionPane.ERROR_MESSAGE);
        }
    }

    // === แก้ไขสินค้า ===
    private void editProduct() {
        try {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "กรุณาเลือกสินค้าที่ต้องการแก้ไข", "แจ้งเตือน",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String id = txtProductID.getText().trim();
            String name = txtName.getText().trim();
            String category = txtCategory.getText().trim();
            String price = txtPrice.getText().trim();
            String size = txtSize.getText().trim();
            String details = txtDetails.getText().trim();

            String sql = "UPDATE product SET name=?, category=?, price=?, size=?, details=? WHERE product_id=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, name);
            pst.setString(2, category);
            pst.setString(3, price);
            pst.setString(4, size);
            pst.setString(5, details);
            pst.setString(6, id);
            pst.executeUpdate();

            loadData();
            clearForm();
            JOptionPane.showMessageDialog(this, "แก้ไขสินค้าสำเร็จ");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "เกิดข้อผิดพลาด: " + e.getMessage(),
                    "ข้อผิดพลาด", JOptionPane.ERROR_MESSAGE);
        }
    }

    // === ลบสินค้า ===
    private void deleteProduct() {
        try {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "กรุณาเลือกสินค้าที่ต้องการลบ", "แจ้งเตือน",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String id = model.getValueAt(selectedRow, 0).toString();
            int confirm = JOptionPane.showConfirmDialog(this, "คุณต้องการลบสินค้านี้หรือไม่?",
                    "ยืนยันการลบ", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                String sql = "DELETE FROM product WHERE product_id=?";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, id);
                pst.executeUpdate();

                loadData();
                clearForm();
                JOptionPane.showMessageDialog(this, "ลบสินค้าสำเร็จ");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "เกิดข้อผิดพลาด: " + e.getMessage(),
                    "ข้อผิดพลาด", JOptionPane.ERROR_MESSAGE);
        }
    }

    // === ล้างฟอร์ม ===
    private void clearForm() {
        txtProductID.setText("");
        txtName.setText("");
        txtCategory.setText("");
        txtPrice.setText("");
        txtSize.setText("");
        txtDetails.setText("");
    }

    // === รันทดสอบหน้าเดียว ===
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ProductManagement().setVisible(true));
    }
}
