package tobikster.streamingtester;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiSelector;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import tobikster.streamingtester.activities.MainActivity;


/**
 * Created by tobikster on 2015-11-15.
 */
@RunWith(AndroidJUnit4.class)
public class TestCase {

	@Rule
	public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

	private UiDevice mDevice;

	@Before
	public void setUp() throws Exception {
		mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
	}

	@Test
	public void testBasicInterface() throws Exception {
		UiObject exoPlayerButton = mDevice.findObject(new UiSelector().resourceId("tobikster.streamingtester:id/exo_player_test_button"));
		UiObject mediaPlayerButton = mDevice.findObject(new UiSelector().resourceId("tobikster.streamingtester:id/media_player_test_button"));
		UiObject webViewButton = mDevice.findObject(new UiSelector().resourceId("tobikster.streamingtester:id/web_view_test_button"));

		for(int i = 0; i < 10; ++i) {
			exoPlayerButton.click();
			mDevice.pressBack();
			mediaPlayerButton.click();
			mDevice.pressBack();
			webViewButton.click();
			mDevice.pressBack();
		}
	}

	@Test
	public void testBasicInterface2() throws Exception {
		for(int i = 0; i < 10; ++i) {
			Espresso.onView(ViewMatchers.withId(R.id.exo_player_test_button)).perform(ViewActions.click());
			Espresso.pressBack();
			Espresso.onView(ViewMatchers.withId(R.id.media_player_test_button)).perform(ViewActions.click());
			Espresso.pressBack();
			Espresso.onView(ViewMatchers.withId(R.id.web_view_test_button)).perform(ViewActions.click());
			Espresso.pressBack();
		}
	}
}
