package tobikster.streamingtester.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import tobikster.streamingtester.R;

public class MainActivity extends Activity  {
	private Button mButton1;
	private Button mButton2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mButton1 = (Button)(findViewById(R.id.button1));
		mButton2 = (Button)(findViewById(R.id.button2));

		mButton1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent startMediaPlayerActivityIntent = new Intent(MainActivity.this, MediaPlayerActivity.class);
				startActivity(startMediaPlayerActivityIntent);
			}
		});
		mButton2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent startWebViewActivityIntent = new Intent(MainActivity.this, WebViewActivity.class);
				startActivity(startWebViewActivityIntent);
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mButton1.setOnClickListener(null);
		mButton1 = null;
		mButton2.setOnClickListener(null);
		mButton2 = null;
	}
}
