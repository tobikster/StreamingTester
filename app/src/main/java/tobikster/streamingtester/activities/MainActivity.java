package tobikster.streamingtester.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import tobikster.streamingtester.R;
import tobikster.streamingtester.fragments.SettingsFragment;

public
class MainActivity extends AppCompatActivity implements View.OnClickListener {
	@SuppressWarnings("unused")
	public static final String LOGCAT_TAG = "MainActivity";

	Button mMediaPlayerTestButton;
	Button mWebViewTestButton;
	Button mExoPlayerTestButton;

	@Override
	protected
	void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mMediaPlayerTestButton = (Button)(findViewById(R.id.media_player_test_button));
		mWebViewTestButton = (Button)(findViewById(R.id.web_view_test_button));
		mExoPlayerTestButton = (Button)(findViewById(R.id.exo_player_test_button));

		mMediaPlayerTestButton.setOnClickListener(this);
		mWebViewTestButton.setOnClickListener(this);
		mExoPlayerTestButton.setOnClickListener(this);

		PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.preferences, false);
	}

	@Override
	protected
	void onDestroy() {
		super.onDestroy();
		mMediaPlayerTestButton.setOnClickListener(null);
		mWebViewTestButton.setOnClickListener(null);
		mExoPlayerTestButton.setOnClickListener(null);
	}

	@Override
	public
	boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public
	boolean onOptionsItemSelected(MenuItem item) {
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
	public
	void onClick(View v) {
		int testType = SettingsFragment.TEST_TYPE_UNKNOWN;
		if(v == mMediaPlayerTestButton) {
			testType = SettingsFragment.TEST_TYPE_MEDIAPLAYER;
		}
		else if(v == mWebViewTestButton) {
			testType = SettingsFragment.TEST_TYPE_WEBVIEW;
		}
		else if(v == mExoPlayerTestButton) {
			testType = SettingsFragment.TEST_TYPE_EXOPLAYER;
		}

		SharedPreferences.Editor preferencesEditor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
		preferencesEditor.putInt(getString(R.string.pref_test_type), testType);
		preferencesEditor.apply();

		Intent startTestActivityIntent = new Intent(this, SamplesListActivity.class);
		startActivity(startTestActivityIntent);
	}
}
