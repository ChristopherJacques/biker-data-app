/*
Name: Christopher Jacques
Course: CNT 4714 Spring 2024
Assignment title: Project 3 – A Two-tier Client-Server Application
*/

// Import statements
import javax.swing.table.AbstractTableModel;
import java.sql.*;
import java.util.Properties;

public class ResultSetTableModel extends AbstractTableModel {
    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;
    private ResultSetMetaData metaData;
    private int numberOfRows;

    // Keep track of database connection status
    private boolean connectedToDatabase = false;

    // Constructor initializes resultSet and obtains its meta data object; determines number of rows
    public ResultSetTableModel(Properties dbProperties, Properties userProperties, String username, String password)
            throws SQLException, ClassNotFoundException {

        // Ensure JDBC driver is loaded
        Class.forName("com.mysql.cj.jdbc.Driver");

        // Establish connection to database
        connection = DriverManager.getConnection(
                dbProperties.getProperty("MYSQL_DB_URL"),
                username,
                password);

        // Create Statement to query database
        statement = connection.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);

        // Update database connection status
        connectedToDatabase = true;
    }

    // Get class that represents column type
    public Class<?> getColumnClass(int column) throws IllegalStateException {
        // Ensure database connection is available
        if (!connectedToDatabase) {
            throw new IllegalStateException("Not Connected to Database");
        }

        // Determine Java class of column
        try {
            String className = metaData.getColumnClassName(column + 1);
            // Return Class object that represents className
            return Class.forName(className);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return Object.class; // If problems occur above, assume type Object
    }

    // Get number of columns in ResultSet
    public int getColumnCount() throws IllegalStateException {
        // Ensure database connection is available
        if (!connectedToDatabase) {
            throw new IllegalStateException("Not Connected to Database");
        }

        // Determine number of columns
        try {
            return metaData.getColumnCount();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return 0; // If problems occur above, return 0 for number of columns
    }

    // Get name of a particular column in ResultSet
    public String getColumnName(int column) throws IllegalStateException {
        // Ensure database connection is available
        if (!connectedToDatabase) {
            throw new IllegalStateException("Not Connected to Database");
        }

        // Determine column name
        try {
            return metaData.getColumnName(column + 1);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return ""; // If problems, return empty string for column name
    }

    // Return number of rows in ResultSet
    public int getRowCount() throws IllegalStateException {
        // Ensure database connection is available
        if (!connectedToDatabase) {
            throw new IllegalStateException("Not Connected to Database");
        }
        return numberOfRows;
    }

    // Obtain value in particular row and column
    public Object getValueAt(int row, int column) throws IllegalStateException {
        // Ensure database connection is available
        if (!connectedToDatabase) {
            throw new IllegalStateException("Not Connected to Database");
        }

        // Obtain a value at specified ResultSet row and column
        try {
            resultSet.absolute(row + 1);
            return resultSet.getObject(column + 1);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return ""; // If problems, return empty string object
    }

    // Set new database query string
    public void setQuery(String query) throws SQLException, IllegalStateException {
        // Ensure database connection is available
        if (!connectedToDatabase) {
            throw new IllegalStateException("Not Connected to Database");
        }

        // Specify query and execute it
        resultSet = statement.executeQuery(query);

        // Obtain meta data for ResultSet
        metaData = resultSet.getMetaData();

        // Determine number of rows in ResultSet
        resultSet.last(); // Move to last row
        numberOfRows = resultSet.getRow(); // Get row number

        // Notify JTable that model has changed
        fireTableStructureChanged();
    }

    // Set new database update-query string
    public int setUpdate(String query) throws SQLException, IllegalStateException {
        // Ensure database connection is available
        if (!connectedToDatabase) {
            throw new IllegalStateException("Not Connected to Database");
        }

        // Specify query and execute it
        int affectedRows = statement.executeUpdate(query);

        // Notify JTable that model has changed
        fireTableDataChanged();

        return affectedRows;
    }

    // Close Statement and Connection
    public void disconnectFromDatabase() {
        if (!connectedToDatabase) {
            return;
        }
        // Close Statement and Connection
        try {
            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        } finally { // Update database connection status
            connectedToDatabase = false;
        }
    }

    // Check database connection status
    public boolean isConnectedToDatabase() {
        return connectedToDatabase;
    }
}
