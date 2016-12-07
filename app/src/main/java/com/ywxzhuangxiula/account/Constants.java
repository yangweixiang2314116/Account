package com.ywxzhuangxiula.account;

public class Constants {

    // Config

    public static final String TAG = "51";

    public static final String ACCOUNT_LOGIN_SMS_APP_KEY = "12b9eb2bc8f00";
    public static final String ACCOUNT_LOGIN_SMS_APP_SECRET = "7687d60f5a5f529313c0e53f51839473";

    public static final int ACCOUNT_FORCE_LOGIN_MAX = 15;

    public static final int ACCOUNT_MORE_INFO_CATEGORY = 0;
    public static final int ACCOUNT_MORE_INFO_BRAND = 1;
    public static final int ACCOUNT_MORE_INFO_POSITION = 2;
    public static final int ACCOUNT_MORE_INFO_SHOPPING_ONLINE = 3;
    public static final int ACCOUNT_MORE_INFO_IMAGE = 4;

    public static final int ACCOUNT_SLIDEING_MENU_ASCEND = 0;
    public static final int ACCOUNT_SLIDEING_MENU_DESCEND = 1;
    public static final int ACCOUNT_SLIDEING_MENU_IMAGE = 2;
    public static final int ACCOUNT_SLIDEING_MENU_SEARCH = 3;
    public static final int ACCOUNT_SLIDEING_MENU_SYNC = 4;
    public static final int ACCOUNT_SLIDEING_MENU_COMMENT = 5;
    public static final int ACCOUNT_SLIDEING_MENU_SETTING = 6;

    public static final int ACCOUNT_MORE_INFO_TYPE_TEXT = 0;
    public static final int ACCOUNT_MORE_INFO_TYPE_IMAGE = 1;

    public static final int ACCOUNT_TAB_INDEX_REGISTER = 0;
    public static final int ACCOUNT_TAB_INDEX_LOGIN = 1;

    /**
     * need to do nothing
     */
    public static final int ACCOUNT_ITEM_ACTION_NEED_NOTHING = 0;

    /**
     * need to create on server
     */
    public static final int ACCOUNT_ITEM_ACTION_NEED_SYNC_ADD = 1;


    /**
     * need to update on server
     */
    public static final int ACCOUNT_ITEM_ACTION_NEED_SYNC_UP = 2;

    /**
     * need to delete on server
     */
    public static final int ACCOUNT_ITEM_ACTION_NEED_SYNC_DELETE = 3;

    /*intent fileter
   */
    public static final String INTENT_NOTIFY_ACCOUNT_CHANGE = "com.ywxzhuangxiula.account.DATA_UPDATE";
    public static final String INTENT_NOTIFY_INVALID_TOKEN = "com.ywxzhuangxiula.account.INVALID_TOKEN";
    public static final String INTENT_NOTIFY_START_SYNC = "com.ywxzhuangxiula.account.START_SYNC";


    public static final int ACCOUNT_SYNC_ERROR = 0;
    public static final int ACCOUNT_SYNC_START = 1;
    public static final int ACCOUNT_SYNC_END = 2;
    public static final int ACCOUNT_SYNC_SUCCESS = 3;
    public static final int ACCOUNT_SYNC_INVALID_TOKEN = 4;

}
