package tobikster.streamingtester.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.exoplayer.MediaCodecUtil;
import com.google.android.exoplayer.util.MimeTypes;

import tobikster.streamingtester.R;
import tobikster.streamingtester.demoplayer.Samples;

public
class ExoPLayerSampleChooserFragment extends Fragment {
	@SuppressWarnings("unused")
	public static final String LOGCAT_TAG = "ExoPlayer";

	private InteractionListener mListener;

	public
	ExoPLayerSampleChooserFragment() {
	}

	public static
	ExoPLayerSampleChooserFragment newInstance() {
		ExoPLayerSampleChooserFragment fragment = new ExoPLayerSampleChooserFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public
	void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public
	View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_exo_player_sample_chooser, container, false);
	}

	@Override
	public
	void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		ListView sampleList = (ListView)(view.findViewById(R.id.sample_list));
		final SampleAdapter sampleAdapter = new SampleAdapter(getActivity());

		sampleAdapter.add(new Header("YouTube DASH"));
		sampleAdapter.addAll((Object[])Samples.YOUTUBE_DASH_MP4);
		sampleAdapter.add(new Header("Widevine GTS DASH"));
		sampleAdapter.addAll((Object[])Samples.WIDEVINE_GTS);
		sampleAdapter.add(new Header("SmoothStreaming"));
		sampleAdapter.addAll((Object[])Samples.SMOOTHSTREAMING);
		sampleAdapter.add(new Header("HLS"));
		sampleAdapter.addAll((Object[])Samples.HLS);
		sampleAdapter.add(new Header("Misc"));
		sampleAdapter.addAll((Object[])Samples.MISC);

		// Add WebM samples if the device has a VP9 decoder.
		try {
			if(MediaCodecUtil.getDecoderInfo(MimeTypes.VIDEO_VP9, false) != null) {
				sampleAdapter.add(new Header("YouTube WebM DASH (Experimental)"));
				sampleAdapter.addAll((Object[])Samples.YOUTUBE_DASH_WEBM);
			}
		}
		catch(MediaCodecUtil.DecoderQueryException e) {
			Log.e(LOGCAT_TAG, "Failed to query vp9 decoder", e);
		}

		sampleList.setAdapter(sampleAdapter);
		sampleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public
			void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Object item = sampleAdapter.getItem(position);
				if(item instanceof Samples.Sample) {
					Samples.Sample selectedSample = (Samples.Sample)(item);
					mListener.onSampleSelected(selectedSample.uri, selectedSample.contentId, selectedSample.type);
				}
			}
		});
	}

	@Override
	public
	void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity instanceof InteractionListener) {
			mListener = (InteractionListener)(activity);
		}
		else {
			throw new ClassCastException(String.format("Activity %s must implement %s interface!", activity.getClass().getSimpleName(), InteractionListener.class.getCanonicalName()));
		}
	}

	@Override
	public
	void onDetach() {
		super.onDetach();
		mListener = null;
	}

	private static
	class SampleAdapter extends ArrayAdapter<Object> {

		public
		SampleAdapter(Context context) {
			super(context, 0);
		}

		@Override
		public
		View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if(view == null) {
				int layoutId = getItemViewType(position) == 1 ? android.R.layout.simple_list_item_1 : R.layout.sample_chooser_inline_header;
				view = LayoutInflater.from(getContext()).inflate(layoutId, null, false);
			}
			Object item = getItem(position);
			String name = null;
			if(item instanceof Samples.Sample) {
				name = ((Samples.Sample)item).name;
			}
			else if(item instanceof Header) {
				name = ((Header)item).name;
			}
			((TextView)view).setText(name);
			return view;
		}

		@Override
		public
		int getItemViewType(int position) {
			return (getItem(position) instanceof Samples.Sample) ? 1 : 0;
		}

		@Override
		public
		int getViewTypeCount() {
			return 2;
		}

	}

	private static
	class Header {

		public final String name;

		public
		Header(String name) {
			this.name = name;
		}

	}

	public
	interface InteractionListener {
		void onSampleSelected(String contentUri, String contentId, int type);
	}
}
