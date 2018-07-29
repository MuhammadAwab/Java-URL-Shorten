package com.url.shorten;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.EventQueue;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import javafx.application.Application;
import javafx.stage.Stage;

import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class Display implements ActionListener, MouseListener {
	JFrame f;
	JPanel pane1, pane2, pane3;
	JLabel orgLabel, lilLabel, tableLabel;
	JButton getShort, runURL;
	JTextField orgField, shortField;
	GroupLayout group1, group2, group3;
	JTable lilURLTable;
	Rectangle tableSize;
	JScrollPane scrollPane;
	HTTPEndPoint httpObj;
	MySQLAccess dbObj;
	Dashboard objDashboard;

	Display() throws SQLException {
		httpObj = new HTTPEndPoint();
		dbObj = new MySQLAccess();
		objDashboard = new Dashboard();

		f = new JFrame("Lil URL ");
		pane1 = new JPanel();
		pane2 = new JPanel();
		pane3 = new JPanel();
		pane1.setBounds(0, 0, 500, 500);
		group1 = new GroupLayout(pane1);
		pane1.setLayout(group1);

		pane2.setBounds(0, 500, 500, 500);
		group2 = new GroupLayout(pane2);
		pane2.setLayout(group2);

		orgLabel = new JLabel("Enter Original URL : ");
		orgLabel.setBounds(50, 50, 200, 30);
		orgField = new JTextField(" ");
		orgField.setBounds(50, 100, 400, 30);
		getShort = new JButton("Make it Short");
		getShort.setBounds(170, 150, 150, 30);
		getShort.addActionListener(this);

		pane3.setBounds(700, 100, 800, 800);
		group3 = new GroupLayout(pane3);
		pane3.setLayout(group3);

		ResultSet rs = dbObj.getLilURLs();
		tableSize = new Rectangle();
		tableSize.setBounds(100, 100, 400, 400);

		tableLabel = new JLabel("List Of Shortened URLs");
		tableLabel.setBounds(225, 50, 200, 30);
		lilURLTable = new JTable(getModel(rs));
		lilURLTable.setBounds(tableSize);
		lilURLTable.setShowGrid(true);
		scrollPane = new JScrollPane(lilURLTable);
		lilURLTable.addMouseListener(this);

		lilLabel = new JLabel("Shortened URL : ");
		lilLabel.setBounds(50, 50, 200, 30);
		shortField = new JTextField(" ");
		shortField.setBounds(50, 100, 400, 30);
		runURL = new JButton("Run this URL");
		runURL.setBounds(170, 150, 150, 30);
		runURL.addActionListener(this);

		pane1.add(orgLabel);
		pane1.add(orgField);
		pane1.add(getShort);

		pane2.add(lilLabel);
		pane2.add(shortField);
		pane2.add(runURL);

		pane3.add(tableLabel);
		pane3.add(lilURLTable);
		pane3.add(scrollPane);

		f.getContentPane().add(pane1);
		f.getContentPane().add(pane2);
		f.getContentPane().add(pane3);
		f.setExtendedState(JFrame.MAXIMIZED_BOTH);
		f.getContentPane().setLayout(null);
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() == getShort) {
			String enteredURL = orgField.getText();
			int id = 0;
			boolean check = false;
			try {
				httpObj.checkStatus(enteredURL);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				check = dbObj.ifURLisPresent(enteredURL);
				if (check == false) {
					dbObj.InsertOriginalURL(enteredURL);
					id = dbObj.getID(enteredURL);
				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ShortenUrl obj = new ShortenUrl();
			String shortUrl = obj.encode(id);
			try {
				dbObj.InsertShortURL(id, enteredURL, shortUrl);
				ResultSet rs = dbObj.getLilURLs();
				lilURLTable.setModel(getModel(rs));
				lilURLTable.updateUI();
			} catch (SQLException | ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			shortField.setText(shortUrl);
			pane2.setVisible(true);
		}
		if (arg0.getSource() == runURL) {
			boolean check = false;
			String shortURL = shortField.getText();
			try {
				check = dbObj.ifShortURLisExpired(shortURL);

			} catch (SQLException | ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (check == true) {
				JOptionPane.showMessageDialog(null, "Sorry..this Link has expired");

			} else {
				String org_url = null;
				URI myUrl = null;
				try {
					int id = dbObj.getIDForShortURL(shortURL);
					dbObj.InsertUsageValueURL(id, shortURL);
					org_url = dbObj.selectOriginalURL(shortURL);
					myUrl = new URI(org_url);
				} catch (URISyntaxException | SQLException | ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				openWebpage(myUrl);
			}
		}

	}

	

	private DefaultTableModel getModel(ResultSet resultSet) throws SQLException {
		ResultSetMetaData metaData = resultSet.getMetaData();
		int columnCount = metaData.getColumnCount();
		Vector<String> columnNames = new Vector<>();

		for (int column = 1; column <= columnCount; column++) {
			columnNames.add(metaData.getColumnName(column));
			System.out.println("ColumnNames " + columnNames);
		}
		DefaultTableModel dataModel = new DefaultTableModel(columnNames, 0);

		while (resultSet.next()) {
			Vector<String> vector = new Vector<>();
			for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
				vector.add(resultSet.getString(columnIndex));
			}
			dataModel.addRow(vector);
		}

		return dataModel;
	}

	public boolean openWebpage(URI uri) {
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			try {
				desktop.browse(uri);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub

		try {
			objDashboard.displayAnalytics(lilURLTable.getValueAt(lilURLTable.getSelectedRow(), 0).toString());
		} catch (SQLException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

}
