package canada.tv.activity;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;
import canada.tv.R;
import canada.tv.utilities.Logger;

public class VideoActivity extends BasicActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_activity);

		VideoView video = (VideoView) findViewById(R.id.video);
		video.setVideoPath("http://www.yo-yo.org/mp4/yu.mp4");
		MediaController mc = new MediaController(this, true);
		mc.setMediaPlayer(video);
		mc.setAnchorView(video);
		video.setMediaController(mc);
		video.requestFocus();
		video.start();
		video.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				finish();
			}
		});
		video.setOnErrorListener(new OnErrorListener() {
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				finish();
				return true;
			}
		});
	}

	@Override
	protected void debug(String message) {
		Logger.debug(getClass(), message);
	}
	
	@Override
	protected void onDestroy() {
		VideoView video = (VideoView) findViewById(R.id.video);
		video.setMediaController(null);
		video.setOnCompletionListener(null);
		video.setOnErrorListener(null);
		super.onDestroy();
	}

}
