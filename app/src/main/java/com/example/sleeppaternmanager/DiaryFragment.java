package com.example.sleeppaternmanager;

import android.app.AlertDialog;
import android.content.Context;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
        //findViewById 한번에 여러개
        final EditText[] diary = new EditText[6];

        for(int i=0;i<diary.length;i++){
            String editId = "diary"+(i+1);
            diary[i] = view.findViewById(getResources().getIdentifier(editId,"id",getActivity().getPackageName()));
        }

        diarySubmit=(Button)view.findViewById(R.id.diarySubmit);
        diarySubmit.setOnClickListener(new View.OnClickListener() {
            private FirebaseFirestore db;
            private DocumentReference docRef;

            @Override
            public void onClick(View view) {

                mNow = System.currentTimeMillis();
                mDate = new Date(mNow);
                String todayDate = mFormat.format(mDate);

                double diaryTime = Double.parseDouble(diary[1].getText().toString());
                double diaryTime2 = Double.parseDouble(diary[2].getText().toString());
                diaryTime2 *= diaryTime;

                Map<String, Object> diarylist = new HashMap<>();
                diarylist.put("diary1", Double.parseDouble(diary[0].getText().toString()));
                diarylist.put("diary2", diaryTime2);
                diarylist.put("diary3", Double.parseDouble(diary[3].getText().toString()));
                diarylist.put("diary4", Double.parseDouble(diary[4].getText().toString()));
                diarylist.put("diary5", Double.parseDouble(diary[5].getText().toString()));

                db = FirebaseFirestore.getInstance();
                docRef = db.collection("userdata").document(userid).collection(todayDate).document("diary");
                docRef.update(diarylist).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("hjhj", "DocumentSnapshot added with ID: " + docRef.getId());
                        Toast.makeText(getContext(),"제출완료되었습니다.",Toast.LENGTH_SHORT).show();
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("hjhj", "Error adding document", e);
                            }
                        });

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

