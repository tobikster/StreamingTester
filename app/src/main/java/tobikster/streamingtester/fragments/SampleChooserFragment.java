package tobikster.streamingtester.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import tobikster.streamingtester.R;
import tobikster.streamingtester.utils.Samples;

public class SampleChooserFragment extends Fragment {
	@SuppressWarnings("unused")
	public static final String LOGCAT_TAG = "ExoPlayer";

	private static final String ARG_TEST_TYPE = "test_type";

	private InteractionListener mListener;
	private int mTestType;

	public SampleChooserFragment() {
	}

	public static SampleChooserFragment newInstance(int testType) {
		SampleChooserFragment fragment = new SampleChooserFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_TEST_TYPE, testType);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof InteractionListener) {
			mListener = (InteractionListener) (context);
		}
		else {
			throw new ClassCastException(String.format("Activity %s must implement %s interface!",
			                                           context.getClass().getSimpleName(),
			                                           InteractionListener.class.getCanonicalName()));
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		if (args != null) {
			mTestType = args.getInt(ARG_TEST_TYPE, SettingsFragment.TEST_TYPE_UNKNOWN);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_sample_chooser, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		ListView sampleList = (ListView) (view.findViewById(R.id.sample_list));
		final SampleAdapter sampleAdapter = new SampleAdapter(getActivity());

		switch (mTestType) {
			case SettingsFragment.TEST_TYPE_WEBVIEW:
				sampleAdapter.add(new Header("DASH"));
				sampleAdapter.addAll((Object[]) Samples.DASH);
				sampleAdapter.add(new Header("HLS"));
				sampleAdapter.addAll((Object[]) Samples.HLS);
				sampleAdapter.add(new Header("DIRECT STREAMING"));
				sampleAdapter.addAll((Object[]) Samples.DIRECT_STREAMING);
				break;

			case SettingsFragment.TEST_TYPE_EXOPLAYER:
				sampleAdapter.add(new Header("DASH"));
				sampleAdapter.addAll((Object[]) Samples.DASH);
				sampleAdapter.add(new Header("HLS"));
				sampleAdapter.addAll((Object[]) Samples.HLS);
				sampleAdapter.add(new Header("DIRECT STREAMING"));
				sampleAdapter.addAll((Object[]) Samples.DIRECT_STREAMING);
				break;

			case SettingsFragment.TEST_TYPE_MEDIAPLAYER:
				sampleAdapter.add(new Header("HLS"));
				sampleAdapter.addAll((Object[]) Samples.HLS);
				sampleAdapter.add(new Header("DIRECT STREAMING"));
				sampleAdapter.addAll((Object[]) Samples.DIRECT_STREAMING);
				break;

		}

		sampleList.setAdapter(sampleAdapter);
		sampleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Object item = sampleAdapter.getItem(position);
				if (item instanceof Samples.Sample) {
					Samples.Sample selectedSample = (Samples.Sample) item;
					mListener.onSampleSelected(selectedSample.uri, selectedSample.contentId, selectedSample.type);
				}
			}
		});
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	public interface InteractionListener {
		void onSampleSelected(String contentUri, String contentId, int type);
	}

	private static class SampleAdapter extends ArrayAdapter<Object> {

		public SampleAdapter(Context context) {
			super(context, 0);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				int layoutId = getItemViewType(position) == 1 ?
				               android.R.layout.simple_list_item_1 :
				               R.layout.sample_chooser_inline_header;
				view = LayoutInflater.from(getContext()).inflate(layoutId, null, false);
			}
			Object item = getItem(position);
			String name = null;
			if (item instanceof Samples.Sample) {
				name = ((Samples.Sample) item).name;
			}
			else if (item instanceof Header) {
				name = ((Header) item).name;
			}
			((TextView) view).setText(name);
			return view;
		}

		@Override
		public int getItemViewType(int position) {
			return (getItem(position) instanceof Samples.Sample) ? 1 : 0;
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

	}

	private static class Header {

		public final String name;

		public Header(String name) {
			this.name = name;
		}

	}
}
