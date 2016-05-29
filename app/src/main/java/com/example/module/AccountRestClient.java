package com.example.module;

import com.example.account.AccountCommonUtil;
import com.example.account.Constants;
import com.example.account.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLSocketFactory;

public class AccountRestClient {
    private static final String BASE_URL = "http://192.168.1.100:8000/";
	//private static final String BASE_URL = "https://192.168.1.104/";

    private static AsyncHttpClient client = null;
	private static String TokenPre = " Token ";
	private static String Token = "";
	private static Context mContext = null;

	private static AccountRestClient mInstance;

	private AccountRestClient() {
	};

	public static AccountRestClient instance(Context context) {
		if (mInstance == null) {
			mInstance = new AccountRestClient();
			client = new AsyncHttpClient();
			mContext = context;
			mInstance.Init(context);
		}
		return mInstance;
	}

	private void Init(Context context)
	{
		Log.i(Constants.TAG, "-AccountRestClient -- Init--start-");

		//client = new AsyncHttpClient();
		/*
		KeyStore localTrustStore = null;
		MySSLSocketFactory sslFactory = null;
		try {
			localTrustStore = KeyStore.getInstance("BKS");
		} catch (KeyStoreException e) {
			Log.i(Constants.TAG, "--KeyStoreException-" + e);
			e.printStackTrace();
		}
		InputStream input = context.getResources().openRawResource(R.raw.codeprojectssl);
		try {
			try {
				localTrustStore.load(input, "311311".toCharArray());
				sslFactory = new MySSLSocketFactory(localTrustStore);
			} catch (IOException e) {
				Log.i(Constants.TAG, "--IOException-" + e);
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				Log.i(Constants.TAG, "--NoSuchAlgorithmException-" + e);
				e.printStackTrace();
			} catch (CertificateException e) {
				Log.i(Constants.TAG, "--CertificateException-" + e);
				e.printStackTrace();
			} catch (UnrecoverableKeyException e) {
				Log.i(Constants.TAG, "--UnrecoverableKeyException-" + e);
				e.printStackTrace();
			} catch (KeyStoreException e) {
				Log.i(Constants.TAG, "--KeyStoreException-" + e);
				e.printStackTrace();
			} catch (KeyManagementException e) {
				Log.i(Constants.TAG, "--KeyManagementException-" + e);
				e.printStackTrace();
			}
		} finally {
			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		Log.i(Constants.TAG, "--setSSLSocketFactory--");
		client.setSSLSocketFactory(sslFactory);
		*/
		if(AccountCommonUtil.IsLogin(mContext))
		{
			String value = AccountCommonUtil.GetToken(mContext);
			Token = TokenPre + value;
			Log.i(Constants.TAG, "--already login in  - token --" + Token);
		}
	}

    public static void get(String url, RequestParams params, JsonHttpResponseHandler responseHandler) {
        client.setTimeout(5000);
    	Log.i(Constants.TAG, "--get getAbsoluteUrl--" + getAbsoluteUrl(url));

    	client.get(getAbsoluteUrl(url), params, responseHandler);
    }

	public static void SetClientToken(String value)
	{ 	
    		Log.i(Constants.TAG, "--SetClientToken----" + value);
		Token = TokenPre + value;
	}

    public static void post(String url, RequestParams params, JsonHttpResponseHandler responseHandler) {
    	
    	client.setTimeout(5000);
    	
    	Log.i(Constants.TAG, "--post getAbsoluteUrl----" + getAbsoluteUrl(url) +"--Token --"+Token);
    	client.addHeader("Authorization", Token);
    	client.post(getAbsoluteUrl(url), params, responseHandler);
    }

	public static void postNoToken(String url, RequestParams params, JsonHttpResponseHandler responseHandler) {

		client.setTimeout(5000);

		Log.i(Constants.TAG, "--post getAbsoluteUrl--" + getAbsoluteUrl(url));
		//client.addHeader(HTTP.CONTENT_TYPE,
		//		"application/x-www-form-urlencoded;charset=UTF-8");
		client.post(getAbsoluteUrl(url), params, responseHandler);
	}

    
    public static void delete(String url, JsonHttpResponseHandler responseHandler) {
    	client.setTimeout(5000);
    	
    	Log.i(Constants.TAG, "--delete getAbsoluteUrl--"+getAbsoluteUrl(url));
    	client.addHeader("Authorization",Token);
    	client.delete(getAbsoluteUrl(url), responseHandler);
    }
    
    public static void put(String url, RequestParams params, JsonHttpResponseHandler responseHandler) {
    	client.setTimeout(5000);
    	
    	Log.i(Constants.TAG, "--put getAbsoluteUrl--"+getAbsoluteUrl(url));
    	client.addHeader("Authorization",Token);
    	//client.addHeader("x-http-method-override","PATCH");
    	
    	client.put(getAbsoluteUrl(url), params, responseHandler);
    }
    
    

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}