package tobikster.streamingtester.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;

import tobikster.streamingtester.R;
import tobikster.streamingtester.broadcastReceivers.BatteryStateReceiver;
import tobikster.streamingtester.dialogs.MediaInfoDialog;
import tobikster.streamingtester.fragments.MediaPlayerFragment;
import tobikster.streamingtester.fragments.TestVideoListFragment;
import tobikster.streamingtester.model.VideoUri;

public class MediaPlayerActivity extends Activity implements MediaInfoDialog.MediaInfoDialogListener, TestVideoListFragment.OnFragmentInteractionListener {
	@SuppressWarnings("unused")
	public static final String LOGCAT_TAG = "MediaPlayerActivity";

	public static final int RESULT_CODE_PICK_FILE = 1;
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
				mVideos = new VideoUri[]{
												new VideoUri("KSNN News You Can Use", "https://archive.org/download/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4"),
												new VideoUri("Wowza", "rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mov"),
				};

				String[] videoNames = new String[mVideos.length];
				for (int i = 0; i < mVideos.length; ++i) {
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
		showPlayerFragment();
	}

	@Override
	public void onOpenVideoRequest() {
		Log.d(LOGCAT_TAG, "Opening file chooser...");
		Intent openFileChooserIntent = new Intent(Intent.ACTION_GET_CONTENT);
		openFileChooserIntent.setType("video/*");
		openFileChooserIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
		startActivityForResult(Intent.createChooser(openFileChooserIntent, "Select video file"), RESULT_CODE_PICK_FILE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode) {
			case RESULT_CODE_PICK_FILE:
				if(RESULT_OK == resultCode) {
					Uri fileUri = data.getData();
					Log.d(LOGCAT_TAG, String.format("%s://%s", fileUri.getScheme(), fileUri.getPath()));

					String[] proj = {MediaStore.Video.Media.DATA};
					Cursor cursor = getContentResolver().query(fileUri, proj, null, null, null);
					int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
					cursor.moveToFirst();
					String fullUrl = cursor.getString(columnIndex);
					cursor.close();
					Log.d(LOGCAT_TAG, String.format("Full URL: %s", fullUrl));
					mMediaPlayerFragment = MediaPlayerFragment.newInstance(new VideoUri("Local file", fullUrl, false));
					showPlayerFragment();
				}
				break;
		}
	}

	private void showPlayerFragment() {
		getFragmentManager().beginTransaction()
				.replace(R.id.fragment_container, mMediaPlayerFragment)
				.addToBackStack(null)
				.commit();
	}
}
