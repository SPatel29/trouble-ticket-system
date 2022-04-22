package src;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
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
				+ " ticketName VARCHAR(32) NOT NULL, "
				+ " userName VARCHAR(32) NOT NULL, "
				+ " startDate DATETIME NOT NULL, "
				+ " closeDate DATETIME, " // set to NULL
				+ " caseStatus VARCHAR(32) NOT NULL, "
				+ " ticketDesc VARCHAR(32) NOT NULL, "
				+ " FOREIGN KEY (userID) references sp_login(userID) ON DELETE CASCADE )";
		try {

			// execute queries to create tables

			statement = getConnection().createStatement();
			System.out.println("Succesfully got connection");
			statement.executeUpdate(createLoginTable);
			System.out.println("Successfully created login table");
			statement.executeUpdate(createOpenTicketTable);
			System.out.println("Successfully created open ticket table");
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
				System.out.println(line);
			}
			br.close();
		} catch (Exception e) {
			System.out.println("There was a problem loading the file");
		}
		try {

			// Setup the connection with the DB

			statement = getConnection().createStatement();

			// create loop to grab each array index containing a list of values
			// and PASS (insert) that data into your User table
			for (List<String> rowData : array) {

				sql = "insert into sp_login(userName, userPassword, adminStatus) "
						+ "values('" + rowData.get(0) + "',"
						+ " '"
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

	public int insertRecords(String userName, String ticketName, String ticketDesc, String userID,
			Timestamp startDate) {
		int id = 0;
		try {
			System.out.println(userID);
			statement = getConnection().createStatement();
			String get_user_name = "SELECT userName FROM sp_login WHERE userID = '" + userID + "'";
			ResultSet nameSet = statement.executeQuery(get_user_name);
			System.out.println("after select statement");
			String insetQuery = "INSERT INTO sp_opentickets (userID, ticketName, userName, startDate, ticketDesc)"
					+ "VALUES (?, ?, ?, ?, ?)";
			PreparedStatement psmt = getConnection().prepareStatement(insetQuery,
					PreparedStatement.RETURN_GENERATED_KEYS);

			if (nameSet.next()) {
				psmt.setInt(1, Integer.parseInt(userID));
				psmt.setString(2, ticketName);
				psmt.setString(3, nameSet.getString(1)); // get userName from resultset
				psmt.setTimestamp(4, startDate);
				// psmt.setTimestamp(5, null);
				// psmt.setString(6, "Open");
				psmt.setString(5, ticketDesc);
				psmt.executeUpdate();
				System.out.println("After executeUpdate statement");
			}
			System.out.println("succesfully added record");
			// retrieve ticket id number newly auto generated upon record insertion
			ResultSet resultSet = null;
			resultSet = psmt.getGeneratedKeys();
			if (resultSet.next()) {
				// retrieve first field in table
				id = resultSet.getInt(1);
				System.out.println("after the id = resultSet...");
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
			results = statement.executeQuery("SELECT * FROM sp_opentickets");
			// connect.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return results;
	}

	public int closeTickets(String ticketID, Timestamp closeTime) {
		try {
			statement = getConnection().createStatement();
			String addClosedDate = "UPDATE sp_opentickets SET closeDate = ?, caseStatus = ? WHERE ticketID = ?";
			PreparedStatement pStatement = getConnection().prepareStatement(addClosedDate);
			pStatement.setTimestamp(1, closeTime);
			pStatement.setString(2, "Closed");
			pStatement.setInt(3, Integer.parseInt(ticketID)); // stopped here
			pStatement.executeUpdate();
			return Integer.parseInt(ticketID);
		} catch (SQLException e2) {
			e2.printStackTrace();
		} // failure. Unable to delete ticket
		return 0;
	}

	public int deleteTicket(int ticketID) {
		try {
			String deleteQuery = "DELETE FROM sp_opentickets WHERE ticketID = ?";
			PreparedStatement pStatement = getConnection().prepareStatement(deleteQuery);
			pStatement.setInt(1, ticketID);
			pStatement.executeUpdate();
			return ticketID;

		} catch (SQLException e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		return 0;
	}

	public int updateTicket(int ticketID, int userID, String userName){
		try{
			String updateTicketQuery = "UPDATE sp_opentickets SET userID = ?, userName = ? WHERE ticketID = ?";
			PreparedStatement pStatement = getConnection().prepareStatement(updateTicketQuery);
			pStatement.setInt(1, userID);
			pStatement.setString(2, userName);
			pStatement.setInt(3, ticketID);
			pStatement.executeUpdate();
			return ticketID;
		}catch(SQLException ex){
			ex.printStackTrace();
		}

		return ticketID;
	}
	// continue coding for updateRecords implementation
	// continue coding for deleteRecords implementation
}
