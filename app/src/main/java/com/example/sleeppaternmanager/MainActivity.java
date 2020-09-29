package com.example.sleeppaternmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    MainFragment mainFr;
    SolutionFragment solutionFr;
    DiaryFragment chatFr;
    CalendarFragment calendarFr;
    AccountFragment accountFr;
    private FragmentManager frManager = getSupportFragmentManager();
    private FragmentTransaction frTransaction = frManager.beginTransaction();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainFr = new MainFragment();
        solutionFr = new SolutionFragment();
        chatFr = new DiaryFragment();
        calendarFr = new CalendarFragment();
        accountFr = new AccountFragment();

        frManager.beginTransaction().add(R.id.frame_layout, mainFr, "메인").commit();
        frManager.beginTransaction().add(R.id.frame_layout, solutionFr, "솔루션").commit();
        frManager.beginTransaction().add(R.id.frame_layout, chatFr, "채팅").commit();
        frManager.beginTransaction().add(R.id.frame_layout, calendarFr, "일정").commit();
        frManager.beginTransaction().add(R.id.frame_layout, accountFr, "설정").commit();

        frManager.beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out).commit();
        //frTransaction.hide(solutionFr).commit();
        //frTransaction.replace(R.id.frame_layout, mainFr).commit();
        //frTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);

        frManager.beginTransaction().hide(solutionFr).commit();
        frManager.beginTransaction().hide(chatFr).commit();
        frManager.beginTransaction().hide(calendarFr).commit();
        frManager.beginTransaction().hide(accountFr).commit();
        frManager.beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out).show(mainFr).commit();


        BottomNavigationView bottomNavView = findViewById(R.id.bottom_nav_view);
        bottomNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                //FragmentTransaction tr = frManager.beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
                switch (menuItem.getItemId()) {
                    case R.id.nav_main :
                        frManager.beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out).show(mainFr).commit();
                        frManager.beginTransaction().hide(solutionFr).commit();
                        frManager.beginTransaction().hide(chatFr).commit();
                        frManager.beginTransaction().hide(calendarFr).commit();
                        frManager.beginTransaction().hide(accountFr).commit();
                        break;
                    case R.id.nav_solution :
                        frManager.beginTransaction().hide(mainFr).commit();
                        frManager.beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out).show(solutionFr).commit();
                        frManager.beginTransaction().hide(chatFr).commit();
                        frManager.beginTransaction().hide(calendarFr).commit();
                        frManager.beginTransaction().hide(accountFr).commit();
                        break;
                    case R.id.nav_chat :
                        frManager.beginTransaction().hide(mainFr).commit();
                        frManager.beginTransaction().hide(solutionFr).commit();
                        frManager.beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out).show(chatFr).commit();
                        frManager.beginTransaction().hide(calendarFr).commit();
                        frManager.beginTransaction().hide(accountFr).commit();
                        break;
                    case R.id.nav_calendar :
                        frManager.beginTransaction().hide(mainFr).commit();
                        frManager.beginTransaction().hide(solutionFr).commit();
                        frManager.beginTransaction().hide(chatFr).commit();
                        frManager.beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out).show(calendarFr).commit();
                        frManager.beginTransaction().hide(accountFr).commit();
                        break;
                    case R.id.nav_account :
                        frManager.beginTransaction().hide(mainFr).commit();
                        frManager.beginTransaction().hide(solutionFr).commit();
                        frManager.beginTransaction().hide(chatFr).commit();
                        frManager.beginTransaction().hide(calendarFr).commit();
                        frManager.beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out).show(accountFr).commit();
                        break;
                }
                return true;
            }
        });

    }
}