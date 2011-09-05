package canada.tv.fragment;

import android.app.Fragment;

public class BasicFragment extends Fragment {
	private boolean isDestroyed;
	
	public boolean isFinished(){
		return isDestroyed;
	}
	
	@Override
	public void onDestroy() {
		isDestroyed = true;
		super.onDestroy();
	}
}
