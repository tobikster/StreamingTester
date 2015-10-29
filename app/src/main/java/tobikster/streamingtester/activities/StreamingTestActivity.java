package tobikster.streamingtester.activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import tobikster.streamingtester.R;
import tobikster.streamingtester.broadcastreceivers.BatteryStateReceiver;
import tobikster.streamingtester.fragments.ExoPlayerFragment;
import tobikster.streamingtester.fragments.MediaPlayerFragment;
import tobikster.streamingtester.fragments.SampleChooserFragment;
import tobikster.streamingtester.fragments.WebViewFragment;

public class StreamingTestActivity extends FragmentActivity implements SampleChooserFragment.InteractionListener {
	@SuppressWarnings("unused")
	public static final String LOGCAT_TAG = "StreamingTest";

	public static final String EXTRA_TEST_TYPE = "test_type";

	public static final int TEST_TYPE_EXO_PLAYER = 0;
	public static final int TEST_TYPE_WEB_VIEW = 1;
	public static final int TEST_TYPE_MEDIA_PLAYER = 2;

	private int mTestType;

	BatteryStateReceiver mBatteryStateReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_streaming_test);

		getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


		Intent intent = getIntent();
		mTestType = intent.getIntExtra(EXTRA_TEST_TYPE, -1);

		if(mTestType != -1) {
			Fragment fragment;
			switch(mTestType) {
				case TEST_TYPE_EXO_PLAYER:
				case TEST_TYPE_MEDIA_PLAYER:
					fragment = SampleChooserFragment.newInstance();
					break;

				case TEST_TYPE_WEB_VIEW:
					fragment = WebViewFragment.newInstance();
					break;

				default:
					fragment = null;
			}
			if(fragment != null) {
				replaceFragment(fragment, false);
			}
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		mBatteryStateReceiver = new BatteryStateReceiver();
	}

	@Override
	protected void onResume() {
		super.onResume();
		IntentFilter batteryStateIntentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		registerReceiver(mBatteryStateReceiver, batteryStateIntentFilter);
	}

	@Override
	protected void onPause() {
		unregisterReceiver(mBatteryStateReceiver);
		super.onPause();
	}

	@Override
	public void onSampleSelected(String contentUri, String contentId, int type) {
		switch(mTestType) {
			case TEST_TYPE_EXO_PLAYER:
				Fragment exoPlayerFragment = ExoPlayerFragment.newInstance(contentUri, contentId, type);
				replaceFragment(exoPlayerFragment);
				break;

			case TEST_TYPE_MEDIA_PLAYER:
				Fragment mediaPlayerFragment = MediaPlayerFragment.newInstance(contentUri);
				replaceFragment(mediaPlayerFragment);
				break;
		}
	}

	private void replaceFragment(Fragment fragment) {
		replaceFragment(fragment, true);
	}

	private void replaceFragment(Fragment fragment, boolean addToBackStack) {
		final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, fragment);
		if(addToBackStack) {
			transaction.addToBackStack(null);
		}
		transaction.commit();
	}
}
