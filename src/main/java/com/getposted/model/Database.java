package com.getposted.model;

import java.sql.Connection;
import java.sql.DriverManager;

import com.getposted.logger.Logging;
import java.util.logging.Logger;
import com.getposted.system.Sysenv;

import java.sql.SQLException;
import java.sql.SQLInvalidAuthorizationSpecException;
import java.lang.ClassNotFoundException;

public class Database {

	private static String url = Sysenv.getEnv("DATABASEURL"); // "jdbc:mariadb://localhost:3306/"
	private static String database = Sysenv.getEnv("DATABASENAME");	// "getPosted"
	private static String userName = Sysenv.getEnv("DATABASEUSERNAME");
	private static String password = Sysenv.getEnv("DATABASEPASSWORD");
	private static String driver = "org.mariadb.jdbc.Driver";

	private Database() {
	}

	public static Connection getConnection() throws SQLException {
		// create connection data type variable
		Connection connection = null;

		// get the Logger object to log the messages
		Logger logger = Logging.getLogger(Database.class.getName());

		// load the driver and register it.
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			logger.severe(String.format(
					"The Driver class not found exception occured. the Driver class name is %s. The exception message is %s",
					driver, e.getMessage()));
		}

		// get the connection object
		try {
			connection = DriverManager.getConnection(url + database, userName, password);
		} catch (SQLInvalidAuthorizationSpecException e) {
			logger.severe(String.format(
					"The SQLInvalidAuthorizationSpecException occured (invalid username or password). The url is correct and the registration is also completed.when tring to get the connection object using DriverManager.getConnection method. The url is %s. The password is %s. The username is %s. The Driver was loaded successfully. The Driver name is %s. The Exception message is %s",
					url, password, userName, driver, e.getMessage()));
		} catch (SQLException e) {
			logger.severe(String.format(
					"The SQLEXception occured when tring to get the connection object using DriverManager.getConnection method. The Driver loading was successful. The url is %s. The password is %s. The username is %s. The Driver was loaded successfully. The Driver name is %s. The Exception message is %s",
					url, password, userName, driver, e.getMessage()));
			throw e;
		}

		// return the connection object
		return connection;
	}

}
