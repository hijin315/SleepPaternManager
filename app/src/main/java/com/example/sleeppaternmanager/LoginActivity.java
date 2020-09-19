package com.example.sleeppaternmanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

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

import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LoginActivity extends Activity {
    private String userid, userpw;
    boolean idCheck, pwCheck;
    boolean loginsaveFlag;
    boolean noSolution;
    Button btn_login, btn_signup;
    long mNow;
    Date mDate;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyyMMdd");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_login);

        final EditText idEt = (EditText) findViewById(R.id.edittext_id);
        final EditText pwEt = (EditText) findViewById(R.id.edittext_password);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_signup = (Button) findViewById(R.id.btn_signup);
        CheckBox loginsaveCb = findViewById(R.id.loginstatuscb);
        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);

        loginsaveFlag = false;

        loginsaveCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                loginsaveFlag = isChecked;
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userid = idEt.getText().toString();
                userpw = EncryptSHA512.EncryptSHA512(pwEt.getText().toString());
                idCheck = false;
                pwCheck = false;

                FirebaseFirestore userDB = FirebaseFirestore.getInstance();
                CollectionReference users = userDB.collection("users");
                Query idQuery = users.whereEqualTo("id", userid);


                idQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot idDoc : task.getResult()) {
                                if(userid.equals(idDoc.get("id").toString())) {
                                    idCheck = true;
                                    Query pwQuery = FirebaseFirestore.getInstance().collection("users").whereEqualTo("id", userid).whereEqualTo("password", userpw);
                                    pwQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if(task.isSuccessful()) {
                                                for(QueryDocumentSnapshot pwDoc : task.getResult()) {
                                                    if(userpw.equals(pwDoc.get("password").toString())) {
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
                                            if(idCheck && pwCheck) {
                                                SharedPreferences userinfo = getSharedPreferences("SavedAccountStatusPreference", MODE_PRIVATE);
                                                SharedPreferences.Editor editor = userinfo.edit();
                                                editor.putBoolean("AutoLoginFlag", loginsaveFlag);
                                                editor.putString("Identification", userid);
                                                editor.putString("IdentificationSecurityKey", userpw);
                                                editor.commit();

                                                getSolutionDB dbtask = new getSolutionDB();
                                                dbtask.execute(userid);
                                            } else if(idCheck && !pwCheck) {
                                                alertBuilder.setTitle("로그인 실패").setMessage("비밀번호가 다릅니다.");
                                                alertBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                    }
                                                });
                                                AlertDialog alert = alertBuilder.create();
                                                alert.show();
                                            } else {
                                                alertBuilder.setTitle("로그인 실패").setMessage("존재하지 않는 계정입니다.");
                                                alertBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                    }
                                                });
                                                AlertDialog alert = alertBuilder.create();
                                                alert.show();
                                            }
                                        }
                                    });
                                } else {
                                    idCheck = false;
                                }
                            }
                        }
                    }
                }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(!idCheck) {
                            alertBuilder.setTitle("로그인 실패").setMessage("존재하지 않는 계정입니다.");
                            alertBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            AlertDialog alert = alertBuilder.create();
                            alert.show();
                        }
                    }
                });
            }
        });


        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent joinIntent = new Intent(getApplicationContext(), JoinActivity.class);
                startActivity(joinIntent);
            }
        });

    }

    private void successGetDBTask() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }


    public class getSolutionDB extends AsyncTask<String, Integer, ArrayList<SolutionItem>> {
        ProgressDialog asyncDialog = new ProgressDialog(LoginActivity.this);
        ArrayList<SolutionItem> array_solution = new ArrayList<>();
        int listCount;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("잠시만 기다려주세요..");
            asyncDialog.show();
        }

        @Override
        protected ArrayList<SolutionItem> doInBackground(String... strings) {
            String name = strings[0];
            FirebaseFirestore db;
            DocumentReference docRef;
            mNow = System.currentTimeMillis();
            mDate = new Date(mNow);
            String todayDate = mFormat.format(mDate);

            db = FirebaseFirestore.getInstance();
            docRef = db.collection("userdata").document(name).collection(todayDate).document("solutions");
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            noSolution = false;
                            listCount = Integer.parseInt(document.get("listCount").toString());
                            for (int i = 0; i < listCount; i++) {
                                String list = document.get("list" + i).toString();
                                Log.d("jkjk", list);
                                String[] solNcheck = list.split(",");
                                String solution = solNcheck[0].split("=")[1];
                                String isRequiredvalue = solNcheck[1].split("=")[1];
                                String check = solNcheck[2].split("=")[1].replace("}","");
                                array_solution.add(new SolutionItem(solution, Boolean.parseBoolean(check),Boolean.parseBoolean(isRequiredvalue)));
                            }
                        } else {
                            //솔루션 없을때
                            noSolution = true;
                        }
                    } else {
                        //db 못 찾을 때
                        noSolution = true;
                    }
                }
            }).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    SolutionFragment.setSolutionData(array_solution);
                    successGetDBTask();
                    asyncDialog.dismiss();
                }
            });
            return array_solution;
        }


        @Override
        protected void onPostExecute(ArrayList<SolutionItem> solutionItems) {
            super.onPostExecute(solutionItems);
        }
    }
}
