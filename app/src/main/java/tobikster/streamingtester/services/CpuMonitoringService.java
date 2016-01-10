package tobikster.streamingtester.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public final
class CpuMonitoringService extends Service {
	@SuppressWarnings("unused")
	private static final String TAG = CpuMonitoringService.class.getSimpleName();

	private final int MESSAGE_TYPE_START_CPU_MONITORING = 1;
	private final int MESSAGE_TYPE_STOP_CPU_MONITORING = 2;

	private final IBinder mBinder;

	private Looper mServiceLooper;
	private ServiceHandler mServiceHandler;

	public
	CpuMonitoringService() {
		mBinder = new Binder();
	}

	@Override
	public
	void onCreate() {
		HandlerThread thread = new HandlerThread("ServiceStartArguments", android.os.Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();

		// Get the HandlerThread's Looper and use it for our Handler
		mServiceLooper = thread.getLooper();
		mServiceHandler = new ServiceHandler(mServiceLooper);
	}

	@Nullable
	@Override
	public
	IBinder onBind(final Intent intent) {
		Log.d(TAG, "Service bind");
		return mBinder;
	}

	private
	void startCpuMonitoring() {
		Message message = mServiceHandler.obtainMessage();
		message.arg1 = MESSAGE_TYPE_START_CPU_MONITORING;
		mServiceHandler.sendMessage(message);
		Log.d(TAG, "CPU monitoring started!");
	}

	private
	void stopCpuMonitoring() {
		Message message = mServiceHandler.obtainMessage();
		message.arg1 = MESSAGE_TYPE_STOP_CPU_MONITORING;
		mServiceHandler.sendMessage(message);
		Log.d(TAG, "CPU monitoring stopped!");
	}

	public
	class Binder extends android.os.Binder {
		public
		void startCpuMonitoring() {
			CpuMonitoringService.this.startCpuMonitoring();
		}

		public
		void stopCpuMonitoring() {
			CpuMonitoringService.this.stopCpuMonitoring();
		}
	}

	// Handler that receives messages from the thread
	private final
	class ServiceHandler extends Handler {
		private Process mTopProcess;
		private InputStream mTopProcessOutput;

		public
		ServiceHandler(Looper looper) {
			super(looper);
		}

		@Override
		public
		void handleMessage(Message msg) {
			switch(msg.arg1) {
				case MESSAGE_TYPE_START_CPU_MONITORING:
					try {
						mTopProcess = Runtime.getRuntime().exec("top -d 0.1 | grep tobikster.streamingtester");
						mTopProcessOutput = mTopProcess.getInputStream();
					}
					catch(IOException ignored) {
					}
					break;

				case MESSAGE_TYPE_STOP_CPU_MONITORING:
					StringBuilder outputBuilder = new StringBuilder();
					BufferedReader reader = new BufferedReader(new InputStreamReader(mTopProcessOutput));
					String line;
					try {
						while((line = reader.readLine()) != null) {
							outputBuilder.append(line).append('\n');
						}
					}
					catch(IOException e) {
						e.printStackTrace();
					}
					Log.d(TAG, outputBuilder.toString());
					mTopProcessOutput = null;
					mTopProcess.destroy();
					mTopProcess = null;
					break;
			}
		}
	}
}
