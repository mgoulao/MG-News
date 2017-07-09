package com.mgoulao.mgnews;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.DatePicker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by msilv on 7/9/2017.
 */

public class FiltersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_filters);


    }

    public static class NewsPreferenceFragment extends PreferenceFragment
            implements Preference.OnPreferenceChangeListener, DatePickerDialog.OnDateSetListener {

        SharedPreferences preferences;
        Calendar mCalendar;
        private int mCurrentYear;
        private int mCurrentMonth;
        private int mCurrentDayofMonth;
        private String startDate;

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.xml);

            preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

            Preference orderByPref = findPreference(getString(R.string.filters_order_by_key));
            bindPreferenceSummaryToValue(orderByPref);

            Preference fromDatePref = findPreference(getString(R.string.filters_from_date_key));

            // Get today's Date
            mCalendar = Calendar.getInstance();
            mCurrentYear = mCalendar.get(Calendar.YEAR);
            mCurrentMonth = mCalendar.get(Calendar.MONTH);
            mCurrentDayofMonth = mCalendar.get(Calendar.DAY_OF_MONTH);
            startDate = dateFormat.format(mCalendar.getTime());


            mCalendar.add(Calendar.DATE, -1);
            startDate = dateFormat.format(mCalendar.getTime());


            if (preferences.getLong(getString(R.string.filters_from_date_key), 0) == 0) {
                fromDatePref.setSummary(startDate);
            } else {
                long longPrefDate = preferences.getLong(
                        getString(R.string.filters_from_date_key), 0
                );

                Date dateObject = new Date(longPrefDate);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dateObject);
                fromDatePref.setSummary(dateFormat.format(calendar.getTime()));
            }

            fromDatePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(final Preference preference) {

                    DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {

                            String datePickedFormatted = "";

                            // Set picked date on calendar
                            mCalendar = Calendar.getInstance();
                            mCalendar.set(Calendar.YEAR, year);
                            mCalendar.set(Calendar.MONTH, monthOfYear);
                            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            Date datePicked = mCalendar.getTime();

                            datePickedFormatted = dateFormat.format(mCalendar.getTime());

                            preference.setSummary(datePickedFormatted);
                            preferences.edit().putLong(getString(R.string.filters_from_date_key), datePicked.getTime()).apply();
                        }
                    }, mCurrentYear, mCurrentMonth, mCurrentDayofMonth);

                    datePickerDialog.show();
                    return true;
                }
            });
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            preference.setSummary(stringValue);

            return true;
        }

        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String preferenceString = preferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, preferenceString);
        }


        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            Log.i("MG", "year: " + year + " month: " + month + " day: " + day);
        }

    }

}
