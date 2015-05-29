package tobikster.streamingtester.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import tobikster.streamingtester.R;
import tobikster.streamingtester.broadcastReceivers.BatteryStateReceiver;

public
class MainActivity extends Activity implements View.OnClickListener {
	@SuppressWarnings("unused")
	public static final String LOGCAT_TAG = "MainActivity";

	private Button mButton1;
	private Button mButton2;
	Button mButton3;

	@Override
	protected
	void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mButton1 = (Button)(findViewById(R.id.button1));
		mButton2 = (Button)(findViewById(R.id.button2));
		mButton3 = (Button)(findViewById(R.id.button3));

		mButton1.setOnClickListener(this);
		mButton2.setOnClickListener(this);
		mButton3.setOnClickListener(this);
	}

	@Override
	protected
	void onDestroy() {
		super.onDestroy();
		mButton1.setOnClickListener(null);
		mButton2.setOnClickListener(null);
		mButton3.setOnClickListener(null);
	}

	@Override
	public
	boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_activity, menu);
		return true;
	}

	@Override
	public
	boolean onOptionsItemSelected(MenuItem item) {
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

	private
	boolean removeBatteryLogFile() {
		return BatteryStateReceiver.removeBatteryLogFile(this);
	}

	@Override
	public
	void onClick(View v) {
		Intent startTestActivityIntent = new Intent(this, StreamingTestActivity.class);
		switch(v.getId()) {
			case R.id.button1:
				startTestActivityIntent.putExtra(StreamingTestActivity.EXTRA_TEST_TYPE, StreamingTestActivity.TEST_TYPE_MEDIA_PLAYER);
				break;

			case R.id.button2:
				startTestActivityIntent.putExtra(StreamingTestActivity.EXTRA_TEST_TYPE, StreamingTestActivity.TEST_TYPE_WEB_VIEW);
				break;

			case R.id.button3:
				startTestActivityIntent.putExtra(StreamingTestActivity.EXTRA_TEST_TYPE, StreamingTestActivity.TEST_TYPE_EXO_PLAYER);
				break;
		}
		startActivity(startTestActivityIntent);

	}
}
