package com.bouloutian.connect_four;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class AmazonInterface {

	public static String messageForURL(final String imageURL) {
		try {
			URL url = new URL(
					"http://ec2-54-82-63-132.compute-1.amazonaws.com/opencv.php?image="
							+ imageURL);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			String line;
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String output = "";
			while ((line = reader.readLine()) != null) {
				output = output + line + "\n";
			}
			reader.close();
			String[] temp = output.split("<BEST_MOVE>");
			if (temp.length > 1) {
				int bestMove = Integer.parseInt(String.valueOf(temp[1]
						.charAt(0)));
				return "Best move is column " + bestMove + ".";
			} else {
				return "OpenCV Error";
			}
		} catch (MalformedURLException e) {
			return "URL Error";
		} catch (IOException e) {
			return "IO Error";
		}
	}

	// Converts a URL to the proper format to use in an HTTP GET request
	private static String convertURL(String url) {
		return url.replace(":", "%3A").replace("/", "%2F");
	}

}
