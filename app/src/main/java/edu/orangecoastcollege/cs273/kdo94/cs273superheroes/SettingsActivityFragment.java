package edu.orangecoastcollege.cs273.kdo94.cs273superheroes;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * A placeholder fragment containing a simple view.
 */
public class SettingsActivityFragment extends PreferenceFragment {
    // Create preferences GUI from preferences.xml file in res/xml
    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.preferences); // load from XML
    }

}
