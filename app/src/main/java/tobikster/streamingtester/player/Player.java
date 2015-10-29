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
package tobikster.streamingtester.player;

import android.media.MediaCodec.CryptoException;
import android.os.Handler;
import android.os.Looper;
import android.view.Surface;

import com.google.android.exoplayer.CodecCounters;
import com.google.android.exoplayer.DummyTrackRenderer;
import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecTrackRenderer;
import com.google.android.exoplayer.MediaCodecTrackRenderer.DecoderInitializationException;
import com.google.android.exoplayer.MediaCodecVideoTrackRenderer;
import com.google.android.exoplayer.MediaFormat;
import com.google.android.exoplayer.TimeRange;
import com.google.android.exoplayer.TrackRenderer;
import com.google.android.exoplayer.audio.AudioTrack;
import com.google.android.exoplayer.chunk.ChunkSampleSource;
import com.google.android.exoplayer.chunk.Format;
import com.google.android.exoplayer.dash.DashChunkSource;
import com.google.android.exoplayer.drm.StreamingDrmSessionManager;
import com.google.android.exoplayer.hls.HlsSampleSource;
import com.google.android.exoplayer.metadata.MetadataTrackRenderer.MetadataRenderer;
import com.google.android.exoplayer.text.Cue;
import com.google.android.exoplayer.text.TextRenderer;
import com.google.android.exoplayer.upstream.BandwidthMeter;
import com.google.android.exoplayer.util.DebugTextViewHelper;
import com.google.android.exoplayer.util.PlayerControl;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A wrapper around {@link ExoPlayer} that provides a higher level interface. It can be prepared
 * with one of a number of {@link RendererBuilder} classes to suit different use cases (e.g. DASH,
 * SmoothStreaming and so on).
 */
public class Player implements ExoPlayer.Listener,
                               ChunkSampleSource.EventListener,
                               HlsSampleSource.EventListener,
                               BandwidthMeter.EventListener,
                               MediaCodecVideoTrackRenderer.EventListener,
                               MediaCodecAudioTrackRenderer.EventListener,
                               StreamingDrmSessionManager.EventListener,
                               DashChunkSource.EventListener,
                               TextRenderer,
                               MetadataRenderer<Map<String, Object>>,
                               DebugTextViewHelper.Provider {


	public static final int DISABLED_TRACK = -1;
	public static final int PRIMARY_TRACK = 0;

	public static final int RENDERER_COUNT = 4;
	public static final int TYPE_VIDEO = 0;
	public static final int TYPE_AUDIO = 1;
	public static final int TYPE_TEXT = 2;
	public static final int TYPE_METADATA = 3;

	private static final int RENDERER_BUILDING_STATE_IDLE = 1;
	private static final int RENDERER_BUILDING_STATE_BUILDING = 2;
	private static final int RENDERER_BUILDING_STATE_BUILT = 3;

	private final RendererBuilder mRendererBuilder;
	private final ExoPlayer mPlayer;
	private final PlayerControl mPlayerControl;
	private final Handler mMainHandler;
	private final CopyOnWriteArrayList<Listener> mListeners;

	private boolean mLoopModeEnabled;
	private int mRendererBuildingState;
	private int mLastReportedPlaybackState;
	private boolean mLastReportedPlayWhenReady;

	private Surface mSurface;
	private TrackRenderer mVideoRenderer;
	private CodecCounters mCodecCounters;
	private Format mVideoFormat;
	private int mVideoTrackToRestore;

	private BandwidthMeter mBandwidthMeter;
	private boolean mBackgrounded;

	private CaptionListener mCaptionListener;
	private Id3MetadataListener mId3MetadataListener;
	private InternalErrorListener mInternalErrorListener;
	private InfoListener mInfoListener;

	public Player(RendererBuilder rendererBuilder) {
		this.mRendererBuilder = rendererBuilder;
		mPlayer = ExoPlayer.Factory.newInstance(RENDERER_COUNT, 1000, 5000);
		mPlayer.addListener(this);
		mPlayerControl = new PlayerControl(mPlayer);
		mMainHandler = new Handler();
		mListeners = new CopyOnWriteArrayList<>();
		mLastReportedPlaybackState = ExoPlayer.STATE_IDLE;
		mRendererBuildingState = RENDERER_BUILDING_STATE_IDLE;
		// Disable text initially.
		mPlayer.setSelectedTrack(TYPE_TEXT, ExoPlayer.TRACK_DISABLED);
		mLoopModeEnabled = false;
	}

	public void setLoopModeEnabled(boolean loopModeEnabled) {
		mLoopModeEnabled = loopModeEnabled;
	}

	public boolean isLoopModeEnabled() {
		return mLoopModeEnabled;
	}

	public PlayerControl getPlayerControl() {
		return mPlayerControl;
	}

	public void addListener(Listener listener) {
		mListeners.add(listener);
	}

	public void removeListener(Listener listener) {
		mListeners.remove(listener);
	}

	public void setInternalErrorListener(InternalErrorListener listener) {
		mInternalErrorListener = listener;
	}

	public void setInfoListener(InfoListener listener) {
		mInfoListener = listener;
	}

	public void setCaptionListener(CaptionListener listener) {
		mCaptionListener = listener;
	}

	public void setMetadataListener(Id3MetadataListener listener) {
		mId3MetadataListener = listener;
	}

	public void setSurface(Surface surface) {
		this.mSurface = surface;
		pushSurface(false);
	}

	public Surface getSurface() {
		return mSurface;
	}

	public void blockingClearSurface() {
		mSurface = null;
		pushSurface(true);
	}

	public int getTrackCount(int type) {
		return mPlayer.getTrackCount(type);
	}

	public MediaFormat getTrackFormat(int type, int index) {
		return mPlayer.getTrackFormat(type, index);
	}

	public int getSelectedTrack(int type) {
		return mPlayer.getSelectedTrack(type);
	}

	public void setSelectedTrack(int type, int index) {
		mPlayer.setSelectedTrack(type, index);
		if(type == TYPE_TEXT && index < 0 && mCaptionListener != null) {
			mCaptionListener.onCues(Collections.<Cue>emptyList());
		}
	}

	public boolean getBackgrounded() {
		return mBackgrounded;
	}

	public void setBackgrounded(boolean backgrounded) {
		if(this.mBackgrounded == backgrounded) {
			return;
		}
		this.mBackgrounded = backgrounded;
		if(backgrounded) {
			mVideoTrackToRestore = getSelectedTrack(TYPE_VIDEO);
			setSelectedTrack(TYPE_VIDEO, DISABLED_TRACK);
			blockingClearSurface();
		}
		else {
			setSelectedTrack(TYPE_VIDEO, mVideoTrackToRestore);
		}
	}

	public void prepare() {
		if(mRendererBuildingState == RENDERER_BUILDING_STATE_BUILT) {
			mPlayer.stop();
		}
		mRendererBuilder.cancel();
		mVideoFormat = null;
		mVideoRenderer = null;
		mRendererBuildingState = RENDERER_BUILDING_STATE_BUILDING;
		maybeReportPlayerState();
		mRendererBuilder.buildRenderers(this);
	}

	/**
	 * Invoked with the results from a {@link RendererBuilder}.
	 *
	 * @param renderers      Renderers indexed by {@link Player} TYPE_* constants. An individual
	 *                       element may be null if there do not exist tracks of the corresponding type.
	 * @param bandwidthMeter Provides an estimate of the currently available bandwidth. May be null.
	 */
	public void onRenderers(TrackRenderer[] renderers, BandwidthMeter bandwidthMeter) {
		for(int i = 0; i < RENDERER_COUNT; ++i) {
			if(renderers[i] == null) {
				// Convert a null renderer to a dummy renderer.
				renderers[i] = new DummyTrackRenderer();
			}
		}
		// Complete preparation.
		this.mVideoRenderer = renderers[TYPE_VIDEO];
		this.mCodecCounters = mVideoRenderer instanceof MediaCodecTrackRenderer ? ((MediaCodecTrackRenderer)mVideoRenderer).codecCounters : renderers[TYPE_AUDIO] instanceof MediaCodecTrackRenderer ? ((MediaCodecTrackRenderer)renderers[TYPE_AUDIO]).codecCounters : null;
		this.mBandwidthMeter = bandwidthMeter;
		pushSurface(false);
		mPlayer.prepare(renderers);
		mRendererBuildingState = RENDERER_BUILDING_STATE_BUILT;
	}

	/**
	 * Invoked if a {@link RendererBuilder} encounters an error.
	 *
	 * @param e Describes the error.
	 */
	public void onRenderersError(Exception e) {
		if(mInternalErrorListener != null) {
			mInternalErrorListener.onRendererInitializationError(e);
		}
		for(Listener listener : mListeners) {
			listener.onError(e);
		}
		mRendererBuildingState = RENDERER_BUILDING_STATE_IDLE;
		maybeReportPlayerState();
	}

	public void setPlayWhenReady(boolean playWhenReady) {
		mPlayer.setPlayWhenReady(playWhenReady);
	}

	public void seekTo(long positionMs) {
		mPlayer.seekTo(positionMs);
	}

	public void release() {
		mRendererBuilder.cancel();
		mRendererBuildingState = RENDERER_BUILDING_STATE_IDLE;
		mSurface = null;
		mPlayer.release();
	}


	public int getPlaybackState() {
		if(mRendererBuildingState == RENDERER_BUILDING_STATE_BUILDING) {
			return ExoPlayer.STATE_PREPARING;
		}
		int playerState = mPlayer.getPlaybackState();
		if(mRendererBuildingState == RENDERER_BUILDING_STATE_BUILT && playerState == ExoPlayer.STATE_IDLE) {
			// This is an edge case where the renderers are built, but are still being passed to the
			// mPlayer's playback thread.
			return ExoPlayer.STATE_PREPARING;
		}
		return playerState;
	}

	@Override
	public Format getFormat() {
		return mVideoFormat;
	}

	@Override
	public BandwidthMeter getBandwidthMeter() {
		return mBandwidthMeter;
	}

	@Override
	public CodecCounters getCodecCounters() {
		return mCodecCounters;
	}

	@Override
	public long getCurrentPosition() {
		return mPlayer.getCurrentPosition();
	}

	public long getDuration() {
		return mPlayer.getDuration();
	}

	public int getBufferedPercentage() {
		return mPlayer.getBufferedPercentage();
	}

	public boolean getPlayWhenReady() {
		return mPlayer.getPlayWhenReady();
	}

	public Looper getPlaybackLooper() {
		return mPlayer.getPlaybackLooper();
	}

	public Handler getMainHandler() {
		return mMainHandler;
	}

	@Override
	public void onPlayerStateChanged(boolean playWhenReady, int state) {
		maybeReportPlayerState();
		if(isLoopModeEnabled() && state == ExoPlayer.STATE_ENDED) {
			mPlayer.seekTo(0);
			mPlayer.setPlayWhenReady(true);
		}
	}

	@Override
	public void onPlayerError(ExoPlaybackException exception) {
		mRendererBuildingState = RENDERER_BUILDING_STATE_IDLE;
		for(Listener listener : mListeners) {
			listener.onError(exception);
		}
	}

	@Override
	public void onVideoSizeChanged(int width, int height, int unappliedTotationDegrees, float pixelWidthHeightRatio) {
		for(Listener listener : mListeners) {
			listener.onVideoSizeChanged(width, height, unappliedTotationDegrees, pixelWidthHeightRatio);
		}
	}

	@Override
	public void onDroppedFrames(int count, long elapsed) {
		if(mInfoListener != null) {
			mInfoListener.onDroppedFrames(count, elapsed);
		}
	}

	@Override
	public void onBandwidthSample(int elapsedMs, long bytes, long bitrateEstimate) {
		if(mInfoListener != null) {
			mInfoListener.onBandwidthSample(elapsedMs, bytes, bitrateEstimate);
		}
	}

	@Override
	public void onDownstreamFormatChanged(int sourceId, Format format, int trigger, long mediaTimeMs) {
		if(mInfoListener == null) {
			return;
		}
		if(sourceId == TYPE_VIDEO) {
			mVideoFormat = format;
			mInfoListener.onVideoFormatEnabled(format, trigger, mediaTimeMs);
		}
		else if(sourceId == TYPE_AUDIO) {
			mInfoListener.onAudioFormatEnabled(format, trigger, mediaTimeMs);
		}
	}

	@Override
	public void onDrmKeysLoaded() {
		// Do nothing.
	}

	@Override
	public void onDrmSessionManagerError(Exception e) {
		if(mInternalErrorListener != null) {
			mInternalErrorListener.onDrmSessionManagerError(e);
		}
	}

	@Override
	public void onDecoderInitializationError(DecoderInitializationException e) {
		if(mInternalErrorListener != null) {
			mInternalErrorListener.onDecoderInitializationError(e);
		}
	}

	@Override
	public void onAudioTrackInitializationError(AudioTrack.InitializationException e) {
		if(mInternalErrorListener != null) {
			mInternalErrorListener.onAudioTrackInitializationError(e);
		}
	}

	@Override
	public void onAudioTrackWriteError(AudioTrack.WriteException e) {
		if(mInternalErrorListener != null) {
			mInternalErrorListener.onAudioTrackWriteError(e);
		}
	}

	@Override
	public void onCryptoError(CryptoException e) {
		if(mInternalErrorListener != null) {
			mInternalErrorListener.onCryptoError(e);
		}
	}

	@Override
	public void onDecoderInitialized(String decoderName, long elapsedRealtimeMs, long initializationDurationMs) {
		if(mInfoListener != null) {
			mInfoListener.onDecoderInitialized(decoderName, elapsedRealtimeMs, initializationDurationMs);
		}
	}

	@Override
	public void onLoadError(int sourceId, IOException e) {
		if(mInternalErrorListener != null) {
			mInternalErrorListener.onLoadError(sourceId, e);
		}
	}

	@Override
	public void onCues(List<Cue> cues) {
		if(mCaptionListener != null && getSelectedTrack(TYPE_TEXT) != DISABLED_TRACK) {
			mCaptionListener.onCues(cues);
		}
	}

	@Override
	public void onMetadata(Map<String, Object> metadata) {
		if(mId3MetadataListener != null && getSelectedTrack(TYPE_METADATA) != DISABLED_TRACK) {
			mId3MetadataListener.onId3Metadata(metadata);
		}
	}

	@Override
	public void onAvailableRangeChanged(TimeRange availableRange) {
		if(mInfoListener != null) {
			mInfoListener.onAvailableRangeChanged(availableRange);
		}
	}

	@Override
	public void onPlayWhenReadyCommitted() {
		// Do nothing.
	}

	@Override
	public void onDrawnToSurface(Surface surface) {
		// Do nothing.
	}

	@Override
	public void onLoadStarted(int sourceId, long length, int type, int trigger, Format format, long mediaStartTimeMs, long mediaEndTimeMs) {
		if(mInfoListener != null) {
			mInfoListener.onLoadStarted(sourceId, length, type, trigger, format, mediaStartTimeMs, mediaEndTimeMs);
		}
	}

	@Override
	public void onLoadCompleted(int sourceId, long bytesLoaded, int type, int trigger, Format format, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs) {
		if(mInfoListener != null) {
			mInfoListener.onLoadCompleted(sourceId, bytesLoaded, type, trigger, format, mediaStartTimeMs, mediaEndTimeMs, elapsedRealtimeMs, loadDurationMs);
		}
	}

	@Override
	public void onLoadCanceled(int sourceId, long bytesLoaded) {
		// Do nothing.
	}

	@Override
	public void onUpstreamDiscarded(int sourceId, long mediaStartTimeMs, long mediaEndTimeMs) {
		// Do nothing.
	}

	private void maybeReportPlayerState() {
		boolean playWhenReady = mPlayer.getPlayWhenReady();
		int playbackState = getPlaybackState();
		if(mLastReportedPlayWhenReady != playWhenReady || mLastReportedPlaybackState != playbackState) {
			for(Listener listener : mListeners) {
				listener.onStateChanged(playWhenReady, playbackState);
			}
			mLastReportedPlayWhenReady = playWhenReady;
			mLastReportedPlaybackState = playbackState;
		}
	}

	private void pushSurface(boolean blockForSurfacePush) {
		if(mVideoRenderer == null) {
			return;
		}

		if(blockForSurfacePush) {
			mPlayer.blockingSendMessage(mVideoRenderer, MediaCodecVideoTrackRenderer.MSG_SET_SURFACE, mSurface);
		}
		else {
			mPlayer.sendMessage(mVideoRenderer, MediaCodecVideoTrackRenderer.MSG_SET_SURFACE, mSurface);
		}
	}

	/**
	 * Builds renderers for the player.
	 */
	public interface RendererBuilder {
		/**
		 * Builds renderers for playback.
		 *
		 * @param player The player for which renderers are being built. {@link Player#onRenderers}
		 *               should be invoked once the renderers have been built. If building fails,
		 *               {@link Player#onRenderersError} should be invoked.
		 */
		void buildRenderers(Player player);

		/**
		 * Cancels the current build operation, if there is one. Else does nothing.
		 * <p/>
		 * A canceled build operation must not invoke {@link Player#onRenderers} or
		 * {@link Player#onRenderersError} on the player, which may have been released.
		 */
		void cancel();
	}

	/**
	 * A listener for core events.
	 */
	public interface Listener {
		void onStateChanged(boolean playWhenReady, int playbackState);

		void onError(Exception e);

		void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio);
	}

	/**
	 * A listener for internal errors.
	 * <p/>
	 * These errors are not visible to the user, and hence this listener is provided for
	 * informational purposes only. Note however that an internal error may cause a fatal
	 * error if the player fails to recover. If this happens, {@link Listener#onError(Exception)}
	 * will be invoked.
	 */
	public interface InternalErrorListener {
		void onRendererInitializationError(Exception e);

		void onAudioTrackInitializationError(AudioTrack.InitializationException e);

		void onAudioTrackWriteError(AudioTrack.WriteException e);

		void onDecoderInitializationError(DecoderInitializationException e);

		void onCryptoError(CryptoException e);

		void onLoadError(int sourceId, IOException e);

		void onDrmSessionManagerError(Exception e);
	}

	/**
	 * A listener for debugging information.
	 */
	public interface InfoListener {
		void onVideoFormatEnabled(Format format, int trigger, long mediaTimeMs);

		void onAudioFormatEnabled(Format format, int trigger, long mediaTimeMs);

		void onDroppedFrames(int count, long elapsed);

		void onBandwidthSample(int elapsedMs, long bytes, long bitrateEstimate);

		void onLoadStarted(int sourceId, long length, int type, int trigger, Format format, long mediaStartTimeMs, long mediaEndTimeMs);

		void onLoadCompleted(int sourceId, long bytesLoaded, int type, int trigger, Format format, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs);

		void onDecoderInitialized(String decoderName, long elapsedRealtimeMs, long initializationDurationMs);

		void onAvailableRangeChanged(TimeRange availableRange);
	}

	/**
	 * A listener for receiving notifications of timed text.
	 */
	public interface CaptionListener {
		void onCues(List<Cue> cues);
	}

	/**
	 * A listener for receiving ID3 metadata parsed from the media stream.
	 */
	public interface Id3MetadataListener {
		void onId3Metadata(Map<String, Object> metadata);
	}

}
