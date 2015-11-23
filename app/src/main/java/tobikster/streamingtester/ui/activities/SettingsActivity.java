package tobikster.streamingtester.ui.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;

import tobikster.streamingtester.ui.fragments.SettingsFragment;

/**
 * Created by tobikster on 2015-11-06.
 */
public class SettingsActivity extends Activity {


	@Override
	public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
		super.onCreate(savedInstanceState, persistentState);

		getFragmentManager().beginTransaction()
		                    .replace(android.R.id.content, new SettingsFragment())
		                    .commit();
	}
}
