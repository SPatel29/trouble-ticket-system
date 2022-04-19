package src;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Dao {
	// instance fields
	static Connection connect = null;
	Statement statement = null;

	// constructor
	public Dao() {

	}

	public Connection getConnection() {
		// Setup the connection with the DB
		try {
			connect = DriverManager
					.getConnection("jdbc:mysql://www.papademas.net:3307/tickets?autoReconnect=true&useSSL=false"
							+ "&user=fp411&password=411");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return connect;
	}

	// CRUD implementation

	public void createTables() {
		// variables for SQL Query table creations
		final String createLoginTable = "CREATE TABLE IF NOT EXISTS sp_login ("
				+ " userID INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY, "
				+ " userName VARCHAR(32) NOT NULL, "
				+ " userPassword VARCHAR(32) NOT NULL, "
				+ " adminStatus INT )";
		final String createOpenTicketTable = "CREATE TABLE IF NOT EXISTS sp_opentickets ( "
				+ " ticketID INT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
				+ " userID INT NOT NULL, "
				+ " userName VARCHAR(32) NOT NULL, "
				+ " startDate DATETIME NOT NULL, "
				+ " ticketDesc VARCHAR(32) NOT NULL, "
				+ " FOREIGN KEY (userID) references sp_login(userID) ON DELETE CASCADE )";
		final String createCloseTicketTable = "CREATE TABLE IF NOT EXISTS sp_closetickets "
				+ " (ticketID INT NOT NULL, "
				+ " startDate DATETIME NOT NULL, "
				+ " closeDate DATETIME NOT NULL, "
				+ " userID INTEGER UNSIGNED NOT NULL, "
				+ " userNAME VARCHAR(32) NOT NULL, "
				+ " FOREIGN KEY (ticketID) references sp_opentickets(ticketID) ON DELETE CASCADE, "
				+ " FOREIGN KEY (userID) references sp_login(userID) ON DELETE CASCADE )";
		try {

			// execute queries to create tables

			statement = getConnection().createStatement();
			System.out.println("Succesfully got connection");
			statement.executeUpdate(createLoginTable);
			System.out.println("Successfully created login table");
			statement.executeUpdate(createOpenTicketTable);
			System.out.println("Successfully created open ticket table");
			statement.executeUpdate(createCloseTicketTable);
			System.out.println("Successfully created close ticket table");
			// end create table
			// close connection/statement object
			statement.close();
			connect.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		// add users to user table
		addUsers();
	}

	public void addUsers() {
		// add list of users from userlist.csv file to users table

		// variables for SQL Query inserts
		String sql;

		Statement statement;
		BufferedReader br;
		List<List<String>> array = new ArrayList<>(); // list to hold (rows & cols)

		// read data from file
		try {
			br = new BufferedReader(new FileReader(new File("./userlist.csv")));

			String line;
			while ((line = br.readLine()) != null) {
				array.add(Arrays.asList(line.split(",")));
			}
		} catch (Exception e) {
			System.out.println("There was a problem loading the file");
		}

		try {

			// Setup the connection with the DB

			statement = getConnection().createStatement();

			// create loop to grab each array index containing a list of values
			// and PASS (insert) that data into your User table
			for (List<String> rowData : array) {

				sql = "insert into jpapa_users(uname,upass,admin) " + "values('" + rowData.get(0) + "'," + " '"
						+ rowData.get(1) + "','" + rowData.get(2) + "');";
				statement.executeUpdate(sql);
			}
			System.out.println("Inserts completed in the given database...");

			// close statement object
			statement.close();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public int insertRecords(String ticketName, String ticketDesc) {
		int id = 0;
		try {
			statement = getConnection().createStatement();
			statement.executeUpdate("Insert into jpapa_tickets" + "(ticket_issuer, ticket_description) values(" + " '"
					+ ticketName + "','" + ticketDesc + "')", Statement.RETURN_GENERATED_KEYS);

			// retrieve ticket id number newly auto generated upon record insertion
			ResultSet resultSet = null;
			resultSet = statement.getGeneratedKeys();
			if (resultSet.next()) {
				// retrieve first field in table
				id = resultSet.getInt(1);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return id;

	}

	public ResultSet readRecords() {

		ResultSet results = null;
		try {
			statement = connect.createStatement();
			results = statement.executeQuery("SELECT * FROM jpapa_tickets");
			// connect.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return results;
	}
	// continue coding for updateRecords implementation

	// continue coding for deleteRecords implementation
}
