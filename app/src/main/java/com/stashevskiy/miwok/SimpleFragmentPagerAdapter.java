package com.stashevskiy.miwok;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

    // Конструктор SimpleFragmentPagerAdapter
    public SimpleFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    // Количество возвращаемых Fragment
    @Override
    public int getCount() {
        return 4;
    }

    // Возвращаем Fragment для каждой категории
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new NumbersFragment();
            case 1:
                return new FamilyFragment();
            case 2:
                return new ColorsFragment();
            case 3:
                return new PhrasesFragment();
            default:
                return null;
        }
    }

    // Устанавливаем подписи вкладок
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Numbers";
            case 1:
                return "Family";
            case 2:
                return "Colors";
            case 3:
                return "Phrases";
            default:
                return null;
        }
    }
}
