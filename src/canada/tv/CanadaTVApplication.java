package canada.tv;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import canada.tv.utilities.Logger;

public class CanadaTVApplication extends Application {

	private static CanadaTVApplication instance;
	private ExecutorService threadPool;

	private CanadaTVApplication() {
	}

	private static CanadaTVApplication getInstance() {
		if (instance == null)
			instance = new CanadaTVApplication();
		return instance;
	}

	public static ExecutorService getThreadPool() {
		ExecutorService threadPool = getInstance().threadPool;
		if (threadPool == null) {
			int NUM_THREADS = 20;
			threadPool = Executors.newFixedThreadPool(NUM_THREADS);
			getInstance().threadPool = threadPool;
		}

		return threadPool;
	}

	private static void debug(String message) {
		Logger.debug(CanadaTVApplication.class, message);
	}

	public static String getStringRequest(String url) {
		HttpEntity entity = getEntity(url);
		if (entity == null)
			return null;

		try {
			return entityToString(entity);
		} catch (ParseException e) {
			debug(e.getMessage());
		} catch (IOException e) {
			debug(e.getMessage());
		}
		return null;
	}

	private static String entityToString(HttpEntity entity) throws ParseException, IOException {
		return EntityUtils.toString(entity);
	}

	public static Bitmap getBitmapRequest(String url) {
		HttpEntity entity = getEntity(url);
		if (entity == null)
			return null;

		return entityToBitmap(entity);
	}

	private static Bitmap entityToBitmap(HttpEntity entity) {
		InputStream is = null;
		try {
			BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
			is = bufHttpEntity.getContent();
			Bitmap bitmap = BitmapFactory.decodeStream(is);
			return bitmap;
		} catch (IOException e) {
			debug(e.getMessage());
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
		return null;
	}

	private static HttpEntity getEntity(String url) {
		debug("Request: " + url);

		HttpGet httpRequest = new HttpGet(url);

		HttpParams httpParameters = new BasicHttpParams();
		DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);

		HttpConnectionParams.setConnectionTimeout(httpParameters, 10000);
		HttpConnectionParams.setSoTimeout(httpParameters, 10000);
		httpClient.getConnectionManager().closeExpiredConnections();
		try {
			HttpResponse response = httpClient.execute(httpRequest);
			return response.getEntity();
		} catch (ClientProtocolException e) {
			debug("Failed clientProtocolException: " + e.getMessage());
		} catch (IOException e) {
			debug("Failed IOException: " + e.getMessage());
		} finally {
			httpClient.getConnectionManager().closeExpiredConnections();
		}
		return null;
	}

}