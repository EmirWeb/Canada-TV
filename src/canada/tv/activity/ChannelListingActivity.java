package canada.tv.activity;

import android.os.Bundle;
import canada.tv.R;
import canada.tv.utilities.Logger;

public class ChannelListingActivity extends BasicActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.channel_listing_activity);
	}
	
	@Override
	protected void debug(String message) {
		Logger.debug(getClass(), message);
	}
}
