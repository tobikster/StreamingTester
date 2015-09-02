package tobikster.streamingtester.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import tobikster.streamingtester.R;


public
class WebViewFragment extends Fragment {
	public static final String LOGCAT_TAG = "WebViewFragment";

	protected Context mContext;
	protected WebView mWebView;

	public
	WebViewFragment() {
	}

	public static
	WebViewFragment newInstance() {
		WebViewFragment fragment = new WebViewFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public
	void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext = activity;
	}

	@Override
	public
	View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_web_view, container, false);
	}

	@Override
	@SuppressLint("setJavaScriptEnabled")
	public
	void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mWebView = (WebView)(view.findViewById(R.id.web_view));
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.addJavascriptInterface(new VideoJavaScriptInterface(), "Android");
		mWebView.loadUrl("file:///android_res/raw/bunny.html");
	}

	protected
	class VideoJavaScriptInterface {
		protected Context mContext;

		public
		VideoJavaScriptInterface() {
			mContext = WebViewFragment.this.mContext;
		}

		@JavascriptInterface
		public
		void showToast(String text) {
			Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
		}
	}
}
