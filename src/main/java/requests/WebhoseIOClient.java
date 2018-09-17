package requests;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;


public class WebhoseIOClient {

	private static final String WEBHOSE_BASE_URL = "http://webhose.io";
	private static WebhoseIOClient mClient;
	private String mApiKey;

	/**
	 * Private constructor
	 */
	private WebhoseIOClient() {
	}

	private WebhoseIOClient(String apiKey) {
		this.mApiKey = apiKey;
	}

	public static WebhoseIOClient getInstance(String apiKey) {
		if (mClient == null) {
			mClient = new WebhoseIOClient(apiKey);
		}

		return mClient;
	}

	public JSONObject getResponse(String rawUrl) throws IOException, URISyntaxException {

		final HttpClient client = new DefaultHttpClient();
		final HttpGet request = new HttpGet(rawUrl);
		final HttpResponse response = client.execute(request);
		final JSONObject obj =new JSONObject(EntityUtils.toString(response.getEntity(),"UTF-8"));
		return obj;		
	}

	public JSONObject query(String endpoint, Map<String, String> queries) throws URISyntaxException, IOException {
		try {
			URIBuilder builder = new URIBuilder(String.format("%s/%s?token=%s&format=json", WEBHOSE_BASE_URL, endpoint, mApiKey));
			for (String key : queries.keySet()) {
				builder.addParameter(key, queries.get(key));
			}		
			
			return getResponse(builder.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}