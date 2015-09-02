package tobikster.streamingtester.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaFormat;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import tobikster.streamingtester.R;

public
class MediaInfoDialog extends DialogFragment {
	@SuppressWarnings("unused")
	public static final String LOGCAT_TAG = "MediaInfoDialog";

	public static final String ARG_VIDEO_BITRATE = "video_bitrate";

	Context mContext;
	MediaInfoDialogListener mMediaInfoDialogListener;
	int mVideoBitrate;

	public static
	MediaInfoDialog newInstance(MediaPlayer.TrackInfo[] infos) {
		MediaInfoDialog dialog = new MediaInfoDialog();
		Bundle args = new Bundle();
		MediaFormat videoFormat = null;
		MediaFormat audioFormat = null;
		for(MediaPlayer.TrackInfo info : infos) {
			switch(info.getTrackType()) {
				case MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_VIDEO:
					if(Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
						Log.d(LOGCAT_TAG, String.format("Getting media format is not supported on API level %d (%s)", Build.VERSION.SDK_INT, Build.VERSION.CODENAME));
					}
					else {
						videoFormat = info.getFormat();
					}
					break;

				case MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_AUDIO:
					if(Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
						Log.d(LOGCAT_TAG, String.format("Getting media format is not supported on API level %d (%s)", Build.VERSION.SDK_INT, Build.VERSION.CODENAME));
					}
					else {
						audioFormat = info.getFormat();
					}
					break;
			}
		}
		args.putBoolean("video_format_null", videoFormat == null);
		args.putBoolean("audio_format_null", audioFormat == null);
		dialog.setArguments(args);
		return dialog;
	}

	public static
	MediaInfoDialog newInstance(int videoBitrate) {
		MediaInfoDialog dialog = new MediaInfoDialog();
		Bundle args = new Bundle();
		args.putInt(ARG_VIDEO_BITRATE, videoBitrate);
		dialog.setArguments(args);
		return dialog;
	}

	@Override
	public
	Dialog onCreateDialog(Bundle savedInstanceState) {
		mContext = getActivity();
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		Bundle args = getArguments();

		if(args.getBoolean("video_format_null")) {
			Toast.makeText(mContext, "Video format inaccessible", Toast.LENGTH_SHORT).show();
		}
		if(args.getBoolean("audio_format_null")) {
			Toast.makeText(mContext, "Audio format inaccessible", Toast.LENGTH_SHORT).show();
		}

		LayoutInflater inflater = getActivity().getLayoutInflater();
		View root = inflater.inflate(R.layout.dialog_media_info, null);
		builder.setView(root)
		       .setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {
			       @Override
			       public
			       void onClick(DialogInterface dialog, int which) {
				       if(mMediaInfoDialogListener != null) {
					       mMediaInfoDialogListener.onOKButtonClicked();
				       }
			       }
		       });
		return builder.create();
	}

	@Override
	public
	void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mMediaInfoDialogListener = (MediaInfoDialogListener)(activity);
		}
		catch(ClassCastException e) {
			throw new ClassCastException(String.format("Activity %s must implement MediaInfoDialogListener interface!", activity.getClass().getSimpleName()));
		}
	}

	@Override
	public
	void onDetach() {
		super.onDetach();
		mMediaInfoDialogListener = null;
	}

	public
	interface MediaInfoDialogListener {
		void onOKButtonClicked();
	}
}
