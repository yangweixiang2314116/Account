package com.example.module;

import com.example.account.Constants;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.util.Log;

import cz.msebera.android.httpclient.protocol.HTTP;

public class AccountRestClient {
    private static final String BASE_URL = "http://192.168.0.17:8000/";

    private static AsyncHttpClient client = new AsyncHttpClient();
	private static String TokenPre = " Token ";
	private static String Token = "";

    public static void get(String url, RequestParams params, JsonHttpResponseHandler responseHandler) {
        client.setTimeout(10000);
    	Log.i(Constants.TAG, "--get getAbsoluteUrl--"+getAbsoluteUrl(url));
    	
    	client.get(getAbsoluteUrl(url), params, responseHandler);
    }

	public static void SetClientToken(String value)
	{
		Token = TokenPre + value;
	}

    public static void post(String url, RequestParams params, JsonHttpResponseHandler responseHandler) {
    	
    	client.setTimeout(10000);
    	
    	Log.i(Constants.TAG, "--post getAbsoluteUrl--" + getAbsoluteUrl(url));
    	client.addHeader("Authorization", Token);
    	client.post(getAbsoluteUrl(url), params, responseHandler);
    }

	public static void postNoToken(String url, RequestParams params, JsonHttpResponseHandler responseHandler) {

		client.setTimeout(10000);

		Log.i(Constants.TAG, "--post getAbsoluteUrl--" + getAbsoluteUrl(url));
		//client.addHeader(HTTP.CONTENT_TYPE,
		//		"application/x-www-form-urlencoded;charset=UTF-8");
		client.post(getAbsoluteUrl(url), params, responseHandler);
	}

    
    public static void delete(String url, JsonHttpResponseHandler responseHandler) {
    	client.setTimeout(10000);
    	
    	Log.i(Constants.TAG, "--delete getAbsoluteUrl--"+getAbsoluteUrl(url));
    	client.addHeader("Authorization",Token);
    	client.delete(getAbsoluteUrl(url), responseHandler);
    }
    
    public static void put(String url, RequestParams params, JsonHttpResponseHandler responseHandler) {
    	client.setTimeout(10000);
    	
    	Log.i(Constants.TAG, "--put getAbsoluteUrl--"+getAbsoluteUrl(url));
    	client.addHeader("Authorization",Token);
    	//client.addHeader("x-http-method-override","PATCH");
    	
    	client.put(getAbsoluteUrl(url), params, responseHandler);
    }
    
    

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}