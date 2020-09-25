package com.example.sleeppaternmanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import android.annotation.SuppressLint;

import android.content.Intent;

import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firestore.v1.Cursor;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

public class CalendarFragment extends Fragment {
    String time,kcal,menu;
    Cursor cursor;
    Date today = new Date();
    AlertDialog.Builder alertBuilder;
    MaterialCalendarView materialCalendarView;
    TextView calendarList;
    SharedPreferences userinfo;
    String userid;
    String[] reservedDates;
    ArrayList<String> reDates;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        reDates = new ArrayList<>();
        userinfo = context.getSharedPreferences("SavedAccountStatusPreference", Context.MODE_PRIVATE);
        userid = userinfo.getString("Identification", "PREF_NONE");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calendar_frame, container, false);
        calendarList = (TextView)view.findViewById(R.id.calendarList);
        alertBuilder = new AlertDialog.Builder(getContext());
        materialCalendarView = (MaterialCalendarView) view.findViewById(R.id.calendarView);

        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(2017, 1,1))
                .setMaximumDate(CalendarDay.from(2030, 11, 31))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();
        materialCalendarView.addDecorators(new SundayDecorator(), new SaturdayDecorator(),new OneDayDecorator());


        final FirebaseFirestore userDB = FirebaseFirestore.getInstance();
        DocumentReference userdata = userDB.collection("userdata").document(userid);
        userdata.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    String str = doc.get("ReserveDate").toString();
                    str = str.substring(1, str.length()-1);
                    reservedDates = str.split(", ");
                    for(String date : reservedDates)
                        reDates.add(date);
                } else{
                    Log.d("jkjk", "reserveDate Task failed");
                }
            }
        }).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String cdList = "";

                for(String date : reservedDates) {
                    cdList += date;
                    cdList += "\n";
                }

                calendarList.setText(cdList);
                new ApiSimulator(reservedDates).executeOnExecutor(Executors.newSingleThreadExecutor());

                materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
                    @Override
                    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                        final CalendarDay fdate = date;
                        int Year = date.getYear();
                        int Month = date.getMonth() + 1;
                        int Day = date.getDay();

                        final String shot_Day = Year + "," + Month + "," + Day;

                        materialCalendarView.clearSelection();

                        if(reDates.contains(shot_Day)) {
                            alertBuilder.setTitle("예약 취소").setMessage(Month + "월 " + Day + "일 예약 취소 하시겠습니까?");
                            alertBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    reDates.remove(shot_Day);
                                    DocumentReference docRef = userDB.collection("userdata").document(userid);
                                    docRef.update("ReserveDate", reDates);
                                    materialCalendarView.removeDecorators();
                                    materialCalendarView.addDecorators(new SundayDecorator(), new SaturdayDecorator(),new OneDayDecorator());
                                    materialCalendarView.addDecorator(new EventDecorator(Color.RED, reDates, getActivity()));
                                    Toast.makeText(getActivity(), "취소되었습니다.", Toast.LENGTH_SHORT).show();
                                    String cdList = "";
                                    for(String s : reDates) {
                                        cdList += s;
                                        cdList += "\n";
                                    }
                                    calendarList.setText(cdList);
                                }
                            });
                            alertBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            AlertDialog alert = alertBuilder.create();
                            alert.show();
                        } else {
                            alertBuilder.setTitle("예약").setMessage(Month + "월 " + Day + "일에 예약 하시겠습니까?");
                            alertBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    DocumentReference docRef = userDB.collection("userdata").document(userid);
                                    reDates.add(shot_Day);
                                    docRef.update("ReserveDate", reDates);
                                    materialCalendarView.addDecorator(new EventDecorator(Color.RED, fdate, getActivity()));
                                    Toast.makeText(getActivity(), "예약되었습니다.", Toast.LENGTH_SHORT).show();
                                    String cdList = "";
                                    for(String s : reDates) {
                                        cdList += s;
                                        cdList += "\n";
                                    }
                                    calendarList.setText(cdList);
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
                    }
                });

            }
        });

        return view;
    }
    private class ApiSimulator extends AsyncTask<Void, Void, List<CalendarDay>> {

        String[] Time_Result;

        ApiSimulator(String[] Time_Result){
            this.Time_Result = Time_Result;
        }

        @Override
        protected List<CalendarDay> doInBackground(@NonNull Void... voids) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Calendar calendar = Calendar.getInstance();
            ArrayList<CalendarDay> dates = new ArrayList<>();

            /*특정날짜 달력에 점표시해주는곳*/
            /*월은 0이 1월 년,일은 그대로*/
            //string 문자열인 Time_Result 을 받아와서 ,를 기준으로짜르고 string을 int 로 변환
            for(int i = 0 ; i < Time_Result.length ; i ++){

                String[] time = Time_Result[i].split(",");
                int year = Integer.parseInt(time[0]);
                int month = Integer.parseInt(time[1]);
                int dayy = Integer.parseInt(time[2]);
                calendar.set(year,month-1,dayy);
                CalendarDay day = CalendarDay.from(calendar);

                dates.add(day);
            }

            return dates;
        }

        @Override
        protected void onPostExecute(@NonNull List<CalendarDay> calendarDays) {
            super.onPostExecute(calendarDays);

            if (getActivity().isFinishing()) {
                return;
            }

            materialCalendarView.addDecorator(new EventDecorator(Color.RED, calendarDays,getActivity()));
        }
    }
}


