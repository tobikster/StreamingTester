package tobikster.streamingtester.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import tobikster.streamingtester.R;
import tobikster.streamingtester.ui.fragments.SampleChooserFragment;
import tobikster.streamingtester.ui.fragments.SettingsFragment;

public class SamplesListActivity extends AppCompatActivity implements SampleChooserFragment.InteractionListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_samples_list);

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		int testType = preferences.getInt(SettingsFragment.PREF_TEST_TYPE, SettingsFragment.TEST_TYPE_UNKNOWN);

		Fragment samplesListFragment = SampleChooserFragment.newInstance(testType);
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, samplesListFragment, "SamplesListFragment");
		transaction.commit();
	}

	@Override
	public void onSampleSelected(String contentUri, String contentId, int type) {
		Intent startTestActivityIntent = new Intent(this, StreamingTestActivity.class);
		startTestActivityIntent.putExtra(StreamingTestActivity.EXTRA_CONTENT_URI, contentUri);
		startTestActivityIntent.putExtra(StreamingTestActivity.EXTRA_CONTENT_ID, contentId);
		startTestActivityIntent.putExtra(StreamingTestActivity.EXTRA_CONTENT_TYPE, type);
		startActivity(startTestActivityIntent);
	}
}
