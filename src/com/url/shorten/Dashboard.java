package com.url.shorten;

import java.sql.SQLException;
import java.text.ParseException;

import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Dashboard {
	MySQLAccess dbObj;
	GroupLayout group1;
	JPanel pane;
	JFrame frame;
	JLabel displayLabel, timesUsed, percentageUsed, expTime;

	public void displayAnalytics(String url) throws SQLException, ParseException {
		dbObj = new MySQLAccess();
		frame = new JFrame("Analytics Dashboard");
		pane = new JPanel();
		pane.setBounds(0, 0, 500, 500);
		group1 = new GroupLayout(pane);
		pane.setLayout(group1);
		displayLabel = new JLabel(url);
		displayLabel.setBounds(150, 50, 300, 30);
		timesUsed = new JLabel();
		int usageValue = dbObj.selectURLUsageValue(url);
		int totalValue = dbObj.TotalUsageValue();
		String timeExp = dbObj.selectExpirationTime(url);
		timesUsed.setText("Number of Times Used    :    " + String.valueOf(usageValue));
		timesUsed.setBounds(50, 100, 200, 30);
		float percentage = (usageValue * 100.0f) / totalValue;
		percentageUsed = new JLabel();
		percentageUsed.setBounds(50, 150, 200, 30);
		percentageUsed.setText("Usage Percentage   :    " + String.valueOf(percentage) + " %");
		expTime = new JLabel();
		expTime.setBounds(50, 200, 300, 30);
		expTime.setText("Expiration Time    :    " + timeExp);
		pane.add(displayLabel);
		pane.add(timesUsed);
		pane.add(percentageUsed);
		pane.add(expTime);
		frame.getContentPane().add(pane);
		frame.getContentPane().setLayout(null);
		frame.setBounds(300, 300, 400, 400);
		frame.setVisible(true);
	}
}