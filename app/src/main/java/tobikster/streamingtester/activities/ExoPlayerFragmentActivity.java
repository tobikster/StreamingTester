package tobikster.streamingtester.activities;

import android.app.Activity;
import android.os.Bundle;

import tobikster.streamingtester.R;
import tobikster.streamingtester.fragments.ExoPLayerSampleChooserFragment;
import tobikster.streamingtester.fragments.ExoPlayerFragment;

public
class ExoPlayerFragmentActivity extends Activity implements ExoPLayerSampleChooserFragment.InteractionListener {

	ExoPLayerSampleChooserFragment mSampleChooserFragment;
	ExoPlayerFragment mExoPlayerFragment;

	@Override
	protected
	void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_exo_player);
		mSampleChooserFragment = ExoPLayerSampleChooserFragment.newInstance();
		getFragmentManager().beginTransaction()
				.add(R.id.fragment_container, mSampleChooserFragment)
				.commit();
	}

	@Override
	public
	void onSampleSelected(String contentUri, String contentId, int type) {
		mExoPlayerFragment = ExoPlayerFragment.newInstance(contentUri, contentId, type);
		getFragmentManager().beginTransaction()
				.replace(R.id.fragment_container, mExoPlayerFragment)
				.addToBackStack(null)
				.commit();
	}
}
