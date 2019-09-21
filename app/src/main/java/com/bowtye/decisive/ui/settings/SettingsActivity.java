package com.bowtye.decisive.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.bowtye.decisive.R;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

public class SettingsActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            Preference license = findPreference(getString(R.string.pref_key_open_source_license));
            Preference resetTutorials = findPreference(getString(R.string.pref_key_reset_tutorials));
            Objects.requireNonNull(license).setOnPreferenceClickListener(preference -> {
                startActivity(new Intent(getActivity(), OssLicensesMenuActivity.class));
                OssLicensesMenuActivity.setActivityTitle(getString(R.string.title_activity_license));
                return true;
            });
            Objects.requireNonNull(resetTutorials).setOnPreferenceClickListener(preference -> {
                Toast.makeText(getContext(), getString(R.string.dialog_reset_tutorials), Toast.LENGTH_SHORT).show();
                MaterialShowcaseView.resetAll(Objects.requireNonNull(getActivity()));
                return true;
            });
            
        }
    }
}