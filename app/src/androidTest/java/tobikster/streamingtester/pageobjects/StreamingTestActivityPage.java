package tobikster.streamingtester.pageobjects;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.matcher.ViewMatchers;

import tobikster.streamingtester.R;
import tobikster.streamingtester.fragments.SettingsFragment;

/**
 * Created by tobikster on 2016-01-23.
 */
public
class StreamingTestActivityPage extends BasePageObject {
	int mTestType;
	public
	StreamingTestActivityPage(int type) {
		mTestType = type;
		switch(mTestType) {
			case SettingsFragment.TEST_TYPE_MEDIAPLAYER:
				Espresso.onView(ViewMatchers.withId(R.id.surface_view)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
				break;

			case SettingsFragment.TEST_TYPE_WEBVIEW:
				break;

			case SettingsFragment.TEST_TYPE_EXOPLAYER:
				break;
		}
	}
}
