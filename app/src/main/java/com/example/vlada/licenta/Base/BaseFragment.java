package com.example.vlada.licenta.Base;


import android.support.v4.app.Fragment;

import com.example.vlada.licenta.Utils.RealmHelper;

public class BaseFragment extends Fragment {
    protected static RealmHelper mRealmHelper;

    public BaseFragment() {
        mRealmHelper = new RealmHelper();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
