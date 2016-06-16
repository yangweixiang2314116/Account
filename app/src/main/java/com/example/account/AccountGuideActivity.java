package com.example.account;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator;


public class AccountGuideActivity extends AppCompatActivity {

    private ViewPager mViewpager = null;
    private me.relex.circleindicator.CircleIndicator mIndicator = null;
    private AccountGuidePagerAdapter mPagerAdapter = null;
    protected ArrayList<Fragment> mFragmentListDataSource = new ArrayList<Fragment>();

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_account_guide);

        mFragmentListDataSource.add(new AccountGuideStyleFragment());
        mFragmentListDataSource.add(new AccountGuideAreaFragment());
        mFragmentListDataSource.add(new AccountGuideBudgetFragment());
        mPagerAdapter = new AccountGuidePagerAdapter(getSupportFragmentManager(), mFragmentListDataSource);
        mViewpager = (ViewPager) findViewById(R.id.viewpager);
        mIndicator = (CircleIndicator) findViewById(R.id.indicator);
        mViewpager.setAdapter(mPagerAdapter);
        mIndicator.setViewPager(mViewpager);
        mPagerAdapter.registerDataSetObserver(mIndicator.getDataSetObserver());
    }

}
