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
package tobikster.streamingtester.fragments;

import android.Manifest.permission;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.accessibility.CaptioningManager;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer.AspectRatioFrameLayout;
import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaCodecTrackRenderer.DecoderInitializationException;
import com.google.android.exoplayer.MediaCodecUtil.DecoderQueryException;
import com.google.android.exoplayer.MediaFormat;
import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.audio.AudioCapabilitiesReceiver;
import com.google.android.exoplayer.drm.UnsupportedDrmException;
import com.google.android.exoplayer.metadata.id3.GeobFrame;
import com.google.android.exoplayer.metadata.id3.Id3Frame;
import com.google.android.exoplayer.metadata.id3.PrivFrame;
import com.google.android.exoplayer.metadata.id3.TxxxFrame;
import com.google.android.exoplayer.text.CaptionStyleCompat;
import com.google.android.exoplayer.text.Cue;
import com.google.android.exoplayer.text.SubtitleLayout;
import com.google.android.exoplayer.util.DebugTextViewHelper;
import com.google.android.exoplayer.util.MimeTypes;
import com.google.android.exoplayer.util.Util;
import com.google.android.exoplayer.util.VerboseLogUtil;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.List;
import java.util.Locale;

import tobikster.streamingtester.R;
import tobikster.streamingtester.exoplayer.EventLogger;
import tobikster.streamingtester.exoplayer.SmoothStreamingTestMediaDrmCallback;
import tobikster.streamingtester.exoplayer.WidevineTestMediaDrmCallback;
import tobikster.streamingtester.exoplayer.player.DashRendererBuilder;
import tobikster.streamingtester.exoplayer.player.ExtractorRendererBuilder;
import tobikster.streamingtester.exoplayer.player.HlsRendererBuilder;
import tobikster.streamingtester.exoplayer.player.Player;
import tobikster.streamingtester.exoplayer.player.SmoothStreamingRendererBuilder;

/**
 * An activity that plays media using {@link Player}.
 */
public class ExoPlayerFragment extends Fragment implements SurfaceHolder.Callback,
                                                           OnClickListener,
                                                           Player.Listener,
                                                           Player.CaptionListener,
                                                           Player.Id3MetadataListener,
                                                           AudioCapabilitiesReceiver.Listener {

	private static final String LOG_TAG = ExoPlayerFragment.class.getSimpleName();
	public static final String ARG_CONTENT_URI = "arg_content_uri";
	public static final String ARG_CONTENT_ID = "content_id";
	public static final String ARG_CONTENT_TYPE = "content_type";
	public static final String ARG_PROVIDER = "provider";
	private static final int MENU_GROUP_TRACKS = 1;
	private static final int ID_OFFSET = 2;

	private static final CookieManager defaultCookieManager;

	static {
		defaultCookieManager = new CookieManager();
		defaultCookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
	}

	private EventLogger mEventLogger;
	private MediaController mMediaController;
	private View mDebugRootView;
	private View mShutterView;
	private AspectRatioFrameLayout mVideoFrame;
	private SurfaceView mSurfaceView;
	private TextView mDebugTextView;
	private TextView mPlayerStateTextView;
	private SubtitleLayout mSubtitleLayout;
	private Button mVideoButton;
	private Button mAudioButton;
	private Button mTextButton;
	private Button mRetryButton;
	private Button mVerboseLogButton;

	private Player mPlayer;
	private DebugTextViewHelper mDebugTextViewHelper;
	private boolean mPlayerNeedsPrepare;

	private long mPlayerPosition;
	private boolean mEnableBackgroundAudio;

	private Uri mContentUri;
	private int mContentType;
	private String mContentId;
	private String mProvider;

	private AudioCapabilitiesReceiver audioCapabilitiesReceiver;

	public ExoPlayerFragment() {
	}

	public static ExoPlayerFragment newInstance(final String contentUri, final String contentId, final int contentType, final String provider) {

		Bundle args = new Bundle();
		args.putString(ARG_CONTENT_URI, contentUri);
		args.putString(ARG_CONTENT_ID, contentId);
		args.putInt(ARG_CONTENT_TYPE, contentType);
		args.putString(ARG_PROVIDER, provider);
		ExoPlayerFragment fragment = new ExoPlayerFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
			preparePlayer(true);
		}
		else {
			Toast.makeText(getContext().getApplicationContext(), R.string.storage_permission_denied, Toast.LENGTH_LONG)
			     .show();
			getActivity().finish();
		}
	}

	@Override
	public void onCreate(@Nullable final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		mContentUri = Uri.parse(args.getString(ARG_CONTENT_URI));
		mContentId = args.getString(ARG_CONTENT_ID);
		mContentType = args.getInt(ARG_CONTENT_TYPE);
		mProvider = args.getString(ARG_PROVIDER);
	}

	@Nullable
	@Override
	public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_exo_player, container, false);

		mShutterView = root.findViewById(R.id.shutter);
		mDebugRootView = root.findViewById(R.id.controls_root);

		mVideoFrame = (AspectRatioFrameLayout) root.findViewById(R.id.video_frame);
		mSurfaceView = (SurfaceView) root.findViewById(R.id.surface_view);
		mDebugTextView = (TextView) root.findViewById(R.id.debug_text_view);

		mPlayerStateTextView = (TextView) root.findViewById(R.id.player_state_view);
		mSubtitleLayout = (SubtitleLayout) root.findViewById(R.id.subtitles);

		mMediaController = new KeyCompatibleMediaController(getContext());
		mMediaController.setAnchorView(root);
		mRetryButton = (Button) root.findViewById(R.id.retry_button);
		mVideoButton = (Button) root.findViewById(R.id.video_controls);
		mAudioButton = (Button) root.findViewById(R.id.audio_controls);
		mTextButton = (Button) root.findViewById(R.id.text_controls);
		mVerboseLogButton = (Button) root.findViewById(R.id.verbose_log_controls);

		return root;
	}

	@Override
	public void onViewCreated(final View root, @Nullable final Bundle savedInstanceState) {
		root.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
					toggleControlsVisibility();
				}
				else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
					view.performClick();
				}
				return true;
			}
		});
		root.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				return !(keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_ESCAPE || keyCode == KeyEvent.KEYCODE_MENU) && mMediaController
				  .dispatchKeyEvent(event);
			}
		});

		mSurfaceView.getHolder().addCallback(this);

		mRetryButton.setOnClickListener(this);
		mVideoButton.setOnClickListener(this);
		mAudioButton.setOnClickListener(this);
		mTextButton.setOnClickListener(this);
		mVerboseLogButton.setOnClickListener(this);

		audioCapabilitiesReceiver = new AudioCapabilitiesReceiver(getContext(), this);
		audioCapabilitiesReceiver.register();

		CookieHandler currentHandler = CookieHandler.getDefault();
		if (currentHandler != defaultCookieManager) {
			CookieHandler.setDefault(defaultCookieManager);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		if (Util.SDK_INT > 23) {
			onShown();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (Util.SDK_INT <= 23 || mPlayer == null) {
			onShown();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (Util.SDK_INT <= 23) {
			onHidden();
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		if (Util.SDK_INT > 23) {
			onHidden();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		audioCapabilitiesReceiver.unregister();
		releasePlayer();
	}

	private void onHidden() {
		if (!mEnableBackgroundAudio) {
			releasePlayer();
		}
		else {
			mPlayer.setBackgrounded(true);
		}
		mShutterView.setVisibility(View.VISIBLE);
	}

	private void releasePlayer() {
		if (mPlayer != null) {
			mDebugTextViewHelper.stop();
			mDebugTextViewHelper = null;
			mPlayerPosition = mPlayer.getCurrentPosition();
			mPlayer.release();
			mPlayer = null;
			mEventLogger.endSession();
			mEventLogger = null;
		}
	}

	private void onShown() {
		configureSubtitleView();
		if (mPlayer == null) {
			if (!maybeRequestPermission()) {
				preparePlayer(true);
			}
		}
		else {
			mPlayer.setBackgrounded(false);
		}
	}

	private void configureSubtitleView() {
		CaptionStyleCompat style;
		float fontScale;
		if (Util.SDK_INT >= 19) {
			style = getUserCaptionStyleV19();
			fontScale = getUserCaptionFontScaleV19();
		}
		else {
			style = CaptionStyleCompat.DEFAULT;
			fontScale = 1.0f;
		}
		mSubtitleLayout.setStyle(style);
		mSubtitleLayout.setFractionalTextSize(SubtitleLayout.DEFAULT_TEXT_SIZE_FRACTION * fontScale);
	}

	/**
	 * Checks whether it is necessary to ask for permission to read storage. If necessary, it also requests permission.
	 *
	 * @return true if a permission request is made. False if it is not necessary.
	 */
	@TargetApi(23)
	private boolean maybeRequestPermission() {
		if (requiresPermission(mContentUri)) {
			requestPermissions(new String[]{permission.READ_EXTERNAL_STORAGE}, 0);
			return true;
		}
		else {
			return false;
		}
	}

	// Internal methods

	private void preparePlayer(boolean playWhenReady) {
		if (mPlayer == null) {
			mPlayer = new Player(getRendererBuilder());
			mPlayer.addListener(this);
			mPlayer.setCaptionListener(this);
			mPlayer.setMetadataListener(this);
			mPlayer.seekTo(mPlayerPosition);
			mPlayerNeedsPrepare = true;
			mMediaController.setMediaPlayer(mPlayer.getPlayerControl());
			mMediaController.setEnabled(true);
			mEventLogger = new EventLogger();
			mEventLogger.startSession();
			mPlayer.addListener(mEventLogger);
			mPlayer.setInfoListener(mEventLogger);
			mPlayer.setInternalErrorListener(mEventLogger);
			mDebugTextViewHelper = new DebugTextViewHelper(mPlayer, mDebugTextView);
			mDebugTextViewHelper.start();
		}
		if (mPlayerNeedsPrepare) {
			mPlayer.prepare();
			mPlayerNeedsPrepare = false;
			updateButtonVisibilities();
		}
		mPlayer.setSurface(mSurfaceView.getHolder().getSurface());
		mPlayer.setPlayWhenReady(playWhenReady);
	}

	@TargetApi(19)
	private CaptionStyleCompat getUserCaptionStyleV19() {
		CaptioningManager captioningManager = (CaptioningManager) getContext().getSystemService(Context.CAPTIONING_SERVICE);
		return CaptionStyleCompat.createFromCaptionStyle(captioningManager.getUserStyle());
	}

	@TargetApi(19)
	private float getUserCaptionFontScaleV19() {
		CaptioningManager captioningManager =
		  (CaptioningManager) getContext().getSystemService(Context.CAPTIONING_SERVICE);
		return captioningManager.getFontScale();
	}

	@TargetApi(23)
	private boolean requiresPermission(Uri uri) {
		return Util.SDK_INT >= 23 && Util.isLocalFileUri(uri) && getContext().checkSelfPermission(permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;
	}

	private Player.RendererBuilder getRendererBuilder() {
		String userAgent = Util.getUserAgent(getContext(), "ExoPlayerDemo");
		switch (mContentType) {
			case Util.TYPE_SS:
				return new SmoothStreamingRendererBuilder(getContext(),
				                                          userAgent,
				                                          mContentUri.toString(),
				                                          new SmoothStreamingTestMediaDrmCallback());
			case Util.TYPE_DASH:
				return new DashRendererBuilder(getContext(),
				                               userAgent,
				                               mContentUri.toString(),
				                               new WidevineTestMediaDrmCallback(mContentId, mProvider));
			case Util.TYPE_HLS:
				return new HlsRendererBuilder(getContext(), userAgent, mContentUri.toString());
			case Util.TYPE_OTHER:
				return new ExtractorRendererBuilder(getContext(), userAgent, mContentUri);
			default:
				throw new IllegalStateException("Unsupported type: " + mContentType);
		}
	}

	private void updateButtonVisibilities() {
		mRetryButton.setVisibility(mPlayerNeedsPrepare ? View.VISIBLE : View.GONE);
		mVideoButton.setVisibility(haveTracks(Player.TYPE_VIDEO) ? View.VISIBLE : View.GONE);
		mAudioButton.setVisibility(haveTracks(Player.TYPE_AUDIO) ? View.VISIBLE : View.GONE);
		mTextButton.setVisibility(haveTracks(Player.TYPE_TEXT) ? View.VISIBLE : View.GONE);
	}

	// User controls

	private boolean haveTracks(int type) {
		return mPlayer != null && mPlayer.getTrackCount(type) > 0;
	}

	private void toggleControlsVisibility() {
		if (mMediaController.isShowing()) {
			mMediaController.hide();
			mDebugRootView.setVisibility(View.GONE);
		}
		else {
			showControls();
		}
	}

	private void showControls() {
		mMediaController.show(0);
		mDebugRootView.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View view) {
		if (mRetryButton == view) {
			preparePlayer(true);
		}
		else if(mVideoButton == view) {
			showVideoPopup(view);
		}
		else if(mAudioButton == view) {
			showAudioPopup(view);
		}
		else if(mTextButton == view) {
			showTextPopup(view);
		}
		else if (mVerboseLogButton == view) {
			showVerboseLogPopup(view);
		}
	}

	@Override
	public void onAudioCapabilitiesChanged(AudioCapabilities audioCapabilities) {
		if (mPlayer == null) {
			return;
		}
		boolean backgrounded = mPlayer.getBackgrounded();
		boolean playWhenReady = mPlayer.getPlayWhenReady();
		releasePlayer();
		preparePlayer(playWhenReady);
		mPlayer.setBackgrounded(backgrounded);
	}

	@Override
	public void onStateChanged(boolean playWhenReady, int playbackState) {
		if (playbackState == ExoPlayer.STATE_ENDED) {
			showControls();
		}
		String text = "playWhenReady=" + playWhenReady + ", playbackState=";
		switch (playbackState) {
			case ExoPlayer.STATE_BUFFERING:
				text += "buffering";
				break;
			case ExoPlayer.STATE_ENDED:
				text += "ended";
				break;
			case ExoPlayer.STATE_IDLE:
				text += "idle";
				break;
			case ExoPlayer.STATE_PREPARING:
				text += "preparing";
				break;
			case ExoPlayer.STATE_READY:
				text += "ready";
				break;
			default:
				text += "unknown";
				break;
		}
		mPlayerStateTextView.setText(text);
		updateButtonVisibilities();
	}

	@Override
	public void onError(Exception e) {
		String errorString = null;
		if (e instanceof UnsupportedDrmException) {
			// Special case DRM failures.
			UnsupportedDrmException unsupportedDrmException = (UnsupportedDrmException) e;
			errorString = getString(Util.SDK_INT < 18 ?
			                        R.string.error_drm_not_supported :
			                        unsupportedDrmException.reason == UnsupportedDrmException.REASON_UNSUPPORTED_SCHEME ?
			                        R.string.error_drm_unsupported_scheme :
			                        R.string.error_drm_unknown);
		}
		else if (e instanceof ExoPlaybackException && e.getCause() instanceof DecoderInitializationException) {
			// Special case for decoder initialization failures.
			DecoderInitializationException decoderInitializationException = (DecoderInitializationException) e.getCause();
			if (decoderInitializationException.decoderName == null) {
				if (decoderInitializationException.getCause() instanceof DecoderQueryException) {
					errorString = getString(R.string.error_querying_decoders);
				}
				else if (decoderInitializationException.secureDecoderRequired) {
					errorString = getString(R.string.error_no_secure_decoder, decoderInitializationException.mimeType);
				}
				else {
					errorString = getString(R.string.error_no_decoder, decoderInitializationException.mimeType);
				}
			}
			else {
				errorString = getString(R.string.error_instantiating_decoder,
				                        decoderInitializationException.decoderName);
			}
		}
		if (errorString != null) {
			Toast.makeText(getContext(), errorString, Toast.LENGTH_LONG).show();
		}
		mPlayerNeedsPrepare = true;
		updateButtonVisibilities();
		showControls();
	}

	@Override
	public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthAspectRatio) {
		mShutterView.setVisibility(View.GONE);
		mVideoFrame.setAspectRatio(height == 0 ? 1 : (width * pixelWidthAspectRatio) / height);
	}

	public void showVideoPopup(View v) {
		PopupMenu popup = new PopupMenu(getContext(), v);
		configurePopupWithTracks(popup, null, Player.TYPE_VIDEO);
		popup.show();
	}

	private void configurePopupWithTracks(PopupMenu popup, final OnMenuItemClickListener customActionClickListener, final int trackType) {
		if (mPlayer == null) {
			return;
		}
		int trackCount = mPlayer.getTrackCount(trackType);
		if (trackCount == 0) {
			return;
		}
		popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				return (customActionClickListener != null && customActionClickListener.onMenuItemClick(item)) || onTrackItemClick(
				  item,
				  trackType);
			}
		});
		Menu menu = popup.getMenu();
		// ID_OFFSET ensures we avoid clashing with Menu.NONE (which equals 0).
		menu.add(MENU_GROUP_TRACKS, Player.TRACK_DISABLED + ID_OFFSET, Menu.NONE, R.string.off);
		for (int i = 0; i < trackCount; i++) {
			menu.add(MENU_GROUP_TRACKS, i + ID_OFFSET, Menu.NONE, buildTrackName(mPlayer.getTrackFormat(trackType, i)));
		}
		menu.setGroupCheckable(MENU_GROUP_TRACKS, true, true);
		menu.findItem(mPlayer.getSelectedTrack(trackType) + ID_OFFSET).setChecked(true);
	}

	private boolean onTrackItemClick(MenuItem item, int type) {
		if (mPlayer == null || item.getGroupId() != MENU_GROUP_TRACKS) {
			return false;
		}
		mPlayer.setSelectedTrack(type, item.getItemId() - ID_OFFSET);
		return true;
	}

	private static String buildTrackName(MediaFormat format) {
		if (format.adaptive) {
			return "auto";
		}
		String trackName;
		if (MimeTypes.isVideo(format.mimeType)) {
			trackName = joinWithSeparator(joinWithSeparator(buildResolutionString(format), buildBitrateString(format)),
			                              buildTrackIdString(format));
		}
		else if (MimeTypes.isAudio(format.mimeType)) {
			trackName = joinWithSeparator(joinWithSeparator(joinWithSeparator(buildLanguageString(format),
			                                                                  buildAudioPropertyString(format)),
			                                                buildBitrateString(format)), buildTrackIdString(format));
		}
		else {
			trackName = joinWithSeparator(joinWithSeparator(buildLanguageString(format), buildBitrateString(format)),
			                              buildTrackIdString(format));
		}
		return trackName.length() == 0 ? "unknown" : trackName;
	}

	private static String joinWithSeparator(String first, String second) {
		return first.length() == 0 ? second : (second.length() == 0 ? first : first + ", " + second);
	}

	private static String buildResolutionString(MediaFormat format) {
		return format.width == MediaFormat.NO_VALUE || format.height == MediaFormat.NO_VALUE ?
		       "" :
		       format.width + "x" + format.height;
	}

	private static String buildBitrateString(MediaFormat format) {
		return format.bitrate == MediaFormat.NO_VALUE ?
		       "" :
		       String.format(Locale.US, "%.2fMbit", format.bitrate / 1000000f);
	}

	private static String buildTrackIdString(MediaFormat format) {
		return format.trackId == null ? "" : " (" + format.trackId + ")";
	}

	private static String buildLanguageString(MediaFormat format) {
		return TextUtils.isEmpty(format.language) || "und".equals(format.language) ? "" : format.language;
	}

	private static String buildAudioPropertyString(MediaFormat format) {
		return format.channelCount == MediaFormat.NO_VALUE || format.sampleRate == MediaFormat.NO_VALUE ?
		       "" :
		       format.channelCount + "ch, " + format.sampleRate + "Hz";
	}

	public void showAudioPopup(View v) {
		PopupMenu popup = new PopupMenu(getContext(), v);
		Menu menu = popup.getMenu();
		menu.add(Menu.NONE, Menu.NONE, Menu.NONE, R.string.enable_background_audio);
		final MenuItem backgroundAudioItem = menu.findItem(0);
		backgroundAudioItem.setCheckable(true);
		backgroundAudioItem.setChecked(mEnableBackgroundAudio);
		OnMenuItemClickListener clickListener = new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				if (item == backgroundAudioItem) {
					mEnableBackgroundAudio = !item.isChecked();
					return true;
				}
				return false;
			}
		};
		configurePopupWithTracks(popup, clickListener, Player.TYPE_AUDIO);
		popup.show();
	}

	// SurfaceHolder.Callback implementation

	public void showTextPopup(View v) {
		PopupMenu popup = new PopupMenu(getContext(), v);
		configurePopupWithTracks(popup, null, Player.TYPE_TEXT);
		popup.show();
	}

	public void showVerboseLogPopup(View v) {
		PopupMenu popup = new PopupMenu(getContext(), v);
		Menu menu = popup.getMenu();
		menu.add(Menu.NONE, 0, Menu.NONE, R.string.logging_normal);
		menu.add(Menu.NONE, 1, Menu.NONE, R.string.logging_verbose);
		menu.setGroupCheckable(Menu.NONE, true, true);
		menu.findItem((VerboseLogUtil.areAllTagsEnabled()) ? 1 : 0).setChecked(true);
		popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				if (item.getItemId() == 0) {
					VerboseLogUtil.setEnableAllTags(false);
				}
				else {
					VerboseLogUtil.setEnableAllTags(true);
				}
				return true;
			}
		});
		popup.show();
	}

	@Override
	public void onCues(List<Cue> cues) {
		mSubtitleLayout.setCues(cues);
	}

	@Override
	public void onId3Metadata(List<Id3Frame> id3Frames) {
		for (Id3Frame id3Frame : id3Frames) {
			if (id3Frame instanceof TxxxFrame) {
				TxxxFrame txxxFrame = (TxxxFrame) id3Frame;
				Log.i(LOG_TAG,
				      String.format("ID3 TimedMetadata %s: description=%s, value=%s",
				                    txxxFrame.id,
				                    txxxFrame.description,
				                    txxxFrame.value));
			}
			else if (id3Frame instanceof PrivFrame) {
				PrivFrame privFrame = (PrivFrame) id3Frame;
				Log.i(LOG_TAG, String.format("ID3 TimedMetadata %s: owner=%s", privFrame.id, privFrame.owner));
			}
			else if (id3Frame instanceof GeobFrame) {
				GeobFrame geobFrame = (GeobFrame) id3Frame;
				Log.i(LOG_TAG,
				      String.format("ID3 TimedMetadata %s: mimeType=%s, filename=%s, description=%s",
				                    geobFrame.id,
				                    geobFrame.mimeType,
				                    geobFrame.filename,
				                    geobFrame.description));
			}
			else {
				Log.i(LOG_TAG, String.format("ID3 TimedMetadata %s", id3Frame.id));
			}
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (mPlayer != null) {
			mPlayer.setSurface(holder.getSurface());
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// Do nothing.
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (mPlayer != null) {
			mPlayer.blockingClearSurface();
		}
	}

	private static final class KeyCompatibleMediaController extends MediaController {

		private MediaPlayerControl playerControl;

		public KeyCompatibleMediaController(Context context) {
			super(context);
		}

		@Override
		public void setMediaPlayer(MediaPlayerControl playerControl) {
			super.setMediaPlayer(playerControl);
			this.playerControl = playerControl;
		}

		@Override
		public boolean dispatchKeyEvent(KeyEvent event) {
			int keyCode = event.getKeyCode();
			if (playerControl.canSeekForward() && (keyCode == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD
			  || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					playerControl.seekTo(playerControl.getCurrentPosition() + 15000); // milliseconds
					show();
				}
				return true;
			}
			else if (playerControl.canSeekBackward() && (keyCode == KeyEvent.KEYCODE_MEDIA_REWIND
			  || keyCode == KeyEvent.KEYCODE_DPAD_LEFT)) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					playerControl.seekTo(playerControl.getCurrentPosition() - 5000); // milliseconds
					show();
				}
				return true;
			}
			return super.dispatchKeyEvent(event);
		}
	}
}
