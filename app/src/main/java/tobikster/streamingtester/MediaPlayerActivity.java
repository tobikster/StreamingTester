package tobikster.streamingtester;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.ViewManager;
import android.widget.ProgressBar;
import android.widget.VideoView;

import java.io.IOException;


public class MediaPlayerActivity extends Activity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener {
	private MediaPlayer mMediaPlayer;
	private VideoView mVideoView;
	private SurfaceHolder mSurfaceHolder;
	private ProgressBar mProgressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_media_player);

		mVideoView = (VideoView) (findViewById(R.id.videoView));
		mProgressBar = (ProgressBar)(findViewById(R.id.progressBar));

		mSurfaceHolder = mVideoView.getHolder();
		mSurfaceHolder.addCallback(this);

		mMediaPlayer = new MediaPlayer();
		mMediaPlayer.setOnPreparedListener(this);
		try {
			mMediaPlayer.setDataSource(this, Uri.parse("https://archive.org/download/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4"));
			mMediaPlayer.prepareAsync();
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mMediaPlayer.stop();
		mMediaPlayer.release();
		mMediaPlayer = null;
		mVideoView = null;
		mSurfaceHolder = null;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mMediaPlayer.setDisplay(mVideoView.getHolder());
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		((ViewManager)(mProgressBar.getParent())).removeView(mProgressBar);
		mMediaPlayer.start();
	}
}
