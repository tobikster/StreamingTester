package tobikster.streamingtester.fragments;

import android.app.Fragment;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
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
import tobikster.streamingtester.dialogs.MediaInfoDialog;

public class MediaPlayerFragment extends Fragment implements MediaPlayer.OnPreparedListener, SurfaceHolder.Callback, MediaController.MediaPlayerControl, MediaPlayer.OnBufferingUpdateListener {
	public static final String TEST_VIDEO_URL = "https://archive.org/download/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4";

	SurfaceView mSurfaceView;
	MediaPlayer mMediaPlayer;
	MediaController mMediaController;
	MediaInfoDialog mMediaInfoDialog;
	MediaPlayer.TrackInfo[] mTrackInfos;

	int mBufferPercentage;
	boolean mMediaPlayerPrepared;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		mMediaPlayer = new MediaPlayer();
		mMediaPlayer.setOnPreparedListener(this);
		mMediaPlayer.setOnBufferingUpdateListener(this);

		mBufferPercentage = 0;
		mMediaPlayerPrepared = false;

		try {
			mMediaPlayer.setDataSource(getActivity(), Uri.parse(TEST_VIDEO_URL));
			mMediaPlayer.prepareAsync();
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
		mMediaController.setAnchorView(mSurfaceView);

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
}
