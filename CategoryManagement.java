import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class CategoryManagement extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtCategoryName;
    private Connection conn;

    public CategoryManagement() {
        setTitle("จัดการหมวดหมู่สินค้า");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel contentPane = new JPanel(new BorderLayout(10, 10));
        contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane);

        JLabel lblTitle = new JLabel("จัดการหมวดหมู่สินค้า", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Tahoma", Font.BOLD, 20));
        contentPane.add(lblTitle, BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[]{"รหัส", "ชื่อหมวดหมู่"}, 0);
        table = new JTable(model);

        table.setFont(new Font("Tahoma", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 14));
        table.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(table);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        JPanel panelBottom = new JPanel(new GridLayout(2, 1, 5, 5));
        contentPane.add(panelBottom, BorderLayout.SOUTH);

        JPanel inputPanel = new JPanel(new FlowLayout());
        JLabel lblName = new JLabel("ชื่อหมวดหมู่:");
        lblName.setFont(new Font("Tahoma", Font.PLAIN, 14)); 
        txtCategoryName = new JTextField(20);
        txtCategoryName.setFont(new Font("Tahoma", Font.PLAIN, 14)); 
        inputPanel.add(lblName);
        inputPanel.add(txtCategoryName);
        panelBottom.add(inputPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnAdd = new JButton("เพิ่ม");
        JButton btnEdit = new JButton("แก้ไข");
        JButton btnDelete = new JButton("ลบ");
        JButton btnBack = new JButton("กลับเมนูหลัก");

        Font btnFont = new Font("Tahoma", Font.PLAIN, 14);
        btnAdd.setFont(btnFont);
        btnEdit.setFont(btnFont);
        btnDelete.setFont(btnFont);
        btnBack.setFont(btnFont);

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnBack);
        panelBottom.add(buttonPanel);

        connectDatabase();

        loadData();
        
        btnAdd.addActionListener(e -> addCategory());

        btnEdit.addActionListener(e -> editCategory());

        btnDelete.addActionListener(e -> deleteCategory());

        btnBack.addActionListener(e -> {
            dispose();
            new AdminMenu().setVisible(true);
        });

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    txtCategoryName.setText(model.getValueAt(row, 1).toString());
                }
            }
        });
    }

    // เชื่อมต่อฐานข้อมูล MySQL
    private void connectDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/shoestore?useSSL=false&serverTimezone=UTC";
            String user = "root"; // เปลี่ยนตาม MySQL ของคุณ
            String pass = "";     // เปลี่ยนตาม MySQL ของคุณ
            conn = DriverManager.getConnection(url, user, pass);
            System.out.println("เชื่อมต่อฐานข้อมูลสำเร็จ");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "เชื่อมต่อฐานข้อมูลไม่สำเร็จ: " + e.getMessage());
        }
    }

    // โหลดข้อมูลจาก DB
    private void loadData() {
        try {
            model.setRowCount(0); // ล้างตารางก่อน
            String sql = "SELECT * FROM category ORDER BY id";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString("id"), rs.getString("name")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // เพิ่มหมวดหมู่
    private void addCategory() {
        try {
            String name = txtCategoryName.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter the category name");
                return;
            }

            // สร้างรหัสใหม่
            String sqlMax = "SELECT MAX(id) FROM category";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sqlMax);
            String newId = "S001";
            if (rs.next() && rs.getString(1) != null) {
                int num = Integer.parseInt(rs.getString(1).substring(1)) + 1;
                newId = String.format("C%03d", num);
            }

            String sql = "INSERT INTO category(id, name) VALUES(?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, newId);
            pst.setString(2, name);
            pst.executeUpdate();
            loadData();
            txtCategoryName.setText("");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // แก้ไขหมวดหมู่
    private void editCategory() {
        try {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select you category to edit");
                return;
            }
            String id = model.getValueAt(selectedRow, 0).toString();
            String newName = txtCategoryName.getText().trim();
            if (newName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "กรุณากรอกชื่อใหม่");
                return;
            }

            String sql = "UPDATE category SET name=? WHERE id=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, newName);
            pst.setString(2, id);
            pst.executeUpdate();
            loadData();
            txtCategoryName.setText("");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ลบหมวดหมู่
    private void deleteCategory() {
        try {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select you category to delete");
                return;
            }
            String id = model.getValueAt(selectedRow, 0).toString();
            int confirm = JOptionPane.showConfirmDialog(this, "Do you want to delete this category?", "Sure", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                String sql = "DELETE FROM category WHERE id=?";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, id);
                pst.executeUpdate();
                loadData();
                txtCategoryName.setText("");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.setProperty("file.encoding", "UTF-8");

        SwingUtilities.invokeLater(() -> new CategoryManagement().setVisible(true));
    }
}
