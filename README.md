# biker-data-app
This project involves creating a Java-based GUI application that connects to a MySQL server via JDBC. The application allows users to execute SQL commands with appropriate permissions and includes a specialized monitoring interface for an accountant client.

## Features
- Java GUI front-end for executing MySQL DDL and DML commands.
- User authentication via properties files.
- Real-time transaction logging to a separate database.
- Specialized interface for an accountant client with query-only access.
- Handles multiple simultaneous client connections.

## Files
- MainClientApp.java: Main application for general users.
- AccountantApp.java: Specialized application for the accountant.
- DisplayQueryResults.java: Helper class for displaying query results.
- ResultSetTableModel.java: Helper class for managing result sets.
- inventory.csv: Input file with item details.
- transactions.csv: Output file logging transactions.
- SQL scripts to create and populate the necessary databases (project3dbscript.sql, project3operationslog.sql).

## Screenshots
![Screenshot 2024-05-25 at 1 49 55 PM](https://github.com/ChristopherJacques/biker-data-app/assets/66994170/d89675de-3de5-41a9-aeab-6384ef385342)
![Screenshot 2024-05-25 at 1 51 08 PM](https://github.com/ChristopherJacques/biker-data-app/assets/66994170/237d63a9-fa4d-4873-837f-f813d25b9e6c)
