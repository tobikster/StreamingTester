package tobikster.streamingtester.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import tobikster.streamingtester.R;
import tobikster.streamingtester.fragments.SettingsFragment;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
	@SuppressWarnings("unused")
	public static final String LOGCAT_TAG = "MainActivity";

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
			case R.id.menu_item_settings:
				Intent startSettingsActivityIntent = new Intent(this, SettingsActivity.class);
				startActivity(startSettingsActivityIntent);
				consumeEvent = true;
				break;

			default:
				consumeEvent = super.onOptionsItemSelected(item);
		}
		return consumeEvent;
	}

	@Override
	public void onClick(View v) {
		int testType = SettingsFragment.TEST_TYPE_UNKNOWN;
		if(v == mMediaPlayerTestButton) {
			testType = SettingsFragment.TEST_TYPE_MEDIAPLAYER;
		}
		else if(v == mWebViewTestButton) {
			testType = SettingsFragment.TEST_TYPE_WEBVIEW;
		}
		else if(v == mExoPlayerYestButton) {
			testType = SettingsFragment.TEST_TYPE_EXOPLAYER;
		}

		SharedPreferences.Editor preferencesEditor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
		preferencesEditor.putInt(SettingsFragment.PREF_TEST_TYPE, testType);
		preferencesEditor.apply();

		Intent startTestActivityIntent = new Intent(this, SamplesListActivity.class);
		startActivity(startTestActivityIntent);
	}
}
