package tobikster.streamingtester;

import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import tobikster.streamingtester.activities.MainActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

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
	public void testBasicInterface() {
		onView(withId(R.id.exo_player_test_button)).perform(click());
		pressBack();
		onView(withId(R.id.media_player_test_button)).perform(click());
		pressBack();
		onView(withId(R.id.web_view_test_button)).perform(click());
	}
}
