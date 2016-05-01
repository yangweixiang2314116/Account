package com.example.module;

import com.example.account.Constants;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.util.Log;

public class AccountRestClient {
    private static final String BASE_URL = "http://192.168.1.106:8000/jz/";

    private static AsyncHttpClient client = new AsyncHttpClient();
	private static String Token = " Token 0f4a0e75359d57dae5bcdda23ee80e9d460d6867";

    public static void get(String url, RequestParams params, JsonHttpResponseHandler responseHandler) {
        client.setTimeout(10000);
    	Log.i(Constants.TAG, "--get getAbsoluteUrl--"+getAbsoluteUrl(url));
    	
    	client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, JsonHttpResponseHandler responseHandler) {
    	
    	client.setTimeout(10000);
    	
    	Log.i(Constants.TAG, "--post getAbsoluteUrl--"+getAbsoluteUrl(url));
    	client.addHeader("Authorization",Token);
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