package com.platypus.android.tablet;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import java.util.Map;


/*
* Only things that should be automatically saved from the teleop panel activity when its closed:
* IP Address
* Port
* Map Center location (where map autopans to on next connect)
* */

public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String KEY_PREF_DEFAULT_IP = "pref_boat_ip";
    public static final String KEY_PREF_DEFAULT_PORT = "pref_boat_port";
    public static final String KEY_PREF_COMMAND_RATE = "pref_command_update_rate";
    public static final String KEY_PREF_LAT = "pref_latitude";
    public static final String KEY_PREF_LON = "pref_longitude";
    public static final String KEY_PREF_SAVE_MAP = "pref_save_location";

    public static final String KEY_PREF_THRUST_MIN = "pref_thrust_min";
    public static final String KEY_PREF_THRUST_MAX = "pref_thrust_max";

    public static final String KEY_PREF_RUDDER_MIN = "pref_rudder_min";
    public static final String KEY_PREF_RUDDER_MAX = "pref_rudder_max";

    public static final String KEY_PREF_PID_THRUST_P = "pref_pid_thrust_p";
    public static final String KEY_PREF_PID_THRUST_I= "pref_pid_thrust_i";
    public static final String KEY_PREF_PID_THRUST_D= "pref_pid_thrust_d";

    public static final String KEY_PREF_PID_RUDDER_P = "pref_pid_rudder_p";
    public static final String KEY_PREF_PID_RUDDER_I= "pref_pid_rudder_i";
    public static final String KEY_PREF_PID_RUDDER_D= "pref_pid_rudder_d";

    public static final String KEY_PREF_PID_LOW_THRUST_P = "pref_pid_low_thrust_p";
    public static final String KEY_PREF_PID_LOW_THRUST_I= "pref_pid_low_thrust_i";
    public static final String KEY_PREF_PID_LOW_THRUST_D= "pref_pid_low_thrust_d";

    public static final String KEY_PREF_PID_LOW_RUDDER_P = "pref_pid_low_rudder_p";
    public static final String KEY_PREF_PID_LOW_RUDDER_I= "pref_pid_low_rudder_i";
    public static final String KEY_PREF_PID_LOW_RUDDER_D= "pref_pid_low_rudder_d";

    public static final String KEY_PREF_SENSOR1 = "pref_sensor_one";
    public static final String KEY_PREF_SENSOR2 = "pref_sensor_two";
    public static final String KEY_PREF_SENSOR3 = "pref_sensor_three";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        Preference pref = findPreference("pref_category");
        //SharedPreferences sharedpref = pref.getSharedPreferences();
        SharedPreferences sharedpref = PreferenceManager.getDefaultSharedPreferences(this);
        //System.out.println("sharedpref: " + sharedpref.getString("pref_pid_low_rudder_p","0.4"));
        Map<String, ?> listOfPref = sharedpref.getAll();
        for (Map.Entry<String, ?> entry : listOfPref.entrySet())
        {
            System.out.println("key found : " + entry.getKey());
            Preference currentPref = findPreference(entry.getKey());
            if (currentPref instanceof EditTextPreference)
            {
                currentPref.setSummary(entry.getValue().toString());
                System.out.println("entry: " + entry.getValue().toString());
            }
        }

        /*
        final CheckBoxPreference save_location_checkbox =
                (CheckBoxPreference) findPreference("pref_save_location");
        save_location_checkbox.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
        {
            public boolean onPreferenceChange(Preference preference, Object newValue)
            {
                return true;
            }
        });
        */
    }
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        Preference pref = findPreference(key);
        if (key.equalsIgnoreCase(KEY_PREF_SAVE_MAP))
        {
            boolean state = sharedPreferences.getBoolean(key, true);
            CheckBoxPreference cbpref = (CheckBoxPreference) pref;
            cbpref.setChecked(state);
        }
        else
        {
            pref.setSummary(sharedPreferences.getString(key,"default"));
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

}

