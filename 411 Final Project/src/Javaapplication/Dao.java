package Javaapplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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
		final String createTicketsTable = "CREATE TABLE Sdavid_tickets(ticket_id INT AUTO_INCREMENT PRIMARY KEY, ticket_issuer VARCHAR(30), ticket_description VARCHAR(200), ticket_status VARCHAR(100), time_stamp TIMESTAMP)";
		final String createUsersTable = "CREATE TABLE Sdavid_users(uid INT AUTO_INCREMENT PRIMARY KEY, uname VARCHAR(30), upass VARCHAR(30), admin int)";

		try {

			// execute queries to create tables

			statement = getConnection().createStatement();

			statement.executeUpdate(createTicketsTable);
			statement.executeUpdate(createUsersTable);
			System.out.println("Created tables in given database...");

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

				sql = "insert into Sdavid_users(uname,upass,admin) " + "values('" + rowData.get(0) + "'," + " '"
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

	public int insertRecords(String ticketName, String ticketDesc, String status, String  timeStamp) {
		int id = 0;
		try {
			statement = getConnection().createStatement();
			
			//extra-credit. Add a time stamp to tickets
			//Opening a ticket adds a status of "open"
			statement.executeUpdate("Insert into Sdavid_tickets" + "(ticket_issuer, ticket_description, ticket_status, time_stamp) values(" + " '"
					+ ticketName + "','" + ticketDesc + "','" + status + "','" + timeStamp + "')", Statement.RETURN_GENERATED_KEYS);

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
			results = statement.executeQuery("SELECT * FROM Sdavid_tickets");
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return results;
	}
	
	// continue coding for updateRecords implementation
	public void updateRecords(String tid, String desc, String status) {
		try {
			//open create statement
			//select the ticket based on the entered tid in GUI
			statement = connect.createStatement();
			ResultSet rsUpdate = statement.executeQuery("SELECT ticket_description FROM Sdavid_tickets WHERE"
					+ "id = " + tid);
			
			//declare String for loop
			String results = null;
			while (rsUpdate.next()) {
				results = rsUpdate.getString("ticket_description");
			}
			
			//extra-credit: using prepared statements to update query
			//requires import of PreparedStatements.java
			PreparedStatement ps = connect.prepareStatement("UPDATE Sdavid_tickets SET ticket_description = ?, status = ?, WHERE id = ?");
			
			//https://www.javatpoint.com/PreparedStatement-interface
			//setting parameters
			String DescUpdate = results + "\nUpdate:" + desc;
			//java said the parameters should swap..so I did, but I thought it was (DescUpgrade, 1)
			//error: these have to match with ps ^
			ps.setString(1, DescUpdate);
			ps.setString(2, status);
			ps.setString(3, tid);
			ps.executeUpdate();
			ps.close();
			
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
	}

	// continue coding for deleteRecords implementation
	public int deleteRecords(int tid) {
		System.out.println();
		try {
			statement = connect.createStatement();
			String delete = "DELETE FROM Sdavid_tickets WHERE id = " + tid;
			statement.executeUpdate(delete);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return tid;
	}
	
}//end of class