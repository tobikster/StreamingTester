package tobikster.streamingtester.activities;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import tobikster.streamingtester.R;
import tobikster.streamingtester.broadcastReceivers.BatteryStateReceiver;
import tobikster.streamingtester.fragments.ExoPLayerSampleChooserFragment;
import tobikster.streamingtester.fragments.ExoPlayerFragment;
import tobikster.streamingtester.fragments.WebViewFragment;

public
class StreamingTestActivity extends Activity implements ExoPLayerSampleChooserFragment.InteractionListener {
	@SuppressWarnings("unused")
	public static final String LOGCAT_TAG = "StreamingTest";

	public static final String EXTRA_TEST_TYPE = "test_type";
	public static final int TEST_TYPE_EXO_PLAYER = 0;
	public static final int TEST_TYPE_WEB_VIEW = 1;
	public static final int TEST_TYPE_MEDIA_PLAYER = 2;

	BatteryStateReceiver mBatteryStateReceiver;

	@Override
	protected
	void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_streaming_test);

		getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		mBatteryStateReceiver = new BatteryStateReceiver();

		Intent intent = getIntent();
		Fragment fragment;
		switch(intent.getIntExtra(EXTRA_TEST_TYPE, -1)) {
			case TEST_TYPE_EXO_PLAYER:
				fragment = ExoPLayerSampleChooserFragment.newInstance();
				break;

			case TEST_TYPE_WEB_VIEW:
				fragment = WebViewFragment.newInstance();
				break;

			case TEST_TYPE_MEDIA_PLAYER:
				Intent startMediaPlayerActivityIntent = new Intent(this, MediaPlayerActivity.class);
				startActivity(startMediaPlayerActivityIntent);
				fragment = null;
				break;

			default:
				fragment = null;
		}
		if(fragment != null) {
			getFragmentManager().beginTransaction()
			                    .replace(R.id.fragment_container, fragment)
			                    .commit();
		}
	}

	@Override
	protected
	void onResume() {
		super.onResume();
		IntentFilter batteryStateIntentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		registerReceiver(mBatteryStateReceiver, batteryStateIntentFilter);
	}

	@Override
	protected
	void onPause() {
		super.onPause();
		unregisterReceiver(mBatteryStateReceiver);
	}

	@Override
	public
	void onSampleSelected(String contentUri, String contentId, int type) {
		Fragment exoPlayerFragment = ExoPlayerFragment.newInstance(contentUri, contentId, type);
		getFragmentManager().beginTransaction()
		                    .replace(R.id.fragment_container, exoPlayerFragment)
		                    .addToBackStack(null)
		                    .commit();
	}
}
