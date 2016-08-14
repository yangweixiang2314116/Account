package com.example.account;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import com.example.module.Account;
import com.example.module.AccountRestClient;
import com.example.module.ImageItem;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import android.content.Context;
import android.util.Log;

public class AccountApiConnector {

	private static AccountApiConnector mInstance;
	public static final int ACCOUNT_IMAGE_SELECT_MAX_COUNT = 4;
	private static Context mcontext = null;
	
    private AccountApiConnector() {
	};

	public static AccountApiConnector instance(Context context) {
		if (mInstance == null) {
			mInstance = new AccountApiConnector();
			mcontext  =  context;
		}
		return mInstance;
	}

	public void  registerMobile(String userName, JsonHttpResponseHandler handler)
	{
		String url = "register2/";

		Log.i(Constants.TAG, "-start to registerMobile and get token --" + url);

		RequestParams params = new RequestParams();

		params.put("username",userName);
		params.put("type", "mobile");

		AccountRestClient.instance(mcontext).postNoToken(url, params, handler);
	}

	public void getToken(String userName, String pwd, JsonHttpResponseHandler handler) {

		String url = "auth/";

		Log.i(Constants.TAG, "-start to -login in and get token --" + url);

		RequestParams params = new RequestParams();

		params.put("username",userName);
		params.put("password", pwd);

		AccountRestClient.instance(mcontext).postNoToken(url, params, handler);

	}

	public void getDetailList(JsonHttpResponseHandler handler) {
		Log.i(Constants.TAG, "-start to -get all account --");
		
		String url = "jz/details/";
		AccountRestClient.instance(mcontext).getWithToken(url, null, handler);
		
	}
	
	public void getHotTagList(double value, JsonHttpResponseHandler handler) {
		Log.i(Constants.TAG, "-start to -get all hot tag --");
		
		String url = "jz/tags/hot/?price="+value;
		AccountRestClient.instance(mcontext).get(url, null, handler);
		
	}
	
	public void getHotBrandList(String category, TextHttpResponseHandler handler) {
		Log.i(Constants.TAG, "-start to -get all hot brand --");

		//String url = "https://kyfw.12306.cn/otn/leftTicket/init";
		String url = "https://192.168.1.220/jz/tags/";
		AccountRestClient.instance(mcontext).get(url, null, handler);

		/*
		String city = "�Ͼ�";
		String url = "jz/brands/hot/?tag="+category+"&city="+city;
		AccountRestClient.instance(mcontext).get(url, null, handler);
		*/
		
	}
	
	public void getHotPositionList(JsonHttpResponseHandler handler) {
		Log.i(Constants.TAG, "-start to -get all hot position --");
		
		String city = "�Ͼ�";
		String url = "jz/shops/hot/?city="+city;
		AccountRestClient.instance(mcontext).get(url, null, handler);
		
	}
	
	
	public void postAccountItem(Account item, JsonHttpResponseHandler handler)
	{
		Log.i(Constants.TAG, "-start to -post account item id--"+item.getId());
		
		String url = "jz/details/";
		RequestParams params = new RequestParams();

		params.put("local_id",item.getId());
		params.put("price",item.Cost);
		params.put("tag",item.Category);
		params.put("brand",item.Brand);
		params.put("note",item.Comments);
		//params.put("addr",item.Position);
		params.put("created", AccountCommonUtil.ConverWholeDateToString(item.CreateTime));
		
		Log.i(Constants.TAG, "--post account item id--"+item.getId());
		Log.i(Constants.TAG, "--post account item price--"+item.Cost);
		Log.i(Constants.TAG, "--post account item tag--"+item.Category);
		Log.i(Constants.TAG, "--post account item brand--"+item.Brand);
		Log.i(Constants.TAG, "--post account item note--"+item.Comments);
		//Log.i(Constants.TAG, "--post account item position--"+item.Position);
		Log.i(Constants.TAG, "--post account item created--"+AccountCommonUtil.ConverWholeDateToString(item.CreateTime));
		
		ArrayList<ImageItem> DetailImageList = (ArrayList<ImageItem>) item.Imageitems();
		if(DetailImageList.size() > 0)
		{
			for(int index = 0; index < DetailImageList.size(); index++)
			{
				String ImagePath = DetailImageList.get(index).Path;
				
				Log.i(Constants.TAG, "--post account item ImagePath--"+ImagePath);
				
				File file = new File(ImagePath);  
				if (file.exists() && file.length() > 0) { 
					try {
						int imageIndex = index + 1;
						String key = "image"+ imageIndex;
						params.put(key,file);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						Log.i(Constants.TAG, "--FileNotFoundException ImagePath-"+ImagePath);
						e.printStackTrace();
					}
				}
			}
		}
		
		
		AccountRestClient.instance(mcontext).post(url, params, handler);
	}
	
	public void deleteAccountItem(Account item, JsonHttpResponseHandler handler)
	{
		Log.i(Constants.TAG, "-start to -delete account item id--"+item.getId());
		Log.i(Constants.TAG, "-start to -delete account AccountId-"+item.AccountId);
		
		String url = "jz/details/"+item.AccountId+"/";
		Log.i(Constants.TAG, "--post account item id--"+item.AccountId);
		AccountRestClient.instance(mcontext).delete(url, handler);
	}
	
	public void updateAccountItem(Account item, JsonHttpResponseHandler handler)
	{
		Log.i(Constants.TAG, "-start to -put account item id--"+item.getId());
		Log.i(Constants.TAG, "-start to -put account AccountId-"+item.AccountId);
		
		
		String url = "jz/details/"+item.AccountId+"/";
		RequestParams params = new RequestParams();
		
		params.put("price",item.Cost);
		//TODO request by server
		params.put("tag",item.Category);
		params.put("brand",item.Brand);
		params.put("note",item.Comments);
		//params.put("position",item.Position);
		params.put("created", AccountCommonUtil.ConverWholeDateToString(item.CreateTime));

		
		Log.i(Constants.TAG, "--post account item id--"+item.getId());
		Log.i(Constants.TAG, "--post account item price--"+item.Cost);
		Log.i(Constants.TAG, "--post account item tag--"+item.Category);
		Log.i(Constants.TAG, "--post account item brand--"+item.Brand);
		Log.i(Constants.TAG, "--post account item note--"+item.Comments);
		//Log.i(Constants.TAG, "--post account item position--"+item.Position);
		
		
		ArrayList<ImageItem> DetailImageList = (ArrayList<ImageItem>) item.Imageitems();
		if(DetailImageList.size() > 0)
		{
			for(int index = 0; index < DetailImageList.size(); index++)
			{
				String ImagePath = DetailImageList.get(index).Path;
				
				Log.i(Constants.TAG, "--post account item ImagePath--"+ImagePath);
				
				File file = new File(ImagePath);  
				if (file.exists() && file.length() > 0) { 
					try {
						int imageIndex = index + 1;
						String key = "image"+ imageIndex;
						params.put(key,file);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						Log.i(Constants.TAG, "--FileNotFoundException ImagePath-"+ImagePath);
						e.printStackTrace();
					}
				}
			}

			//remove other old images
			int count = DetailImageList.size();
			for(; count < ACCOUNT_IMAGE_SELECT_MAX_COUNT ; count++)
			{
				int suffix = count + 1;
				String key = "image"+ suffix;
				params.put(key,"");
			}
		}

		Log.i(Constants.TAG, "--put  request params--"+params.toString());

		AccountRestClient.instance(mcontext).put(url, params, handler);
	}

	public void postFeedback(String feedback, JsonHttpResponseHandler handler)
	{
		Log.i(Constants.TAG, "-start to -postFeedback--"+feedback);

		String url = "jz/feedbacks/";

		RequestParams params = new RequestParams();

		params.put("feedback",feedback);

		AccountRestClient.instance(mcontext).post(url, params ,handler);
	}

}
