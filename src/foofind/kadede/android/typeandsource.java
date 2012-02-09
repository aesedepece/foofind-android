package foofind.kadede.android;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class typeandsource extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.typeandsource);
	}
}
