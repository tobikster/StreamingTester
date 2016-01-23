package tobikster.streamingtester.pageobjects;

import android.support.annotation.IdRes;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.matcher.ViewMatchers;

import tobikster.streamingtester.R;
import tobikster.streamingtester.fragments.SettingsFragment;

/**
 * Created by tobikster on 2016-01-23.
 */
public
class MainActivityPage extends BasePageObject {

	public
	MainActivityPage() {
		Espresso.onView(ViewMatchers.withId(R.id.media_player_test_button)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
		Espresso.onView(ViewMatchers.withId(R.id.web_view_test_button)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
		Espresso.onView(ViewMatchers.withId(R.id.exo_player_test_button)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
	}

	private
	void clickTestButton(@IdRes int buttonId) {
		Espresso.onView(ViewMatchers.withId(buttonId)).perform(ViewActions.click());
	}

	public
	SamplesListActivityPage openMediaPlayerTest() {
		clickTestButton(R.id.media_player_test_button);
		return new SamplesListActivityPage(SettingsFragment.TEST_TYPE_MEDIAPLAYER);
	}
}
