/*
Name: Christopher Jacques
Course: CNT 4714 Spring 2024
Assignment title: Project 3 – A Two-tier Client-Server Application
*/

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileNotFoundException;

public class DisplayQueryResults extends JFrame {
   // GUI components
    private JTextArea sqlCommandArea;
    private JTextArea sqlResultArea;
    private JComboBox<String> dbUrlPropertiesDropdown;
    private JComboBox<String> userPropertiesDropdown;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton connectButton;
    private JButton disconnectButton;
    private JButton clearSqlCommandButton;
    private JButton executeSqlCommandButton;
    private JButton clearResultWindowButton;
    private JLabel connectionStatusLabel;
    private ResultSetTableModel tableModel;

    public DisplayQueryResults() {
        super("SQL Client Application - (CJ CNT 4714 - Spring 2024 - Project 3)");

        // Connection Details Panel
        JPanel connectionDetailsPanel = new JPanel();
        connectionDetailsPanel.setLayout(new GridLayout(5, 2, 5, 5));
        connectionDetailsPanel.setBorder(BorderFactory.createTitledBorder("Connection Details"));

        // Dropdown for DB URL properties
        dbUrlPropertiesDropdown = new JComboBox<>(new String[]{"project3.properties", "bikedb.properties", "operationslog.properties"});
        userPropertiesDropdown = new JComboBox<>(new String[]{"root.properties", "client1.properties", "client2.properties", "theaccountant.properties"});

        // text feilds
        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);

        // Connect to Database
        connectButton = new JButton("Connect to Database");
        disconnectButton = new JButton("Disconnect From Database");

        // Adding components to connection details panel
        connectionDetailsPanel.add(new JLabel("DB URL Properties"));
        connectionDetailsPanel.add(dbUrlPropertiesDropdown);
        connectionDetailsPanel.add(new JLabel("User Properties"));
        connectionDetailsPanel.add(userPropertiesDropdown);
        connectionDetailsPanel.add(new JLabel("Username"));
        connectionDetailsPanel.add(usernameField);
        connectionDetailsPanel.add(new JLabel("Password"));
        connectionDetailsPanel.add(passwordField);
        connectionDetailsPanel.add(connectButton);
        connectionDetailsPanel.add(disconnectButton);

        // SQL Command Panel
        JPanel sqlCommandPanel = new JPanel(new BorderLayout(5, 5));
        sqlCommandPanel.setBorder(BorderFactory.createTitledBorder("Enter An SQL Command"));

        sqlCommandArea = new JTextArea(5, 20);
        sqlCommandPanel.add(new JScrollPane(sqlCommandArea), BorderLayout.CENTER);

        clearSqlCommandButton = new JButton("Clear SQL Command");
        executeSqlCommandButton = new JButton("Execute SQL Command");

        JPanel sqlCommandButtonsPanel = new JPanel();
        sqlCommandButtonsPanel.add(clearSqlCommandButton);
        sqlCommandButtonsPanel.add(executeSqlCommandButton);

        sqlCommandPanel.add(sqlCommandButtonsPanel, BorderLayout.SOUTH);

        // SQL Result Panel
        sqlResultArea = new JTextArea(10, 20);
        sqlResultArea.setEditable(false);

        JPanel sqlResultPanel = new JPanel(new BorderLayout(5, 5));
        sqlResultPanel.setBorder(BorderFactory.createTitledBorder("SQL Execution Result Window"));

        sqlResultPanel.add(new JScrollPane(sqlResultArea), BorderLayout.CENTER);

        clearResultWindowButton = new JButton("Clear Result Window");
        JPanel clearResultPanel = new JPanel();
        clearResultPanel.add(clearResultWindowButton);

        sqlResultPanel.add(clearResultPanel, BorderLayout.SOUTH);

                // Connection Status Panel
        JPanel connectionStatusPanel = new JPanel(new BorderLayout());
        connectionStatusPanel.setBorder(new EmptyBorder(10, 0, 10, 0)); // Adds some padding around the label

        connectionStatusLabel = new JLabel("Not Connected", SwingConstants.CENTER);
        connectionStatusLabel.setForeground(Color.RED);
        connectionStatusPanel.add(connectionStatusLabel, BorderLayout.CENTER);

        // Add panels to the frame
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(connectionDetailsPanel, BorderLayout.NORTH);
        topPanel.add(connectionStatusPanel, BorderLayout.CENTER);
        topPanel.add(sqlCommandPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(sqlResultPanel, BorderLayout.CENTER);

        // Set up the Connect to Database button action
        connectButton.addActionListener(this::connectToDatabaseAction);
        disconnectButton.addActionListener(this::disconnectFromDatabaseAction);
        executeSqlCommandButton.addActionListener(this::executeSqlCommand);
        

        // Set the frame attributes
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null); // Center on screen
        setVisible(true);
    }

    // Action performed method for connectButton
    private void connectToDatabaseAction(ActionEvent event) {
      // Obtain connection details from the UI components
      String dbURL = (String) dbUrlPropertiesDropdown.getSelectedItem();
      String userProperties = (String) userPropertiesDropdown.getSelectedItem();
      String username = usernameField.getText();
      char[] password = passwordField.getPassword();
  
      // Now use the properties to establish a connection
      try {
          Properties dbProps = loadProperties(dbURL);
          Properties userProps = loadProperties(userProperties);
  
          // Here's the added Class.forName, replace with your JDBC driver class if different
          Class.forName("com.mysql.cj.jdbc.Driver"); 
  
          tableModel = new ResultSetTableModel(dbProps, userProps, username, new String(password));
          connectionStatusLabel.setText("Connected to: " + dbProps.getProperty("MYSQL_DB_URL"));
      } catch (ClassNotFoundException ex) {
          JOptionPane.showMessageDialog(this, "MySQL JDBC driver not found.", "Driver Error", JOptionPane.ERROR_MESSAGE);
      } catch (SQLException ex) {
          JOptionPane.showMessageDialog(this, "Failed to connect: " + ex.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
      } finally {
          // Clear the password field for security
          Arrays.fill(password, '0');
          // Clear the text fields after trying to connect
          usernameField.setText("");
          passwordField.setText("");
      }
  }
  

    // Action performed method for disconnectButton
    private void disconnectFromDatabaseAction(ActionEvent event) {
        if (tableModel != null) {
            tableModel.disconnectFromDatabase();
            connectionStatusLabel.setText("Not Connected");
        }
    }

    // Load properties from the given file name
    private Properties loadProperties(String propertiesFileName) {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(propertiesFileName)) {
            if (input == null) {
                throw new FileNotFoundException("Property file '" + propertiesFileName + "' not found in the classpath");
            }
            props.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return props;
    }

    // Execute command action
    private void executeSqlCommand(ActionEvent event) {
      // Ensure there's a connection and a valid command.
      if (tableModel == null || !tableModel.isConnectedToDatabase()) {
          JOptionPane.showMessageDialog(this, "Please connect to a database first.", "Not Connected", JOptionPane.WARNING_MESSAGE);
          return;
      }
  
      try {
          String sqlCommand = sqlCommandArea.getText();
          if (sqlCommand.trim().isEmpty()) {
              JOptionPane.showMessageDialog(this, "SQL command is empty.", "No Command", JOptionPane.WARNING_MESSAGE);
              return;
          }
  
          // Execute the command based on whether it's a SELECT or an update operation.
          sqlCommand = sqlCommand.trim().toUpperCase();
          if (sqlCommand.startsWith("SELECT")) {
              tableModel.setQuery(sqlCommand);
  
              // Create a JTable with the new model data.
              JTable resultTable = new JTable(tableModel);
  
              // Clear the existing content in the result area and add the new result table.
              JScrollPane scrollPane = new JScrollPane(resultTable);
              sqlResultArea.removeAll();
              sqlResultArea.setLayout(new BorderLayout()); // Set layout manager
              sqlResultArea.add(scrollPane, BorderLayout.CENTER);
              sqlResultArea.revalidate(); // Refresh the panel
              sqlResultArea.repaint(); // Redraw the panel
          } else {
              int result = tableModel.setUpdate(sqlCommand);
              sqlResultArea.setText("Update executed successfully. Rows affected: " + result);
          }
      } catch (SQLException ex) {
          JOptionPane.showMessageDialog(this, ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
      }
  }
  


    // Main method to run the application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DisplayQueryResults());
    }
}
