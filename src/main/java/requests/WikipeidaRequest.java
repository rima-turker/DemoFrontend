package requests;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class WikipeidaRequest {
   
	 public static void main(String[] args) {
		 requestWikipeida("Einstein");
	    }
	
	public static String requestWikipeida(
            String text)
    {
        try {
            final HttpClient client = new DefaultHttpClient();
            String url = "https://en.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exintro&explaintext&redirects=1&titles="
                    + text;
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
