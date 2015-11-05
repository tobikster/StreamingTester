package tobikster.streamingtester.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import tobikster.streamingtester.R;
import tobikster.streamingtester.broadcastreceivers.BatteryStateReceiver;

public class MainActivity extends FragmentActivity implements View.OnClickListener {
	@SuppressWarnings("unused")
	public static final String LOGCAT_TAG = "MainActivity";
	public static final int TEST_TYPE_UNKNOWN = 0;
	public static final int TEST_TYPE_MEDIA_PLAYER = 1;
	public static final int TEST_TYPE_WEB_VIEW = 2;
	public static final int TEST_TYPE_EXOPLAYER = 3;

	Button mMediaPlayerTestButton;
	Button mWebViewTestButton;
	Button mExoPlayerYestButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mMediaPlayerTestButton = (Button)(findViewById(R.id.media_player_test_button));
		mWebViewTestButton = (Button)(findViewById(R.id.web_view_test_button));
		mExoPlayerYestButton = (Button)(findViewById(R.id.exo_player_test_button));

		mMediaPlayerTestButton.setOnClickListener(this);
		mWebViewTestButton.setOnClickListener(this);
		mExoPlayerYestButton.setOnClickListener(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mMediaPlayerTestButton.setOnClickListener(null);
		mWebViewTestButton.setOnClickListener(null);
		mExoPlayerYestButton.setOnClickListener(null);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean consumeEvent;
		switch(item.getItemId()) {
			case R.id.menu_item_clear_battery_log:
				if(removeBatteryLogFile()) {
					Toast.makeText(this, "Battery log file removed successfully", Toast.LENGTH_SHORT).show();
				}
				else {
					Toast.makeText(this, "There is a problem with removing battery log file!", Toast.LENGTH_SHORT).show();
				}
				consumeEvent = true;
				break;
			default:
				consumeEvent = super.onOptionsItemSelected(item);
		}
		return consumeEvent;
	}

	private boolean removeBatteryLogFile() {
		return BatteryStateReceiver.removeBatteryLogFile(this);
	}

	@Override
	public void onClick(View v) {
		int testType = TEST_TYPE_UNKNOWN;
		if(v == mMediaPlayerTestButton) {
			testType = TEST_TYPE_MEDIA_PLAYER;
//			startTestActivityIntent.putExtra(StreamingTestActivity.EXTRA_TEST_TYPE, SamplesListActivity.TEST_TYPE_MEDIA_PLAYER);
		}
		else if(v == mWebViewTestButton) {
			testType = TEST_TYPE_WEB_VIEW;
//			startTestActivityIntent.putExtra(StreamingTestActivity.EXTRA_TEST_TYPE, SamplesListActivity.TEST_TYPE_WEB_VIEW);
		}
		else if(v == mExoPlayerYestButton) {
			testType = TEST_TYPE_EXOPLAYER;
//			startTestActivityIntent.putExtra(StreamingTestActivity.EXTRA_TEST_TYPE, SamplesListActivity.TEST_TYPE_EXOPLAYER);
		}
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//		getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt(getString(R.string.pref_test_type), testType);
		editor.apply();
		Intent startTestActivityIntent = new Intent(this, SamplesListActivity.class);
		startActivity(startTestActivityIntent);
	}
}
