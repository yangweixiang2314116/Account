package com.example.account;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.account.Constants;

public class AccountRegisterFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(Constants.TAG, "-----------onCreate--------");
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View content = inflater.inflate(R.layout.register_fragment_layout, container, false);
        return content;
    }
}