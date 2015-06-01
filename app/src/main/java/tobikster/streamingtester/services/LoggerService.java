package tobikster.streamingtester.services;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public
class LoggerService extends IntentService {
	@SuppressWarnings("unused")
	public static final String LOGCAT_TAG = "LoggerService";

	public
	LoggerService() {
		super("LoggerService");
	}

	@Override
	public
	void onCreate() {
		super.onCreate();
		Log.d(LOGCAT_TAG, "Service created!");
	}

	@Override
	public
	void onDestroy() {
		Log.d(LOGCAT_TAG, "Service destroyed!");
		super.onDestroy();
	}

	@Override
	protected
	void onHandleIntent(Intent intent) {
		Log.d(LOGCAT_TAG, "Intent handled");
	}
}
