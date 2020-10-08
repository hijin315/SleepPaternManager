package com.example.sleeppaternmanager;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import me.relex.circleindicator.CircleIndicator;
import me.relex.circleindicator.Config;

public class MainFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_frame, container, false);
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        AppCompatImageButton mainBtn1, mainBtn2;
        mainBtn1 = (AppCompatImageButton)view.findViewById(R.id.mainBtn1);
        mainBtn2 = (AppCompatImageButton)view.findViewById(R.id.mainBtn2);
        mainBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Introduce.class);
                startActivity(intent);
            }
        });
        mainBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Notice.class);
                startActivity(intent);
            }
        });
        int indicatorWidth = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10,
                getResources().getDisplayMetrics()) + 0.5f);
        int indicatorHeight = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4,
                getResources().getDisplayMetrics()) + 0.5f);
        int indicatorMargin = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6,
                getResources().getDisplayMetrics()) + 0.5f);

        ViewPager viewpager = view.findViewById(R.id.mainViewPager);

        CircleIndicator indicator = view.findViewById(R.id.mainIndicator);
        Config config = new Config.Builder().width(indicatorWidth)
                .height(indicatorHeight)
                .margin(indicatorMargin)
                .animator(R.animator.indicator_animation)
                .animatorReverse(R.animator.indicator_animator_reverse)
                .drawable(R.drawable.black_radius_square)
                .build();
        indicator.initialize(config);

        viewpager.setAdapter(new SamplePagerAdapter());
        indicator.setViewPager(viewpager);
    }
}