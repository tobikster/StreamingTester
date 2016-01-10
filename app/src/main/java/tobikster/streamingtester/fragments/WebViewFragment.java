package tobikster.streamingtester.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import org.nikkii.embedhttp.HttpServer;
import org.nikkii.embedhttp.handler.HttpRequestHandler;
import org.nikkii.embedhttp.impl.HttpRequest;
import org.nikkii.embedhttp.impl.HttpResponse;
import org.nikkii.embedhttp.impl.HttpStatus;

import java.io.IOException;
import java.io.InputStream;

import tobikster.streamingtester.R;


public
class WebViewFragment extends Fragment {
	@SuppressWarnings("unused")
	public static final String LOGCAT_TAG = "WebViewFragment";
	private static final String ARG_URL = "url";

	private WebView mWebView;
	private String mUrl;

	private HttpServer mServer;

	public
	WebViewFragment() {
	}

	public static
	WebViewFragment newInstance(String url) {
		WebViewFragment fragment = new WebViewFragment();
		Bundle args = new Bundle();
		args.putString(ARG_URL, url);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public
	void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		if(args != null) {
			mUrl = args.getString(ARG_URL, "");
		}

		try {
			mServer = new HttpServer();
			mServer.addRequestHandler(new HttpRequestHandler() {
				@Override
				public
				HttpResponse handleRequest(HttpRequest request) {
					String uri = request.getUri().substring(1);
					HttpResponse response = null;
					try {
						InputStream input = getActivity().getAssets().open(uri);
						response = new HttpResponse(HttpStatus.OK, input);
					}
					catch(IOException e) {
						e.printStackTrace();
					}
					return response;
				}
			});
			mServer.bind(8081);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
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
		mWebView.loadUrl(mUrl);

	}

	@Override
	public
	void onResume() {
		super.onResume();
		mWebView.onResume();
		if(mServer != null) {
			mServer.start();
		}
	}

	@Override
	public
	void onPause() {
		super.onPause();
		mWebView.onPause();
		if(mServer != null) {
			mServer.stop();
		}
	}

	protected
	class VideoJavaScriptInterface {
		protected Context mContext;

		public
		VideoJavaScriptInterface() {
			mContext = WebViewFragment.this.getActivity();
		}

		@JavascriptInterface
		public
		void showToast(String text) {
			Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
		}
	}
}
