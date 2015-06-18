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
//		mWebView.loadUrl("http://r1---sn-2apm-f5fee.c.youtube.com/videoplayback?id=604ed5ce52eda7ee&itag=22&source=youtube&sparams=expire,id,ip,ipbits,mm,mn,ms,mv,pl,source&ip=31.178.122.240&ipbits=0&expire=19000000000&signature=36A0B7CB7E43730C2FAB5B9C41128776A29C4446.4B179C04899889D3A989A9A0AF892CAA0C34F712&key=cms1&cms_redirect=yes&mm=31&mn=sn-2apm-f5fee&ms=au&mt=1433768625&mv=m&pl=17");
//		mWebView.loadUrl("http://broken-links.com/tests/video/");
		mWebView.loadUrl("file:///android_res/raw/bunny.html");
	}
}
