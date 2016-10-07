package com.mbientlab.metawear.starter;

/**
 * Created by artohei on 07/10/16.
 */
    import android.os.Bundle;
    import android.preference.PreferenceActivity;

    public class UsersSettingsActivity extends PreferenceActivity {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.settings);

        }
    }