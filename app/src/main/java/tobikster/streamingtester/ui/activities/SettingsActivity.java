package tobikster.streamingtester.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import tobikster.streamingtester.R;
import tobikster.streamingtester.ui.fragments.SettingsFragment;
import tobikster.streamingtester.utils.FileUtils;

/**
 * Created by tobikster on 2015-11-06.
 */
public class SettingsActivity extends Activity {
	private static final String ACTION_REMOVE_BATTERY_LOG_FILE = "tobikster.streamingtester.REMOVE_BATTERY_LOG_FILE";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		// Display the fragment as the main content.
		getFragmentManager().beginTransaction()
		                    .replace(android.R.id.content, new SettingsFragment())
		                    .commit();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if(intent.getAction() != null && intent.getAction().equals(ACTION_REMOVE_BATTERY_LOG_FILE)) {
			if(FileUtils.removeBatteryLogFile(this)) {
				Toast.makeText(this, R.string.info_battery_log_file_removed_successfully, Toast.LENGTH_LONG).show();
			}
		}
	}
}
