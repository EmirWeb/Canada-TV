package canada.tv.activity;


import java.io.IOException;
import java.nio.channels.Channels;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Address;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.Toast;
import canada.tv.R;
import canada.tv.utilities.Logger;

public abstract class BasicActivity extends Activity {

	private boolean isFinished;
	private boolean isPaused;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		try {
			PackageManager pm = getPackageManager();
			ApplicationInfo ai = new ApplicationInfo();
			ai = pm.getApplicationInfo(getPackageName(), 0);
			Logger.setDebuggable((ai.flags & ApplicationInfo.FLAG_DEBUGGABLE) == ApplicationInfo.FLAG_DEBUGGABLE);
		} catch (NameNotFoundException e) {
			debug(getString(R.string.could_not_set_debugabble));
		}

		isFinished = false;
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		isPaused = false;
		super.onResume();
	}

	@Override
	protected void onPause() {
		isPaused = true;
		super.onPause();
	}

	@Override
	public void finish() {
		isFinished = true;
		super.finish();
	}

	public boolean isFinished() {
		return isFinished;
	}

	public boolean isPaused() {
		return isPaused;
	}

	protected abstract void debug(String message);

	public void shortAlert(String alert) {
		alert(alert, true);
	}

	public void longAlert(String alert) {
		alert(alert, false);
	}

	private void alert(final String alert, final boolean isShort) {

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (isPaused())
					return;

				int length = Toast.LENGTH_LONG;
				if (isShort)
					length = Toast.LENGTH_SHORT;
				Toast.makeText(getApplicationContext(), alert, length).show();
			}
		});
	}

	protected void vibrate(final long milliseconds) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				v.vibrate(milliseconds);
			}
		});
	}
}
