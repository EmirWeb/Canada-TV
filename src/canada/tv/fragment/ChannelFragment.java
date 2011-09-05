package canada.tv.fragment;

import java.net.URLEncoder;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ListFragment;
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
import canada.tv.model.Program;
import canada.tv.model.ProgramDetails;
import canada.tv.utilities.Logger;
import canada.tv.utilities.Statics;

public class ChannelFragment extends ListFragment {
	
	public static ChannelFragment initialize(Channel channel){
		Bundle args = new Bundle();
		args.putSerializable(Statics.s().GUIDE, channel);
		ChannelFragment c = new ChannelFragment();
		c.setArguments(args);
		return c;
	}

	public Channel getChannel() {
		Bundle arguments = getArguments();

		if (arguments == null)
			return null;

		return (Channel) arguments.getSerializable(Statics.s().GUIDE);
	}

	@Override
	public void onStart(){
		super.onStart();
		Channel channel = getChannel();
		if (channel != null) {
			List<Program> programs = channel.getPrograms();
			setListAdapter(new ArrayAdapter<Program>(getActivity(), R.layout.channel_list_item, programs) {
				@Override
				public View getView(int position, View v, ViewGroup parent) {
					if (v == null) {
						LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
						v = vi.inflate(R.layout.channel_list_item, null);
					}

					Program program = getItem(position);

					TextView channelNumberTextView = (TextView) v.findViewById(R.id.number);
					channelNumberTextView.setText(program.getUserTimes());

					TextView channelNameTextView = (TextView) v.findViewById(R.id.name);
					channelNameTextView.setText(program.getTitle());
					return v;
				}
			});
		} else {
			setListAdapter(null);
		}
	}
	
	@Override
	public void onStop() {
		setListAdapter(null);
		super.onPause();
	}

	protected void getProgram(final Program program) {
		AsyncTask<Program, Integer, JSONObject> task = new AsyncTask<Program, Integer, JSONObject>() {

			@Override
			protected JSONObject doInBackground(Program... params) {
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

				String ERROR_CODE = "ErrorCode";
				String MESSAGE = "Message";
				String errorMessage = "Server Error.";

				if (result == null)
					((BasicActivity) getActivity()).shortAlert("CONNECTION ERROR");
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
					((BasicActivity) getActivity()).shortAlert(errorMessage);
					debug(error);
				} else {
					// try {
					// ActivityController.ChannelListingActivity(getActivity(),
					// new Guide(result));
					
					try {
						ProgramDetails programDetails = new ProgramDetails(result);
						program.setProgramDetails(programDetails);
						showDetails(program);
					} catch (JSONException e) {
						shortAlert(errorMessage);
						debug(e.getMessage());
					}
					// } catch (JSONException e) {
					// ((BasicActivity) getActivity()).shortAlert(errorMessage);
					// debug(e.getMessage());
					// }
				}
			}

			private String getChannelInformation(Program program) {
				String encodedTmsID = URLEncoder.encode(program.getTmsID());
				String url = "http://app.canada.com/entertainment/tribune.svc/ProgramSummary?format=json&programId=";
				url += encodedTmsID;

				return CanadaTVApplication.getStringRequest(url);
			}
		};
		task.execute(program);
	}

	protected void showDetails(Program program) {
		ProgramFragment details = ProgramFragment.initialize(program);
		FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
		fragmentTransaction.replace(R.id.program, details);
		fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();
	}

	protected void shortAlert(String message) {
		((BasicActivity)getActivity()).shortAlert(message);		
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Program program = (Program) l.getItemAtPosition(position);
		if (program.hasDetails())
			showDetails(program);
		else
			getProgram(program);
	}

	private void debug(String message) {
		Logger.debug(getClass(), message);
	}

}