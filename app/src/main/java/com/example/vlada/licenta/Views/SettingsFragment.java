package com.example.vlada.licenta.Views;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vlada.licenta.R;

public class SettingsFragment extends PreferenceFragment {

//    private final static String PREF_BODYWEIGHT_KEY = "bodyweight_key";
//    private final static String PREF_GENDER_KEY = "list_gender";
//

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        // Load the Preferences from the XML file
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.app_preferences);
//        int body_weight = getPreferenceManager().getSharedPreferences().getInt(PREF_BODYWEIGHT_KEY, 0);
//        boolean isMale = Objects.equals(getPreferenceManager().getSharedPreferences().getString(PREF_GENDER_KEY, "Male"), "Male");
//        ((HomeActivity) getActivity()).onDataPass(body_weight, isMale);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        view.setBackgroundColor(getResources().getColor(android.R.color.white));
        getActivity().setTitle("Settings");
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


}

