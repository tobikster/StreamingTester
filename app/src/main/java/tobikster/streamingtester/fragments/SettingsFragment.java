package tobikster.streamingtester.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import tobikster.streamingtester.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragment {
	public static final String PREF_TEST_TYPE = "test_type";
	public static final int TEST_TYPE_UNKNOWN = 0;
	public static final int TEST_TYPE_MEDIAPLAYER = 1;
	public static final int TEST_TYPE_EXOPLAYER = 2;
	public static final int TEST_TYPE_WEBVIEW = 3;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preferences);
	}
}
