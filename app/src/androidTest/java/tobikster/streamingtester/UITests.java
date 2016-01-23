package tobikster.streamingtester;

import android.os.SystemClock;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import tobikster.streamingtester.activities.MainActivity;
import tobikster.streamingtester.pageobjects.MainActivityPage;


/**
 * Created by tobikster on 2015-11-15.
 */
@RunWith(AndroidJUnit4.class)
public
class UITests {

	@Rule
	public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

	@Test
	public
	void testOpenMediaPlayer() throws Exception {
		new MainActivityPage().openMediaPlayerTest().selectSample("Dizzy");
	}

	@Test
	public
	void testExoPlayerDash() throws Exception {
		Espresso.onView(ViewMatchers.withId(R.id.exo_player_test_button)).perform(ViewActions.click());
		Espresso.onData(Utils.Matchers.sampleNameMatches(Matchers.containsString("Google Glass"))).atPosition(0).perform(ViewActions.click());
		SystemClock.sleep(1 * 60 * 1000);
		Espresso.pressBack();
	}
}
