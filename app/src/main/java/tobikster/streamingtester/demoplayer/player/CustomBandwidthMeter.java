package tobikster.streamingtester.demoplayer.player;

import android.os.Handler;

import com.google.android.exoplayer.upstream.BandwidthMeter;
import com.google.android.exoplayer.util.Assertions;
import com.google.android.exoplayer.util.Clock;
import com.google.android.exoplayer.util.SlidingPercentile;
import com.google.android.exoplayer.util.SystemClock;

/**
 * Created by tobikster on 2015-06-18.
 */
public
class CustomBandwidthMeter implements BandwidthMeter {

	public static final int DEFAULT_MAX_WEIGHT = 2000;

	protected final Handler eventHandler;
	protected final EventListener eventListener;
	protected final Clock clock;
	protected final SlidingPercentile slidingPercentile;

	protected long bytesAccumulator;
	protected long startTimeMs;
	protected long lastBytesReportMs;
	protected long bitrateEstimate;
	protected int streamCount;

	public
	CustomBandwidthMeter() {
		this(null, null);
	}

	public
	CustomBandwidthMeter(Handler eventHandler, EventListener eventListener) {
		this(eventHandler, eventListener, new SystemClock());
	}

	public
	CustomBandwidthMeter(Handler eventHandler, EventListener eventListener, Clock clock) {
		this(eventHandler, eventListener, clock, DEFAULT_MAX_WEIGHT);
	}

	public
	CustomBandwidthMeter(Handler eventHandler, EventListener eventListener, int maxWeight) {
		this(eventHandler, eventListener, new SystemClock(), maxWeight);
	}

	public
	CustomBandwidthMeter(Handler eventHandler, EventListener eventListener, Clock clock,
	                     int maxWeight) {
		this.eventHandler = eventHandler;
		this.eventListener = eventListener;
		this.clock = clock;
		this.slidingPercentile = new SlidingPercentile(maxWeight);
		bitrateEstimate = NO_ESTIMATE;
	}

	@Override
	public synchronized
	long getBitrateEstimate() {
		return bitrateEstimate;
	}

	@Override
	public synchronized
	void onTransferStart() {
		if(streamCount == 0) {
			startTimeMs = clock.elapsedRealtime();
			lastBytesReportMs = startTimeMs;
		}
		streamCount++;
	}

	@Override
	public synchronized
	void onBytesTransferred(int bytes) {
		bytesAccumulator += bytes;
		long nowMs = clock.elapsedRealtime();
		int elapsedMs = (int)(nowMs - lastBytesReportMs);
		notifyBandwidthSample(elapsedMs, bytes, NO_ESTIMATE);
		lastBytesReportMs = nowMs;
	}

	@Override
	public synchronized
	void onTransferEnd() {
		Assertions.checkState(streamCount > 0);
		long nowMs = clock.elapsedRealtime();
		int elapsedMs = (int)(nowMs - startTimeMs);
		if(elapsedMs > 0) {
			float bitsPerSecond = (bytesAccumulator * 8000) / elapsedMs;
			slidingPercentile.addSample((int)Math.sqrt(bytesAccumulator), bitsPerSecond);
			float bandwidthEstimateFloat = slidingPercentile.getPercentile(0.5f);
			bitrateEstimate = Float.isNaN(bandwidthEstimateFloat) ? NO_ESTIMATE
					                  : (long)bandwidthEstimateFloat;
			notifyBandwidthSample(elapsedMs, bytesAccumulator, bitrateEstimate);
		}
		streamCount--;
		if(streamCount > 0) {
			startTimeMs = nowMs;
		}
		bytesAccumulator = 0;
	}

	private
	void notifyBandwidthSample(final int elapsedMs, final long bytes, final long bitrate) {
		if(eventHandler != null && eventListener != null) {
			eventHandler.post(new Runnable() {
				@Override
				public
				void run() {
					eventListener.onBandwidthSample(elapsedMs, bytes, bitrate);
				}
			});
		}
	}
}
