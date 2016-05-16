package com.example.account;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.example.module.Account;

import java.util.ArrayList;


/**
 * Created by Administrator on 2016/5/16.
 */
public class AccountSortActivity  extends ActionBarActivity  implements AdapterView.OnItemClickListener{

    private Intent mIntent = null;
    private Context mContext = null;
    private int mSortType ;
    private ListView m_TotalAllAccountList = null;
    private AccountTotalDetailListAdapter m_DetailListAdapter = null;
    protected ArrayList<Account> mDetailListDataSource = new ArrayList<Account>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        setContentView(R.layout.activity_account_sort);

        mContext = this;
        mIntent = getIntent();

        Bundle bundle = getIntent().getExtras();

        if (bundle != null ) {
            mSortType = bundle.getInt("value");

            Log.i(Constants.TAG, "------AccountSortActivity----onCreate -sort---" + mSortType);
            if(Constants.ACCOUNT_SLIDEING_MENU_ASCEND == mSortType)
            {
                getSupportActionBar().setTitle(getString(R.string.account_sort_asc));
                new PrepareTask().execute();
            }
            else if (Constants.ACCOUNT_SLIDEING_MENU_DESCEND == mSortType)
            {
                getSupportActionBar().setTitle(getString(R.string.account_sort_desc));
                new PrepareTask().execute();
            }
            else
            {
                Log.i(Constants.TAG, "------AccountSortActivity----onCreate -- not support sort---" + mSortType);
                return ;
            }

        }
        else
        {
            Log.i(Constants.TAG, "------AccountSortActivity----onCreate -bundle==null----");
            return ;
        }

        mContext = this;

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO Auto-generated method stub
        Log.i(Constants.TAG, "------start AccountDetailActivity--------");
        Intent intent = new Intent(this, AccountDetailActivity.class);

        Account current = mDetailListDataSource.get(position);
        intent.putExtra("id", current.getId());
        this.startActivity(intent);
        finish();
    }

    private void m_InitalSortAccountList()
    {
        m_TotalAllAccountList = (ListView) findViewById(R.id.account_sort_list);

        m_DetailListAdapter = new AccountTotalDetailListAdapter(this,mDetailListDataSource);
        m_TotalAllAccountList.setAdapter(m_DetailListAdapter);

        m_TotalAllAccountList.setOnItemClickListener(AccountSortActivity.this);

        m_DetailListAdapter.updateUI();

    }

    private class PrepareTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO Auto-generated method stub

            Log.i(Constants.TAG, "------start get all account from DB--------");

            if(mSortType == Constants.ACCOUNT_SLIDEING_MENU_ASCEND) {
                mDetailListDataSource = (ArrayList<Account>) Account.getSortAscAclcounts();
            }
            else if(mSortType == Constants.ACCOUNT_SLIDEING_MENU_DESCEND)
            {
                mDetailListDataSource = (ArrayList<Account>) Account.getSortDescAclcounts();
            }

            Log.i(Constants.TAG, "------mDetailListDataSource.size()--------"+mDetailListDataSource.size());

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            Log.i(Constants.TAG, "------onPostExecute--------");
            super.onPostExecute(result);
            m_InitalSortAccountList();
        }
    }

    @SuppressLint("NewApi")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
