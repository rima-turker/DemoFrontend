package requests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import model.FullTextNewsResult;
import model.PartialTextNewsResult;

public class NewsRequest {

	public static FullTextNewsResult requestNewsWebhose(String category) {
		final FullTextNewsResult finalResult = new FullTextNewsResult();

		WebhoseIOClient webhoseClient = WebhoseIOClient.getInstance("d7f1e16e-a94c-4d89-9536-7b303c0e7e0e");

		Map<String, String> queries = new HashMap<String, String>();
		queries.put("q", "site_category:" + category + " language:english site_type:news");

		queries.put("sort", "crawled");

		JSONObject obj;
		try {
			obj = webhoseClient.query("filterWebContent", queries);
			JSONArray postArray = obj.getJSONArray("posts");
			final int randomNumber = new Random().nextInt(postArray.length());

			for (int i = 0; i < postArray.length(); i++) {
				final JSONObject o = (JSONObject) postArray.get(i);
				if (i == randomNumber) {
					finalResult.setFullText(o.getString("text"));
					finalResult.setTitle(((JSONObject) o.get("thread")).getString("title"));
					finalResult.setSource(((JSONObject) o.get("thread")).getString("site_full"));
					finalResult.setLink(((JSONObject) o.get("thread")).getString("url"));
					return finalResult;
				}
			}
			return null;
		} catch (URISyntaxException | IOException e) {
			return null;
		}

	}

	public static FullTextNewsResult requestNewsWebhose() {
		final FullTextNewsResult finalResult = new FullTextNewsResult();

		final WebhoseIOClient webhoseClient = WebhoseIOClient.getInstance("d7f1e16e-a94c-4d89-9536-7b303c0e7e0e");

		final Map<String, String> queries = new HashMap<String, String>();
		queries.put("q", "language:english site_type:news");

		queries.put("sort", "crawled");

		JSONObject obj;
		try {
			obj = webhoseClient.query("filterWebContent", queries);
			final JSONArray postArray = obj.getJSONArray("posts");
			final int randomNumber = new Random().nextInt(postArray.length());

			for (int i = 0; i < postArray.length(); i++) {
				final JSONObject o = (JSONObject) postArray.get(i);
				if (i == randomNumber) {
					finalResult.setFullText(o.getString("text"));
					finalResult.setTitle(((JSONObject) o.get("thread")).getString("title"));
					finalResult.setSource(((JSONObject) o.get("thread")).getString("site_full"));
					finalResult.setLink(((JSONObject) o.get("thread")).getString("url"));
					return finalResult;
				}
			}
			return null;
		} catch (URISyntaxException | IOException e) {
			return null;
		}

	}

	public static PartialTextNewsResult requestNewsApi() {
		try {

			final PartialTextNewsResult finalResult = new PartialTextNewsResult();

			final HttpClient client = new DefaultHttpClient();
			final String url = "https://newsapi.org/v2/everything?q=the&apiKey=fa3a923cfb1c4efcb920676e5d92e147";
			final HttpGet request = new HttpGet(url);
			final HttpResponse response = client.execute(request);
			final JSONObject obj = new JSONObject(EntityUtils.toString(response.getEntity(), "UTF-8"));

			final JSONArray sourceArray = obj.getJSONArray("articles");

			final int randomNumber = new Random().nextInt(sourceArray.length());

			for (int i = 0; i < sourceArray.length(); i++) {
				final JSONObject oo = (JSONObject) sourceArray.get(i);
				if (i == randomNumber) {
					finalResult.setDescription(oo.getString("description"));
					finalResult.setTitle(oo.getString("title"));
					finalResult.setLink(oo.getString("url"));
					finalResult.setSource(((JSONObject) oo.get("source")).getString("name"));
					return finalResult;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
