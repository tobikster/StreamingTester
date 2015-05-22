package tobikster.streamingtester.fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;

import tobikster.streamingtester.R;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class TestVideoListFragment extends ListFragment {
	public static final String ARG_VIDEO_URLS = "video_urls";
	private OnFragmentInteractionListener mListener;

	public static TestVideoListFragment newInstance(String... videoArray) {
		TestVideoListFragment fragment = new TestVideoListFragment();
		Bundle args = new Bundle();
		ArrayList<String> videoList = new ArrayList<>();
		Collections.addAll(videoList, videoArray);
		args.putStringArrayList(ARG_VIDEO_URLS, videoList);
		fragment.setArguments(args);
		return fragment;
	}

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public TestVideoListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		Bundle args = getArguments();

		ArrayList<String> videoList = args.getStringArrayList(ARG_VIDEO_URLS);
		setListAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, videoList));
	}


	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnFragmentInteractionListener) activity;
		}
		catch (ClassCastException e) {
			throw new ClassCastException(String.format("%s must implement OnFragmentInteractionListener", activity.getClass().getSimpleName()));
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		if (mListener != null) {
			mListener.onListItemSelected(position);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.video_list_fragment, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean result;
		switch(item.getItemId()) {
			case R.id.menu_item_open_local_video_file:
				if(mListener != null) {
					mListener.onOpenVideoRequest();
				}
				result = true;
				break;
			default:
				result = super.onOptionsItemSelected(item);
		}
		return result;
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated
	 * to the activity and potentially other fragments contained in that
	 * activity.
	 * <p/>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnFragmentInteractionListener {
		void onListItemSelected(int position);
		void onOpenVideoRequest();
	}

}
