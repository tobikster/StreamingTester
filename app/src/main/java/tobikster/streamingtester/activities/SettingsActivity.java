package tobikster.streamingtester.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import tobikster.streamingtester.R;
import tobikster.streamingtester.fragments.SettingsFragment;
import tobikster.streamingtester.utils.FileUtils;

/**
 * Created by tobikster on 2015-11-06.
 */
public
class SettingsActivity extends AppCompatActivity {
	private static final String ACTION_REMOVE_BATTERY_LOG_FILE = "tobikster.streamingtester.REMOVE_BATTERY_LOG_FILE";
	private static final String ACTION_INITIALIZE_DATABASE = "tobikster.streamingtester.INITIALIZE_DATABASE";

	@Override
	protected
	void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		// Display the fragment as the main content.
		getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
	}

	@Override
	protected
	void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if(intent.getAction() != null) {
			switch(intent.getAction()) {
				case ACTION_REMOVE_BATTERY_LOG_FILE:
					if(FileUtils.removeBatteryLogFile(this)) {
						Snackbar.make(findViewById(android.R.id.content), R.string.info_battery_log_file_removed_successfully, Snackbar.LENGTH_SHORT).show();
					}
					break;

				case ACTION_INITIALIZE_DATABASE:
					Toast.makeText(this, R.string.info_operation_not_supported_yet, Toast.LENGTH_LONG).show();
					break;
			}
		}
	}
}
