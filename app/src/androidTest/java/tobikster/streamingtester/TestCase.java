package tobikster.streamingtester;

import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;

import org.junit.Before;

import tobikster.streamingtester.activities.MainActivity;

/**
 * Created by tobikster on 2015-11-15.
 */
public class TestCase extends ActivityInstrumentationTestCase2<MainActivity> {
	private MainActivity mMainActivity;

	public TestCase() {
		super(MainActivity.class);
	}

	@Before
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		injectInstrumentation(InstrumentationRegistry.getInstrumentation());
		mMainActivity = getActivity();
	}
}
