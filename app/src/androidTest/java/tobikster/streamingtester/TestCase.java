package tobikster.streamingtester;

import android.support.annotation.IdRes;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

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

	@Test
	public void testButtons() throws Exception {
		clickButtonsAndBack(R.id.exo_player_test_button, R.id.media_player_test_button, R.id.web_view_test_button);
	}

	private void clickButtonsAndBack(@IdRes int... buttonsIds) {
		for(@IdRes int buttonId:buttonsIds) {
			Espresso.onView(ViewMatchers.withId(buttonId)).perform(ViewActions.click());
			Espresso.pressBack();
		}
	}
}
