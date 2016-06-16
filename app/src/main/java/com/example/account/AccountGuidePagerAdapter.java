package com.example.account;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import java.util.List;

public class AccountGuidePagerAdapter extends FragmentPagerAdapter{
    private List<Fragment> list;

    public AccountGuidePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public AccountGuidePagerAdapter(FragmentManager fm, List<Fragment> list) {
        super(fm);
        this.list = list;
    }

    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }
}