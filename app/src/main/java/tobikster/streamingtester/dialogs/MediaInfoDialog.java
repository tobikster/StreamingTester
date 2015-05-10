package tobikster.streamingtester.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.media.MediaFormat;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import tobikster.streamingtester.R;

/**
 * Created by tobikster on 2015-05-08.
 */
public class MediaInfoDialog extends DialogFragment {
	private MediaInfoDialogListener mMediaInfoDialogListener;

	private TextView mCodecValue;

	public static MediaInfoDialog getInstance(MediaPlayer.TrackInfo[] infos) {
		MediaInfoDialog dialog = new MediaInfoDialog();
		Bundle args = new Bundle();
		MediaFormat videoFormat = null;
		MediaFormat audioFormat = null;
		for (MediaPlayer.TrackInfo info : infos) {
			switch (info.getTrackType()) {
				case MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_VIDEO:
					videoFormat = info.getFormat();
					break;

				case MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_AUDIO:
					audioFormat = info.getFormat();
					break;
			}
		}
		dialog.setArguments(args);
		return dialog;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		Bundle args = getArguments();

		LayoutInflater inflater = getActivity().getLayoutInflater();
		View root = inflater.inflate(R.layout.dialog_media_info, null);
		builder.setView(root)
				.setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (mMediaInfoDialogListener != null) {
							mMediaInfoDialogListener.onOKButtonClicked();
						}
					}
				});

		mCodecValue = (TextView) (root.findViewById(R.id.codec_value));


		return builder.create();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mMediaInfoDialogListener = (MediaInfoDialogListener) (activity);
		}
		catch (ClassCastException e) {
			throw new ClassCastException(String.format("Activity %s must implement MediaInfoDialogListener interface!", activity.getClass().getSimpleName()));
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mMediaInfoDialogListener = null;
	}

	public interface MediaInfoDialogListener {
		void onOKButtonClicked();
	}
}
