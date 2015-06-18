package tobikster.streamingtester.preferences;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;

/**
 * Created by tobikster on 2015-06-08.
 */
public
class BroadcastPreference extends Preference implements Preference.OnPreferenceClickListener {

	public
	BroadcastPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOnPreferenceClickListener(this);
	}

	@Override
	public
	boolean onPreferenceClick(Preference preference) {
		getContext().sendBroadcast(preference.getIntent());
		return true;
	}
}
