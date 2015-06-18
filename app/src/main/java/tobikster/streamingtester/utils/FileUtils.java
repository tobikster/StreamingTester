package tobikster.streamingtester.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;

public
class FileUtils {
	public static final String LOGCAT_TAG = "FileUtils";

	public static
	boolean isExternalStorageWritable() {
		return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
	}

	public static
	File getExternalStorageFile(Context context, String directoryType, String fileName) {
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
				result = new File(outputDir, fileName);
			}
		}
		return result;
	}

	public static
	boolean removeExternalStorageFile(Context context, String directoryType, String fileName) {
		boolean result = false;
		File file = getExternalStorageFile(context, directoryType, fileName);
		if(file != null) {
			result = !file.exists() || file.delete();
		}
		return result;
	}
}
