package tobikster.streamingtester.utils;

import android.widget.TextView;

import com.google.android.exoplayer.CodecCounters;
import com.google.android.exoplayer.chunk.Format;
import com.google.android.exoplayer.upstream.BandwidthMeter;

/**
 * Created by tobikster on 2015-09-02.
 */
public
class DebugTextViewHelper implements Runnable {
	static final int REFRESH_INTERVAL_MS = 1000;

	final TextView mTextView;
	final Provider mDebuggable;

	public
	DebugTextViewHelper(Provider debuggable, TextView textView) {
		mTextView = textView;
		mDebuggable = debuggable;
	}

	public
	void start() {
		stop();
		run();
	}

	public
	void stop() {
		mTextView.removeCallbacks(this);
	}

	@Override
	public
	void run() {
		mTextView.setText(getRenderString());
		mTextView.postDelayed(this, REFRESH_INTERVAL_MS);
	}

	String getRenderString() {
		return getTimeString() + " " + getQualityString() + " " + getBandwidthString() + " " + getVideoCodecCountersString();
	}

	private
	String getTimeString() {
		return "ms(" + mDebuggable.getCurrentPosition() + ")";
	}

	private
	String getQualityString() {
		Format format = mDebuggable.getFormat();
		String qualityString = format == null ? "id:? br:? h:?" : String.format("id: %s br: %d, h: %d", format.id, format.bitrate, format.height);
		qualityString += String.format(" PIF: %.2f", mDebuggable.getPlaybackInterruptionFactor());
		return qualityString;
	}

	private
	String getBandwidthString() {
		BandwidthMeter bandwidthMeter = mDebuggable.getBandwidthMeter();
		if(bandwidthMeter == null || bandwidthMeter.getBitrateEstimate() == BandwidthMeter.NO_ESTIMATE) {
			return "bw:?";
		}
		else {
			return "bw:" + (bandwidthMeter.getBitrateEstimate() / 1000);
		}
	}

	private
	String getVideoCodecCountersString() {
		CodecCounters codecCounters = mDebuggable.getCodecCounters();
		return codecCounters == null ? "" : codecCounters.getDebugString();
	}

	/**
	 * Provides debug information about an ongoing playback.
	 */
	public
	interface Provider {
		/**
		 * Returns the current playback position, in milliseconds.
		 */
		long getCurrentPosition();
		/**
		 * Returns a format whose information should be displayed, or null.
		 */
		Format getFormat();
		/**
		 * Returns a {@link BandwidthMeter} whose estimate should be displayed, or null.
		 */
		BandwidthMeter getBandwidthMeter();
		/**
		 * Returns a {@link CodecCounters} whose information should be displayed, or null.
		 */
		CodecCounters getCodecCounters();
		float getPlaybackInterruptionFactor();
	}
}
