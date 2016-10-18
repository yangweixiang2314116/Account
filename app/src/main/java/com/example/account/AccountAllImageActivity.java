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
import android.widget.GridView;
import android.widget.TextView;

import com.example.module.BaseActivity;
import com.example.module.ImageItem;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;


/**
 * Created by Administrator on 2016/5/16.
 */
public class AccountAllImageActivity  extends BaseActivity implements AdapterView.OnItemClickListener{

    private Context mContext = null;
    private GridView m_AllImageGridView = null;
    private TextView mNoAccountText = null;
    private AccountAllImageListAdapter m_DetailListAdapter = null;
    protected ArrayList<ImageItem> mImageListDataSource = new ArrayList<ImageItem>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setTitle(getString(R.string.account_browser_picture));
        setContentView(R.layout.activity_account_all_image);

        mContext = this;
        new PrepareTask().execute();
        MobclickAgent.onEvent(mContext, "enter_sort");//TODO
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO Auto-generated method stub
        Log.i(Constants.TAG, "------start AccountDetailActivity--------");
        Intent intent = new Intent(this, AccountDetailActivity.class);

        ImageItem current = mImageListDataSource.get(position);
        intent.putExtra("id", current.account.getId());
        this.startActivity(intent);
        finish();
    }

    private void m_InitalSortAccountList()
    {
        m_AllImageGridView = (GridView) findViewById(R.id.id_gridView);

        mNoAccountText =  (TextView) findViewById(R.id.empty_no_account);

        m_DetailListAdapter = new AccountAllImageListAdapter(this,mImageListDataSource);
        m_AllImageGridView.setAdapter(m_DetailListAdapter);

        m_AllImageGridView.setOnItemClickListener(AccountAllImageActivity.this);

        m_DetailListAdapter.updateUI();

        if(mImageListDataSource.size() > 0 )
        {
            m_AllImageGridView.setVisibility(View.VISIBLE);
            mNoAccountText.setVisibility(View.GONE);
        }
        else
        {
            m_AllImageGridView.setVisibility(View.GONE);
            mNoAccountText.setVisibility(View.VISIBLE);
        }

    }

    private class PrepareTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO Auto-generated method stub

            Log.i(Constants.TAG, "------start get all Image from DB--------");
            mImageListDataSource = (ArrayList<ImageItem>) ImageItem.getAllImages();

            Log.i(Constants.TAG, "------mDetailListDataSource.size()--------"+mImageListDataSource.size());

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

    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}

