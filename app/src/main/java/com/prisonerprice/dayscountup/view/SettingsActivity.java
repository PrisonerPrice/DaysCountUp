package com.prisonerprice.dayscountup.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.prisonerprice.dayscountup.R;
import com.prisonerprice.dayscountup.middleware.DataExchanger;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            Preference clearAllDataPreference = findPreference("clear_all");
            clearAllDataPreference.setOnPreferenceClickListener(preference -> {
                //DataExchanger.getInstance(getContext()).truncateDatabase();
                new MaterialAlertDialogBuilder(getContext())
                        .setTitle(getResources().getString(R.string.truncate_alert_title))
                        .setMessage(getResources().getString(R.string.truncate_alert_message))
                        .setNegativeButton(getResources().getString(R.string.truncate_alert_cancel), new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setPositiveButton(getResources().getString(R.string.truncate_alert_accept), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DataExchanger.getInstance(getContext()).truncateDatabase();
                            }
                        })
                        .show();
                return true;
            });

        }

    }
}