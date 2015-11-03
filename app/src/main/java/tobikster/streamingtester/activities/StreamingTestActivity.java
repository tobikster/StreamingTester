package tobikster.streamingtester.activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
			Fragment fragment = null;
			switch(mTestType) {
				case TEST_TYPE_EXO_PLAYER:
				case TEST_TYPE_MEDIA_PLAYER:
					fragment = SampleChooserFragment.newInstance();
					break;

				case TEST_TYPE_WEB_VIEW:
					fragment = WebViewFragment.newInstance();
					break;
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
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_streaming_tester, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean eventConsumed;
		switch(item.getItemId()) {
			case R.id.menu_item_toggle_cpu_monitoring:
				eventConsumed = true;
				break;

			default:
				eventConsumed = super.onOptionsItemSelected(item);
		}
		return eventConsumed;
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

	private static class CPUMonitoringTask extends AsyncTask<Integer, Void, Void> {

		@Override
		protected Void doInBackground(Integer... params) {
			int refreshInterval = params[0];
			try {
				Process process = Runtime.getRuntime().exec(String.format("top -d %d", refreshInterval));
				BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			}
			catch(IOException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
}
