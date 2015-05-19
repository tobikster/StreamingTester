package tobikster.streamingtester.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import tobikster.streamingtester.R;


public class WebViewActivity extends Activity {
	public static final String LOGCAT_TAG = "WebViewActivity";

	private WebView mWebView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_PROGRESS);

		setContentView(R.layout.activity_web_view);

		mWebView = (WebView)(findViewById(R.id.webView));
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setWebChromeClient(new WebChromeClient(){
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
				Log.d(LOGCAT_TAG, String.format("Progress: %d", newProgress));
				WebViewActivity.this.setProgress(newProgress * 1000);
			}
		});
		mWebView.loadUrl("http://broken-links.com/tests/video/");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
