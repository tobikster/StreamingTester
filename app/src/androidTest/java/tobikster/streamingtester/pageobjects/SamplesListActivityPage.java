package tobikster.streamingtester.pageobjects;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.matcher.ViewMatchers;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import tobikster.streamingtester.R;
import tobikster.streamingtester.Utils;

/**
 * Created by tobikster on 2016-01-23.
 */
public
class SamplesListActivityPage extends BasePageObject {
	int mTestType;

	public
	SamplesListActivityPage(int testType) {
		mTestType = testType;
		Espresso.onView(ViewMatchers.withId(R.id.sample_list)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
	}

	public
	StreamingTestActivityPage selectSample(Matcher<String> nameMatcher) {
		Espresso.onData(Utils.Matchers.sampleNameMatches(nameMatcher)).inAdapterView(ViewMatchers.withId(R.id.sample_list)).perform(ViewActions.click());
		return new StreamingTestActivityPage(mTestType);
	}

	public
	StreamingTestActivityPage selectSample(String name) {
		return selectSample(Matchers.is(name));
	}
}
