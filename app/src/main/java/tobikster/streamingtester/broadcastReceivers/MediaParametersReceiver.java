package tobikster.streamingtester.broadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public
class MediaParametersReceiver extends BroadcastReceiver {
	@SuppressWarnings("unused")
	public static final String LOGCAT_TAG = "MediaParametersReceiver";
	public static final String ACTION_MEDIA_PARAMETER_CHANGED = "tobikster.streamingtester.ACTION_MEDIA_PARAMETER_CHANGED";

	public
	MediaParametersReceiver() {
	}

	@Override
	public
	void onReceive(Context context, Intent intent) {
		Log.d(LOGCAT_TAG, "Media parameter changed! (but I don't know which and why...)");
	}
}
