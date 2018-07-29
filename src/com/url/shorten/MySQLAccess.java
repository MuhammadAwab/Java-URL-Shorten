package com.url.shorten;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

public class MySQLAccess {
	Connection conn = null;
	String ConnectionURL = "localhost:3306/my_url_database";
	String userName = "root";
	String password = "Ghumman12!";
	String tableName = "url_data_store";

	MySQLAccess() {

		try {
			

			conn = DriverManager.getConnection("jdbc:mysql://" + ConnectionURL + "?autoReconnect=true&useSSL=false",
					userName, password);
				
		} catch (SQLException ex) {
	
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}
	}

	public void InsertOriginalURL(String url) throws SQLException {

		String insertQuery = "INSERT INTO " + tableName + "(original_url)" + " values ('" + url + "')";
		PreparedStatement preparedStmt = conn.prepareStatement(insertQuery);
		preparedStmt.execute();
	}

	public void InsertShortURL(int id, String org_url, String gen_url) throws SQLException, ParseException {
		String expTime = getExpirationTime();
		System.out.println(expTime);
		String insertQuery = "UPDATE " + tableName + " SET generated_url = '" + gen_url + "' , expiration_time = '"
				+ expTime + "' WHERE original_url='" + org_url + "' AND url_id='" + id + "'";
		PreparedStatement preparedStmt = conn.prepareStatement(insertQuery);
		preparedStmt.execute();
	}

	public void InsertUsageValueURL(int id, String gen_url) throws SQLException, ParseException {
		int currentValue = this.selectURLUsageValue(gen_url);
		currentValue++;
		String insertQuery = "UPDATE " + tableName + " SET generated_url_usage = '" + currentValue
				+ " ' WHERE generated_url='" + gen_url + "' AND url_id='" + id + "'";
		PreparedStatement preparedStmt = conn.prepareStatement(insertQuery);
		preparedStmt.execute();
	}

	public void SelectQuery() throws SQLException {
		String selectQuery = " SELECT * FROM " + tableName;
		PreparedStatement preparedStmt = conn.prepareStatement(selectQuery);
		ResultSet rs = preparedStmt.executeQuery();
		while (rs.next()) {
			int id = rs.getInt("url_id");
			String org_url = rs.getString("original_url");
			String gen_url = rs.getString("generated_url");



		}
	}

	public int getID(String url) throws SQLException {
		String selectQuery = "SELECT url_id FROM " + tableName + " WHERE original_url='" + url + "'";
		PreparedStatement preparedStmt = conn.prepareStatement(selectQuery);
		ResultSet rs = preparedStmt.executeQuery();
		int id = 0;
		while (rs.next()) {
			id = rs.getInt("url_id");
		}
		return id;
	}

	public int getIDForShortURL(String url) throws SQLException {
		String selectQuery = "SELECT url_id FROM " + tableName + " WHERE generated_url='" + url + "'";
		PreparedStatement preparedStmt = conn.prepareStatement(selectQuery);
		ResultSet rs = preparedStmt.executeQuery();
		int id = 0;
		while (rs.next()) {
			id = rs.getInt("url_id");
		}
		return id;
	}

	public ResultSet getLilURLs() throws SQLException {
		String selectQuery = "SELECT generated_url FROM " + tableName;
		PreparedStatement preparedStmt = conn.prepareStatement(selectQuery);
		ResultSet rs = preparedStmt.executeQuery();
		return rs;
	}

	public boolean ifURLisPresent(String url) throws SQLException {
		String selectQuery = "SELECT  generated_url,IF(original_url='" + url + "','YES','NO') FROM " + tableName;
		PreparedStatement preparedStmt = conn.prepareStatement(selectQuery);
		ResultSet rs = preparedStmt.executeQuery();
		while (rs.next()) {
			String ans = rs.getString(2);
			if (ans.equalsIgnoreCase("yes")) {
				return true;
			}
		}

		return false;
	}

	public boolean ifShortURLisExpired(String url) throws SQLException, ParseException {
		String selectQuery = "SELECT  expiration_time FROM " + tableName + " WHERE generated_url='" + url + "'";
		PreparedStatement preparedStmt = conn.prepareStatement(selectQuery);
		ResultSet rs = preparedStmt.executeQuery();
		while (rs.next()) {
			String ans = rs.getString("expiration_time");
			if (checkExpirationTime(ans)) {
				return true;
			}
		}

		return false;
	}

	public String selectOriginalURL(String url) throws SQLException, ParseException {
		String selectQuery = "SELECT  original_url FROM " + tableName + " WHERE generated_url='" + url + "'";
		PreparedStatement preparedStmt = conn.prepareStatement(selectQuery);
		ResultSet rs = preparedStmt.executeQuery();
		String ans = null;
		while (rs.next()) {
			ans = rs.getString("original_url");

	

		}

		return ans;
	}

	public int selectURLUsageValue(String url) throws SQLException, ParseException {
		String selectQuery = "SELECT  generated_url_usage FROM " + tableName + " WHERE generated_url='" + url + "'";
		PreparedStatement preparedStmt = conn.prepareStatement(selectQuery);
		ResultSet rs = preparedStmt.executeQuery();
		int ans = 0;
		while (rs.next()) {
			ans = rs.getInt("generated_url_usage");


		}

		return ans;
	}

	public String selectExpirationTime(String url) throws SQLException, ParseException {
		String selectQuery = "SELECT  expiration_time FROM " + tableName + " WHERE generated_url='" + url + "'";
		PreparedStatement preparedStmt = conn.prepareStatement(selectQuery);
		ResultSet rs = preparedStmt.executeQuery();
		String ans = null;
		while (rs.next()) {
			ans = rs.getString("expiration_time");

	
		}

		return ans;
	}

	public int TotalUsageValue() throws SQLException, ParseException {
		String selectQuery = "SELECT  generated_url_usage FROM " + tableName;
		PreparedStatement preparedStmt = conn.prepareStatement(selectQuery);
		ResultSet rs = preparedStmt.executeQuery();
		int ans = 0;
		int total = 0;
		while (rs.next()) {
			ans = rs.getInt("generated_url_usage");
			total = total + ans;


		}

		return total;
	}

	@SuppressWarnings("deprecation")
	public String getExpirationTime() throws ParseException {
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date now = new Date();
		int newMin = now.getMinutes() + 5;
		now.setMinutes(newMin);
		String strDate = sdfDate.format(now);
		return strDate;
	}

	public boolean checkExpirationTime(String expTime) throws ParseException {
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date exp = sdfDate.parse(expTime);
		Date now = new Date();
		if (now.after(exp)) {
			return true;
		}
		return false;
	}
}
