package com.example.sleeppaternmanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DiaryFragment extends Fragment {
    long mNow;
    Date mDate;
    SharedPreferences userinfo;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyyMMdd");
    Button diarySubmit;
    String userid;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.diary_frame, container, false);
        diarySubmit=(Button)view.findViewById(R.id.diarySubmit);

        diarySubmit.setOnClickListener(new View.OnClickListener() {
        private FirebaseFirestore db;
        private DocumentReference docRef;
        @Override
            public void onClick(View view) {
                mNow = System.currentTimeMillis();
                mDate = new Date(mNow);
                String todayDate = mFormat.format(mDate);
                this.db = FirebaseFirestore.getInstance();
                this.docRef = db.collection("userdata").document(userid).collection(todayDate).document("solutions");
            }
        });

        return view;
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        userinfo = context.getSharedPreferences("SavedAccountStatusPreference", Context.MODE_PRIVATE);
        userid = userinfo.getString("Identification", "PREF_NONE");
    }

}

