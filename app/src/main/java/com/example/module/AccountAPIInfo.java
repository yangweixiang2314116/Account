package com.example.module;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.account.AccountCommonUtil;
import com.example.account.Constants;
import android.os.Looper;
import android.util.Log;

public class AccountAPIInfo
{
	public long LocalId;
    public long AccountId;
    public Double Cost;
    public String Category;
    public String Brand;
    public String Position;
    public String Comments;
    public long CreateTime;
    public long UpdatedTime;
    public ArrayList<String> Thumbnails;
    
    public AccountAPIInfo()
    {
    	
    }
    
    public boolean build(JSONObject response) 
	{
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Throwable warn = new Throwable("Please do not execute Animation.build(JSONObject object) " +
                    "in Main thread, it's bad performance and may block the ui thread");
            throw new RuntimeException(warn);
        }
        

        AccountAPIInfo item = null;
		try {
			item.LocalId = response.getLong("local_id");
			item.AccountId = response.getLong("id");
			item.Cost = response.getDouble("price");
			item.Category = response.getString("tag");
			item.Brand = response.getString("brand");
			item.Comments = response.getString("note");
			item.Position = response.getString("addr");	
			
			String modify = response.getString("modified");
			
			Log.i(Constants.TAG, "--AccountAPIInfo--build-local_id-" + item.LocalId);
			Log.i(Constants.TAG, "--AccountAPIInfo--build-AccountId-" + item.AccountId);
			Log.i(Constants.TAG, "--AccountAPIInfo--build-Cost-" + item.Cost);
			Log.i(Constants.TAG, "--AccountAPIInfo--build-Category-" + item.Category);
			Log.i(Constants.TAG, "--AccountAPIInfo--build-Brand-" + item.Brand);
			Log.i(Constants.TAG, "--AccountAPIInfo--build-Comments-" + item.Comments);
			Log.i(Constants.TAG, "--AccountAPIInfo--build-Position-" + item.Position);
			Log.i(Constants.TAG, "--AccountAPIInfo--build-modify time-" + modify);
			
		    item.UpdatedTime = AccountCommonUtil.ConverStringToDate(modify);
		    
		    for(int index = 1; index <= 4; index++)
		    {
		    	String Label = "image"+index;
		    	
		    	Log.i(Constants.TAG, "--AccountAPIInfo--build-image label--" + Label);
		    	
				if(!response.isNull(Label))
				{
					String ImagePath = response.getString(Label);
					Log.i(Constants.TAG, "--AccountAPIInfo--build-ImagePath--" + ImagePath);
					Thumbnails.add(ImagePath);
				}
				else
				{
					break;
				}
		    }
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		finally {
			return true;
		}
	}
    
}
