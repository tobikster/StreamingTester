package tobikster.streamingtester.broadcastReceivers;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.List;

public class BatteryStateReceiver extends BroadcastReceiver {
	@SuppressWarnings("unused")
	public static final String LOGCAT_TAG = "BatteryStateReceiver";

	public static final String LOG_FILE_NAME_BATTERY_LEVEL = "battery_level.csv";

	@Override
	public void onReceive(Context context, Intent intent) {
		int batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int batteryScale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

		float currentBatteryLevel = batteryLevel / (float) (batteryScale);

		ActivityManager activityManager = (ActivityManager) (context.getSystemService(Context.ACTIVITY_SERVICE));
		@SuppressWarnings("deprecation")
		List<ActivityManager.RunningTaskInfo> taskInfo = activityManager.getRunningTasks(1);
		String currentActivity = taskInfo.get(0).topActivity.getShortClassName();

		if (!isExternalStorageWritable()) {
			Log.e(LOGCAT_TAG, "External storage is unavailable for writing!");
		}
		else {
			File outputFileDir = context.getExternalFilesDir(null);
			if (outputFileDir != null && !outputFileDir.isDirectory() && !outputFileDir.mkdirs()) {
				Log.e(LOGCAT_TAG, "Output folder doesn't exist and cannot be created!");
			}
			else {
				BufferedWriter writer = null;
				try {
					File outputFile = new File(outputFileDir, LOG_FILE_NAME_BATTERY_LEVEL);
					writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile, true), "utf-8"));
					Calendar currentDate = Calendar.getInstance();
					writer.write(String.format("%s\t%s\t%f\n", currentDate.getTime().toString(), currentActivity, currentBatteryLevel));
				}
				catch (IOException e) {
					e.printStackTrace();
				}
				finally {
					try {
						if (writer != null) {
							writer.close();
						}
					}
					catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public static boolean removeBatteryLogFile(Context context) {
		boolean result = false;
		File outputFile = getOutputFile(context, null, LOG_FILE_NAME_BATTERY_LEVEL);
		if (outputFile != null) {
			result = !outputFile.exists() || outputFile.delete();
		}
		return result;
	}

	private static boolean isExternalStorageWritable() {
		return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
	}

	private static File getOutputFile(Context context, String directoryType, String name) {
		File result = null;
		if (!isExternalStorageWritable()) {
			Log.e(LOGCAT_TAG, "External storage is unavailable for writing!");
		}
		else {
			File outputDir = context.getExternalFilesDir(directoryType);
			if (outputDir == null || (!outputDir.mkdirs() && !outputDir.isDirectory())) {
				Log.e(LOGCAT_TAG, "Output folder doesn't exist and cannot be created!");
			}
			else {
				result = new File(outputDir, name);
			}
		}
		return result;
	}
}
