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

import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
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
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaFormat;
import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.audio.AudioCapabilitiesReceiver;
import com.google.android.exoplayer.drm.UnsupportedDrmException;
import com.google.android.exoplayer.metadata.GeobMetadata;
import com.google.android.exoplayer.metadata.PrivMetadata;
import com.google.android.exoplayer.metadata.TxxxMetadata;
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
import java.util.Map;

import tobikster.streamingtester.R;
import tobikster.streamingtester.player.EventLogger;
import tobikster.streamingtester.player.Player;
import tobikster.streamingtester.player.SmoothStreamingTestMediaDrmCallback;
import tobikster.streamingtester.player.WidevineTestMediaDrmCallback;
import tobikster.streamingtester.player.renderersbuilders.DashRendererBuilder;
import tobikster.streamingtester.player.renderersbuilders.ExtractorRendererBuilder;
import tobikster.streamingtester.player.renderersbuilders.HlsRendererBuilder;
import tobikster.streamingtester.player.renderersbuilders.SmoothStreamingRendererBuilder;

/**
 * An activity that plays media using {@link Player}.
 */
public class ExoPlayerFragment extends Fragment implements SurfaceHolder.Callback,
                                                            OnClickListener,
                                                            Player.Listener,
                                                            Player.CaptionListener,
                                                            Player.Id3MetadataListener,
                                                            AudioCapabilitiesReceiver.Listener {

	// For use within demo app code.
	public static final String ARG_CONTENT_URI = "content_uri";
	public static final String ARG_CONTENT_ID = "content_id";
	public static final String ARG_CONTENT_TYPE = "content_type";
	public static final int TYPE_DASH = 0;
	public static final int TYPE_SS = 1;
	public static final int TYPE_HLS = 2;
	public static final int TYPE_OTHER = 3;

	private static final String TAG = "PlayerActivity";
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
	private boolean mBackgroundAudioEnabled;

	private Uri mContentUri;
	private String mContentId;
	private int mContentType;

	private AudioCapabilitiesReceiver audioCapabilitiesReceiver;

	public ExoPlayerFragment() {
	}

	public static ExoPlayerFragment newInstance(String contentUri, String contentId, int contentType) {
		ExoPlayerFragment instance = new ExoPlayerFragment();
		Bundle args = new Bundle();
		args.putString(ARG_CONTENT_URI, contentUri);
		args.putString(ARG_CONTENT_ID, contentId);
		args.putInt(ARG_CONTENT_TYPE, contentType);
		instance.setArguments(args);
		return instance;
	}

	// Activity lifecycle


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		if(args != null) {
			mContentUri = Uri.parse(args.getString(ARG_CONTENT_URI));
			mContentId = args.getString(ARG_CONTENT_ID);
			mContentType = args.getInt(ARG_CONTENT_TYPE, TYPE_OTHER);
		}
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		return inflater.inflate(R.layout.fragment_exo_player, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		View root = view.findViewById(R.id.root);
		root.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
					toggleControlsVisibility();
				}
				else if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
					view.performClick();
				}
				return true;
			}
		});
		root.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				return !(keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_ESCAPE || keyCode == KeyEvent.KEYCODE_MENU) && mMediaController.dispatchKeyEvent(event);
			}
		});

		mShutterView = view.findViewById(R.id.shutter);
		mDebugRootView = view.findViewById(R.id.controls_root);

		mVideoFrame = (AspectRatioFrameLayout)view.findViewById(R.id.video_frame);
		mSurfaceView = (SurfaceView)view.findViewById(R.id.surface_view);
		mSurfaceView.getHolder().addCallback(this);
		mDebugTextView = (TextView)view.findViewById(R.id.debug_text_view);

		mPlayerStateTextView = (TextView)view.findViewById(R.id.player_state_view);
		mSubtitleLayout = (SubtitleLayout)view.findViewById(R.id.subtitles);

		mMediaController = new MediaController(getActivity());
		mMediaController.setAnchorView(root);
		mRetryButton = (Button)view.findViewById(R.id.retry_button);
		mVideoButton = (Button)view.findViewById(R.id.video_controls);
		mAudioButton = (Button)view.findViewById(R.id.audio_controls);
		mTextButton = (Button)view.findViewById(R.id.text_controls);
		mVerboseLogButton = (Button)view.findViewById(R.id.verbose_log_controls);

		mRetryButton.setOnClickListener(this);
		mVideoButton.setOnClickListener(this);
		mAudioButton.setOnClickListener(this);
		mTextButton.setOnClickListener(this);
		mVerboseLogButton.setOnClickListener(this);

		CookieHandler currentHandler = CookieHandler.getDefault();
		if(currentHandler != defaultCookieManager) {
			CookieHandler.setDefault(defaultCookieManager);
		}

		audioCapabilitiesReceiver = new AudioCapabilitiesReceiver(getActivity(), this);
		audioCapabilitiesReceiver.register();
	}

	@Override
	public void onResume() {
		super.onResume();
		configureSubtitleView();
		if(mPlayer == null) {
			preparePlayer(true);
		}
		else {
			mPlayer.setBackgrounded(false);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if(!mBackgroundAudioEnabled) {
			releasePlayer();
		}
		else {
			mPlayer.setBackgrounded(true);
		}
		mShutterView.setVisibility(View.VISIBLE);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		audioCapabilitiesReceiver.unregister();
		releasePlayer();
	}

	// OnClickListener methods

	@Override
	public void onClick(View view) {
		if(view == mRetryButton) {
			preparePlayer(true);
		}
		else if(view == mVideoButton) {
			showVideoPopup(view);
		}
		else if(view == mAudioButton) {
			showAudioPopup(view);
		}
		else if(view == mTextButton) {
			showTextPopup(view);
		}
		else if(view == mVerboseLogButton) {
			showVerboseLogPopup(view);
		}
	}

	// AudioCapabilitiesReceiver.Listener methods

	@Override
	public void onAudioCapabilitiesChanged(AudioCapabilities audioCapabilities) {
		if(mPlayer == null) {
			return;
		}
		boolean backgrounded = mPlayer.getBackgrounded();
		boolean playWhenReady = mPlayer.getPlayWhenReady();
		releasePlayer();
		preparePlayer(playWhenReady);
		mPlayer.setBackgrounded(backgrounded);
	}

	// Internal methods

	private Player.RendererBuilder getRendererBuilder() {
		String userAgent = Util.getUserAgent(getActivity(), "ExoPlayerDemo");
		switch(mContentType) {
			case TYPE_SS:
				return new SmoothStreamingRendererBuilder(getActivity(), userAgent, mContentUri.toString(), new SmoothStreamingTestMediaDrmCallback());
			case TYPE_DASH:
				return new DashRendererBuilder(getActivity(), userAgent, mContentUri.toString(), new WidevineTestMediaDrmCallback(mContentId));
			case TYPE_HLS:
				return new HlsRendererBuilder(getActivity(), userAgent, mContentUri.toString());
			case TYPE_OTHER:
				return new ExtractorRendererBuilder(getActivity(), userAgent, mContentUri);
			default:
				throw new IllegalStateException("Unsupported type: " + mContentType);
		}
	}

	private void preparePlayer(boolean playWhenReady) {
		if(mPlayer == null) {
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
		if(mPlayerNeedsPrepare) {
			mPlayer.prepare();
			mPlayerNeedsPrepare = false;
			updateButtonVisibilities();
		}
		mPlayer.setSurface(mSurfaceView.getHolder().getSurface());
		mPlayer.setPlayWhenReady(playWhenReady);
	}

	private void releasePlayer() {
		if(mPlayer != null) {
			mDebugTextViewHelper.stop();
			mDebugTextViewHelper = null;
			mPlayerPosition = mPlayer.getCurrentPosition();
			mPlayer.release();
			mPlayer = null;
			mEventLogger.endSession();
			mEventLogger = null;
		}
	}

	// DemoPlayer.Listener implementation

	@Override
	public void onStateChanged(boolean playWhenReady, int playbackState) {
		if(playbackState == ExoPlayer.STATE_ENDED) {
			showControls();
		}
		String text = "playWhenReady=" + playWhenReady + ", playbackState=";
		switch(playbackState) {
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
		if(e instanceof UnsupportedDrmException) {
			// Special case DRM failures.
			UnsupportedDrmException unsupportedDrmException = (UnsupportedDrmException)e;
			int stringId = Util.SDK_INT < 18 ? R.string.drm_error_not_supported : unsupportedDrmException.reason == UnsupportedDrmException.REASON_UNSUPPORTED_SCHEME ? R.string.drm_error_unsupported_scheme : R.string.drm_error_unknown;
			Toast.makeText(getActivity(), stringId, Toast.LENGTH_LONG).show();
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

	// User controls

	private void updateButtonVisibilities() {
		mRetryButton.setVisibility(mPlayerNeedsPrepare ? View.VISIBLE : View.GONE);
		mVideoButton.setVisibility(haveTracks(Player.TYPE_VIDEO) ? View.VISIBLE : View.GONE);
		mAudioButton.setVisibility(haveTracks(Player.TYPE_AUDIO) ? View.VISIBLE : View.GONE);
		mTextButton.setVisibility(haveTracks(Player.TYPE_TEXT) ? View.VISIBLE : View.GONE);
	}

	private boolean haveTracks(int type) {
		return mPlayer != null && mPlayer.getTrackCount(type) > 0;
	}

	public void showVideoPopup(View v) {
		PopupMenu popup = new PopupMenu(getActivity(), v);
		configurePopupWithTracks(popup, null, Player.TYPE_VIDEO);
		popup.show();
	}

	public void showAudioPopup(View v) {
		PopupMenu popup = new PopupMenu(getActivity(), v);
		Menu menu = popup.getMenu();
		menu.add(Menu.NONE, Menu.NONE, Menu.NONE, R.string.enable_background_audio);
		final MenuItem backgroundAudioItem = menu.findItem(0);
		backgroundAudioItem.setCheckable(true);
		backgroundAudioItem.setChecked(mBackgroundAudioEnabled);
		OnMenuItemClickListener clickListener = new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				if(item == backgroundAudioItem) {
					mBackgroundAudioEnabled = !item.isChecked();
					return true;
				}
				return false;
			}
		};
		configurePopupWithTracks(popup, clickListener, Player.TYPE_AUDIO);
		popup.show();
	}

	public void showTextPopup(View v) {
		PopupMenu popup = new PopupMenu(getActivity(), v);
		configurePopupWithTracks(popup, null, Player.TYPE_TEXT);
		popup.show();
	}

	public void showVerboseLogPopup(View v) {
		PopupMenu popup = new PopupMenu(getActivity(), v);
		Menu menu = popup.getMenu();
		menu.add(Menu.NONE, 0, Menu.NONE, R.string.logging_normal);
		menu.add(Menu.NONE, 1, Menu.NONE, R.string.logging_verbose);
		menu.setGroupCheckable(Menu.NONE, true, true);
		menu.findItem((VerboseLogUtil.areAllTagsEnabled()) ? 1 : 0).setChecked(true);
		popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				if(item.getItemId() == 0) {
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

	private void configurePopupWithTracks(PopupMenu popup, final OnMenuItemClickListener customActionClickListener, final int trackType) {
		if(mPlayer == null) {
			return;
		}
		int trackCount = mPlayer.getTrackCount(trackType);
		if(trackCount == 0) {
			return;
		}
		popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				return (customActionClickListener != null && customActionClickListener.onMenuItemClick(item)) || onTrackItemClick(item, trackType);
			}
		});
		Menu menu = popup.getMenu();
		// ID_OFFSET ensures we avoid clashing with Menu.NONE (which equals 0)
		menu.add(MENU_GROUP_TRACKS, ExoPlayer.TRACK_DISABLED + ID_OFFSET, Menu.NONE, R.string.off);
		for(int i = 0; i < trackCount; i++) {
			menu.add(MENU_GROUP_TRACKS, i + ID_OFFSET, Menu.NONE, buildTrackName(mPlayer.getTrackFormat(trackType, i)));
		}
		menu.setGroupCheckable(MENU_GROUP_TRACKS, true, true);
		menu.findItem(mPlayer.getSelectedTrack(trackType) + ID_OFFSET).setChecked(true);
	}

	private static String buildTrackName(MediaFormat format) {
		if(format.adaptive) {
			return "auto";
		}
		String trackName;
		if(MimeTypes.isVideo(format.mimeType)) {
			trackName = joinWithSeparator(joinWithSeparator(buildResolutionString(format), buildBitrateString(format)), buildTrackIdString(format));
		}
		else if(MimeTypes.isAudio(format.mimeType)) {
			trackName = joinWithSeparator(joinWithSeparator(joinWithSeparator(buildLanguageString(format), buildAudioPropertyString(format)), buildBitrateString(format)), buildTrackIdString(format));
		}
		else {
			trackName = joinWithSeparator(joinWithSeparator(buildLanguageString(format), buildBitrateString(format)), buildTrackIdString(format));
		}
		return trackName.length() == 0 ? "unknown" : trackName;
	}

	private static String buildResolutionString(MediaFormat format) {
		return format.width == MediaFormat.NO_VALUE || format.height == MediaFormat.NO_VALUE ? "" : format.width + "x" + format.height;
	}

	private static String buildAudioPropertyString(MediaFormat format) {
		return format.channelCount == MediaFormat.NO_VALUE || format.sampleRate == MediaFormat.NO_VALUE ? "" : format.channelCount + "ch, " + format.sampleRate + "Hz";
	}

	private static String buildLanguageString(MediaFormat format) {
		return TextUtils.isEmpty(format.language) || "und".equals(format.language) ? "" : format.language;
	}

	private static String buildBitrateString(MediaFormat format) {
		return format.bitrate == MediaFormat.NO_VALUE ? "" : String.format(Locale.US, "%.2fMbit", format.bitrate / 1000000f);
	}

	private static String joinWithSeparator(String first, String second) {
		return first.length() == 0 ? second : (second.length() == 0 ? first : first + ", " + second);
	}

	private static String buildTrackIdString(MediaFormat format) {
		return format.trackId == MediaFormat.NO_VALUE ? "" : String.format(Locale.US, " (%d)", format.trackId);
	}

	private boolean onTrackItemClick(MenuItem item, int type) {
		if(mPlayer == null || item.getGroupId() != MENU_GROUP_TRACKS) {
			return false;
		}
		mPlayer.setSelectedTrack(type, item.getItemId() - ID_OFFSET);
		return true;
	}

	private void toggleControlsVisibility() {
		if(mMediaController.isShowing()) {
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

	// DemoPlayer.CaptionListener implementation

	@Override
	public void onCues(List<Cue> cues) {
		mSubtitleLayout.setCues(cues);
	}

	// DemoPlayer.MetadataListener implementation

	@Override
	public void onId3Metadata(Map<String, Object> metadata) {
		for(Map.Entry<String, Object> entry : metadata.entrySet()) {
			if(TxxxMetadata.TYPE.equals(entry.getKey())) {
				TxxxMetadata txxxMetadata = (TxxxMetadata)entry.getValue();
				Log.i(TAG, String.format("ID3 TimedMetadata %s: description=%s, value=%s", TxxxMetadata.TYPE, txxxMetadata.description, txxxMetadata.value));
			}
			else if(PrivMetadata.TYPE.equals(entry.getKey())) {
				PrivMetadata privMetadata = (PrivMetadata)entry.getValue();
				Log.i(TAG, String.format("ID3 TimedMetadata %s: owner=%s", PrivMetadata.TYPE, privMetadata.owner));
			}
			else if(GeobMetadata.TYPE.equals(entry.getKey())) {
				GeobMetadata geobMetadata = (GeobMetadata)entry.getValue();
				Log.i(TAG, String.format("ID3 TimedMetadata %s: mimeType=%s, filename=%s, description=%s", GeobMetadata.TYPE, geobMetadata.mimeType, geobMetadata.filename, geobMetadata.description));
			}
			else {
				Log.i(TAG, String.format("ID3 TimedMetadata %s", entry.getKey()));
			}
		}
	}

	// SurfaceHolder.Callback implementation

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if(mPlayer != null) {
			mPlayer.setSurface(holder.getSurface());
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// Do nothing.
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if(mPlayer != null) {
			mPlayer.blockingClearSurface();
		}
	}

	private void configureSubtitleView() {
		CaptionStyleCompat style;
		float fontScale;
		if(Util.SDK_INT >= 19) {
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

	@TargetApi(19)
	private float getUserCaptionFontScaleV19() {
		CaptioningManager captioningManager = (CaptioningManager)getActivity().getSystemService(Context.CAPTIONING_SERVICE);
		return captioningManager.getFontScale();
	}

	@TargetApi(19)
	private CaptionStyleCompat getUserCaptionStyleV19() {
		CaptioningManager captioningManager = (CaptioningManager)getActivity().getSystemService(Context.CAPTIONING_SERVICE);
		return CaptionStyleCompat.createFromCaptionStyle(captioningManager.getUserStyle());
	}

}