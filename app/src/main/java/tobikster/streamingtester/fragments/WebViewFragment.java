package tobikster.streamingtester.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import tobikster.streamingtester.R;


public
class WebViewFragment extends Fragment {
	public static final String LOGCAT_TAG = "WebViewFragment";

	private WebView mWebView;

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
	View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_web_view, container, false);
	}

	@Override
	public
	void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mWebView = (WebView)(view.findViewById(R.id.web_view));
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setWebChromeClient(new WebChromeClient());
		mWebView.loadUrl("http://broken-links.com/tests/video/");
	}
}
