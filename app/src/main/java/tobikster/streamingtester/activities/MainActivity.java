package tobikster.streamingtester.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import tobikster.streamingtester.R;
import tobikster.streamingtester.broadcastReceivers.BatteryStateReceiver;

public class MainActivity extends Activity  {
	@SuppressWarnings("unused")
	public static final String LOGCAT_TAG = "MainActivity";

	private Button mButton1;
	private Button mButton2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mButton1 = (Button)(findViewById(R.id.button1));
		mButton2 = (Button)(findViewById(R.id.button2));

		mButton1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent startMediaPlayerActivityIntent = new Intent(MainActivity.this, MediaPlayerActivity.class);
				startActivity(startMediaPlayerActivityIntent);
			}
		});
		mButton2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent startWebViewActivityIntent = new Intent(MainActivity.this, WebViewActivity.class);
				startActivity(startWebViewActivityIntent);
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mButton1.setOnClickListener(null);
		mButton2.setOnClickListener(null);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_activity, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean result;
		switch(item.getItemId()) {
			case R.id.menu_item_clear_battery_log:
				if(removeBatteryLogFile()) {
					Toast.makeText(this, "Battery log file removed successfully", Toast.LENGTH_SHORT).show();
				}
				else {
					Toast.makeText(this, "There is a problem with removing battery log file!", Toast.LENGTH_SHORT).show();
				}
				result = true;
				break;
			default:
				result = super.onOptionsItemSelected(item);
		}
		return result;
	}

	private boolean removeBatteryLogFile() {
		return BatteryStateReceiver.removeBatteryLogFile(this);
	}
}
