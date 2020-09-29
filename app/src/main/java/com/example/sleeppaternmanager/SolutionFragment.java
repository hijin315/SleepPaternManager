package com.example.sleeppaternmanager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SolutionFragment extends Fragment {
    ProgressDialog dialog;
    AlertDialog.Builder alertBuilder;
    SwipeRefreshLayout swipeRefreshLayout;
    ListView listview;
    static ArrayList<SolutionItem> array_solution = new ArrayList<>();
    SolutionAdapter adapter;
    static boolean noSolution;
    boolean isCreated = false;
    SharedPreferences userinfo;
    String userid;
    long mNow;
    Date mDate;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyyMMdd");

    private FirebaseFirestore db;
    private DocumentReference docRef;

    protected void getSolutionData() {
        array_solution.clear();
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        String todayDate = mFormat.format(mDate);
        this.db = FirebaseFirestore.getInstance();
        this.docRef = db.collection("userdata").document(userid).collection(todayDate).document("solutions");
        this.docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()) {
                        noSolution = false;
                        int listCount = Integer.parseInt(document.get("listCount").toString());
                            for(int i=0; i < listCount; i++) {
                            String list = document.get("list" + i).toString();
                            String[] solNcheck = list.split(",");
                            String solution = solNcheck[0].split("=")[1];
                            String isRequiredvalue = solNcheck[1].split("=")[1];
                            String check = solNcheck[2].split("=")[1].replace("}","");

                            array_solution.add(new SolutionItem(solution, Boolean.parseBoolean(check),Boolean.parseBoolean(isRequiredvalue)));
                        }

                    } else {
                        //솔루션 없을 때
                        noSolution = true;
                    }
                } else {
                    //db 못찾을 때
                    noSolution = true;
                }
            }
        }).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(noSolution || array_solution.isEmpty()) {
                    array_solution.add(new SolutionItem("$NO_DATA$", false, false));
                    noSolution = true;
                }
                if(isCreated) {
                    if(noSolution) {
                        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        });
                    } else {
                        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(final AdapterView<?> parent, final View view, final int position, long id) {
                                alertBuilder.setTitle("제출").setMessage("제출하시겠습니까?");
                                alertBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        setDBCheck(position, array_solution.get(position).isCheck());
                                    }
                                });
                                alertBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                AlertDialog alert = alertBuilder.create();
                                alert.show();
                            }
                        });
                    }
                }
                adapter = new SolutionAdapter(getActivity().getApplicationContext(), array_solution);
                adapter.notifyDataSetChanged();
                listview.setAdapter(adapter);
                swipeRefreshLayout.setRefreshing(false);
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        userinfo = context.getSharedPreferences("SavedAccountStatusPreference", Context.MODE_PRIVATE);
        userid = userinfo.getString("Identification", "PREF_NONE");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.solution_frame, container, false);

        dialog = new ProgressDialog(view.getContext());
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        alertBuilder = new AlertDialog.Builder(getContext());
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);

        if(noSolution || array_solution.isEmpty()) {
            array_solution.add(new SolutionItem("$NO_DATA$", false, false));
            noSolution = true;
        }
        adapter = new SolutionAdapter(this.getActivity().getApplicationContext(), array_solution);
        adapter.notifyDataSetChanged();
        listview = view.findViewById(R.id.solution_list);
        listview.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getSolutionData();
            }
        });

        if(noSolution) {
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                }
            });
        } else {
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(final AdapterView<?> parent, final View view, final int position, long id) {
                    alertBuilder.setTitle("제출").setMessage("제출하시겠습니까?");
                    alertBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setDBCheck(position, array_solution.get(position).isCheck());
                        }
                    });
                    alertBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                }
            });
        }
        isCreated = true;
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void setDBCheck(final int index, final boolean currentCheck) {
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        String todayDate = mFormat.format(mDate);
        this.db = FirebaseFirestore.getInstance();
        this.docRef = db.collection("userdata").document(userid).collection(todayDate).document("solutions");
        docRef.update("list"+index+".check", true).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                if(currentCheck==true) {
                    alertBuilder.setTitle("").setMessage("이미 제출하였습니다. 취소하시겠습니까?");
                    alertBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            docRef.update("list"+index+".check",false);
                            getSolutionData();
                        }
                    });
                    alertBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog alert2 = alertBuilder.create();
                    alert2.show();
                }
                else {
                    dialog.setMessage("제출중...");
                    dialog.show();
                }
                getSolutionData();
            }
        });
    }

    public static void setSolutionData(ArrayList<SolutionItem> data) {
        array_solution = data;
    }
}
