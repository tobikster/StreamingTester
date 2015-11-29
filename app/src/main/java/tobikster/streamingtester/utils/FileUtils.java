package tobikster.streamingtester.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;

import tobikster.streamingtester.broadcastreceivers.BatteryStateReceiver;

public class FileUtils {
	public static final String LOGCAT_TAG = "FileUtils";

	public static boolean isExternalStorageWritable() {
		return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
	}

	public static File getExternalStorageFile(Context context, String directoryType, String fileName) {
		File result = null;
		if(!isExternalStorageWritable()) {
			Log.e(LOGCAT_TAG, "External storage is unavailable for writing!");
		}
		else {
			File outputDir = context.getExternalFilesDir(directoryType);
			if(outputDir == null || (!outputDir.mkdirs() && !outputDir.isDirectory())) {
				Log.e(LOGCAT_TAG, "Output folder doesn't exist and cannot be created!");
			}
			else {
				result = new File(outputDir, fileName);
			}
		}
		return result;
	}

	public static boolean removeExternalStorageFile(Context context, String directoryType, String fileName) {
		boolean result = false;
		File file = getExternalStorageFile(context, directoryType, fileName);
		if(file != null) {
			result = !file.exists() || file.delete();
		}
		return result;
	}

	public static boolean removeBatteryLogFile(Context context) {
		return removeExternalStorageFile(context, null, BatteryStateReceiver.LOG_FILE_NAME_BATTERY_LEVEL);
	}
}
