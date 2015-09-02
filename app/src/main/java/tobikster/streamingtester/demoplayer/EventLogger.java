/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tobikster.streamingtester.demoplayer;

import android.media.MediaCodec.CryptoException;
import android.os.SystemClock;
import android.util.Log;

import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaCodecTrackRenderer.DecoderInitializationException;
import com.google.android.exoplayer.TimeRange;
import com.google.android.exoplayer.audio.AudioTrack;
import com.google.android.exoplayer.chunk.Format;
import com.google.android.exoplayer.util.VerboseLogUtil;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

import tobikster.streamingtester.demoplayer.player.DemoPlayer;

/**
 * Logs player events using {@link Log}.
 */
public
class EventLogger implements DemoPlayer.Listener, DemoPlayer.InfoListener, DemoPlayer.InternalErrorListener {

	private static final String LOGCAT_TAG = "EventLogger";
	private static final NumberFormat TIME_FORMAT;

	static {
		TIME_FORMAT = NumberFormat.getInstance(Locale.US);
		TIME_FORMAT.setMinimumFractionDigits(2);
		TIME_FORMAT.setMaximumFractionDigits(2);
	}

	private long sessionStartTimeMs;
	private long[] loadStartTimeMs;
	private long[] seekRangeValuesUs;
	File mOutputFile;

	public
	EventLogger() {
		loadStartTimeMs = new long[DemoPlayer.RENDERER_COUNT];
		mOutputFile = null;
	}

	public
	void startSession() {
		sessionStartTimeMs = SystemClock.elapsedRealtime();
		Log.d(LOGCAT_TAG, "start [0]");
	}

	public
	void endSession() {
		Log.d(LOGCAT_TAG, "end [" + getSessionTimeString() + "]");
	}

	// DemoPlayer.Listener

	@Override
	public
	void onStateChanged(boolean playWhenReady, int state) {
		Log.d(LOGCAT_TAG, "state [" + getSessionTimeString() + ", " + playWhenReady + ", " + getStateString(state) + "]");
	}

	@Override
	public
	void onError(Exception e) {
		Log.e(LOGCAT_TAG, "playerFailed [" + getSessionTimeString() + "]", e);
	}

	@Override
	public
	void onVideoSizeChanged(int width, int height, float pixelWidthHeightRatio) {
		Log.d(LOGCAT_TAG, "videoSizeChanged [" + width + ", " + height + ", " + pixelWidthHeightRatio + "]");
	}

	// DemoPlayer.InfoListener

	@Override
	public
	void onBandwidthSample(int elapsedMs, long bytes, long bitrateEstimate) {
		Log.d(LOGCAT_TAG, "bandwidth [" + getSessionTimeString() + ", " + bytes + ", " + getTimeString(elapsedMs) + ", " + bitrateEstimate + "]");
	}

	@Override
	public
	void onDroppedFrames(int count, long elapsed) {
		Log.d(LOGCAT_TAG, "droppedFrames [" + getSessionTimeString() + ", " + count + "]");
	}

	@Override
	public
	void onLoadStarted(int sourceId, long length, int type, int trigger, Format format, int mediaStartTimeMs, int mediaEndTimeMs) {
		loadStartTimeMs[sourceId] = SystemClock.elapsedRealtime();
		if(VerboseLogUtil.isTagEnabled(LOGCAT_TAG)) {
			Log.v(LOGCAT_TAG, "loadStart [" + getSessionTimeString() + ", " + sourceId + ", " + type + ", " + mediaStartTimeMs + ", " + mediaEndTimeMs + "]");
		}
	}

	@Override
	public
	void onLoadCompleted(int sourceId, long bytesLoaded, int type, int trigger, Format format, int mediaStartTimeMs, int mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs) {
		if(VerboseLogUtil.isTagEnabled(LOGCAT_TAG)) {
			long downloadTime = SystemClock.elapsedRealtime() - loadStartTimeMs[sourceId];
			Log.v(LOGCAT_TAG, "loadEnd [" + getSessionTimeString() + ", " + sourceId + ", " + downloadTime + "]");
		}
	}

	@Override
	public
	void onVideoFormatEnabled(Format format, int trigger, int mediaTimeMs) {
		Log.d(LOGCAT_TAG, String.format("videoFormat [time: %s, id: %s, trigger: %d, codecs: %s, bitrate: %d", getSessionTimeString(), format.id, trigger, format.codecs, format.bitrate));
	}

	@Override
	public
	void onAudioFormatEnabled(Format format, int trigger, int mediaTimeMs) {
		Log.d(LOGCAT_TAG, "audioFormat [" + getSessionTimeString() + ", " + format.id + ", " + Integer.toString(trigger) + "]");
	}

	// DemoPlayer.InternalErrorListener

	@Override
	public
	void onLoadError(int sourceId, IOException e) {
		printInternalError("loadError", e);
	}

	@Override
	public
	void onRendererInitializationError(Exception e) {
		printInternalError("rendererInitError", e);
	}

	@Override
	public
	void onDrmSessionManagerError(Exception e) {
		printInternalError("drmSessionManagerError", e);
	}

	@Override
	public
	void onDecoderInitializationError(DecoderInitializationException e) {
		printInternalError("decoderInitializationError", e);
	}

	@Override
	public
	void onAudioTrackInitializationError(AudioTrack.InitializationException e) {
		printInternalError("audioTrackInitializationError", e);
	}

	@Override
	public
	void onAudioTrackWriteError(AudioTrack.WriteException e) {
		printInternalError("audioTrackWriteError", e);
	}

	@Override
	public
	void onCryptoError(CryptoException e) {
		printInternalError("cryptoError", e);
	}

	@Override
	public
	void onDecoderInitialized(String decoderName, long elapsedRealtimeMs, long initializationDurationMs) {
		Log.d(LOGCAT_TAG, "decoderInitialized [" + getSessionTimeString() + "]");
	}

	@Override
	public
	void onSeekRangeChanged(TimeRange seekRange) {
		seekRangeValuesUs = seekRange.getCurrentBoundsUs(seekRangeValuesUs);
		Log.d(LOGCAT_TAG, "seekRange [ " + seekRange.type + ", " + seekRangeValuesUs[0] + ", "
				                  + seekRangeValuesUs[1] + "]");
	}

	private
	void printInternalError(String type, Exception e) {
		Log.e(LOGCAT_TAG, "internalError [" + getSessionTimeString() + ", " + type + "]", e);
	}

	public
	void setOutputFile(File file) {
		mOutputFile = file;
		Log.d(LOGCAT_TAG, String.format("Output file set to %s", file.getAbsolutePath()));
	}

	private
	String getStateString(int state) {
		switch(state) {
			case ExoPlayer.STATE_BUFFERING:
				return "Buffering";
			case ExoPlayer.STATE_ENDED:
				return "Ended";
			case ExoPlayer.STATE_IDLE:
				return "Idle";
			case ExoPlayer.STATE_PREPARING:
				return "Preparing";
			case ExoPlayer.STATE_READY:
				return "Ready";
			default:
				return "?";
		}
	}

	private
	String getSessionTimeString() {
		return getTimeString(SystemClock.elapsedRealtime() - sessionStartTimeMs);
	}

	private
	String getTimeString(long timeMs) {
		return TIME_FORMAT.format((timeMs) / 1000f);
	}
}
