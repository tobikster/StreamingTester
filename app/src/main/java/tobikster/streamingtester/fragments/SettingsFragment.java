package tobikster.streamingtester.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import tobikster.streamingtester.R;

public
class SettingsFragment extends PreferenceFragment {

	@Override
	public
	void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}
}
