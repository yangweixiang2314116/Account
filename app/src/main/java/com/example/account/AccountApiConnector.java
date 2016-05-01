package com.example.account;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import com.example.module.Account;
import com.example.module.AccountRestClient;
import com.example.module.ImageItem;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.util.Log;

public class AccountApiConnector {

	private static AccountApiConnector mInstance;
	
    private AccountApiConnector() {
	};

	public static AccountApiConnector instance() {
		if (mInstance == null) {
			mInstance = new AccountApiConnector();
		}
		return mInstance;
	}
	
	public void getDetailList(JsonHttpResponseHandler handler) {
		Log.i(Constants.TAG, "-start to -get all account --");
		
		String url = "details/";
		AccountRestClient.get(url, null, handler);
		
	}
	
	public void getHotTagList(double value, JsonHttpResponseHandler handler) {
		Log.i(Constants.TAG, "-start to -get all hot tag --");
		
		String url = "tags/hot/?price="+value;
		AccountRestClient.get(url, null, handler);
		
	}
	
	public void getHotBrandList(String category, JsonHttpResponseHandler handler) {
		Log.i(Constants.TAG, "-start to -get all hot brand --");
		
		String city = "南京";
		String url = "brands/hot/?tag="+category+"&city="+city;
		AccountRestClient.get(url, null, handler);
		
	}
	
	public void getHotPositionList(JsonHttpResponseHandler handler) {
		Log.i(Constants.TAG, "-start to -get all hot position --");
		
		String city = "南京";
		String url = "shops/hot/?city="+city;
		AccountRestClient.get(url, null, handler);
		
	}
	
	
	public void postAccountItem(Account item, JsonHttpResponseHandler handler)
	{
		Log.i(Constants.TAG, "-start to -post account item id--"+item.getId());
		
		String url = "details/";
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
		Log.i(Constants.TAG, "--post account item position--"+item.Position);
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
		
		
		AccountRestClient.post(url, params, handler);
	}
	
	public void deleteAccountItem(Account item, JsonHttpResponseHandler handler)
	{
		Log.i(Constants.TAG, "-start to -delete account item id--"+item.getId());
		Log.i(Constants.TAG, "-start to -delete account AccountId-"+item.AccountId);
		
		String url = "details/"+item.AccountId+"/";
		Log.i(Constants.TAG, "--post account item id--"+item.AccountId);
		AccountRestClient.delete(url, handler);
	}
	
	public void updateAccountItem(Account item, JsonHttpResponseHandler handler)
	{
		Log.i(Constants.TAG, "-start to -put account item id--"+item.getId());
		Log.i(Constants.TAG, "-start to -put account AccountId-"+item.AccountId);
		
		
		String url = "details/"+item.AccountId+"/";
		RequestParams params = new RequestParams();
		
		params.put("price",item.Cost);
		//TODO request by server
		params.put("tag",item.Category);
		params.put("brand",item.Brand);
		params.put("note",item.Comments);
		params.put("position",item.Position);
		params.put("created", AccountCommonUtil.ConverWholeDateToString(item.CreateTime));

		
		Log.i(Constants.TAG, "--post account item id--"+item.getId());
		Log.i(Constants.TAG, "--post account item price--"+item.Cost);
		Log.i(Constants.TAG, "--post account item tag--"+item.Category);
		Log.i(Constants.TAG, "--post account item brand--"+item.Brand);
		Log.i(Constants.TAG, "--post account item note--"+item.Comments);
		Log.i(Constants.TAG, "--post account item position--"+item.Position);
		
		
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
		
		
		AccountRestClient.put(url, params, handler);
	}
}
