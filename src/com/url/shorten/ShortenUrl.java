package com.url.shorten;

public class ShortenUrl {
	private String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	private String companyURL = "lilurl.com/";

	public String encode(int num) {
		StringBuilder s = new StringBuilder();
		while (num > 0) {
			s.append(characters.charAt(num % 62));
			num /= 62;
		}
		String returnURL = companyURL.concat(s.reverse().toString());
		return returnURL;
	}
}
