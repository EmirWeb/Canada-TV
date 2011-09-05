package canada.tv;

import android.app.Activity;
import android.content.Intent;
import canada.tv.activity.ChannelListingActivity;
import canada.tv.activity.VideoActivity;
import canada.tv.model.Guide;
import canada.tv.model.ProgramDetails;

public class ActivityController {

	public static void ChannelListingActivity(Activity activity, Guide guide) {
		Intent channelListingActivity = new Intent(activity.getApplicationContext(), ChannelListingActivity.class);
		channelListingActivity.putExtra(activity.getString(R.string.guide_extra), guide);
		activity.startActivity(channelListingActivity);
	}
	
	public static void ChannelListingActivity(Activity activity, ProgramDetails programDetails) {
		Intent VideoActivity = new Intent(activity.getApplicationContext(), VideoActivity.class);
		VideoActivity.putExtra(activity.getString(R.string.program_details_extra), programDetails);
		activity.startActivity(VideoActivity);
	}
}
