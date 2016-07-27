package com.example.account;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar.LayoutParams;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.module.Account;
import com.example.module.SearchHistory;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/5/22.
 */
public class AccountSearchActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    private Intent mIntent = null;
    private Context mContext = null;
    private SearchView  mSearchView  = null;
    private String mCurSearchContent = "";
    private SearchView.SearchAutoComplete mEdit = null;

    private ListView m_SearchHistorytList = null;
    protected ArrayList<SearchHistory> mSearchHistoryDataSource = new ArrayList<SearchHistory>();
    private RelativeLayout mSearchHistoryLayout = null;

    private ListView m_SearchAccountList = null;
    private AccountTotalDetailListAdapter m_SearchListAdapter = null;
    protected ArrayList<Account> mSearchListDataSource = new ArrayList<Account>();
    private RelativeLayout mSearchResultLayout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(Constants.TAG, "------enter into AccountSearchActivity----onCreate--Start--");
        mContext = this;
        mIntent = getIntent();

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayShowCustomEnabled(true);

       // getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_HOME_AS_UP
       //         | ActionBar.DISPLAY_SHOW_CUSTOM ) ;

        setContentView(R.layout.activity_account_search);

        m_InitSearchView();

        getWindow() . setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE) ;

        m_SearchAccountList = (ListView) findViewById(R.id.account_search_result_list);

        m_SearchHistorytList = (ListView) findViewById(R.id.account_search_history_list);

        mSearchResultLayout = (RelativeLayout)findViewById(R.id.account_search_result_part);

        mSearchHistoryLayout = (RelativeLayout)findViewById(R.id.account_search_history_part);

        mShowSearchHistoryList();
        mHideSearchResultContent();

        MobclickAgent.onEvent(mContext, "enter_search");

        Log.i(Constants.TAG, "------enter into AccountSearchActivity----onCreate--end--");

    }

    private void mShowSearchHistoryList()
    {
        mSearchHistoryDataSource = (ArrayList<SearchHistory>) SearchHistory.GetHistoryItems();

        Log.i(Constants.TAG, "------mSearchHistoryDataSource.size()--------"+mSearchHistoryDataSource.size());

        if(mSearchHistoryDataSource.size() > 0)
        {
            mSearchHistoryLayout.setVisibility(View.VISIBLE);

            List<HashMap<String,Object>> data = new ArrayList<HashMap<String,Object>>();
            for(int index = 0 ; index < mSearchHistoryDataSource.size(); index++){
                HashMap<String,Object>map = new HashMap<String,Object>();
                map.put("content", mSearchHistoryDataSource.get(index).Content);
                data.add(map);
            }
            SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.account_search_history_list_item, new String[]{"content"}, new int[]{R.id.search_history_content});
            m_SearchHistorytList.setAdapter(adapter);
            m_SearchHistorytList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    SearchHistory current = mSearchHistoryDataSource.get(position);
                    mCurSearchContent = current.Content;
                    if(mSearchView != null)
                    {
                        mSearchView.setQuery(mCurSearchContent, false);
                    }
                    new PrepareSearchTask(mCurSearchContent).execute();
                }
            });

            TextView   clearSearchButton  = (TextView) findViewById(R.id.clear_search_history);

            Log.i(Constants.TAG, "------setOnClickListener---clearSearchButton--");
            clearSearchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(Constants.TAG, "------enter into onClick---clearSearchButton--");
                    SearchHistory.deleteAll();
                    Toast.makeText(mContext, getString(R.string.accout_search_clear_history_success), Toast.LENGTH_SHORT).show();
                    mHideSearchHistoryList();
                }
            });
        }
        else
        {
             m_SearchHistorytList.setAdapter(null);
            mSearchHistoryLayout.setVisibility(View.GONE);
        }
    }

    private void mHideSearchHistoryList()
    {
        mSearchHistoryLayout.setVisibility(View.GONE);
    }

    private void m_InitSearchView()
    {
        LayoutInflater inflater = ( LayoutInflater ) getSystemService ( Context . LAYOUT_INFLATER_SERVICE ) ;

        View customActionBarView = inflater . inflate ( R . layout . search_view_title , null ) ;

        mSearchView = (SearchView) customActionBarView . findViewById ( R .id.search_view ) ;

        mSearchView.setVisibility(View.VISIBLE);
        mSearchView.setIconifiedByDefault(true);
        mSearchView.setIconified(false);
        mSearchView.setQueryHint(getString(R.string.account_search_hint));
        if (Build.VERSION.SDK_INT >= 14) {
            // when edittest is empty, don't show cancal button
            mSearchView.onActionViewExpanded();
        }

        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                return false;
            }
        });
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(mContext, "onQueryTextSubmit--"+query, Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Toast.makeText(mContext, "text change", Toast.LENGTH_SHORT).show();

                if (newText != null && newText.length() > 0) {
                    //show search result
                    mCurSearchContent = newText;
                    new PrepareSearchTask(newText).execute();
                } else {
                    // show search history
                    mHideSearchResultContent();
                    mShowSearchHistoryList();
                }
                return false;
            }
        });

        LayoutParams params = new LayoutParams( LayoutParams. WRAP_CONTENT ,
                LayoutParams. WRAP_CONTENT , Gravity . CENTER_VERTICAL
                | Gravity. RIGHT ) ;

        getSupportActionBar(). setCustomView(customActionBarView, params) ;

        m_ChangeSearchViewDefaultStyle();
    }

    private void m_ChangeSearchViewDefaultStyle()
    {
        //mEdit = (SearchView.SearchAutoComplete) mSearchView.findViewById(R.id.search_src_text);
        //mEdit.setTextColor(getResources().getColor(R.color.white_color));
        //mEdit.setHintTextColor(getResources().getColor(R.color.list_line_color));

        LinearLayout  inputLine   = (LinearLayout) mSearchView.findViewById(R.id.search_plate);
        inputLine.setBackgroundColor(Color.WHITE);

        //ImageView icTip = (ImageView) mSearchView.findViewById(R.id.search_button);
        //icTip.setImageResource(R.mipmap.ic_search);

        //ImageView  CloseButton  = (ImageView) mSearchView.findViewById(R.id.search_close_btn);
        //CloseButton.setImageResource(R.mipmap.abc_ic_clear_mtrl_alpha);
    }

    private void mHideSearchResultContent()
    {
        mSearchResultLayout.setVisibility(View.GONE);
    }

    private void mShowSearchResultContent()
    {
        mHideSearchHistoryList();
        mSearchResultLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       //getMenuInflater().inflate(R.menu.account_add_category, menu);
        return true;
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

    private void m_InitalSearchAccountList()
    {

        TextView   textNumber  = (TextView) findViewById(R.id.total_search_number_value);

        if(textNumber != null) {
            int nSearchNumber = mSearchListDataSource.size();

            textNumber.setText("" + nSearchNumber);
        }


        if(mSearchListDataSource.size() > 0) {
            m_SearchListAdapter = new AccountTotalDetailListAdapter(this, mSearchListDataSource);
            m_SearchAccountList.setAdapter(m_SearchListAdapter);

            m_SearchAccountList.setOnItemClickListener(AccountSearchActivity.this);

            m_SearchListAdapter.updateUI();

        }
        else
        {
                //TODO
            m_SearchAccountList.setAdapter(null);
        }

        mShowSearchResultContent();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i(Constants.TAG, "------start AccountDetailActivity--------");
        Intent intent = new Intent(this, AccountDetailActivity.class);

       if(false == SearchHistory.IsExistSearchContent(mCurSearchContent)) {
           SearchHistory item = new SearchHistory();
           item.Content = mCurSearchContent;
           item.save();
       }
        else {
           Log.i(Constants.TAG, "------mCurSearchContent already on DB--------"+mCurSearchContent);
       }

        Account current = mSearchListDataSource.get(position);
        intent.putExtra("id", current.getId());
        this.startActivity(intent);
    }

    private class PrepareSearchTask extends AsyncTask<Void, Void, Boolean> {

        private String mSearchContent;
        public PrepareSearchTask(String  searchContent) {
            mSearchContent = searchContent;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO Auto-generated method stub

            Log.i(Constants.TAG, "------start search all account from DB----mSearchContent----"+mSearchContent);

            mSearchListDataSource = (ArrayList<Account>) Account.getSearchResultAccounts(mSearchContent);

            Log.i(Constants.TAG, "------mDetailListDataSource.size()--------"+mSearchListDataSource.size());

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            Log.i(Constants.TAG, "------onPostExecute--------");
            super.onPostExecute(result);
            m_InitalSearchAccountList();
        }
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
