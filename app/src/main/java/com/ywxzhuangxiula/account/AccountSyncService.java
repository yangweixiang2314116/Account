package com.ywxzhuangxiula.account;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ywxzhuangxiula.module.Account;
import com.ywxzhuangxiula.module.AccountAPIInfo;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import cz.msebera.android.httpclient.Header;

public class AccountSyncService extends Service {

    private Account m_CurrentItem = null;

    private AccountTotalActivity.OnProgressListener onProgressListener;

    private int mSyncStatus = Constants.ACCOUNT_SYNC_END;

    private static final int SYNC_UP_TOTAL = 1;
    private static final int SYNC_UP_FINISH_ITEM = 2;
    private static final int SYNC_DOWN_FINISH = 3;

    private int mSyncUpTotalCount = 0;
    private boolean mbSyncDownFinish = true;
    private Handler syncHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case SYNC_UP_TOTAL:
                {
                    Log.i(Constants.TAG, "------start to sync up-SYNC_UP_TOTAL --" + msg.arg1);
                    mSyncUpTotalCount = msg.arg1;
                    if(mSyncUpTotalCount == 0)
                    {
                        new SyncTask(false, true, syncHandler).execute();
                    }
                }
                    break;
                case SYNC_UP_FINISH_ITEM:
                {
                    mSyncUpTotalCount--;
                    Log.i(Constants.TAG, "------start to sync up-SYNC_UP_FINISH_ITEM --" + mSyncUpTotalCount);

                    if(mSyncUpTotalCount == 0)
                    {
                        new SyncTask(false, true, syncHandler).execute();
                    }
                }
                    break;
                case SYNC_DOWN_FINISH:
                {
                    Log.i(Constants.TAG, "------start to sync up-SYNC_DOWN_FINISH --" );
                    mbSyncDownFinish = true;
                    m_UpdateSyncStatus();
                }
                break;
                default:
                    break;
            }
        };
    };

    private void m_UpdateSyncStatus()
    {
        Log.i(Constants.TAG, "------m_UpdateSyncStatus  -mSyncUpTotalCount --" + mSyncUpTotalCount + "---mbSyncDownFinish-- "+mbSyncDownFinish);
        if(mSyncUpTotalCount == 0 && mbSyncDownFinish)
        {
            Log.i(Constants.TAG, "------m_UpdateSyncStatus  -from --" + mSyncStatus + "---to--end ");
            mSyncStatus = Constants.ACCOUNT_SYNC_END;
            onProgressListener.onProgress(Constants.ACCOUNT_SYNC_END);
        }
    }

    public void setOnProgressListener(AccountTotalActivity.OnProgressListener onProgressListener) {
        this.onProgressListener = onProgressListener;
    }


    @Override
    public void onCreate() {
        Log.i(Constants.TAG, "--AccountSyncService----onCreate--");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(Constants.TAG, "--AccountSyncService----onStartCommand--");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.i(Constants.TAG, "--AccountSyncService----onDestroy--");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(Constants.TAG, "--AccountSyncService----onBind--");
        return new AccountSyncServiceBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(Constants.TAG, "--AccountSyncService----onUnbind--");
        return super.onUnbind(intent);
    }


    public class AccountSyncServiceBinder extends Binder {
        public void startSync() {
            Log.i(Constants.TAG, "--AccountSyncServiceBinder----startSync--");
            if (mSyncStatus != Constants.ACCOUNT_SYNC_END) {
                Toast.makeText(AccountSyncService.this, R.string.account_sync_service_already_start, Toast.LENGTH_SHORT).show();
                return;
            }

            if (onProgressListener != null) {

                onProgressListener.onProgress(Constants.ACCOUNT_SYNC_START);
                mSyncStatus = Constants.ACCOUNT_SYNC_START;
            }
            new SyncTask(true, false, syncHandler).execute();
        }

        public AccountSyncService getService() {
            return AccountSyncService.this;
        }

        public int getSyncStatus() {
            return mSyncStatus;
        }
    }


    class SyncTask extends AsyncTask<Void, Integer, Void> {

        Handler mHandler;
        boolean mSyncUp;
        boolean mSyncDown;
        ArrayList<Account> mAccountSyncUpList = new ArrayList<Account>();

        public SyncTask(Boolean syncUp, Boolean syncDown, Handler handler) {
            this(syncUp, syncDown);
            Log.i(Constants.TAG, "--SyncTask-----");
            mHandler = handler;
        }

        private SyncTask(Boolean syncUp, Boolean syncDown) {
            mSyncUp = syncUp;
            mSyncDown = syncDown;
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (mSyncUp == false && mSyncDown == false) {
                return null;
            }

            try {
                if (mSyncUp)
                    syncUp();
                if (mSyncDown)
                    syncDown();
                //publishProgress(new Integer[]{Constants.ACCOUNT_SYNC_SUCCESS});
            } catch (Exception e) {
                publishProgress(new Integer[]{Constants.ACCOUNT_SYNC_ERROR});
                return null;
            } finally {
                //publishProgress(new Integer[]{Constants.ACCOUNT_SYNC_END});
            }
            return null;
        }

        private void syncUp() {

            Log.i(Constants.TAG, "------start to sync up-1234--");

            mAccountSyncUpList.clear();
            Log.i(Constants.TAG, "------start to sync up-4--");
            List<Account> AccountList = Account.getAllAccounts();
            Log.i(Constants.TAG, "------start to sync AccountList ---" + AccountList.size());
            for (int index = 0; index < AccountList.size(); index++) {
                Account item = AccountList.get(index);
                if (item.isNeedSyncCreate() || item.isNeedSyncUp() || item.isNeedSyncDelete()) {
                    mAccountSyncUpList.add(item);
                }
            }

            Log.i(Constants.TAG, "------start to sync mAccountSyncUpList ---" + mAccountSyncUpList.size());
            try {
                m_ProcessSyncUpItems();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    private boolean m_ProcessSyncUpItems() throws InterruptedException {

        int nTotalCount = mAccountSyncUpList.size();
       // final CountDownLatch latch = new CountDownLatch(nTotalCount);
        //Log.i(Constants.TAG, "------CountDownLatch -nTotalCount-" + latch.getCount());
        Message upTotal = new Message();
        upTotal.what = SYNC_UP_TOTAL;
        upTotal.arg1 = nTotalCount;
        mHandler.sendMessage(upTotal);

        for (int index = 0; index < mAccountSyncUpList.size(); index++) {
            m_CurrentItem = mAccountSyncUpList.get(index);
            Log.i(Constants.TAG, "------start to sync up-m_CurrentItem --" + m_CurrentItem.getId());
            if (m_CurrentItem.isNeedSyncCreate()) {
                Log.i(Constants.TAG, "------try to---Create on server -id----" + m_CurrentItem.getId());

                mHandler.post(new AccountCreateRunable(m_CurrentItem));

            } else if (m_CurrentItem.isNeedSyncUp()) {
                Log.i(Constants.TAG, "------try to---Sync up on server -id----" + m_CurrentItem.getId());

                mHandler.post(new AccountUpdateRunable(m_CurrentItem));

            } else if (m_CurrentItem.isNeedSyncDelete()) {
                Log.i(Constants.TAG, "------try to---Delete on server -id----" + m_CurrentItem.getId() + "--m_CurrentItem.AccountId--" + m_CurrentItem.AccountId);

                if (false == m_CurrentItem.isSyncOnServer())//local item, not exist on server , delete directly
                {
                    Account.deleteOne(m_CurrentItem);
                } else {
                    mHandler.post(new AccountDeleteRunable(m_CurrentItem));
                }
            } else {
                Log.i(Constants.TAG, "------do nothing----" + m_CurrentItem.getId());
            }
        }

        Log.i(Constants.TAG, "-----await finish---");

        return true;
    }

    private void syncDown() {

        mHandler.post(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub

                AccountApiConnector.instance(AccountSyncService.this).getDetailList(new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                        Log.i(Constants.TAG, "---getDetailList--onSuccess--response---" + response);
                        new ProcessSyncDownTask(response).execute();

                    }

                    @Override

                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                        super.onFailure(statusCode, headers, throwable, response);
                        Log.i(Constants.TAG, "---getDetailList--onFailure--responseString---" + response + "--statusCode--" + statusCode);

                        if(statusCode == 401)
                        {
                            AccountCommonUtil.sendBroadcastForAccountInvalidToken(AccountSyncService.this);
                            Log.i(Constants.TAG, "-----sendBroadcastForAccountInvalidToken---");
                        }else {
                            Toast.makeText(AccountSyncService.this, R.string.account_sync_service_error, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        Log.i(Constants.TAG, "---sendEmptyMessage--SYNC_DOWN_FINISH---");
                        syncHandler.sendEmptyMessage(SYNC_DOWN_FINISH);
                    }

                });
            }
        });

    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (mHandler == null) {
            return;
        }
        switch (values[0]) {
            case Constants.ACCOUNT_SYNC_START:
                mSyncStatus = Constants.ACCOUNT_SYNC_START;
                onProgressListener.onProgress(Constants.ACCOUNT_SYNC_START);
                break;
            case Constants.ACCOUNT_SYNC_END:
                mSyncStatus = Constants.ACCOUNT_SYNC_END;
                onProgressListener.onProgress(Constants.ACCOUNT_SYNC_END);
                break;
            case Constants.ACCOUNT_SYNC_ERROR:
                mSyncStatus = Constants.ACCOUNT_SYNC_ERROR;
                onProgressListener.onProgress(Constants.ACCOUNT_SYNC_ERROR);
                break;
            case Constants.ACCOUNT_SYNC_SUCCESS:
                mSyncStatus = Constants.ACCOUNT_SYNC_SUCCESS;
                onProgressListener.onProgress(Constants.ACCOUNT_SYNC_SUCCESS);
                break;
            default:
                break;
        }
    }
}

private class ProcessSyncUpTask extends AsyncTask<Void, Void, Boolean> {
    private JSONObject m_responseObject;
    private int m_Action = Constants.ACCOUNT_ITEM_ACTION_NEED_NOTHING;

    public ProcessSyncUpTask(JSONObject responseObject, int Action) {
        m_responseObject = responseObject;
        m_Action = Action;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        Log.i(Constants.TAG, "----ProcessSyncDownTask--doInBackground----");
        Boolean bResult = false;
        try {
            switch (m_Action) {
                case Constants.ACCOUNT_ITEM_ACTION_NEED_SYNC_DELETE: {
                    Log.i(Constants.TAG, "------already delete on server , now delete on DB----");
                    Account item = Account.parseDelete(m_responseObject);

                    Account.deleteOne(item);
                }
                break;
                case Constants.ACCOUNT_ITEM_ACTION_NEED_SYNC_UP:
                case Constants.ACCOUNT_ITEM_ACTION_NEED_SYNC_ADD: {
                    Account item = Account.build(m_responseObject);
                    Log.i(Constants.TAG, "------already sync on server- local id---" + item.getId() + "accound id" + item.AccountId);
                    item.SyncStatus = Constants.ACCOUNT_ITEM_ACTION_NEED_NOTHING;
                    item.save();
                }
                break;
                default:
                    break;
            }
            bResult = true;
        } catch (Exception e) {
            e.printStackTrace();
            bResult = false;
        } finally {
            return bResult;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        Log.i(Constants.TAG, "----ProcessSyncUpTask--doInBackground----");

        syncHandler.sendEmptyMessage(SYNC_UP_FINISH_ITEM);
        Log.i(Constants.TAG, "---sendEmptyMessage--SYNC_UP_FINISH_ITEM---");
    }
}

private class ProcessSyncDownTask extends AsyncTask<Void, Void, Boolean> {
    private JSONArray m_responseObject = new JSONArray();
    private ArrayList<AccountAPIInfo> m_detailList = null;


    public ProcessSyncDownTask(JSONArray responseObject) {
        m_responseObject = responseObject;
        m_detailList = new ArrayList<AccountAPIInfo>();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        Boolean bResult = false;
        Log.i(Constants.TAG, "----ProcessSyncDownTask--doInBackground---m_responseObject-" + m_responseObject);
        try {
            Log.i(Constants.TAG, "---m_responseObject.length()-------" + m_responseObject.length());
            for (int index = 0; index < m_responseObject.length(); index++) {
                Log.i(Constants.TAG, "----------" + m_responseObject.getJSONObject(index));

                AccountAPIInfo item = AccountAPIInfo.build(m_responseObject.getJSONObject(index));
                m_detailList.add(item);
            }

            int nSyncDownChangedItem = 0;
            for (int index = 0; index < m_detailList.size(); index++) {
                AccountAPIInfo Serveritem = m_detailList.get(index);

                Log.i(Constants.TAG, "----Serveritem.AccountId------" + Serveritem.AccountId);
                Log.i(Constants.TAG, "---Serveritem.UpdatedTime------" + Serveritem.UpdatedTime);

                Account LocalItem = Account.findAccountByAccountId(Serveritem.AccountId);

                if (LocalItem == null) {
                    Log.i(Constants.TAG, "---can not find --Serveritem.AccountId------" + Serveritem.AccountId);
                    //download and create this item
                    Account.buildDownLoad(Serveritem);
                    nSyncDownChangedItem++;
                } else if (LocalItem.UpdatedTime != Serveritem.UpdatedTime) {
                    Log.i(Constants.TAG, "---LocalItem.UpdatedTime------" + LocalItem.UpdatedTime);
                    //sync this item
                    LocalItem.sync(Serveritem);
                    nSyncDownChangedItem++;
                } else {
                    Log.i(Constants.TAG, "-this item did not change--need do nothing------");
                    continue;
                }
            }

            //TODO if the item had delete on server , need check every item on DB. if not exist in server list , need delete on DB
            List<Account> AccountList = Account.getAllAccounts();
            Log.i(Constants.TAG, "------check items in  AccountList exist on server or not---" + AccountList.size());
            for (int index = 0; index < AccountList.size(); index++) {
                AccountAPIInfo searchItem = new AccountAPIInfo(AccountList.get(index));
                Account localItem = AccountList.get(index);
                if (m_detailList.contains(searchItem) == false && localItem.isNeedSyncCreate() == false) {
                    Log.i(Constants.TAG, "------this item had been delete by other device - AccountId ---" + searchItem.AccountId);

                    Account.deleteOne(localItem);
                    nSyncDownChangedItem++;
                }

            }

            if (nSyncDownChangedItem > 0) {
                Log.i(Constants.TAG, "------ProcessSyncDownTask  nSyncDownChangedItem---" + nSyncDownChangedItem);
                AccountCommonUtil.sendBroadcastForAccountDataChange(AccountSyncService.this);
            }
            bResult = true;
        } catch (Exception e) {
            e.printStackTrace();
            bResult = false;
        } finally {
            return bResult;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        Log.i(Constants.TAG, "----ProcessSyncDownTask--onPostExecute----");
    }
}

public class AccountCreateRunable implements Runnable {
    private Account mItem = null;
    private Handler mHandler = null;

    public AccountCreateRunable(Account item) {
        this.mItem = item;
    }

    public void run() {
        Log.i(Constants.TAG, "----AccountCreateRunable ---run----" + mItem.getId());

        AccountApiConnector.instance(AccountSyncService.this).postAccountItem(mItem, new AccountCreateJsonHttpResponseHandler());
    }

    private class AccountCreateJsonHttpResponseHandler extends JsonHttpResponseHandler {


        public AccountCreateJsonHttpResponseHandler() {

        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            // If the response is JSONObject instead of expected JSONArray
            Log.i(Constants.TAG, "---postAccountItem--onSuccess--response---" + response + "--statusCode--" + statusCode);

            new ProcessSyncUpTask(response, Constants.ACCOUNT_ITEM_ACTION_NEED_SYNC_ADD).execute();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable){
            super.onFailure(statusCode, headers, responseString, throwable);
            Log.i(Constants.TAG, "---postAccountItem--onFailure--statusCode---" + statusCode);
            Log.i(Constants.TAG, "---postAccountItem--onFailure--responseString---" + responseString);

            if(statusCode == 401)
            {
                AccountCommonUtil.sendBroadcastForAccountInvalidToken(AccountSyncService.this);
                Log.i(Constants.TAG, "-----sendBroadcastForAccountInvalidToken---");
            }else {
                Toast.makeText(AccountSyncService.this, R.string.account_sync_service_error, Toast.LENGTH_SHORT).show();
            }

            syncHandler.sendEmptyMessage(SYNC_UP_FINISH_ITEM);
            Log.i(Constants.TAG, "---sendEmptyMessage--SYNC_UP_FINISH_ITEM---");
        }


        @Override
        public void onFinish() {
            super.onFinish();
        }

    }
}

public class AccountUpdateRunable implements Runnable {
    private Account mItem = null;

    public AccountUpdateRunable(Account item) {
        this.mItem = item;
    }

    public void run() {
        Log.i(Constants.TAG, "----AccountUpdateRunable ---run----" + mItem.getId());

        AccountApiConnector.instance(AccountSyncService.this).updateAccountItem(mItem, new AccountUpdateJsonHttpResponseHandler());
    }

    private class AccountUpdateJsonHttpResponseHandler extends JsonHttpResponseHandler {

        public AccountUpdateJsonHttpResponseHandler() {

        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            // If the response is JSONObject instead of expected JSONArray
            Log.i(Constants.TAG, "---updateAccountItem--onSuccess--response---" + response);
            new ProcessSyncUpTask(response, Constants.ACCOUNT_ITEM_ACTION_NEED_SYNC_UP).execute();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable){
            super.onFailure(statusCode, headers, responseString, throwable);
            Log.i(Constants.TAG, "---updateAccountItem--onFailure--statusCode---" + statusCode);
            Log.i(Constants.TAG, "---updateAccountItem--onFailure--responseString---" + responseString);

            syncHandler.sendEmptyMessage(SYNC_UP_FINISH_ITEM);
            Log.i(Constants.TAG, "---sendEmptyMessage--SYNC_UP_FINISH_ITEM---");

            if(statusCode == 401)
            {
                AccountCommonUtil.sendBroadcastForAccountInvalidToken(AccountSyncService.this);
                Log.i(Constants.TAG, "-----sendBroadcastForAccountInvalidToken---");
            }else {
                Toast.makeText(AccountSyncService.this, R.string.account_sync_service_error, Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        public void onFinish() {
            super.onFinish();
        }
    }
}

public class AccountDeleteRunable implements Runnable {
    private Account mItem = null;

    public AccountDeleteRunable(Account item) {
        this.mItem = item;
    }

    public void run() {
        Log.i(Constants.TAG, "----AccountDeleteRunable ---run----" + mItem.getId());

        AccountApiConnector.instance(AccountSyncService.this).deleteAccountItem(mItem, new AccountDeleteJsonHttpResponseHandler());
    }

    private class AccountDeleteJsonHttpResponseHandler extends JsonHttpResponseHandler {

        public AccountDeleteJsonHttpResponseHandler() {

        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            // If the response is JSONObject instead of expected JSONArray
            Log.i(Constants.TAG, "---postAccountItem--onSuccess--response---" + response);
            new ProcessSyncUpTask(response, Constants.ACCOUNT_ITEM_ACTION_NEED_SYNC_DELETE).execute();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable){
            super.onFailure(statusCode, headers, responseString, throwable);
            Log.i(Constants.TAG, "---deleteAccountItem--onFailure--statusCode---" + statusCode);
            Log.i(Constants.TAG, "---deleteAccountItem--onFailure--responseString---" + responseString);

            syncHandler.sendEmptyMessage(SYNC_UP_FINISH_ITEM);
            Log.i(Constants.TAG, "---sendEmptyMessage--SYNC_UP_FINISH_ITEM---");

            if(statusCode == 401)
            {
                AccountCommonUtil.sendBroadcastForAccountInvalidToken(AccountSyncService.this);
                Log.i(Constants.TAG, "-----sendBroadcastForAccountInvalidToken---");
            }else {
                Toast.makeText(AccountSyncService.this, R.string.account_sync_service_error, Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        public void onFinish() {
            super.onFinish();
        }

    }

}
}
