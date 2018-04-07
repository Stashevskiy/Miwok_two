package com.stashevskiy.miwok;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Устанавливаем в качестве содержимого макет activity_main.xml
        setContentView(R.layout.activity_main);

        // Находим ViewPager, который позволит пользователю переключаться между Fragments
        ViewPager viewPager = findViewById(R.id.viewPager);

        // Создаем адаптер, который знает, какой фрагмент должен отображаться на каждой странице
        SimpleFragmentPagerAdapter adapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager());

        // Установка адаптера для ViewPager
        viewPager.setAdapter(adapter);

        // Находим TabLayout (важно добавить в Gradle 'com.android.support:design:26.1.0')
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        // Устанавливаем ему ViewPager
        tabLayout.setupWithViewPager(viewPager);
    }
}
