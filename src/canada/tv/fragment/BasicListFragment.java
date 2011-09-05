package canada.tv.fragment;

import android.app.ListFragment;

public class BasicListFragment extends ListFragment {
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
