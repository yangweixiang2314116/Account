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
    public ArrayList<String> Thumbnails = new ArrayList<String>();
    
    public AccountAPIInfo()
    {
    	
    }

	public AccountAPIInfo(Account item)
	{
		 LocalId = item.getId();
		 AccountId = item.AccountId;
		 Cost = item.Cost;
		Category = item.Category;
		 Brand = item.Brand;
		//Position = item.Position;
		 Comments =item.Comments ;
		CreateTime = item.CreateTime;
		UpdatedTime = item.UpdatedTime;

		ArrayList<ImageItem> ImageList = (ArrayList<ImageItem>) item.Imageitems();
		for(int index = 0 ; index < ImageList.size(); index++)
		{
			this.Thumbnails.add(ImageList.get(index).ServerPath);
		}

	}

	public boolean equals(Object o) {
		if (o instanceof AccountAPIInfo) {
			AccountAPIInfo u = (AccountAPIInfo) o;
				return AccountId == u.AccountId;
		} else {
			return false;
		}
	}

	public static AccountAPIInfo build(JSONObject response)
	{
        if (Looper.myLooper() == Looper.getMainLooper()) {
			Log.i(Constants.TAG, "--AccountAPIInfo--Work in Main Looper !!!-" );
            Throwable warn = new Throwable("Please do not execute Account.build(JSONObject object) " +
                    "in Main thread, it's bad performance and may block the ui thread");
            throw new RuntimeException(warn);
        }
        

        AccountAPIInfo item = new AccountAPIInfo();
		try {
			item.LocalId =  response.isNull("local_id") ? 0:response.getLong("local_id");
			item.AccountId =  response.isNull("id") ? 0:response.getLong("id");
			item.Cost = response.isNull("price") ? 0:response.getDouble("price");
			item.Category = response.isNull("tag") ? "":response.getString("tag");
			item.Brand = response.isNull("brand") ? "":response.getString("brand");
			item.Comments = response.isNull("note") ? "":response.getString("note");
			item.Position = response.isNull("addr") ? "":response.getString("addr");
			
			String modify = response.getString("modified");
			String created = response.getString("created");
			
			Log.i(Constants.TAG, "--AccountAPIInfo--build-local_id-" + item.LocalId);
			Log.i(Constants.TAG, "--AccountAPIInfo--build-AccountId-" + item.AccountId);
			Log.i(Constants.TAG, "--AccountAPIInfo--build-Cost-" + item.Cost);
			Log.i(Constants.TAG, "--AccountAPIInfo--build-Category-" + item.Category);
			Log.i(Constants.TAG, "--AccountAPIInfo--build-Brand-" + item.Brand);
			Log.i(Constants.TAG, "--AccountAPIInfo--build-Comments-" + item.Comments);
			Log.i(Constants.TAG, "--AccountAPIInfo--build-Position-" + item.Position);
			Log.i(Constants.TAG, "--AccountAPIInfo--build-modify time-" + modify);
			Log.i(Constants.TAG, "--AccountAPIInfo--build-created time-" + created);

		    item.UpdatedTime = AccountCommonUtil.ConverStringToDate(modify);
			Log.i(Constants.TAG, "--AccountAPIInfo--build-UpdatedTime-" +  item.UpdatedTime);

			item.CreateTime = AccountCommonUtil.ConverStringToDate(created);
			Log.i(Constants.TAG, "--AccountAPIInfo--build-CreateTime-" +  item.CreateTime);

		    for(int index = 1; index <= 4; index++)
		    {
		    	String Label = "image"+index;
		    	
		    	Log.i(Constants.TAG, "--AccountAPIInfo--build-image label--" + Label);
		    	
				if(!response.isNull(Label))
				{
					String ImagePath = response.getString(Label);
					Log.i(Constants.TAG, "--AccountAPIInfo--build-ImagePath--" + ImagePath);
					if(ImagePath.equals("") == false) {
						item.Thumbnails.add(ImagePath);
					}
				}
				else
				{
					break;
				}
		    }
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.i(Constants.TAG, "--AccountAPIInfo--JSONException-" );
			return null;
		}
		finally {
			return item;
		}
	}
    
}
