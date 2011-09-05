package canada.tv.activity;

import java.net.URLEncoder;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import canada.tv.ActivityController;
import canada.tv.CanadaTVApplication;
import canada.tv.R;
import canada.tv.model.Guide;
import canada.tv.utilities.Logger;

public class PostalCodeActivity extends BasicActivity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.postal_code_activity);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		Button submitButton = (Button) findViewById(R.id.submit);
		submitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				TextView postalCodeTextView = (TextView) findViewById(R.id.postal_code);
				String postalCode = postalCodeTextView.getText().toString();

				if (postalCode.equals(""))
					shortAlert("Please enter a postal code.");
				else if (!postalCode.matches("[A-Za-z]\\d[A-Za-z]\\d[A-Za-z]\\d"))
					shortAlert("Please follow the following convention: X1X1X1");
				else
					getGuide(postalCode);

				return;
			}
		});
		super.onResume();
	}

	protected void getGuide(final String postalCode) {
		AsyncTask<String, Integer, JSONObject> task = new AsyncTask<String, Integer, JSONObject>() {
			ProgressDialog connectionDialog;

			@Override
			protected void onPreExecute() {
				connectionDialog = ProgressDialog.show(PostalCodeActivity.this, "", "Retreiving listing...", true);
				super.onPreExecute();
			}

			@Override
			protected JSONObject doInBackground(String... params) {
				String channels = getChannelInformation(params[0]);

				if (channels == null)
					return null;

				try {
					JSONObject jsonChannels = new JSONObject(channels);
					return jsonChannels;
				} catch (JSONException e) {
					debug(e.getMessage());
				}
				return new JSONObject();
			}

			@Override
			protected void onPostExecute(JSONObject result) {

				if (!connectionDialog.isShowing()) {
					connectionDialog = null;
					return;
				}

				String ERROR_CODE = "ErrorCode";
				String MESSAGE = "Message";
				String errorMessage = "Server Error.";

				if (result == null)
					shortAlert("CONNECTION ERROR");
				else if (!result.isNull(ERROR_CODE)) {
					String error = "SERVER ERROR: ";
					int errorCode;
					try {
						errorCode = result.getInt(ERROR_CODE);
						try {
							errorMessage = result.getString(MESSAGE);
							error += "Code: " + errorCode + " Message: " + errorMessage;
						} catch (JSONException e) {
							error += "Code: " + errorCode + " Message: No message";
							debug(e.getMessage());
						}
					} catch (JSONException e) {
						debug("IMPOSSIBLE ERROR: " + e.getMessage());
					}
					shortAlert(errorMessage);
					debug(error);
				} else {
					try {
						Guide guide = new Guide(result, postalCode);
						ActivityController.ChannelListingActivity(PostalCodeActivity.this, guide);
					} catch (JSONException e) {
						shortAlert(errorMessage);
						debug(e.getMessage());
					}
				}

				connectionDialog.dismiss();
				connectionDialog = null;
			}

			private String getChannelInformation(String postalCode) {
				Calendar c = Calendar.getInstance();
				long time = c.getTimeInMillis() - 2 * 24 * 60 * 60 * 1000;
//				c.setTimeInMillis(time);

				String day = Integer.toString(c.get(Calendar.DAY_OF_MONTH)).trim();
				if (day.length() == 1)
					day = "0" + day;

				String month = Integer.toString(c.get(Calendar.MONTH) + 1).trim();
				if (month.length() == 1)
					month = "0" + month;

				String year = Integer.toString(c.get(Calendar.YEAR)).trim();

				String encodedPostalCode = URLEncoder.encode(postalCode);

				String hour = Integer.toString(c.get(Calendar.HOUR_OF_DAY)).trim();
				if (hour.length() == 1)
					hour = "0" + hour;

				String minutes = Integer.toString(c.get(Calendar.MINUTE)).trim();
				if (minutes.length() == 1)
					minutes = "0" + minutes;

				String seconds = Integer.toString(c.get(Calendar.SECOND)).trim();
				if (seconds.length() == 1)
					seconds = "0" + seconds;

				String url = "http://app.canada.com/entertainment/tribune.svc/ListTVProgramsByHeadend?format=json&headendId=0005580&postalCode=";
				url += encodedPostalCode;
				url += "&duration=1&maxRows=100&startrow=0&startTime=" + year + "-" + month + "-" + day + "T" + hour + ":" + minutes + ":" + seconds;

				return CanadaTVApplication.getStringRequest(url);
			}
		};
		task.execute(postalCode);
	}

	@Override
	protected void debug(String message) {
		Logger.debug(getClass(), message);
	}

}
