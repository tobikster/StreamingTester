package tobikster.streamingtester.fragments;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;

import tobikster.streamingtester.R;


public class WebViewFragment extends Fragment {
	@SuppressWarnings("unused")
	public static final String LOGCAT_TAG = "WebViewFragment";
	private static final String ARG_URL = "url";

	private WebView mWebView;
	private String mUrl;

	public WebViewFragment() {
	}

	public static WebViewFragment newInstance(String url) {
		WebViewFragment fragment = new WebViewFragment();
		Bundle args = new Bundle();
		args.putString(ARG_URL, url);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		if(args != null) {
			mUrl = args.getString(ARG_URL, "");
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

		mWebView = (WebView)(view.findViewById(R.id.web_view));
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.addJavascriptInterface(new VideoJavaScriptInterface(), "Android");
		mWebView.loadUrl(mUrl);

//		String pageContent = null;
//		AssetManager assetManager = getActivity().getAssets();
//		BufferedReader reader = null;
//		try {
//			reader = new BufferedReader(new InputStreamReader(assetManager.open(mUrl.substring(22))));
//			StringBuilder builder = new StringBuilder();
//			String line;
//			while((line = reader.readLine()) != null) {
//				builder.append(line);
//			}
//
//			pageContent = builder.toString();
//		}
//		catch(IOException e) {
//			e.printStackTrace();
//		}
//		finally {
//			if(reader != null) {
//				try {
//					reader.close();
//				}
//				catch(IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		mWebView.loadDataWithBaseURL(null, pageContent, "text/html", "utf-8", null);

	}

	@Override
	public void onPause() {
		super.onPause();
		mWebView.onPause();
	}

	protected class VideoJavaScriptInterface {
		protected Context mContext;

		public VideoJavaScriptInterface() {
			mContext = WebViewFragment.this.getActivity();
		}

		@JavascriptInterface
		public void showToast(String text) {
			Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
		}
	}
}
