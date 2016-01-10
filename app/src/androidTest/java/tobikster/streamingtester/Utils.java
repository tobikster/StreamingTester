package tobikster.streamingtester;

import android.support.test.espresso.matcher.BoundedMatcher;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import tobikster.streamingtester.utils.Samples;

/**
 * Created by tobikster on 2015-12-20.
 */
public
class Utils {
	static
	class Matchers {
		public static
		Matcher<Object> sampleNameMatches(final Matcher<String> nameMatcher) {
			return new BoundedMatcher<Object, Samples.Sample>(Samples.Sample.class) {
				@Override
				public
				void describeTo(Description description) {
					description.appendText("Sample name matches: ");
					description.appendDescriptionOf(nameMatcher);
				}

				@Override
				protected
				boolean matchesSafely(Samples.Sample item) {
					return nameMatcher.matches(item.name);
				}
			};
		}
	}
}
