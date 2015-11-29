package tobikster.streamingtester.ui.fragments;

import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;

import java.io.IOException;

import tobikster.streamingtester.R;
import tobikster.streamingtester.model.VideoUri;

public class MediaPlayerFragment extends Fragment implements MediaPlayer.OnPreparedListener,
                                                             SurfaceHolder.Callback,
                                                             MediaController.MediaPlayerControl,
                                                             MediaPlayer.OnBufferingUpdateListener,
                                                             MediaPlayer.OnInfoListener,
                                                             MediaPlayer.OnVideoSizeChangedListener,
                                                             MediaPlayer.OnErrorListener {

	public static final String LOGCAT_TAG = "MediaPlayerFragment";
	public static final String ARG_VIDEO_NAME = "video_name";
	public static final String ARG_VIDEO_URI = "video_url";
	public static final String ARG_VIDEO_REMOTE = "video_is_remote";

	public static final String TEST_VIDEO_URL = "https://archive.org/download/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4";
	private static final String TEST_RTSP_URL = "rtsp://184.72.239.149/vod/mp4:BigBuckBunny_115k.mov";

	private View mRootView;
	private SurfaceView mSurfaceView;
	private MediaPlayer mMediaPlayer;
	private MediaController mMediaController;
	private MediaPlayer.TrackInfo[] mTrackInfos;
	private MediaMetadataRetriever mMediaMetadataRetriever;

	VideoUri mVideoUri;

	int mBufferPercentage;
	boolean mMediaPlayerPrepared;

	public MediaPlayerFragment() {
		mVideoUri = null;
		mMediaPlayerPrepared = false;
		mBufferPercentage = 0;
	}

	public static MediaPlayerFragment newInstance(String uri) {
		MediaPlayerFragment instance = new MediaPlayerFragment();
		Bundle args = new Bundle();
		args.putString(ARG_VIDEO_URI, uri);
		args.putBoolean(ARG_VIDEO_REMOTE, true);
		instance.setArguments(args);
		return instance;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		Bundle args = getArguments();
		if(args != null) {
			String videoName = args.getString(ARG_VIDEO_NAME, null);
			String videoUri = args.getString(ARG_VIDEO_URI, TEST_VIDEO_URL);
			boolean videoIsRemote = args.getBoolean(ARG_VIDEO_REMOTE, true);
			mVideoUri = new VideoUri(videoName, videoUri, videoIsRemote);
		}

		mMediaPlayer = new MediaPlayer();
		mMediaPlayer.setOnPreparedListener(this);
		mMediaPlayer.setOnBufferingUpdateListener(this);
		mMediaPlayer.setOnInfoListener(this);
		mMediaPlayer.setOnErrorListener(this);

		mMediaMetadataRetriever = new MediaMetadataRetriever();

		try {
			if(mVideoUri != null) {
				Uri fileUri = Uri.parse(mVideoUri.getUri());
				mMediaPlayer.setDataSource(getActivity(), fileUri);
				mMediaPlayer.prepareAsync();
//				mMediaPlayer.setDataSource(getActivity(), Uri.parse(TEST_RTSP_URL));
//				mMediaPlayer.prepareAsync();
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_media_player, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mRootView = view.findViewById(R.id.root);
		mRootView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						toggleControlsVisibility();
						break;

					case MotionEvent.ACTION_UP:
						mRootView.performClick();
						break;
				}
				return true;
			}
		});

		mSurfaceView = (SurfaceView)(view.findViewById(R.id.surface_view));

		SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
		surfaceHolder.addCallback(this);

		mMediaController = new MediaController(getActivity());
		mMediaController.setMediaPlayer(this);
		mMediaController.setAnchorView(view);

		mSurfaceView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mMediaController.show();
				return false;
			}
		});
	}

	private void toggleControlsVisibility() {
		if(mMediaController.isShowing()) {
			mMediaController.hide();
		}
		else {
			showControls();
		}
	}

	private void showControls() {
		mMediaController.show(0);
	}

	@Override
	public void onDestroy() {
		mMediaPlayer.stop();
		mMediaPlayer.release();
		super.onDestroy();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.fragment_media_player, menu);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		// menu.findItem(R.id.action_show_media_info_dialog).setEnabled(mMediaPlayerPrepared);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean consume_event;
		switch(item.getItemId()) {
			case R.id.action_show_media_info_dialog:
				showMediaInfoDialog();
				consume_event = true;
				break;

			case R.id.menu_item_loop_mode:
				item.setChecked(!item.isChecked());
				mMediaPlayer.setLooping(item.isChecked());
				consume_event = true;
				break;

			default:
				consume_event = super.onOptionsItemSelected(item);
		}
		return consume_event;
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		SurfaceHolder holder = mSurfaceView.getHolder();
		int width = mSurfaceView.getWidth();
		int height = mSurfaceView.getHeight();
		float boxWidth = width;
		float boxHeight = height;
		float videoWidth = mp.getVideoWidth();
		float videoHeight = mp.getVideoHeight();

		float wr = boxWidth / videoWidth;
		float hr = boxHeight / videoHeight;
		float ar = videoWidth / videoHeight;

		if(wr > hr) {
			width = (int)(boxHeight * ar);
		}
		else {
			height = (int)(boxWidth / ar);
		}

		holder.setFixedSize(width, height);
		mp.start();
		mTrackInfos = mp.getTrackInfo();
		mMediaPlayer.setLooping(true);
		mMediaPlayerPrepared = true;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mMediaPlayer.setDisplay(holder);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mMediaPlayer.setDisplay(null);
	}

	private void showMediaInfoDialog() {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			for(MediaPlayer.TrackInfo trackInfo : mMediaPlayer.getTrackInfo()) {
				switch(trackInfo.getTrackType()) {
					case MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_VIDEO:
						Log.d(LOGCAT_TAG, String.format("Video format: %s", trackInfo.getFormat().toString()));
						break;

					case MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_AUDIO:
						Log.d(LOGCAT_TAG, String.format("Audio format: %s", trackInfo.getFormat().toString()));
						break;
				}
			}
		}
	}

	@Override
	public void start() {
		mMediaPlayer.start();
	}

	@Override
	public void pause() {
		mMediaPlayer.pause();
	}

	@Override
	public int getDuration() {
		return mMediaPlayer.getDuration();
	}

	@Override
	public int getCurrentPosition() {
		return mMediaPlayer.getCurrentPosition();
	}

	@Override
	public void seekTo(int pos) {
		mMediaPlayer.seekTo(pos);
	}

	@Override
	public boolean isPlaying() {
		return mMediaPlayer.isPlaying();
	}

	@Override
	public int getBufferPercentage() {
		return mBufferPercentage;
	}

	@Override
	public boolean canPause() {
		return true;
	}

	@Override
	public boolean canSeekBackward() {
		return true;
	}

	@Override
	public boolean canSeekForward() {
		return true;
	}

	@Override
	public int getAudioSessionId() {
		return mMediaPlayer.getAudioSessionId();
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		mBufferPercentage = percent;
	}

	@Override
	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		switch(what) {
			case MediaPlayer.MEDIA_INFO_UNKNOWN:
				break;
			case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
				Log.d(LOGCAT_TAG, "MEDIA_INFO_VIDEO_TRACK_LAGGING");
				break;
			case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
				Log.d(LOGCAT_TAG, "MEDIA_INFO_VIDEO_RENDERING_START");
				break;
			case MediaPlayer.MEDIA_INFO_BUFFERING_START:
				Log.d(LOGCAT_TAG, "MEDIA_INFO_BUFFERING_START");
				break;
			case MediaPlayer.MEDIA_INFO_BUFFERING_END:
				Log.d(LOGCAT_TAG, "MEDIA_INFO_BUFFERING_END");
				//case MediaPlayer.MEDIA_INFO_NETWORK_BANDWIDTH:
			case 703:
				Log.d(LOGCAT_TAG, "MEDIA_INFO_NETWORK_BANDWIDTH");
				break;
			case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
				Log.d(LOGCAT_TAG, "MEDIA_INFO_BAD_INTERLEAVING");
				break;
			case MediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
				Log.d(LOGCAT_TAG, "MEDIA_INFO_NOT_SEEKABLE");
				break;
			case MediaPlayer.MEDIA_INFO_METADATA_UPDATE:
				Log.d(LOGCAT_TAG, "MEDIA_INFO_METADATA_UPDATE");
				break;
			case MediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE:
				Log.d(LOGCAT_TAG, "MEDIA_INFO_UNSUPPORTED_SUBTITLE");
				break;
			case MediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT:
				Log.d(LOGCAT_TAG, "MEDIA_INFO_SUBTITLE_TIMED_OUT");
				break;
		}
		Log.d(LOGCAT_TAG, String.format("Extra: %d", extra));
		return false;
	}

	@Override
	public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		Log.d(LOGCAT_TAG, String.format("Error: %d, %d", what, extra));
		return true;
	}
}
