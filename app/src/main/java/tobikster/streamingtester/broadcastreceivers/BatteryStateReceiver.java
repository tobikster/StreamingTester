package tobikster.streamingtester.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Calendar;

import tobikster.streamingtester.utils.FileUtils;

public class BatteryStateReceiver extends BroadcastReceiver {
	@SuppressWarnings("unused")
	public static final String TAG = "BatteryStateReceiver";

	public static final String LOG_FILE_NAME_BATTERY_LEVEL = "battery_level.csv";

	@Override
	public void onReceive(Context context, Intent intent) {
		switch(intent.getAction()) {
			case Intent.ACTION_BATTERY_CHANGED:
				int batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
				int batteryScale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

				float currentBatteryLevel = batteryLevel / (float)(batteryScale);

				BufferedWriter writer = null;
				try {
					File outputFile = FileUtils.getExternalStorageFile(context, null, LOG_FILE_NAME_BATTERY_LEVEL);
					writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile, true), "utf-8"));
					Calendar currentDate = Calendar.getInstance();
					writer.write(String.format("%s\t%f\n", currentDate.getTime().toString(), currentBatteryLevel));
				}
				catch(IOException e) {
					e.printStackTrace();
				}
				finally {
					try {
						if(writer != null) {
							writer.close();
						}
					}
					catch(IOException e) {
						e.printStackTrace();
					}
				}
				break;
		}
	}
}