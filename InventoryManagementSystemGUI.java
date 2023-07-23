import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class InventoryManagementSystemGUI {

    // Database connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/products_database";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "7599";

    // SQL statements
    private static final String INSERT_PRODUCT = "INSERT INTO products (name, description, quantity, price) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_PRODUCT = "UPDATE products SET name = ?, description = ?, quantity = ?, price = ? WHERE id = ?";
    private static final String DELETE_PRODUCT = "DELETE FROM products WHERE id = ?";
    private static final String SELECT_ALL_PRODUCTS = "SELECT * FROM products";
    private static final String SELECT_PRODUCT_BY_NAME = "SELECT * FROM products WHERE name LIKE ?";

    private JFrame frame;
    private JTextField nameField;
    private JTextField descriptionField;
    private JTextField quantityField;
    private JTextField priceField;
    private JTable productsTable;
    private DefaultTableModel tableModel;

    public static void main(String[] args) {
        try {
            // Set the look and feel to system default
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        EventQueue.invokeLater(() -> {
            try {
                InventoryManagementSystemGUI window = new InventoryManagementSystemGUI();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public InventoryManagementSystemGUI() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 700, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JPanel panel = new JPanel();
        panel.setBounds(0, 0, 684, 461);
        frame.getContentPane().add(panel);
        panel.setLayout(null);

        JLabel titleLabel = new JLabel("Inventory Management System");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        titleLabel.setBounds(150, 10, 400, 25);
        panel.add(titleLabel);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setBounds(50, 60, 80, 25);
        panel.add(nameLabel);

        JLabel descriptionLabel = new JLabel("Description:");
        descriptionLabel.setBounds(50, 100, 80, 25);
        panel.add(descriptionLabel);

        JLabel quantityLabel = new JLabel("Quantity:");
        quantityLabel.setBounds(50, 140, 80, 25);
        panel.add(quantityLabel);

        JLabel priceLabel = new JLabel("Price:");
        priceLabel.setBounds(50, 180, 80, 25);
        panel.add(priceLabel);

        nameField = new JTextField();
        nameField.setBounds(140, 60, 200, 25);
        panel.add(nameField);

        descriptionField = new JTextField();
        descriptionField.setBounds(140, 100, 200, 25);
        panel.add(descriptionField);

        quantityField = new JTextField();
        quantityField.setBounds(140, 140, 200, 25);
        panel.add(quantityField);

        priceField = new JTextField();
        priceField.setBounds(140, 180, 200, 25);
        panel.add(priceField);

        JButton addButton = new JButton("Add Product");
        addButton.setBounds(140, 220, 120, 25);
        panel.add(addButton);
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addProduct();
            }
        });

        JButton updateButton = new JButton("Update Product");
        updateButton.setBounds(270, 220, 120, 25);
        panel.add(updateButton);
        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateProduct();
            }
        });

        JButton deleteButton = new JButton("Delete Product");
        deleteButton.setBounds(400, 220, 120, 25);
        panel.add(deleteButton);
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteProduct();
            }
        });


        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(50, 260, 600, 180);
        panel.add(scrollPane);

        productsTable = new JTable();
        tableModel = new DefaultTableModel(new Object[][] {}, new String[] { "ID", "Name", "Description", "Quantity", "Price" }) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        productsTable.setModel(tableModel);
        scrollPane.setViewportView(productsTable);

        JButton viewButton = new JButton("View Products");
        viewButton.setBounds(50, 450, 120, 25);
        panel.add(viewButton);
        viewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showAllProducts();
            }
        });

        JButton searchButton = new JButton("Search Products");
        searchButton.setBounds(190, 450, 140, 25);
        panel.add(searchButton);
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchProductsByName();
            }
        });

        JButton loadAllButton = new JButton("Load All Products");
        loadAllButton.setBounds(350, 450, 160, 25);
        panel.add(loadAllButton);
        loadAllButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadAllProducts();
            }
        });

        // Set table cell alignment to center
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        productsTable.setDefaultRenderer(Object.class, centerRenderer);

        // Add padding to table cells
        productsTable.setIntercellSpacing(new Dimension(10, 5));

        // Set table header font and background color
        productsTable.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 12));
        productsTable.getTableHeader().setBackground(Color.WHITE);
        productsTable.getTableHeader().setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Set table row height
        productsTable.setRowHeight(25);
    }
    private void addProduct() {
        String name = nameField.getText();
        String description = descriptionField.getText();
        String quantityText = quantityField.getText();
        String priceText = priceField.getText();

        if (name.isEmpty() || description.isEmpty() || quantityText.isEmpty() || priceText.isEmpty()) {
            System.out.println("Please fill in all fields.");
            return;
        }

        int quantity;
        double price;
        try {
            quantity = Integer.parseInt(quantityText);
            price = Double.parseDouble(priceText);
        } catch (NumberFormatException e) {
            System.out.println("Invalid quantity or price format.");
            return;
        }

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(INSERT_PRODUCT)) {
            statement.setString(1, name);
            statement.setString(2, description);
            statement.setInt(3, quantity);
            statement.setDouble(4, price);
            statement.executeUpdate();
            System.out.println("Product added successfully.");
            clearFields();
            showAllProducts();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void updateProduct() {
        int selectedRow = productsTable.getSelectedRow();
        if (selectedRow == -1) {
            System.out.println("No product selected.");
            return;
        }

        int productId = (int) productsTable.getValueAt(selectedRow, 0);
        String name = nameField.getText();
        String description = descriptionField.getText();
        String quantityText = quantityField.getText();
        String priceText = priceField.getText();

        if (name.isEmpty() || description.isEmpty() || quantityText.isEmpty() || priceText.isEmpty()) {
            System.out.println("Please fill in all fields.");
            return;
        }

        int quantity;
        double price;
        try {
            quantity = Integer.parseInt(quantityText);
            price = Double.parseDouble(priceText);
        } catch (NumberFormatException e) {
            System.out.println("Invalid quantity or price format.");
            return;
        }

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(UPDATE_PRODUCT)) {
            statement.setString(1, name);
            statement.setString(2, description);
            statement.setInt(3, quantity);
            statement.setDouble(4, price);
            statement.setInt(5, productId);
            statement.executeUpdate();
            System.out.println("Product updated successfully.");
            clearFields();
            showAllProducts();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    private void loadAllProducts() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(SELECT_ALL_PRODUCTS)) {

            tableModel.setRowCount(0); // Clear existing data from the table model

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String description = resultSet.getString("description");
                int quantity = resultSet.getInt("quantity");
                double price = resultSet.getDouble("price");

                // Add the product data to the table model
                tableModel.addRow(new Object[] { id, name, description, quantity, price });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void deleteProduct() {
        int selectedRow = productsTable.getSelectedRow();
        if (selectedRow == -1) {
            System.out.println("No product selected.");
            return;
        }

        int productId = (int) productsTable.getValueAt(selectedRow, 0);

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(DELETE_PRODUCT)) {
            statement.setInt(1, productId);
            statement.executeUpdate();
            System.out.println("Product deleted successfully.");
            clearFields();
            showAllProducts();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void showAllProducts() {
        tableModel.setRowCount(0);

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(SELECT_ALL_PRODUCTS)) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String description = resultSet.getString("description");
                int quantity = resultSet.getInt("quantity");
                double price = resultSet.getDouble("price");
                Object[] rowData = { id, name, description, quantity, price };
                tableModel.addRow(rowData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void searchProductsByName() {
        String name = nameField.getText();

        if (name.isEmpty()) {
            System.out.println("Please enter a name to search.");
            return;
        }

        tableModel.setRowCount(0);

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(SELECT_PRODUCT_BY_NAME)) {
            statement.setString(1, "%" + name + "%");
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String productName = resultSet.getString("name");
                    String description = resultSet.getString("description");
                    int quantity = resultSet.getInt("quantity");
                    double price = resultSet.getDouble("price");
                    Object[] rowData = { id, productName, description, quantity, price };
                    tableModel.addRow(rowData);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        nameField.setText("");
        descriptionField.setText("");
        quantityField.setText("");
        priceField.setText("");
    }
}
