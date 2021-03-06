package com.geelaro.blackboard.settings;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.geelaro.blackboard.R;
import com.geelaro.blackboard.main.MainHomeActivity;
import com.geelaro.blackboard.utils.LanguageUtils;
import com.geelaro.blackboard.weather.model.sync.WeatherSyncUtils;

/**
 * Created by brian on 2017/12/30.
 */

public class SettingsFragment extends PreferenceFragment implements
        Preference.OnPreferenceChangeListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = SettingsFragment.class.getSimpleName();
    private static final String KEY_LIST = "language_list";
    private static final String KEY_LOCATION = "location";
    private SharedPreferences prefs;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Add Preferences's Resource
        addPreferencesFromResource(R.xml.pref_general);
        //bind
        bindPreferenceSummaryToValue(findPreference(KEY_LIST));
        bindPreferenceSummaryToValue(findPreference(KEY_LOCATION));

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        prefs.registerOnSharedPreferenceChangeListener(this);


    }

    @Override
    public void onPause() {
        super.onPause();
        prefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(this);


        // Trigger the listener immediately with the preference's
        // current value.
        onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        String stringValue = value.toString();

        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list.
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(stringValue);

            // Set the summary to reflect the new value.
            preference.setSummary(
                    index >= 0
                            ? listPreference.getEntries()[index]
                            : null);

        } else {
            // For all other preferences, set the summary to the value's
            // simple string representation.
            preference.setSummary(stringValue);
        }
        return true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Activity activity = getActivity();
        if (key.equals(KEY_LIST)) {
            // 先判断语言选项是否更改
            if (LanguageUtils.isLanguageChanged()) {
                Settings.needRecreate = true;
            }
            if (Settings.needRecreate = true) {
                restartHomeActivity();
            }
        } else if (key.equals(KEY_LOCATION)) {
            WeatherSyncUtils.startImmediateSync(activity);//立即更新
        }

    }

    private void restartHomeActivity() {
        Intent intent = new Intent(getActivity(), MainHomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("setLanguage", true);
        startActivity(intent);
    }


}
