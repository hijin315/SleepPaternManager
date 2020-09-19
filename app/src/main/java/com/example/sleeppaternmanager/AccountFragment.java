package com.example.sleeppaternmanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AccountFragment extends Fragment {
    Button logoutBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.account_frame, container, false);
        logoutBtn = view.findViewById(R.id.account_logoutBtn);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences userinfo = getContext().getSharedPreferences("SavedAccountStatusPreference", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = userinfo.edit();
                editor.putBoolean("AutoLoginFlag", false);
                editor.putString("Identification", "PREF_NONE");
                editor.putString("IdentificationSecurityKey", "PREF_NONE");
                editor.commit();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        return view;
    }
}
