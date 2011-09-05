package canada.tv.fragment;

import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import canada.tv.ActivityController;
import canada.tv.CanadaTVApplication;
import canada.tv.R;
import canada.tv.activity.PostalCodeActivity;
import canada.tv.model.Program;
import canada.tv.model.ProgramDetails;
import canada.tv.utilities.Logger;
import canada.tv.utilities.Statics;

public class ProgramFragment extends BasicFragment {

	private Bitmap thumbnail;

	public static ProgramFragment initialize(Program program) {
		Bundle args = new Bundle();
		args.putSerializable(Statics.s().PROGRAM, program);
		ProgramFragment programFragment = new ProgramFragment();
		programFragment.setArguments(args);
		return programFragment;
	}


	protected void getImage(String url) {
		AsyncTask<String, Integer, Bitmap> thumbnailTask = new AsyncTask<String, Integer, Bitmap>() {

			@Override
			protected Bitmap doInBackground(String... params) {
				return CanadaTVApplication.getBitmapRequest(params[0]);
			}

			@Override
			protected void onPostExecute(Bitmap result) {
				if (isFinished() || result == null)
					return;

				thumbnail = result;

				ImageView imageView = (ImageView) getView().findViewById(R.id.program_image);
				if (imageView != null)
					imageView.setImageBitmap(result);
			}

		};

		thumbnailTask.execute(url);

	}

	protected void debug(String message) {
		Logger.debug(getClass(), message);
	}

	private final String NULL = "null";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LinearLayout programFragmentLayout = (LinearLayout) inflater.inflate(R.layout.program_fragment, container, false);

		Program program = getProgram();

		if (program == null)
			return programFragmentLayout;

		ProgramDetails programDetails = program.getProgramDetails();

		String title = programDetails.getTitle();

		if (title == null || title.trim().equals(NULL))
			title = Statics.s().NA;

		TextView titleTextView = (TextView) programFragmentLayout.findViewById(R.id.title);
		titleTextView.setText(title);

		String description = programDetails.getDescription();

		if (description == null || description.trim().equals(NULL))
			description = Statics.s().NA;

		TextView descriptionTextView = (TextView) programFragmentLayout.findViewById(R.id.description);
		descriptionTextView.setText(description);

		String programType = programDetails.getProgramType();

		if (programType == null || programType.trim().equals(NULL))
			programType = Statics.s().NA;

		TextView programTypeTextView = (TextView) programFragmentLayout.findViewById(R.id.program_type);
		programTypeTextView.setText(programType);

		if (thumbnail != null) {
			ImageView imageView = (ImageView) programFragmentLayout.findViewById(R.id.program_image);
			if (imageView != null)
				imageView.setImageBitmap(thumbnail);
		}

		Button watchButton = (Button) programFragmentLayout.findViewById(R.id.watch);
		watchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ActivityController.ChannelListingActivity(getActivity(), getProgram().getProgramDetails());
			}
		});
		return programFragmentLayout;
	}

	@Override
	public void onStart() {
		if (thumbnail == null) {
			AsyncTask<String, Integer, String> thumbnailTask = new AsyncTask<String, Integer, String>() {

				@Override
				protected String doInBackground(String... params) {
					return CanadaTVApplication.getStringRequest(params[0]);
				}

				@Override
				protected void onPostExecute(String result) {
					if (isFinished() || result == null)
						return;
					try {
						JSONObject json = new JSONObject(result);
						JSONObject searchResponse = json.getJSONObject("SearchResponse");
						JSONObject image = searchResponse.getJSONObject("Image");
						JSONArray results = image.getJSONArray("Results");
						JSONObject jsonImage = results.getJSONObject(0);
						String url = jsonImage.getString("MediaUrl");
						getImage(url);
					} catch (JSONException e) {
						debug(e.getMessage());
					}
				}
			};
			Program program = getProgram();
			if (program == null)
				return;

			String key = getString(R.string.bing_api_key);
			String query = program.getTitle() + " TV show";
			query = URLEncoder.encode(query);
			String url = "http://api.bing.net/json.aspx?AppId=" + key + "&Query=" + query + "&Sources=Image&Version=2.0&Market=en-us&Adult=Moderate&Image.Count=1&Image.Offset=0";
			thumbnailTask.execute(url);
		}
		super.onStart();
	}

	private Program getProgram() {
		Bundle args = getArguments();
		if (args == null)
			return null;
		return (Program) args.get(Statics.s().PROGRAM);
	}
	
	@Override
	public void onDestroyView() {
		Button watchButton = (Button) getView().findViewById(R.id.watch);
		watchButton.setOnClickListener(null);
		super.onDestroyView();
	}
}
