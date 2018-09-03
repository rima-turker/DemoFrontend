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
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import model.FullTextNewsResult;
import model.PartialTextNewsResult;

public class NewsRequest {
	/**
	 * Due to encoding problem we depricated this function and switched to normal
	 * Json library.
	 * 
	 * @param category
	 * @return
	 */
	@Deprecated
	public static FullTextNewsResult requestNewsWebhoseOld(String category) {
		final FullTextNewsResult finalResult = new FullTextNewsResult();

		WebhoseIOClient webhoseClient = WebhoseIOClient.getInstance("d7f1e16e-a94c-4d89-9536-7b303c0e7e0e");

		Map<String, String> queries = new HashMap<String, String>();
		queries.put("q", "site_category:" + category + " language:english site_type:news");

		queries.put("sort", "crawled");

		JsonElement result;
		try {
			result = webhoseClient.query("filterWebContent", queries);

			JsonArray postArray = result.getAsJsonObject().getAsJsonArray("posts");

			final int randomNumber = new Random().nextInt(postArray.size());

			for (int i = 0; i < postArray.size(); i++) {
				final JsonElement o = postArray.get(i);
				if (i == randomNumber) {
					finalResult.setFullText(o.getAsJsonObject().get("text").getAsString());
					finalResult
							.setTitle(o.getAsJsonObject().get("thread").getAsJsonObject().get("title").getAsString());
					finalResult.setSource(
							o.getAsJsonObject().get("thread").getAsJsonObject().get("site_full").getAsString());
					finalResult.setLink(o.getAsJsonObject().get("thread").getAsJsonObject().get("url").getAsString());
					return finalResult;
				}
			}
			return null;
		} catch (URISyntaxException | IOException e) {
			// LOG.error(e.getMessage());
			return null;
		}

	}

	public static FullTextNewsResult requestNewsWebhose(String category) {
		final FullTextNewsResult finalResult = new FullTextNewsResult();

		WebhoseIOClient webhoseClient = WebhoseIOClient.getInstance("d7f1e16e-a94c-4d89-9536-7b303c0e7e0e");

		Map<String, String> queries = new HashMap<String, String>();
		queries.put("q", "site_category:" + category + " language:english site_type:news");

		queries.put("sort", "crawled");

		JsonElement result;
		try {
			result = webhoseClient.query("filterWebContent", queries);
			final JSONObject obj = new JSONObject(result.toString());
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

	public static PartialTextNewsResult requestNewsApi(String category) {
		try {

			final PartialTextNewsResult finalResult = new PartialTextNewsResult();

			final HttpClient client = new DefaultHttpClient();
			String url = "https://newsapi.org/v2/top-headlines?country=us&category=" + category
					+ "&apiKey=fa3a923cfb1c4efcb920676e5d92e147";
			final HttpGet request = new HttpGet(url);
			final HttpResponse response = client.execute(request);
			final BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			final StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}

			String json = result.toString();

			JsonParser parser = new JsonParser();
			com.google.gson.JsonObject o = parser.parse(json).getAsJsonObject();

			final JsonArray sourceArray = o.getAsJsonObject().getAsJsonArray("articles");

			final int randomNumber = new Random().nextInt(sourceArray.size());

			for (int i = 0; i < sourceArray.size(); i++) {
				final JsonElement oo = sourceArray.get(i);
				if (i == randomNumber) {
					finalResult.setDescription(oo.getAsJsonObject().get("description").getAsString());
					finalResult.setTitle(oo.getAsJsonObject().get("title").getAsString());
					finalResult.setLink(oo.getAsJsonObject().get("url").getAsString());
					finalResult
							.setSource(oo.getAsJsonObject().get("source").getAsJsonObject().get("name").getAsString());
					return finalResult;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
