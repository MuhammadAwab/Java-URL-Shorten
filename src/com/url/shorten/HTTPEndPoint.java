package com.url.shorten;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.swing.JOptionPane;

public class HTTPEndPoint {
	HTTPEndPoint() {

	}

	public void checkStatus(String orgURL) throws IOException {
		String url = orgURL;
		String status = getStatus(url);
		System.out.println(url + "\t\tStatus:" + status);
	}

	public String getStatus(String url) throws IOException {

		String result = "";
		try {
			URL siteURL = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) siteURL.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();

			int code = connection.getResponseCode();
			if (code == 200) {
				result = "Green";
				
			} else {
				result = "Dont Know";
				JOptionPane.showMessageDialog(null, "URL not valid");
			}
		} catch (Exception e) {
			result = "->Red<-";
		}
		return result;
	}

}