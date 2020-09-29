package com.example.sleeppaternmanager;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SplashActivity extends Activity {
    boolean idCheck, pwCheck;
    long mNow;
    Date mDate;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyyMMdd");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        SharedPreferences userinfo = getSharedPreferences("SavedAccountStatusPreference", MODE_PRIVATE);
        final String savedUserId = userinfo.getString("Identification", "PREF_NONE");
        boolean autologin = userinfo.getBoolean("AutoLoginFlag", false);
        Log.d("jkjk", "스플래쉬 savedUserID : " + savedUserId + "| autoflag : " + autologin);

        if(autologin) {
            final String savedUserPw = userinfo.getString("IdentificationSecurityKey", "PREF_NONE");
            if(savedUserId.equals("PREF_NONE")) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
            idCheck = false;
            pwCheck = false;

            FirebaseFirestore userDB = FirebaseFirestore.getInstance();
            CollectionReference users = userDB.collection("users");
            Query idQuery = users.whereEqualTo("id", savedUserId);
            idQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()) {
                        for(QueryDocumentSnapshot idDoc : task.getResult()) {
                            if(savedUserId.equals(idDoc.get("id").toString())) {
                                idCheck = true;
                                Query pwQuery = FirebaseFirestore.getInstance().collection("users").whereEqualTo("password", savedUserPw);
                                pwQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if(task.isSuccessful()) {
                                            for(QueryDocumentSnapshot pwDoc : task.getResult()) {
                                                if(savedUserPw.equals(pwDoc.get("password").toString())) {
                                                    pwCheck = true;
                                                } else {
                                                    pwCheck = false;
                                                }
                                            }
                                        }
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        final ArrayList<SolutionItem> array_solution = new ArrayList<>();
                                        if(idCheck && pwCheck) {
                                            mNow = System.currentTimeMillis();
                                            mDate = new Date(mNow);
                                            String todayDate = mFormat.format(mDate);
                                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                                            DocumentReference docRef = db.collection("userdata").document(savedUserId).collection(todayDate).document("solutions");

                                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if(task.isSuccessful()) {
                                                        DocumentSnapshot document = task.getResult();
                                                        if(document.exists()) {
                                                            int listCount = Integer.parseInt(document.get("listCount").toString());
                                                            for(int i = 0; i < listCount; i++) {
                                                                String list = document.get("list"+i).toString();
                                                                String[] solNcheck = list.split(",");
                                                                String solution = solNcheck[0].split("=")[1];
                                                                String isRequiredvalue = solNcheck[1].split("=")[1];
                                                                String check = solNcheck[2].split("=")[1].replace("}", "");
                                                                array_solution.add(new SolutionItem(solution, Boolean.parseBoolean(check),Boolean.parseBoolean(isRequiredvalue)));
                                                            }
                                                        } else {
                                                            // 솔루션 없을 때
                                                        }
                                                    } else {
                                                        // db 못 찾을 때
                                                    }
                                                }
                                            }).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    SolutionFragment.setSolutionData(array_solution);
                                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                                    CollectionReference userdata = db.collection("userdata");
                                                    userdata.document(savedUserId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                            if(!documentSnapshot.exists()) {
                                                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                                                Map<String, Object> data = new HashMap<>();
                                                                data.put("ReserveDate", Arrays.asList());
                                                                db.collection("userdata").document(savedUserId).set(data);
                                                            }
                                                        }
                                                    }).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    }
                                });
                            } else {
                                idCheck = false;
                            }
                        }
                    }
                }
            });
        } else {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
