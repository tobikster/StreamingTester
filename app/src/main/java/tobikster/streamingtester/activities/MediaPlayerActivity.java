package tobikster.streamingtester.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import tobikster.streamingtester.R;
import tobikster.streamingtester.broadcastReceivers.BatteryStateReceiver;
import tobikster.streamingtester.dialogs.MediaInfoDialog;

public class MediaPlayerActivity extends Activity implements MediaInfoDialog.MediaInfoDialogListener {

	BatteryStateReceiver mBatteryStateReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_media_player);

		IntentFilter batteryIntentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		mBatteryStateReceiver = new BatteryStateReceiver();
		registerReceiver(mBatteryStateReceiver, batteryIntentFilter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mBatteryStateReceiver);
	}

	@Override
	public void onOKButtonClicked() {
	}
}
