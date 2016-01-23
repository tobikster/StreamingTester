package tobikster.streamingtester.pageobjects;

import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.UiDevice;

/**
 * Created by tobikster on 2016-01-23.
 */
public abstract
class BasePageObject {
	UiDevice mDevice;

	public BasePageObject() {
		mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
	}
}
