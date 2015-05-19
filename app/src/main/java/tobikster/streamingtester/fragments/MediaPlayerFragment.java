package tobikster.streamingtester.fragments;

import android.app.Fragment;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.Toast;

import java.io.FileDescriptor;
import java.io.IOException;

import tobikster.streamingtester.R;
import tobikster.streamingtester.dialogs.MediaInfoDialog;
import tobikster.streamingtester.model.VideoUri;

public class MediaPlayerFragment extends Fragment implements MediaPlayer.OnPreparedListener, SurfaceHolder.Callback, MediaController.MediaPlayerControl, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnInfoListener, MediaPlayer.OnVideoSizeChangedListener {
	public static final String LOGCAT_TAG = "MediaPlayerFragment";
	public static final String ARG_VIDEO_NAME = "video_name";
	public static final String ARG_VIDEO_URI = "video_url";
	public static final String ARG_VIDEO_REMOTE = "video_is_remote";

	public static final String TEST_VIDEO_URL = "https://archive.org/download/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4";

	SurfaceView mSurfaceView;
	MediaPlayer mMediaPlayer;
	MediaController mMediaController;
	MediaInfoDialog mMediaInfoDialog;
	MediaPlayer.TrackInfo[] mTrackInfos;

	VideoUri mVideoUri;

	int mBufferPercentage;
	boolean mMediaPlayerPrepared;

	public MediaPlayerFragment() {
		mVideoUri = null;
		mMediaPlayerPrepared = false;
		mBufferPercentage = 0;
	}

	public static MediaPlayerFragment newInstance(VideoUri videoUri) {
		MediaPlayerFragment instance = new MediaPlayerFragment();
		Bundle args = new Bundle();
		args.putString(ARG_VIDEO_NAME, videoUri.getName());
		args.putString(ARG_VIDEO_URI, videoUri.getUri());
		args.putBoolean(ARG_VIDEO_REMOTE, videoUri.isRemote());
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

		try {
			if(mVideoUri != null) {
				if (mVideoUri.isRemote()) {
					mMediaPlayer.setDataSource(mVideoUri.getUri());
					mMediaPlayer.prepareAsync();
				}
				else {
					Log.d(LOGCAT_TAG, "Playing local resources is not supported yet");
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_media_player, container, false);
		mSurfaceView = (SurfaceView) (view.findViewById(R.id.surface_view));

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
		return view;
	}

	@Override
	public void onDestroy() {
		mMediaPlayer.stop();
		mMediaPlayer.release();
		super.onDestroy();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.media_player_fragment, menu);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.action_show_media_info_dialog).setEnabled(mMediaPlayerPrepared);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean result;
		switch (item.getItemId()) {
			case R.id.action_show_media_info_dialog:
				showMediaInfoDialog();
				result = true;
				break;

			default:
				result = super.onOptionsItemSelected(item);
		}
		return result;
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
			width = (int) (boxHeight * ar);
		}
		else {
			height = (int)(boxWidth / ar);
		}

		holder.setFixedSize(width, height);
		mp.start();
		mTrackInfos = mp.getTrackInfo();
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
		mMediaInfoDialog = MediaInfoDialog.getInstance(mTrackInfos);
		mMediaInfoDialog.show(getFragmentManager(), "MediaInfoDialog");
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
		Log.d(LOGCAT_TAG, String.format("New info: %d (%d)", what, extra));
		if(MediaPlayer.MEDIA_INFO_METADATA_UPDATE == what) {
			Toast.makeText(getActivity(), "Metadata update!", Toast.LENGTH_SHORT).show();
		}
		return false;
	}

	@Override
	public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
	}
}
