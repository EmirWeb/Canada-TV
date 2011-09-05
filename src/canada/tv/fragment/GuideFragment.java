package canada.tv.fragment;

import java.net.URLEncoder;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.FragmentTransaction;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import canada.tv.CanadaTVApplication;
import canada.tv.R;
import canada.tv.activity.BasicActivity;
import canada.tv.model.Channel;
import canada.tv.model.Guide;
import canada.tv.utilities.Logger;

public class GuideFragment extends BasicListFragment {
	private final String LAST_CHANNEL = "lastChannel";
	private int lastChannel = 0;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (savedInstanceState != null)
			lastChannel = savedInstanceState.getInt(LAST_CHANNEL, 0);

	}

	@Override
	public void onStart() {
		Guide guide = (Guide) getActivity().getIntent().getSerializableExtra(getString(R.string.guide_extra));

		setListAdapter(new ArrayAdapter<Channel>(getActivity(), R.layout.channel_list_item, guide.getChannels()) {
			@Override
			public View getView(int position, View v, ViewGroup parent) {
				if (v == null) {
					LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					v = vi.inflate(R.layout.channel_list_item, null);
				}

				Channel channel = getItem(position);

				TextView channelNumberTextView = (TextView) v.findViewById(R.id.number);
				channelNumberTextView.setText(Integer.toString(channel.getNumber()));

				TextView channelNameTextView = (TextView) v.findViewById(R.id.name);
				channelNameTextView.setText(channel.getName());

				return v;
			}
		});

		debug("PAUSE");
		
		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		setChannel((Channel) getListView().getItemAtPosition(lastChannel));
		super.onResume();
	}
	
	@Override
	public void onStop() {
		setListAdapter(null);
		super.onPause();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(LAST_CHANNEL, lastChannel);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Channel channel = (Channel) l.getItemAtPosition(position);
		setChannel(channel);
	}

	void setChannel(Channel channel) {
		lastChannel = channel.getPosition();

		getListView().setItemChecked(lastChannel, true);

		ChannelFragment details = (ChannelFragment) getFragmentManager().findFragmentById(R.id.channel);
		if (details == null || details.getChannel() == null || !details.getChannel().getCallSign().equals(channel.getCallSign())) {
			getChannel(channel);
		} else
			Logger.debug(getClass(), "NOT UPDATED");
	}

	protected void getChannel(final Channel channel) {
		if (channel.getLastUpdate() != -1 && System.currentTimeMillis() - channel.getLastUpdate() < 5 * 60 * 1000) {
			showChannel(channel);
			return;
		}
		channel.setLastUpdate(System.currentTimeMillis());
		AsyncTask<Channel, Integer, JSONObject> task = new AsyncTask<Channel, Integer, JSONObject>() {

			@Override
			protected JSONObject doInBackground(Channel... params) {
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

				if (isFinished())
					return;

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
						Guide guide = new Guide(result);
						for (Channel c : guide.getChannels()) {
							if (channel.getCallSign().equals(c.getCallSign())) {
								channel.setPorgrams(c.getPrograms());
								break;
							}
						}
						showChannel(channel);
					} catch (JSONException e) {
						shortAlert(errorMessage);
						debug(e.getMessage());
					}

				}
			}

			private String getChannelInformation(Channel channel) {

				if (channel == null)
					return null;

				Calendar c = Calendar.getInstance();

				String day = Integer.toString(c.get(Calendar.DAY_OF_MONTH)).trim();
				if (day.length() == 1)
					day = "0" + day;

				String month = Integer.toString(c.get(Calendar.MONTH) + 1).trim();
				if (month.length() == 1)
					month = "0" + month;

				String year = Integer.toString(c.get(Calendar.YEAR)).trim();

				String encodedPostalCode = URLEncoder.encode(channel.getPostalCode());

				String hour = Integer.toString(c.get(Calendar.HOUR_OF_DAY)).trim();
				if (hour.length() == 1)
					hour = "0" + hour;

				String minutes = Integer.toString(c.get(Calendar.MINUTE)).trim();
				if (minutes.length() == 1)
					minutes = "0" + minutes;

				String seconds = Integer.toString(c.get(Calendar.SECOND)).trim();
				if (seconds.length() == 1)
					seconds = "0" + seconds;

				String position = Integer.toString(channel.getPosition());

				String url = "http://app.canada.com/entertainment/tribune.svc/ListTVProgramsByHeadend?format=json&headendId=0005580&postalCode=";
				url += encodedPostalCode;
				url += "&duration=600&maxRows=1&startrow=" + position + "&startTime=" + year + "-" + month + "-" + day + "T" + hour + ":" + minutes + ":" + seconds;

				return CanadaTVApplication.getStringRequest(url);
			}
		};
		task.execute(channel);
	}

	protected void showChannel(Channel channel) {
		if (lastChannel != channel.getPosition())
			return;

		
		ChannelFragment details = ChannelFragment.initialize(channel);
		ChannelNameFragment channelNameFragment = ChannelNameFragment.initialize(channel);
		FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
		fragmentTransaction.replace(R.id.channel_name, channelNameFragment);
		fragmentTransaction.replace(R.id.channel, details);
		fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();
	}

	protected void debug(String message) {
		Logger.debug(getClass(), message);
	}

	private void shortAlert(String message) {
		((BasicActivity) getActivity()).shortAlert(message);
	}
}
