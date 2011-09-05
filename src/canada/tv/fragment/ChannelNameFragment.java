package canada.tv.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import canada.tv.R;
import canada.tv.model.Channel;
import canada.tv.model.Program;
import canada.tv.model.ProgramDetails;
import canada.tv.utilities.Statics;

public class ChannelNameFragment extends BasicFragment {

	public static ChannelNameFragment initialize(Channel channel) {
		Bundle args = new Bundle();
		args.putSerializable(Statics.s().CHANNEL, channel);
		ChannelNameFragment programFragment = new ChannelNameFragment();
		programFragment.setArguments(args);
		return programFragment;
	}

	private final String NULL = "null";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LinearLayout channelNameFragment = (LinearLayout) inflater.inflate(R.layout.channel_name_fragment, container, false);

		Channel channel = getChannel();

		if (channel == null)
			return channelNameFragment;

		String title = channel.getName();

		if (title == null || title.trim().equals(NULL))
			title = Statics.s().NA;

		TextView titleTextView = (TextView) channelNameFragment.findViewById(R.id.title);
		titleTextView.setText(title);

		return channelNameFragment;
	}

	private Channel getChannel() {
		Bundle args = getArguments();
		if (args == null)
			return null;
		return (Channel) args.get(Statics.s().CHANNEL);
	}
}
