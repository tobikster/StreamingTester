package tobikster.streamingtester.activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import tobikster.streamingtester.R;
import tobikster.streamingtester.broadcastreceivers.BatteryStateReceiver;
import tobikster.streamingtester.fragments.ExoPlayerFragment;
import tobikster.streamingtester.fragments.MediaPlayerFragment;
import tobikster.streamingtester.fragments.SettingsFragment;
import tobikster.streamingtester.fragments.WebViewFragment;
import tobikster.streamingtester.utils.Samples;

public
class StreamingTestActivity extends AppCompatActivity {
	@SuppressWarnings("unused")
	public static final String LOGCAT_TAG = "StreamingTest";
	public static final String EXTRA_CONTENT_ID = "extra_content_id";
	public static final String EXTRA_CONTENT_URI = "extra_content_uri";
	public static final String EXTRA_CONTENT_TYPE = "extra_content_type";
	private static final String TAG = StreamingTestActivity.class.getSimpleName();

	BatteryStateReceiver mBatteryStateReceiver;

	@Override
	protected
	void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_streaming_test);

		getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		int testType = preferences.getInt(getString(R.string.pref_test_type), SettingsFragment.TEST_TYPE_UNKNOWN);

		Intent intent = getIntent();

		if(testType != SettingsFragment.TEST_TYPE_UNKNOWN) {
			Fragment fragment = null;

			String contentId = intent.getStringExtra(EXTRA_CONTENT_ID);
			String contentUri = intent.getStringExtra(EXTRA_CONTENT_URI);
			int contentType = intent.getIntExtra(EXTRA_CONTENT_TYPE, Samples.TYPE_OTHER);

			switch(testType) {
				case SettingsFragment.TEST_TYPE_EXOPLAYER:
					fragment = ExoPlayerFragment.newInstance(contentUri, contentId, contentType, "");
					break;
				case SettingsFragment.TEST_TYPE_MEDIAPLAYER:
					fragment = MediaPlayerFragment.newInstance(contentUri);
					break;

				case SettingsFragment.TEST_TYPE_WEBVIEW:
					fragment = WebViewFragment.newInstance(contentUri, contentType);
					break;
			}
			if(fragment != null) {
				replaceFragment(fragment, false);
			}
		}
	}

	private
	void replaceFragment(Fragment fragment, boolean addToBackStack) {
		final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, fragment);
		if(addToBackStack) {
			transaction.addToBackStack(null);
		}
		transaction.commit();
	}

	@Override
	protected
	void onStop() {
		super.onStop();
	}

	@Override
	protected
	void onPause() {
		unregisterReceiver(mBatteryStateReceiver);
		super.onPause();
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
	void onStart() {
		super.onStart();
		mBatteryStateReceiver = new BatteryStateReceiver();
	}

	@Override
	public
	boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_streaming_tester, menu);
		return true;
	}

	@Override
	public
	boolean onOptionsItemSelected(MenuItem item) {
		boolean eventConsumed;
		switch(item.getItemId()) {
			case R.id.menu_item_toggle_cpu_monitoring:
//				new CpuMonitoringTask().execute((Void)null);
				eventConsumed = true;
				break;

			default:
				eventConsumed = super.onOptionsItemSelected(item);
		}
		return eventConsumed;
	}

	private static
	class CpuMonitoringTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected
		Void doInBackground(Void... params) {
			try {
				Process process = Runtime.getRuntime().exec("top -d 0.1");
				final BufferedReader outputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

				String line;
				while((line = outputReader.readLine()) != null) {
					Log.d(TAG, String.format("TOP command output: %s", line));
				}
			}
			catch(IOException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
}
