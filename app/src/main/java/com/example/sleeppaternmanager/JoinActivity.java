package com.example.sleeppaternmanager;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class JoinActivity extends Activity {
    EditText idEt, pwEt, pwCfEt, nameEt, telEt;
    TextView nameTv, idTv, pwTv, pwCfTv;
    Button joinBtn, backBtn;
    boolean pass;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        idEt = findViewById(R.id.joinIdEt);
        pwEt = findViewById(R.id.joinPwEt);
        pwCfEt = findViewById(R.id.joinPwCfEt);
        nameEt = findViewById(R.id.joinNameEt);
        telEt = findViewById(R.id.joinTelEt);
        nameTv = findViewById(R.id.joinNameTv);
        idTv = findViewById(R.id.joinIdTv);
        pwTv = findViewById(R.id.joinPwTv);
        pwCfTv = findViewById(R.id.joinPwCfTv);
        joinBtn = findViewById(R.id.joinJoinBtn);
        backBtn = findViewById(R.id.joinBackBtn);

        final AlertDialog.Builder alrB = new AlertDialog.Builder(this);

        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pass = true;
                String userid = idEt.getText().toString();
                String userpw = pwEt.getText().toString();
                String pwcheck = pwCfEt.getText().toString();

                if(!userpw.equals(pwcheck)) {
                    pwCfEt.setTextColor(Color.RED);
                    pwCfEt.setHintTextColor(Color.RED);
                    pwCfTv.setText("⛔ 비밀번호가 다릅니다.");
                    pass = false;
                }
                if(nameEt.getText().toString().replace(" ", "").equals("")) {
                    nameEt.setHintTextColor(Color.RED);
                    nameTv.setText("⛔ 이름을 입력해주세요");
                    pass = false;
                }
                if(idEt.getText().toString().replace(" ", "").equals("")) {
                    idEt.setHintTextColor(Color.RED);
                    idTv.setText("⛔ 아이디를 입력해주세요.");
                    pass = false;
                }
                if(pwEt.getText().toString().replace(" ", "").equals("")) {
                    pwEt.setHintTextColor(Color.RED);
                    pwTv.setText("⛔ 비밀번호를 입력해주세요.");
                    pass = false;
                }
                if(pwCfEt.getText().toString().replace(" ", "").equals("")) {
                    pwCfEt.setHintTextColor(Color.RED);
                    pwCfTv.setText("⛔ 비밀번호를 입력해주세요.");
                    pass = false;
                }

                if(pass) {
                    final Map<String, Object> newUser = new HashMap<>();
                    newUser.put("id", userid);
                    newUser.put("image", "NO");
                    newUser.put("name", nameEt.getText().toString());
                    String enc = EncryptSHA512.EncryptSHA512(userpw);
                    newUser.put("password", enc);
                    newUser.put("phone", telEt.getText().toString());

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    final CollectionReference collRef = db.collection("users");
                    Query idQuery = collRef.whereEqualTo("id", userid);
                    idQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if(queryDocumentSnapshots.isEmpty()) {
                                collRef.add(newUser).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        alrB.setTitle("회원가입 완료!").setMessage(nameEt.getText().toString() + "님 환영합니다.");
                                        alrB.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                finish();
                                            }
                                        });
                                        AlertDialog alert = alrB.create();
                                        alert.show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        alrB.setTitle("회원가입 실패").setMessage("서버에 문제가 있습니다. 다시 시도해 보세요");
                                        alrB.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        });
                                        AlertDialog alert = alrB.create();
                                        alert.show();
                                    }
                                });
                            } else {
                                idEt.setTextColor(Color.RED);
                                idTv.setText("⛔ 이미 존재하는 아이디 입니다.");
                            }
                        }
                    });
                }
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void onJoinEditTextClick(View view) {
        EditText et = findViewById(view.getId());
        switch (et.getId()) {
            case R.id.joinNameEt :
                et.setHint("이름");
                nameTv.setText("");
                break;
            case R.id.joinIdEt :
                et.setHint("아이디");
                idTv.setText("");
                break;
            case R.id.joinPwEt :
                et.setHint("비밀번호");
                pwTv.setText("");
                break;
            case R.id.joinPwCfEt :
                et.setHint("비밀번호 확인");
                pwCfTv.setText("");
                break;
        }
        et.setHintTextColor(Color.GRAY);
        et.setTextColor(Color.BLACK);
    }
}
