package tobikster.streamingtester.fragments;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.CaptioningManager;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.PopupMenu;
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
 * A placeholder fragment containing a simple view.
 */
public class ExoPlayerFragment extends Fragment implements SurfaceHolder.Callback,
                                                           View.OnClickListener,
                                                           Player.Listener,
                                                           Player.CaptionListener,
                                                           Player.Id3MetadataListener,
                                                           AudioCapabilitiesReceiver.Listener {
	@SuppressWarnings("unused")
	private static final String LOGCAT_TAG = "PlayerActivity";

	public static final String EXTRA_CONTENT_URI = "content_uri";
	public static final String EXTRA_CONTENT_TYPE = "content_type";
	public static final String EXTRA_CONTENT_ID = "content_id";
	public static final int TYPE_DASH = 0;
	public static final int TYPE_SS = 1;
	public static final int TYPE_HLS = 2;
	public static final int TYPE_OTHER = 3;

	private static final int MENU_GROUP_TRACKS = 1;
	private static final int ID_OFFSET = 2;

	private static final CookieManager defaultCookieManager;

	static {
		defaultCookieManager = new CookieManager();
		defaultCookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
	}

	private EventLogger eventLogger;
	private MediaController mediaController;
	private View debugRootView;
	private View shutterView;
	private SurfaceView surfaceView;
	private AspectRatioFrameLayout videoFrame;
	private TextView debugTextView;
	private TextView playerStateTextView;
	private SubtitleLayout subtitleLayout;
	private Button videoButton;
	private Button audioButton;
	private Button textButton;
	private Button retryButton;

	private Player player;
	private DebugTextViewHelper debugViewHelper;
	private boolean playerNeedsPrepare;

	private long playerPosition;
	private boolean enableBackgroundAudio;

	private Uri contentUri;
	private int contentType;
	private String contentId;

	private AudioCapabilitiesReceiver audioCapabilitiesReceiver;
	private AudioCapabilities audioCapabilities;

	public ExoPlayerFragment() {
	}

	public static ExoPlayerFragment newInstance(String contentUri, String contentId, int contentType) {
		ExoPlayerFragment instance = new ExoPlayerFragment();
		Bundle args = new Bundle();
		args.putString(EXTRA_CONTENT_URI, contentUri);
		args.putString(EXTRA_CONTENT_ID, contentId);
		args.putInt(EXTRA_CONTENT_TYPE, contentType);
		instance.setArguments(args);
		return instance;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		return inflater.inflate(R.layout.fragment_exo_player, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		Bundle args = getArguments();
		if(args != null) {
			contentUri = Uri.parse(args.getString(EXTRA_CONTENT_URI));
			contentId = args.getString(EXTRA_CONTENT_ID);
			contentType = args.getInt(EXTRA_CONTENT_TYPE, -1);
		}

		videoFrame = (AspectRatioFrameLayout)view.findViewById(R.id.video_frame);
		videoFrame.setOnTouchListener(new View.OnTouchListener() {
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
		videoFrame.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				return keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE && mediaController.dispatchKeyEvent(event);
			}
		});
		audioCapabilitiesReceiver = new AudioCapabilitiesReceiver(getActivity().getApplicationContext(), this);

		shutterView = view.findViewById(R.id.shutter);
		debugRootView = view.findViewById(R.id.controls_root);

		surfaceView = (SurfaceView)view.findViewById(R.id.surface_view);
		surfaceView.getHolder().addCallback(this);
		debugTextView = (TextView)view.findViewById(R.id.debug_text_view);

		playerStateTextView = (TextView)view.findViewById(R.id.player_state_view);
		subtitleLayout = (SubtitleLayout)view.findViewById(R.id.subtitles);

		mediaController = new MediaController(getActivity());
		mediaController.setAnchorView(videoFrame);
		retryButton = (Button)view.findViewById(R.id.retry_button);
		retryButton.setOnClickListener(this);
		videoButton = (Button)view.findViewById(R.id.video_controls);
		audioButton = (Button)view.findViewById(R.id.audio_controls);
		textButton = (Button)view.findViewById(R.id.text_controls);
		Button verboseLogControlsButton = (Button)view.findViewById(R.id.verbose_log_controls);

		videoButton.setOnClickListener(this);
		audioButton.setOnClickListener(this);
		textButton.setOnClickListener(this);
		verboseLogControlsButton.setOnClickListener(this);

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

		// The player will be prepared on receiving audio capabilities.
		audioCapabilitiesReceiver.register();
	}

	@Override
	public void onPause() {
		super.onPause();
		if(!enableBackgroundAudio) {
			releasePlayer();
		}
		else {
			player.setBackgrounded(true);
		}
		audioCapabilitiesReceiver.unregister();
		shutterView.setVisibility(View.VISIBLE);
	}

	@Override
	public void onDestroy() {
		releasePlayer();
		super.onDestroy();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.fragment_exo_player, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean consumeEvent;
		switch(item.getItemId()) {
			case R.id.menu_item_loop_mode:
				item.setChecked(!item.isChecked());
				player.setLoopModeEnabled(item.isChecked());
				consumeEvent = true;
				break;

			default:
				consumeEvent = super.onOptionsItemSelected(item);
		}
		return consumeEvent;
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.retry_button:
				preparePlayer();
				break;

			case R.id.video_controls:
				showVideoPopup(view);
				break;

			case R.id.audio_controls:
				showAudioPopup(view);
				break;

			case R.id.text_controls:
				showTextPopup(view);
				break;

			case R.id.verbose_log_controls:
				showVerboseLogPopup(view);
				break;
		}
	}

	@Override
	public void onAudioCapabilitiesChanged(AudioCapabilities audioCapabilities) {
		boolean audioCapabilitiesChanged = !audioCapabilities.equals(this.audioCapabilities);
		if(player == null || audioCapabilitiesChanged) {
			this.audioCapabilities = audioCapabilities;
			releasePlayer();
			preparePlayer();
		}
		else {
			player.setBackgrounded(false);
		}
	}

	private Player.RendererBuilder getRendererBuilder() {
		String userAgent = Util.getUserAgent(getActivity(), "ExoPlayerDemo");
		switch(contentType) {
			case TYPE_SS:
				return new SmoothStreamingRendererBuilder(getActivity(), userAgent, contentUri.toString(), new SmoothStreamingTestMediaDrmCallback());
			case TYPE_DASH:
				return new DashRendererBuilder(getActivity(), userAgent, contentUri.toString(), new WidevineTestMediaDrmCallback(contentId));
			case TYPE_HLS:
				return new HlsRendererBuilder(getActivity(), userAgent, contentUri.toString());
			case TYPE_OTHER:
				return new ExtractorRendererBuilder(getActivity(), userAgent, contentUri);
			default:
				throw new IllegalStateException("Unsupported type: " + contentType);
		}
	}

	private void preparePlayer() {
		if(player == null) {
			player = new Player(getRendererBuilder());
			player.addListener(this);
			player.setCaptionListener(this);
			player.setMetadataListener(this);
			player.seekTo(playerPosition);
			playerNeedsPrepare = true;
			mediaController.setMediaPlayer(player.getPlayerControl());
			mediaController.setEnabled(true);
			eventLogger = new EventLogger();
			eventLogger.startSession();
			player.addListener(eventLogger);
			player.setInfoListener(eventLogger);
			player.setInternalErrorListener(eventLogger);
			debugViewHelper = new DebugTextViewHelper(player, debugTextView);
			debugViewHelper.start();
		}
		if(playerNeedsPrepare) {
			player.prepare();
			playerNeedsPrepare = false;
			updateButtonVisibilities();
		}
		player.setSurface(surfaceView.getHolder().getSurface());
		player.setPlayWhenReady(true);
		player.setLoopModeEnabled(true);
	}

	private void releasePlayer() {
		if(player != null) {
			debugViewHelper.stop();
			debugViewHelper = null;
			playerPosition = player.getCurrentPosition();
			player.release();
			player = null;
			eventLogger.endSession();
			eventLogger = null;
		}
	}

	// DemoPlayer.Listener implementation

	@Override
	public void onStateChanged(boolean playWhenReady, int playbackState) {
		if(playbackState == ExoPlayer.STATE_ENDED && !player.isLoopModeEnabled()) {
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
		playerStateTextView.setText(text);
		updateButtonVisibilities();
	}

	@Override
	public void onError(Exception e) {
		if(e instanceof UnsupportedDrmException) {
			// Special case DRM failures.
			UnsupportedDrmException unsupportedDrmException = (UnsupportedDrmException)e;
			int stringId = Util.SDK_INT < 18 ? R.string.drm_error_not_supported : unsupportedDrmException.reason == UnsupportedDrmException.REASON_UNSUPPORTED_SCHEME ? R.string.drm_error_unsupported_scheme : R.string.drm_error_unknown;
			Toast.makeText(getActivity().getApplicationContext(), stringId, Toast.LENGTH_LONG).show();
		}
		playerNeedsPrepare = true;
		updateButtonVisibilities();
		showControls();
	}

	@Override
	public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthAspectRatio) {
		shutterView.setVisibility(View.GONE);
		videoFrame.setAspectRatio(height == 0 ? 1 : (width * pixelWidthAspectRatio) / height);
	}

	// User controls

	private void updateButtonVisibilities() {
		retryButton.setVisibility(playerNeedsPrepare ? View.VISIBLE : View.GONE);
		videoButton.setVisibility(haveTracks(Player.TYPE_VIDEO) ? View.VISIBLE : View.GONE);
		audioButton.setVisibility(haveTracks(Player.TYPE_AUDIO) ? View.VISIBLE : View.GONE);
		textButton.setVisibility(haveTracks(Player.TYPE_TEXT) ? View.VISIBLE : View.GONE);
	}

	private boolean haveTracks(int type) {
		return player != null && player.getTrackCount(type) > 0;
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
		backgroundAudioItem.setChecked(enableBackgroundAudio);
		PopupMenu.OnMenuItemClickListener clickListener = new PopupMenu.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				if(item == backgroundAudioItem) {
					enableBackgroundAudio = !item.isChecked();
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
		popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
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

	private void configurePopupWithTracks(PopupMenu popup, final PopupMenu.OnMenuItemClickListener customActionClickListener, final int trackType) {
		if(player == null) {
			return;
		}
		int trackCount = player.getTrackCount(trackType);
		if(trackCount == 0) {
			return;
		}
		popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				return (customActionClickListener != null && customActionClickListener.onMenuItemClick(item)) || onTrackItemClick(item, trackType);
			}
		});
		Menu menu = popup.getMenu();
		// ID_OFFSET ensures we avoid clashing with Menu.NONE (which equals 0)
		menu.add(MENU_GROUP_TRACKS, Player.DISABLED_TRACK + ID_OFFSET, Menu.NONE, R.string.off);
		for(int i = 0; i < trackCount; i++) {
			menu.add(MENU_GROUP_TRACKS, i + ID_OFFSET, Menu.NONE, buildTrackName(player.getTrackFormat(trackType, i)));
		}
		menu.setGroupCheckable(MENU_GROUP_TRACKS, true, true);
		menu.findItem(player.getSelectedTrack(trackType) + ID_OFFSET).setChecked(true);
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
		if(player == null || item.getGroupId() != MENU_GROUP_TRACKS) {
			return false;
		}
		player.setSelectedTrack(type, item.getItemId() - ID_OFFSET);
		return true;
	}

	private void toggleControlsVisibility() {
		if(mediaController.isShowing()) {
			mediaController.hide();
			debugRootView.setVisibility(View.GONE);
		}
		else {
			showControls();
		}
	}

	private void showControls() {
		mediaController.show(0);
		debugRootView.setVisibility(View.VISIBLE);
	}

	// DemoPlayer.CaptionListener implementation
	@Override
	public void onCues(List<Cue> cues) {
		subtitleLayout.setCues(cues);
	}

	// DemoPlayer.MetadataListener implementation

	@Override
	public void onId3Metadata(Map<String, Object> metadata) {
		for(Map.Entry<String, Object> entry : metadata.entrySet()) {
			if(TxxxMetadata.TYPE.equals(entry.getKey())) {
				TxxxMetadata txxxMetadata = (TxxxMetadata)entry.getValue();
				Log.i(LOGCAT_TAG, String.format("ID3 TimedMetadata %s: description=%s, value=%s", TxxxMetadata.TYPE, txxxMetadata.description, txxxMetadata.value));
			}
			else if(PrivMetadata.TYPE.equals(entry.getKey())) {
				PrivMetadata privMetadata = (PrivMetadata)entry.getValue();
				Log.i(LOGCAT_TAG, String.format("ID3 TimedMetadata %s: owner=%s", PrivMetadata.TYPE, privMetadata.owner));
			}
			else if(GeobMetadata.TYPE.equals(entry.getKey())) {
				GeobMetadata geobMetadata = (GeobMetadata)entry.getValue();
				Log.i(LOGCAT_TAG, String.format("ID3 TimedMetadata %s: mimeType=%s, filename=%s, description=%s", GeobMetadata.TYPE, geobMetadata.mimeType, geobMetadata.filename, geobMetadata.description));
			}
			else {
				Log.i(LOGCAT_TAG, String.format("ID3 TimedMetadata %s", entry.getKey()));
			}
		}
	}

	// SurfaceHolder.Callback implementation

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if(player != null) {
			player.setSurface(holder.getSurface());
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// Do nothing.
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if(player != null) {
			player.blockingClearSurface();
		}
	}

	private void configureSubtitleView() {
		CaptionStyleCompat captionStyle;
		float fontScale;
		if(Util.SDK_INT >= 19) {
			captionStyle = getUserCaptionStyleV19();
			fontScale = getUserCaptionFontScaleV19();
		}
		else {
			captionStyle = CaptionStyleCompat.DEFAULT;
			fontScale = 1.0f;
		}
		subtitleLayout.setStyle(captionStyle);
		subtitleLayout.setFractionalTextSize(SubtitleLayout.DEFAULT_TEXT_SIZE_FRACTION * fontScale);
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
