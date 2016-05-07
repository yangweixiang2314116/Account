package com.example.account;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.RadioGroup;

import java.util.ArrayList;

public class AccountLoginRegisterActivity extends ActionBarActivity
{
    private RadioGroup m_RadioGroup;
    private ViewPager m_ViewPager;
    private ArrayList<Fragment> m_FragmentList;
    private int m_CurrIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_account_login_register);

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        m_FragmentList = new ArrayList<Fragment>();
        m_FragmentList.add(new AccountRegisterFragment());
        m_FragmentList.add(new AccountLoginFragment());

        m_ViewPager = (ViewPager)findViewById(R.id.content);

        m_ViewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), m_FragmentList));
        m_ViewPager.setCurrentItem(Constants.ACCOUNT_TAB_INDEX_REGISTER);
        m_ViewPager.addOnPageChangeListener(new MyOnPageChangeListener());


        setup_tab();
    }


    private boolean setup_tab()
    {
        Log.i(Constants.TAG, "-----------setup_tab--------");
        m_RadioGroup = (RadioGroup) findViewById(R.id.rg_tab);

        m_RadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.i(Constants.TAG, "onCheckedChanged  ---" + checkedId);
                switch (checkedId) {
                    case R.id.radioRegister:
                        m_ViewPager.setCurrentItem(Constants.ACCOUNT_TAB_INDEX_REGISTER);
                        break;
                    case R.id.radioLogin:
                        m_ViewPager.setCurrentItem(Constants.ACCOUNT_TAB_INDEX_LOGIN);
                        break;
                    default:
                        Log.i(Constants.TAG, "do not find this check id" + checkedId);
                        break;
                }

            }
        });
        return true;
    }


    public class MyFragmentPagerAdapter extends FragmentPagerAdapter
    {
        ArrayList<Fragment> list;
        public MyFragmentPagerAdapter(FragmentManager fm,ArrayList<Fragment> list) {
            super(fm);
            this.list = list;

        }


        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Fragment getItem(int arg0) {
            return list.get(arg0);
        }

    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageSelected(int arg0) {
            // TODO Auto-generated method stub
            m_CurrIndex = arg0;
            Log.i(Constants.TAG, "this check tab index ----"+ m_CurrIndex );

            m_RadioGroup = (RadioGroup) findViewById(R.id.rg_tab);

            switch(m_CurrIndex)
            {
                case Constants.ACCOUNT_TAB_INDEX_REGISTER:
                    m_RadioGroup.check(R.id.radioRegister);
                    break;
                case Constants.ACCOUNT_TAB_INDEX_LOGIN:
                    m_RadioGroup.check(R.id.radioLogin);
                    break;
                default:
                    Log.i(Constants.TAG, "do not find this check id"+ m_CurrIndex );
                    break;
            }

        }
    }

}