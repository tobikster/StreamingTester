package tobikster.streamingtester.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;

import tobikster.streamingtester.R;
import tobikster.streamingtester.broadcastReceivers.BatteryStateReceiver;
import tobikster.streamingtester.dialogs.MediaInfoDialog;
import tobikster.streamingtester.fragments.MediaPlayerFragment;
import tobikster.streamingtester.fragments.TestVideoListFragment;
import tobikster.streamingtester.model.VideoUri;

public class MediaPlayerActivity extends Activity implements MediaInfoDialog.MediaInfoDialogListener, TestVideoListFragment.OnFragmentInteractionListener {
	@SuppressWarnings("unused")
	public static final String LOGCAT_TAG = "MediaPlayerActivity";
	public static final String TEST_VIDEO_URL = "https://archive.org/download/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4";

	MediaPlayerFragment mMediaPlayerFragment;
	BatteryStateReceiver mBatteryStateReceiver;

	VideoUri[] mVideos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_media_player);

		if (findViewById(R.id.fragment_container) != null) {
			if (savedInstanceState == null) {
				AssetManager manager = getAssets();
				mVideos = new VideoUri[1];
				mVideos[0] = new VideoUri("KSNN News You Can Use", TEST_VIDEO_URL);

				String[] videoNames = new String[mVideos.length];
				for(int i = 0; i < mVideos.length; ++i) {
					videoNames[i] = mVideos[i].getName();
				}

				TestVideoListFragment videoListFragment = TestVideoListFragment.newInstance(videoNames);
				getFragmentManager().beginTransaction()
						.add(R.id.fragment_container, videoListFragment)
						.commit();
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		IntentFilter batteryIntentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		mBatteryStateReceiver = new BatteryStateReceiver();
		registerReceiver(mBatteryStateReceiver, batteryIntentFilter);
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(mBatteryStateReceiver);
	}

	@Override
	public void onOKButtonClicked() {
	}

	@Override
	public void onListItemSelected(int position) {
		mMediaPlayerFragment = MediaPlayerFragment.newInstance(mVideos[position]);
		FragmentManager manager = getFragmentManager();
		manager.beginTransaction()
				.replace(R.id.fragment_container, mMediaPlayerFragment)
				.addToBackStack(null)
				.commit();
	}
}
