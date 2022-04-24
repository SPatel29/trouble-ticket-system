package src;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.security.MessageDigest;
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
		final String createLoginTable = "CREATE TABLE IF NOT EXISTS spate_login ("
				+ " userID INT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
				+ " userName VARCHAR(55) NOT NULL, "
				+ " userPassword VARCHAR(256) NOT NULL, "
				+ " adminStatus TINYINT )";
		final String createOpenTicketTable = "CREATE TABLE IF NOT EXISTS spate_opentickets ( "
				+ " ticketID INT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
				+ " userID INT NOT NULL, "
				+ " ticketName VARCHAR(100) NOT NULL, "
				+ " userName VARCHAR(55) NOT NULL, "
				+ " startDate DATETIME NOT NULL, "
				+ " closeDate DATETIME NULL, " // sdefault value is set to NULL
				+ " caseStatus VARCHAR(50), "
				+ " ticketDesc VARCHAR(150) NOT NULL, "
				+ " FOREIGN KEY (userID) references spate_login(userID) ON DELETE CASCADE )";
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
				String password = rowData.get(1);

				MessageDigest md = MessageDigest.getInstance("SHA-256");
				md.update(password.getBytes());

				byte byteData[] = md.digest();

				// convert the byte to hex format method 1
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < byteData.length; i++) {
					sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
				}

				sql = "insert into spate_login(userName, userPassword, adminStatus) "
						+ "values('" + rowData.get(0) + "',"
						+ " '"
						+ sb.toString() + "','" + rowData.get(2) + "');";
				statement.executeUpdate(sql);
			}
			System.out.println("Inserts completed in the given database...");

			// close statement object
			statement.close();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public int insertRecords(int userID, String ticketName, String ticketDesc, Timestamp currentTime) {
		int id = 0;
		try {
			System.out.println(userID);
			statement = getConnection().createStatement();
			String userNameQuery = "SELECT userName FROM spate_login WHERE userID = '" + userID + "'";
			ResultSet nameSet = statement.executeQuery(userNameQuery);
			// System.out.println("after select statement");
			String insetQuery = "INSERT INTO spate_opentickets (userID, ticketName, userName, startDate, ticketDesc)"
					+ "VALUES (?, ?, ?, ?, ?)";
			PreparedStatement psmt = getConnection().prepareStatement(insetQuery,
					PreparedStatement.RETURN_GENERATED_KEYS);

			if (nameSet.next()) {
				psmt.setInt(1, userID);
				psmt.setString(2, ticketName);
				psmt.setString(3, nameSet.getString(1)); // get userName from resultset
				psmt.setTimestamp(4, currentTime);
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
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return id;

	}

	public ResultSet readRecords(boolean adminStatus, int userID, int ticketID) {

		ResultSet results = null;
		try {
			if (ticketID > 0 && !adminStatus) {
				String query = "SELECT * FROM spate_opentickets WHERE userID = ? AND ticketID = ?";
				PreparedStatement pStatement = getConnection().prepareStatement(query);
				pStatement.setInt(1, userID);
				pStatement.setInt(2, ticketID);
				return pStatement.executeQuery();

			} else if (ticketID > 0 && adminStatus) {
				String query = "SELECT * FROM spate_opentickets WHERE ticketID = ?";
				PreparedStatement pStatement = getConnection().prepareStatement(query);
				pStatement.setInt(1, ticketID);
				return pStatement.executeQuery();
			} else if (adminStatus) {
				String query = "SELECT * FROM spate_opentickets";
				PreparedStatement pStatement = getConnection().prepareStatement(query);
				return pStatement.executeQuery();
			} else if (!adminStatus) {
				String query = "SELECT * FROM spate_opentickets WHERE userID = ?";
				PreparedStatement pStatement = getConnection().prepareStatement(query);
				pStatement.setInt(1, userID);
				return pStatement.executeQuery();
			}
			// connect.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return results;
	}

	public int closeTickets(String ticketID, Timestamp closeTime, int userID, boolean adminStatus) {
		try {
			String addClosedDate = "UPDATE spate_opentickets SET closeDate = ?, caseStatus = ? WHERE ticketID = ?";
			if (!adminStatus) {
				addClosedDate = "UPDATE spate_opentickets SET closeDate = ?, caseStatus = ? WHERE ticketID = ? AND userID = ?";
			}
			PreparedStatement pStatement = getConnection().prepareStatement(addClosedDate);
			pStatement.setTimestamp(1, closeTime);
			pStatement.setString(2, "Closed");
			pStatement.setInt(3, Integer.parseInt(ticketID));
			if (!adminStatus) {
				pStatement.setInt(4, userID);
			}
			if (pStatement.executeUpdate() > 0) {
				return Integer.parseInt(ticketID);
			}
		} catch (SQLException e2) {
			e2.printStackTrace();
		} // failure. Unable to delete ticket
		return 0;
	}

	public int deleteTicket(int ticketID) {
		try {
			String findTicketQuery = "DELETE FROM spate_opentickets WHERE ticketID = ?";
			PreparedStatement ticketStatement = getConnection().prepareStatement(findTicketQuery);
			ticketStatement.setInt(1, ticketID);
			if (ticketStatement.executeUpdate() > 0) {
				return ticketID;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		return 0;
	}

	public int updateTicket(int ticketID, String description) {
		try {
			String updateTicketQuery = "UPDATE spate_opentickets SET ticketDesc = ? WHERE ticketID = ?";
			PreparedStatement pStatement = getConnection().prepareStatement(updateTicketQuery);
			pStatement.setString(1, description);
			pStatement.setInt(2, ticketID);
			if (pStatement.executeUpdate() > 0) {
				return ticketID;
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return 0;
	}
	// continue coding for updateRecords implementation
	// continue coding for deleteRecords implementation
}
