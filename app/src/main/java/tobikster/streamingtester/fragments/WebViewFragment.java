package tobikster.streamingtester.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import tobikster.streamingtester.R;
import tobikster.streamingtester.utils.Samples;


public class WebViewFragment extends Fragment {
	@SuppressWarnings("unused") private static final String TAG = WebViewFragment.class.getSimpleName();

	private static final String ARG_URL = "url";
	private static final String ARG_STREAM_TYPE = "stream_type";

	private static final String INDEX_DASH_URL = "html/streaming_tester/index_dash.html";

	private WebView mWebView;

	private String mContentUrl;
	private int mStreamType;

	public static WebViewFragment newInstance(final String url, final int type) {
		WebViewFragment fragment = new WebViewFragment();
		Bundle args = new Bundle();
		args.putString(ARG_URL, url);
		args.putInt(ARG_STREAM_TYPE, type);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		if (args != null) {
			mContentUrl = args.getString(ARG_URL, "");
			mStreamType = args.getInt(ARG_STREAM_TYPE);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_web_view, container, false);
	}

	@Override
	@SuppressLint("setJavaScriptEnabled")
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mWebView = (WebView) (view.findViewById(R.id.web_view));
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.addJavascriptInterface(new VideoJavaScriptInterface(), "Android");

		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(
				getActivity().getApplicationContext());
		final String mediaServerAddress = preferences.getString(getString(R.string.pref_media_server_address),
		                                                        getString(R.string.pref_default_media_server_address));
		final String mediaServerPort = preferences.getString(getString(R.string.pref_media_server_port),
		                                                     getString(R.string.pref_default_media_server_port));

		switch (mStreamType) {
			case Samples.TYPE_DASH: {
				Log.d(TAG, "onViewCreated: loading dash page");
				Uri uri = new Uri.Builder().scheme("http")
				                           .encodedAuthority(mediaServerAddress + ":" + mediaServerPort)
				                           .encodedPath(INDEX_DASH_URL)
				                           .appendQueryParameter("url", "/" + mContentUrl)
				                           .appendQueryParameter("type",
				                                                 Integer.toString(mStreamType))
				                           .build();
				Log.d(TAG, String.format("onViewCreated: loading url: %s", uri));
				mWebView.loadUrl(uri.toString());
				break;
			}
			case Samples.TYPE_HLS:
			case Samples.TYPE_OTHER: {
				Log.d(TAG, "onViewCreated: loading hls or other page");
				mWebView.loadUrl("file:///android_asset/www/index.html?type=" + mStreamType + "&url=http://10.0.0.2:8081/" + mContentUrl);
				break;
			}
		}

	}

	@Override
	public void onResume() {
		super.onResume();
		mWebView.onResume();
		Log.d(TAG, "onResume: WebView resumed");
	}

	@Override
	public void onPause() {
		super.onPause();
		mWebView.onPause();
		Log.d(TAG, "onPause: WebView paused");
	}

	protected class VideoJavaScriptInterface {
		protected Context mContext;

		public VideoJavaScriptInterface() {
			mContext = WebViewFragment.this.getContext();
		}

		@JavascriptInterface
		public void showToast(String text) {
			Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
		}

		@JavascriptInterface
		public void logd(String text) {
			Log.d(TAG, "Javascript log: " + text);
		}
	}
}
