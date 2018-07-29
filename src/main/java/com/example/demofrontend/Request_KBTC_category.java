package com.example.demofrontend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import util.URLUTF8Encoder;


@SuppressWarnings("deprecation")
public class Request_KBTC_category {

	private static final String BASE_URL = "http://127.0.0.1:4567/";

	public static String sendHttpGet(String text)  {
		try {
			final HttpClient client = new DefaultHttpClient();
			System.out.println("input Text: " +text);
			System.out.println("encoded Text: " +URLUTF8Encoder.encode(text,false));
			String url = BASE_URL+"category?"+"text="+ URLUTF8Encoder.encode(text,false);
			final HttpGet request = new HttpGet(url);
			final HttpResponse response = client.execute(request);
			final BufferedReader rd = new BufferedReader(
					new InputStreamReader(response.getEntity().getContent()));

			final StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			return result.toString();
		} 
		catch (Exception e) {
			System.err.println("I am here");
			System.out.println(e.getMessage());
		}
		return null;
	}

	public static String run(String text) {

		try {

			final URL url = new URL(BASE_URL+"category?"+"text="+text);
			final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			System.err.println("Accessing REST API...");
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}
			System.err.println("Received result from REST API.");
			final BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			final StringBuilder result = new StringBuilder("");
			String output;
			while ((output = br.readLine()) != null) {
				result.append(output);
			}
			conn.disconnect();
			return result.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}