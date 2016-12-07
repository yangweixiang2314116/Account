package com.ywxzhuangxiula.account;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.ywxzhuangxiula.module.Account;
import com.ywxzhuangxiula.module.BaseActivity;
import com.ywxzhuangxiula.module.BrandHistory;
import com.ywxzhuangxiula.module.CategoryHistory;
import com.ywxzhuangxiula.module.DialogHelp;
import com.ywxzhuangxiula.module.OfflineHistory;
import com.ywxzhuangxiula.module.OnlineHistory;
import com.ywxzhuangxiula.module.SearchHistory;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.umeng.analytics.MobclickAgent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/5/22.
 */
public class AccountSearchActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private Intent mIntent = null;
    private Context mContext = null;
    private MaterialSearchView mSearchView;
    private String mCurSearchContent = "";
    private FlowLayout mGuessSearchFlowLayout = null;
    private RelativeLayout mGuessSearchLayout = null;

    private FlowLayout mSearchHistoryFlowLayout = null;

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

        //setTheme(R.style.MIS_NO_ACTIONBAR);
        setContentView(R.layout.activity_account_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.search_toolbar);
        if(toolbar != null){
            setSupportActionBar(toolbar);
        }
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayUseLogoEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setTitle(getString(R.string.account_search));
        }

        mInitSearchView();

        getWindow() . setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE) ;

        m_SearchAccountList = (ListView) findViewById(R.id.account_search_result_list);

        //m_SearchHistorytList = (ListView) findViewById(R.id.account_search_history_list);

        mSearchResultLayout = (RelativeLayout)findViewById(R.id.account_search_result_part);

        mSearchHistoryLayout = (RelativeLayout)findViewById(R.id.account_search_history_part);

        mSearchHistoryFlowLayout = (FlowLayout) findViewById(R.id.search_history_content);

        mShowSearchHistoryList();
        mHideSearchResultContent();
        mShowGuessSearchList();
        MobclickAgent.onEvent(mContext, "enter_search");

        Log.i(Constants.TAG, "------enter into AccountSearchActivity----onCreate--end--");

    }

    private void mShowGuessSearchList()
    {
        mGuessSearchFlowLayout = (FlowLayout)findViewById(R.id.guess_search_content);
        mGuessSearchLayout = (RelativeLayout)findViewById(R.id.account_guess_search_part);

        ArrayList<String> guessList = new ArrayList<String>();
        ArrayList<BrandHistory>  recentlyBrand =  (ArrayList<BrandHistory>) BrandHistory.GetHistoryItemsForSearch();
        for(int index = 0 ; index < recentlyBrand.size(); index++){
            guessList.add(recentlyBrand.get(index).Content);
        }

        ArrayList<CategoryHistory>  recentlyCategory =  (ArrayList<CategoryHistory>) CategoryHistory.GetHistoryItemsForSearch();
        for(int index = 0 ; index < recentlyCategory.size(); index++){
            guessList.add(recentlyCategory.get(index).Content);
        }

        ArrayList<OnlineHistory>  recentlyOnline =  (ArrayList<OnlineHistory>) OnlineHistory.GetHistoryItemsForSearch();
        for(int index = 0 ; index < recentlyOnline.size(); index++){
            guessList.add(recentlyOnline.get(index).Content);
        }

        ArrayList<OfflineHistory>  recentlyOffline =  (ArrayList<OfflineHistory>) OfflineHistory.GetHistoryItemsForSearch();
        for(int index = 0 ; index < recentlyOffline.size(); index++){
            guessList.add(recentlyOffline.get(index).name);
        }

        if(guessList.size() > 0)
        {
            for(int index = 0 ; index < guessList.size(); index++){
                addTextView(guessList.get(index));
            }
        }
        else
        {
            mGuessSearchFlowLayout.setVisibility(View.INVISIBLE);
        }

    }

    public void addTextViewRecently(SearchHistory searchItem) {

        TextView recentlyTag = (TextView) LayoutInflater.from(this).inflate(R.layout.flow_layout_item, mSearchHistoryFlowLayout, false);
        recentlyTag.setText(searchItem.Content);
        recentlyTag.setTag(searchItem);
        recentlyTag.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                TextView tag = (TextView)v;

                String current = tag.getText().toString();
                Log.i(Constants.TAG, "----search--" + current);

                if(mSearchView != null)
                {
                    mSearchView.setQuery(current, false);
                }
                mCurSearchContent = current;
                mSearchView.clearFocus();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                new PrepareSearchTask(mCurSearchContent).execute();
            }

        });

        recentlyTag.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                TextView offItem = (TextView) v;
                SearchHistory chose = (SearchHistory) offItem.getTag();
                if (null == chose) {
                    Log.i(Constants.TAG, "------null == chose--------");
                    return false;
                }
                m_ShowDeletePoup(chose, offItem);
                return false;
            }
        });

        mSearchHistoryFlowLayout.addView(recentlyTag);
    }

    private void m_ShowDeletePoup(SearchHistory chose , TextView view) {
        final SearchHistory itemDelete = chose;
        final TextView itemView = view;

        Log.i(Constants.TAG, "------m_ShowDeletePoup--------" + itemDelete.Content);

        android.support.v7.app.AlertDialog.Builder dialog = DialogHelp.getConfirmDialog(mContext, getString(R.string.confirm_to_delete_recently_use), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                itemDelete.delete();
                mSearchHistoryFlowLayout.removeView(itemView);
                dialogInterface.dismiss();
            }
        });
        dialog.show();
    }

    public void addTextView(String tvName) {

        TextView categoryTag = (TextView) LayoutInflater.from(this).inflate(R.layout.flow_layout_item, mGuessSearchFlowLayout, false);
        categoryTag.setText(tvName);
        categoryTag.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                TextView tag = (TextView)v;

                String current = tag.getText().toString();
                Log.i(Constants.TAG, "----search--" + current);

                if(mSearchView != null)
                {
                    mSearchView.setQuery(current, false);
                    mCurSearchContent = current;
                }
                mSearchView.clearFocus();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                new PrepareSearchTask(mCurSearchContent).execute();
            }

        });

        mGuessSearchFlowLayout.addView(categoryTag);
    }

    private void mShowSearchHistoryList()
    {
        mSearchHistoryDataSource = (ArrayList<SearchHistory>) SearchHistory.GetHistoryItems();

        Log.i(Constants.TAG, "------mSearchHistoryDataSource.size()--------"+mSearchHistoryDataSource.size());

        if(mSearchHistoryDataSource.size() > 0)
        {
            mSearchHistoryLayout.setVisibility(View.VISIBLE);
            mSearchHistoryFlowLayout.removeAllViews();
            for(int index = 0 ; index < mSearchHistoryDataSource.size(); index++){
                addTextViewRecently(mSearchHistoryDataSource.get(index));
            }

            /*
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
                    mSearchView.clearFocus();
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                    new PrepareSearchTask(mCurSearchContent).execute();
                }
            });
            */

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
             //m_SearchHistorytList.setAdapter(null);
            mSearchHistoryLayout.setVisibility(View.GONE);
        }
    }

    private void mHideSearchHistoryList()
    {
        mSearchHistoryLayout.setVisibility(View.GONE);
    }

    private void mHideSearchResultContent()
    {
        mSearchResultLayout.setVisibility(View.GONE);
    }

    private void mShowSearchResultContent()
    {
        mHideSearchHistoryList();
        mGuessSearchLayout.setVisibility(View.INVISIBLE);
        mSearchResultLayout.setVisibility(View.VISIBLE);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        mSearchView.setMenuItem(item);

        return true;
    }


    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private void mInitSearchView()
    {
        mSearchView = (MaterialSearchView) findViewById(R.id.search_view);
        mSearchView.setVoiceSearch(false);
        mSearchView.setCursorDrawable(R.drawable.color_cursor);
        mSearchView.setEllipsize(true);
        mSearchView.setHint(getString(R.string.account_search_hint));
        mSearchView.setHintTextColor(getResources().getColor(R.color.more_info_text_color));
        mSearchView.showSearch();
        //mSearchView.setBackgroundColor(getResources().getColor(R.color.actionbar_background_color));
        //mSearchView.setSuggestions(getResources().getStringArray(R.array.query_suggestions));
        mSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i(Constants.TAG, "------onQueryTextSubmit-----");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                Log.i(Constants.TAG, "------onQueryTextChange-----"+newText);
                if (newText != null && newText.length() > 0) {
                    //show search result
                    mCurSearchContent = newText;
                    new PrepareSearchTask(newText).execute();
                } else {
                    // show search history
                    mHideSearchResultContent();
                    mShowSearchHistoryList();
                    mGuessSearchLayout.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });

        mSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
                Log.i(Constants.TAG, "------onSearchViewShown-----");
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
                Log.i(Constants.TAG, "------onSearchViewClosed-----");
            }
        });
    }
}
