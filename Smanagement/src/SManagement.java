import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;

public class SManagement {
    
    static final String URL = "jdbc:mysql://localhost:3306/StudentManagement";
    static final String USER = "root";
    static final String PASSWORD = "nethaji123";

    
    private static Connection conn;

    public static void main(String[] args) {
        connectDatabase();
        new LoginFrame();
    }

    private static void connectDatabase() {
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database Connection Failed!", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
    private static Font loadCustomFont(float size) {
        try {
            
            InputStream fontStream = SManagement.class.getResourceAsStream("/fonts/Poppins-Black.ttf");
            if (fontStream == null) {
                throw new IOException("Font file not found!");
            }
            
            Font font = Font.createFont(Font.TRUETYPE_FONT, fontStream);
            return font.deriveFont(size);
        } catch (Exception e) {
            e.printStackTrace();
            return new Font("Arial", Font.PLAIN, (int) size);
        }
    }

    static class BaseFrame extends JFrame {
        BaseFrame(String title, int width, int height) {
            setTitle(title);
            setSize(width, height);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);

            
            BackgroundPanel backgroundPanel = new BackgroundPanel();
            setContentPane(backgroundPanel);

            
            setLayout(new BorderLayout());
        }
    }

    static class BackgroundPanel extends JPanel {
        private Image background;

        BackgroundPanel() {
            try {
                
                background = new ImageIcon(getClass().getResource("/images/background.jpeg")).getImage();
            } catch (Exception e) {
                e.printStackTrace();
                background = null;
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g); 

            if (background != null) {
                
                g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
            } else {
                System.err.println("Background image not loaded!");
            }
        }
    }

    static class LoginFrame extends BaseFrame {
        LoginFrame() {
            super("Login", 600, 400);

            
            BackgroundPanel backgroundPanel = new BackgroundPanel();
            setContentPane(backgroundPanel);

            
            JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
            panel.setOpaque(false); 
            panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50)); 

            
            JLabel userLabel = new JLabel("Username:");
            userLabel.setFont(loadCustomFont(16));
            JTextField userField = new JTextField();
            userField.setPreferredSize(new Dimension(150, 25));
            userField.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

            
            JLabel passLabel = new JLabel("Password:");
            passLabel.setFont(loadCustomFont(16));
            JPasswordField passField = new JPasswordField();
            passField.setPreferredSize(new Dimension(150, 25));
            passField.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

            
            JButton loginButton = new JButton("Login");
            loginButton.setFont(loadCustomFont(16));
            loginButton.setBackground(new Color(34, 150, 243)); 
            loginButton.setForeground(Color.WHITE); 
            loginButton.setFocusPainted(false);
            loginButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

            
            panel.add(userLabel);
            panel.add(userField);
            panel.add(passLabel);
            panel.add(passField);
            panel.add(new JLabel()); 
            panel.add(loginButton);

            
            add(panel, BorderLayout.CENTER);

            
            loginButton.addActionListener(e -> {
                String username = userField.getText().trim();
                String password = new String(passField.getPassword()).trim();

                if (username.equals("admin") && password.equals("admin")) {
                    new AdminFrame(); 
                    dispose(); 
                } else {
                    if (validateStudentLogin(username, password)) {
                        new StudentFrame(username); 
                        dispose(); 
                    } else {
                        JOptionPane.showMessageDialog(this, "Invalid credentials!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            setVisible(true); 
        }

       
        private boolean validateStudentLogin(String username, String password) {
            try {
                String query = "SELECT * FROM students WHERE username = ? AND password = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, username);
                stmt.setString(2, password);

                ResultSet rs = stmt.executeQuery();
                return rs.next(); 
            } catch (SQLException e) {
                e.printStackTrace();
                return false; 
            }
        }
    }

    static class AdminFrame extends BaseFrame {
        AdminFrame() {
            super("Admin Dashboard", 600, 400);

            
            JPanel panel = new JPanel();
            panel.setLayout(new GridBagLayout());
            panel.setOpaque(false); 
            panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50)); 

            
            JButton addButton = createStyledButton("Add Student");
            JButton updateButton = createStyledButton("Update Student");
            JButton deleteButton = createStyledButton("Delete Student");
            JButton viewButton = createStyledButton("View All Students");

            
            JButton[] buttons = { addButton, updateButton, deleteButton, viewButton };

            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0; 
            gbc.gridy = 0; 
            gbc.fill = GridBagConstraints.HORIZONTAL; 
            gbc.insets = new Insets(10, 10, 10, 10); 

            // Adding buttons to the panel
            for (JButton button : buttons) {
                panel.add(button, gbc);
                gbc.gridy++;
            }

            // Adding the panel to the frame
            add(panel, BorderLayout.CENTER);

            // Adding action listeners to buttons
            addButton.addActionListener(e -> new AddStudentFrame());
            updateButton.addActionListener(e -> new UpdateStudentFrame());
            deleteButton.addActionListener(e -> new DeleteStudentFrame());
            viewButton.addActionListener(e -> new ViewStudentsFrame());

            setVisible(true); // Display the frame
        }

       
        private JButton createStyledButton(String text) {
            JButton button = new JButton(text);
            button.setFont(loadCustomFont(14)); // Set font size
            button.setBackground(new Color(34, 150, 243)); // Blue button background
            button.setForeground(Color.WHITE); // White text color
            button.setFocusPainted(false); // Remove focus border
            button.setPreferredSize(new Dimension(200, 40)); // Consistent button size
            button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2)); // Add a black border
            return button;
        }
    }

    static class StudentFrame extends BaseFrame {
        StudentFrame(String username) {
            super("Student Dashboard", 400, 200);

            // Welcome label
            JLabel welcomeLabel = new JLabel("Welcome, " + username + "!");
            welcomeLabel.setFont(loadCustomFont(20)); // Large font size for a welcoming message
            welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);

            // Marks label
            JLabel marksLabel = new JLabel("Your Marks: " + fetchStudentMarks(username));
            marksLabel.setFont(loadCustomFont(18)); // Slightly smaller font size for details
            marksLabel.setHorizontalAlignment(SwingConstants.CENTER);

            // Adding components to the frame
            add(welcomeLabel, BorderLayout.NORTH);
            add(marksLabel, BorderLayout.CENTER);

            setVisible(true); // Display the frame
        }

        /**
         * Fetches the marks of the student from the database.
         *
         * @param username the username of the student
         * @return the marks of the student or 0 if not found
         */
        private int fetchStudentMarks(String username) {
            String query = "SELECT marks FROM students WHERE username = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("marks");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return 0; // Default value if no marks are found
        }
    }

    static class AddStudentFrame extends BaseFrame {
        AddStudentFrame() {
            super("Add Student", 600, 400);

            // Panel for form layout
            JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10)); // Added gaps for better spacing
            formPanel.setOpaque(false);
            formPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50)); // Add padding around the panel

            // Form fields
            JLabel nameLabel = new JLabel("Name:");
            nameLabel.setFont(loadCustomFont(16));
            JTextField nameField = new JTextField();

            JLabel marksLabel = new JLabel("Marks:");
            marksLabel.setFont(loadCustomFont(16));
            JTextField marksField = new JTextField();

            JLabel usernameLabel = new JLabel("Username:");
            usernameLabel.setFont(loadCustomFont(16));
            JTextField usernameField = new JTextField();

            JLabel passwordLabel = new JLabel("Password:");
            passwordLabel.setFont(loadCustomFont(16));
            JPasswordField passwordField = new JPasswordField();

            JButton addButton = new JButton("Add");
            addButton.setFont(loadCustomFont(16));
            addButton.setBackground(new Color(34, 150, 243)); // Blue color
            addButton.setForeground(Color.WHITE);
            addButton.setFocusPainted(false);
            addButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

            // Adding components to the form panel
            formPanel.add(nameLabel);
            formPanel.add(nameField);
            formPanel.add(marksLabel);
            formPanel.add(marksField);
            formPanel.add(usernameLabel);
            formPanel.add(usernameField);
            formPanel.add(passwordLabel);
            formPanel.add(passwordField);
            formPanel.add(new JLabel()); // Placeholder for alignment
            formPanel.add(addButton);

            // Add form panel to the frame
            add(formPanel, BorderLayout.CENTER);

            // Action listener for Add button
            addButton.addActionListener(e -> {
                String name = nameField.getText().trim();
                String marksText = marksField.getText().trim();
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();

                // Validate inputs
                if (name.isEmpty() || marksText.isEmpty() || username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill all fields!", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    int marks = Integer.parseInt(marksText);

                    String query = "INSERT INTO students (name, marks, username, password) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement stmt = conn.prepareStatement(query)) {
                        stmt.setString(1, name);
                        stmt.setInt(2, marks);
                        stmt.setString(3, username);
                        stmt.setString(4, password);
                        stmt.executeUpdate();
                    }

                    JOptionPane.showMessageDialog(this, "Student Added Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Marks should be a valid number!", "Error", JOptionPane.ERROR_MESSAGE);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error Adding Student!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            setVisible(true); // Make the frame visible
        }
    }

 
    static class UpdateStudentFrame extends BaseFrame {
        UpdateStudentFrame() {
            super("Update Student", 600, 400);

            // Panel for the form layout
            JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10)); // Added gaps for better spacing
            formPanel.setOpaque(false);
            formPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50)); // Add padding around the panel

            // Form fields
            JLabel usernameLabel = new JLabel("Username:");
            usernameLabel.setFont(loadCustomFont(16));
            JTextField usernameField = new JTextField();

            JLabel newNameLabel = new JLabel("New Name:");
            newNameLabel.setFont(loadCustomFont(16));
            JTextField newNameField = new JTextField();

            JLabel newMarksLabel = new JLabel("New Marks:");
            newMarksLabel.setFont(loadCustomFont(16));
            JTextField newMarksField = new JTextField();

            JButton updateButton = new JButton("Update");
            updateButton.setFont(loadCustomFont(16));
            updateButton.setBackground(new Color(34, 150, 243)); // Blue color
            updateButton.setForeground(Color.WHITE);
            updateButton.setFocusPainted(false);
            updateButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

            // Add components to the form panel
            formPanel.add(usernameLabel);
            formPanel.add(usernameField);
            formPanel.add(newNameLabel);
            formPanel.add(newNameField);
            formPanel.add(newMarksLabel);
            formPanel.add(newMarksField);
            formPanel.add(new JLabel()); // Placeholder for alignment
            formPanel.add(updateButton);

            // Add the form panel to the frame
            add(formPanel, BorderLayout.CENTER);

            // Action listener for the Update button
            updateButton.addActionListener(e -> {
                String username = usernameField.getText().trim();
                String newName = newNameField.getText().trim();
                String newMarksText = newMarksField.getText().trim();

                // Validate inputs
                if (username.isEmpty() || newName.isEmpty() || newMarksText.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill all fields!", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    int newMarks = Integer.parseInt(newMarksText);

                    String query = "UPDATE students SET name = ?, marks = ? WHERE username = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(query)) {
                        stmt.setString(1, newName);
                        stmt.setInt(2, newMarks);
                        stmt.setString(3, username);

                        int rowsUpdated = stmt.executeUpdate();
                        if (rowsUpdated > 0) {
                            JOptionPane.showMessageDialog(this, "Student Updated Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                            dispose();
                        } else {
                            JOptionPane.showMessageDialog(this, "No student found with the given username.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Marks should be a valid number!", "Error", JOptionPane.ERROR_MESSAGE);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error Updating Student!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            setVisible(true); // Make the frame visible
        }
    }

    
 
    static class DeleteStudentFrame extends BaseFrame {
        DeleteStudentFrame() {
            super("Delete Student", 600, 400);

            
            JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10)); 
            formPanel.setOpaque(false);
            formPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50)); 

            
            JLabel usernameLabel = new JLabel("Username:");
            usernameLabel.setFont(loadCustomFont(16));
            JTextField usernameField = new JTextField();

            JButton deleteButton = new JButton("Delete");
            deleteButton.setFont(loadCustomFont(16));
            deleteButton.setBackground(new Color(255, 69, 58)); 
            deleteButton.setForeground(Color.WHITE);
            deleteButton.setFocusPainted(false);
            deleteButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

            
            formPanel.add(usernameLabel);
            formPanel.add(usernameField);
            formPanel.add(new JLabel()); 
            formPanel.add(deleteButton);

            
            add(formPanel, BorderLayout.CENTER);

            
            deleteButton.addActionListener(e -> {
                String username = usernameField.getText().trim();

                
                if (username.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter the username!", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    String query = "DELETE FROM students WHERE username = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(query)) {
                        stmt.setString(1, username);

                        int rowsDeleted = stmt.executeUpdate();
                        if (rowsDeleted > 0) {
                            JOptionPane.showMessageDialog(this, "Student Deleted Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                            dispose();
                        } else {
                            JOptionPane.showMessageDialog(this, "No student found with the given username.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error Deleting Student!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            setVisible(true); 
        }
    }

  static class ViewStudentsFrame extends BaseFrame {
    ViewStudentsFrame() {
        super("View All Students", 800, 500); 


        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false);

        
        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        JLabel titleLabel = new JLabel("All Students");
        titleLabel.setFont(loadCustomFont(16)); 
        topPanel.add(titleLabel);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); 

        
        JTable table = new JTable();
        table.setIntercellSpacing(new Dimension(10, 10)); 
        table.setRowHeight(30); 
        table.setGridColor(Color.LIGHT_GRAY); 
        table.setShowGrid(true); 

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(750, 350)); 
        mainPanel.add(scrollPane, BorderLayout.CENTER); 

        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshButton = new JButton("Refresh");
        bottomPanel.add(refreshButton);

       
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        
        add(mainPanel, BorderLayout.CENTER);

        
        try {
            String query = "SELECT * FROM students";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            
            DefaultTableModel model = new DefaultTableModel();
            for (int i = 1; i <= columnCount; i++) {
                model.addColumn(metaData.getColumnName(i));
            }

            while (rs.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = rs.getObject(i);
                }
                model.addRow(row);
            }

            table.setModel(model);

        } catch (SQLException ex) {
            
            JOptionPane.showMessageDialog(this, "Error fetching student data: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        refreshButton.addActionListener(e -> {
            refreshTableData(table);
        });

       
        setVisible(true);
    }

    private void refreshTableData(JTable table) {
        try {
            String query = "SELECT * FROM students";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Create table model with dynamic columns
            DefaultTableModel model = new DefaultTableModel();
            for (int i = 1; i <= columnCount; i++) {
                model.addColumn(metaData.getColumnName(i));
            }

            while (rs.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = rs.getObject(i);
                }
                model.addRow(row);
            }

            table.setModel(model);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching student data: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
}