package com.example.account.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.account.Constants;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * Created by Administrator on 2016/5/11.
 */
public class WXEntryActivity  extends Activity implements IWXAPIEventHandler{

    private IWXAPI api;
    public static final String WX_APP_ID = "wx6ad865f237de6489";
    public static final String WX_APP_SECRET = "4812942c73b028e479d004fbc7128b9f";
    public static final String WX_APP_SCOPE = "snsapi_userinfo";
    public static final String WX_APP_STATE = "zxjz_login";

    private static String GET_REQUEST_ACCESS_TOKEN =
            "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";

    private static String GET_REQUEST_USER_INFO =
            "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(Constants.TAG, "------WXEntryActivity----onCreate -start----");
        api = WXAPIFactory.createWXAPI(this, WX_APP_ID, false);
        api.handleIntent(getIntent(), this);

        m_RequestLogin();

        Log.i(Constants.TAG, "------WXEntryActivity----onCreate -end----");

    }

    private boolean  m_RequestLogin()
    {
        if( !api.isWXAppInstalled()){
            Toast.makeText(this, "请先安装微信应用", Toast.LENGTH_SHORT).show();
            return false;
        }
        if( !api.isWXAppSupportAPI() ){
            Toast.makeText(this, "请先更新微信应用", Toast.LENGTH_SHORT).show();
            return false;
        }
        api.registerApp(WX_APP_ID);
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = WX_APP_SCOPE;
        req.state = WX_APP_STATE;
        boolean result = api.sendReq(req);
        Log.i(Constants.TAG, "------WXEntryActivity----m_RequestLogin -result----"+result);
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.i(Constants.TAG, "------WXEntryActivity----onNewIntent -enter----");
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
        finish();
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {

        Log.i(Constants.TAG, "------WXEntryActivity----onResp -baseResp.errCode----"+baseResp.errCode);

        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK: {
                String code = ((SendAuth.Resp) baseResp).code;
                Log.i(Constants.TAG, "------WXEntryActivity----onResp -code----" + code);
            }
            break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:

                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:

                break;
            default:

                break;
        }

    }

    private static String getTokenURL(String code ){
        String tokenRequest = GET_REQUEST_ACCESS_TOKEN.replace("APPID", WX_APP_ID).
                replace("SECRET", WX_APP_SECRET).
                replace("CODE",code);
        return tokenRequest;
    }

    private  String getAccessToken()
    {
        /*
         Request requestToken = new Request.Builder().url(tokenRequestUrl).build();
                      response = httpClient.newCall(requestToken).execute();
                      String tokenResponse = response.body().string();
                      Log.i(TAG, "tokenResponse = " + tokenResponse);
                      weChatTokenDo = JSON.parseObject(tokenResponse, WeiXinTokenDO.class);
         */
        return "";
    }

    private String getWxUserInfo()
    {
        /*
                              String userInfoRequest = getUserInfoRequest(weChatTokenDo.getAccess_token(), weChatTokenDo.getOpenid());
                     Request requestUserInfo = new Request.Builder().url(userInfoRequest).build();
                     response = httpClient.newCall(requestUserInfo).execute();
                     String userInfoResponse = response.body().string();
                     Log.i(TAG, "userInfo = " + userInfoResponse);
                     String openid = JSON.parseObject    (userInfoResponse).getString("openid");
         */
        return "";
    }
}
