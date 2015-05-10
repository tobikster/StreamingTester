package tobikster.streamingtester.broadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

public class BatteryStateReceiver extends BroadcastReceiver {
	public static final String LOGCAT_TAG = "BatteryStateReceiver";
	public static final String LOG_FILE_NAME_BATTERY_LEVEL = "battery_level.csv";

	@Override
	public void onReceive(Context context, Intent intent) {
		int batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int batteryScale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

		float currentBatteryLevel = batteryLevel / (float)(batteryScale);

		try(FileOutputStream outputStream = context.openFileOutput(LOG_FILE_NAME_BATTERY_LEVEL, Context.MODE_APPEND)) {
			Calendar now = Calendar.getInstance();
			outputStream.write(String.format("%s\t%f\n", now.getTime().toString(), currentBatteryLevel).getBytes());
		}
		catch(IOException e) {
			e.printStackTrace();
		}

//		try(FileWriter writer = new FileWriter(new File(context.getExternalFilesDir(null), LOG_FILE_NAME_BATTERY_LEVEL), true)) {
//			Calendar now = Calendar.getInstance();
//			String message = String.format("%s\t%f\n", now.getTime().toString(), currentBatteryLevel);
//			writer.write(message);
//		}
//		catch (IOException e) {
//			e.printStackTrace();
//		}
	}
}
