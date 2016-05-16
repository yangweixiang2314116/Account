package com.example.module;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.example.account.AccountCommonUtil;
import com.example.account.Constants;

import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;


@Table(name = "Accounts")
public class Account extends Model implements Parcelable {

    @Column(name = "AccountId")
    public long AccountId;
    @Column(name = "Cost")
    public Double Cost;
    @Column(name = "Category")
    public String Category;
    @Column(name = "Brand")
    public String Brand;
    @Column(name = "Position")
    public String Position;
    @Column(name = "Comments")
    public String Comments;
    @Column(name = "CreateTime")
    public long CreateTime;
    @Column(name = "LastSyncTime")
    public long LastSyncTime;
    @Column(name = "UpdatedTime")
    public long UpdatedTime;
    @Column(name = "SyncStatus")
    public Integer SyncStatus;
    

	
    public List<ImageItem> Imageitems() {
        return getMany(ImageItem.class, "Account");
    }

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
    public static final Creator<Account> CREATOR = new Creator<Account>() {
        @Override
        public Account createFromParcel(Parcel parcel) {
            return new Account(parcel);
        }

        @Override
        public Account[] newArray(int size) {
            return new Account[size];
        }
    };
    
    public Account(Parcel in) {
        this(in.readLong(), in.readDouble(),in.readString(), in.readString(), in.readString(),in.readString(),
        		in.readLong(), in.readLong(),in.readLong(), in.readInt());
    }

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		// TODO Auto-generated method stub
        parcel.writeLong(AccountId);
        parcel.writeDouble(Cost);
        parcel.writeString(Category);
        parcel.writeString(Brand);
        parcel.writeString(Position);
        parcel.writeString(Comments);
        parcel.writeLong(CreateTime);
        parcel.writeLong(LastSyncTime);
        parcel.writeLong(UpdatedTime);
        parcel.writeInt(SyncStatus);
		
	}
	
    public Account() {
    	AccountId = 0;
    	CreateTime = System.currentTimeMillis();
    	UpdatedTime = System.currentTimeMillis();
    	Category = "";
    	Brand = "";
    	Position = "";
		Comments = "";
    	Cost = 0.0;
    	
    	SyncStatus = Constants.ACCOUNT_ITEM_ACTION_NEED_SYNC_ADD;
    }
    
    private Account(long account, Double cost, String category,
    		String brand, String position, String comments,
            long createTime, long synctime, long updateTime, Integer action) {
		super();

		AccountId = account;
		Cost = cost;
		Category = category;
		Brand = brand;
		Position = position;
		Comments = comments;
		CreateTime = createTime;
		UpdatedTime = updateTime;
		LastSyncTime = synctime;
		SyncStatus = action;
    }
    
    private Account(long account, Double cost, String category,
    		String brand, String position, String comments,
            long createTime, long updateTime) {
		super();
		AccountId = account;
		Cost = cost;
		Category = category;
		Brand = brand;
		Position = position;
		Comments = comments;
		CreateTime = createTime;
		UpdatedTime = updateTime;
    }
    
    public static List<Account> getNormalAccounts(){
        return new Select()
                .from(Account.class)
                .where("SyncStatus != ?",Constants.ACCOUNT_ITEM_ACTION_NEED_SYNC_DELETE)
                .orderBy("CreateTime desc")
                .execute();
    }

	public static List<Account> getSortDescAclcounts(){
		return new Select()
				.from(Account.class)
				.where("SyncStatus != ?", Constants.ACCOUNT_ITEM_ACTION_NEED_SYNC_DELETE)
				.orderBy("Cost desc")
				.execute();
	}

	public static List<Account> getSortAscAclcounts(){
		return new Select()
				.from(Account.class)
				.where("SyncStatus != ?",Constants.ACCOUNT_ITEM_ACTION_NEED_SYNC_DELETE)
				.orderBy("Cost asc")
				.execute();
	}


    public static List<Account> getAllAccounts(){
        return new Select()
                .from(Account.class)
                .orderBy("CreateTime desc")
                .execute();
    }
    
    
	private void setSyncStatus(int syncstatus) {
		SyncStatus = syncstatus;
	}
	
	public int getSyncStatus() {
		return SyncStatus;
	}

	
	public void setNeedSyncDelete() {
		setSyncStatus(Constants.ACCOUNT_ITEM_ACTION_NEED_SYNC_DELETE);
	}

	public void setNeedSyncUp() {
		setSyncStatus(Constants.ACCOUNT_ITEM_ACTION_NEED_SYNC_UP);
	}

	public boolean isNeedSyncUp() {
		if (SyncStatus == Constants.ACCOUNT_ITEM_ACTION_NEED_SYNC_UP) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isSyncOnServer() {
		if (AccountId == 0) {
			return false;
		} else {
			return true;
		}
	}
	
	
	public boolean isNeedSyncCreate() {
		if (SyncStatus == Constants.ACCOUNT_ITEM_ACTION_NEED_SYNC_ADD) {
			return true;
		} else {
			return false;
		}
	}
	

	public boolean isNeedSyncDelete() {
		if (SyncStatus == Constants.ACCOUNT_ITEM_ACTION_NEED_SYNC_DELETE) {
			return true;
		} else {
			return false;
		}
	}
	
    public static void deleteOne(Account item){
    	
    	if(null == item)
    	{
    		Log.i(Constants.TAG, "--deleteOne--invalid item !!!!-");
    		return ;
    	}
        new Delete().from(Account.class).where("id = ?",item.getId()).execute();
    }
    
    
    public static Account parseDelete(JSONObject response) 
	{
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Throwable warn = new Throwable("Please do not execute Animation.build(JSONObject object) " +
                    "in Main thread, it's bad performance and may block the ui thread");
            throw new RuntimeException(warn);
        }
        

        Account item = null;
		try {
			
			long local_id = response.getLong("local_id");
			item = Account.load(Account.class, local_id);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.i(Constants.TAG, "--Account--build-JSONException-"+e);
			e.printStackTrace();
		}
		finally {
			return item;
		}
	}
    
    
    public static Account build(JSONObject response) 
	{
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Throwable warn = new Throwable("Please do not execute Animation.build(JSONObject object) " +
                    "in Main thread, it's bad performance and may block the ui thread");
            throw new RuntimeException(warn);
        }
        

        Account item = null;
		try {
			
			long local_id = response.getLong("local_id");
			item = Account.load(Account.class, local_id);
				
			Log.i(Constants.TAG, "--Account--build--" + response.get("id"));
			
			item.AccountId = response.isNull("id") ? 0 : response.getLong("id");
			item.Cost = response.isNull("price") ? 0.0 :response.getDouble("price");
			

			if(response.getString("tag") == "null")
			{
				item.Category = "";
				
				if(item.Category.isEmpty())
				{
					Log.i(Constants.TAG, "--Account--build-AAAA-");
				}
			}
			else
			{
				item.Category = response.getString("tag");
			}
			
			
			if(response.getString("brand") == "null")
			{
				item.Brand = "";
			}
			else
			{
				item.Brand = response.getString("brand");
			}
			
			item.Comments = response.isNull("note") ? "":response.getString("note");
			item.Position = response.isNull("addr") ? "":response.getString("addr");
			
			String modify = response.isNull("modified") ? "":response.getString("modified");
			
			Log.i(Constants.TAG, "--Account--build-local_id-" + local_id);
			Log.i(Constants.TAG, "--Account--build-AccountId-" + item.AccountId);
			Log.i(Constants.TAG, "--Account--build-Cost-" + item.Cost);
			Log.i(Constants.TAG, "--Account--build-Category-" + item.Category);
			Log.i(Constants.TAG, "--Account--build-Brand-" + item.Brand);
			Log.i(Constants.TAG, "--Account--build-Comments-" + item.Comments);
			Log.i(Constants.TAG, "--Account--build-Position-" + item.Position);
			
			Log.i(Constants.TAG, "--Account--build-modify time-" + modify);
			
		    item.UpdatedTime = AccountCommonUtil.ConverStringToDate(modify);
		    
		    ArrayList<String> Thumbnails = new ArrayList<String>();
		    
		    List<ImageItem> LocalList =  item.Imageitems();
		    for(int index = 1; index <= 4; index++)
		    {
		    	String Label = "image"+index;
		    	
		    	Log.i(Constants.TAG, "--Account--build-image label--" + Label);
		    	
				if(!response.isNull(Label))
				{
					String ImagePath = response.getString(Label);
					Log.i(Constants.TAG, "--Account--build-ImagePath--" + ImagePath);
					
					ImageItem image = LocalList.get(index-1);
					image.ServerPath = ImagePath;
					image.save();
				}
				else
				{
					break;
				}
		    }
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.i(Constants.TAG, "--Account--build-JSONException-"+e);
			e.printStackTrace();
		}
		finally {
			return item;
		}
	}
	
    public static Account build(AccountAPIInfo data) 
    {	
		return new Account(data.AccountId,data.Cost,data.Category,data.Brand,data.Position,
				data.Comments, data.CreateTime,data.UpdatedTime);
    	
    }
    
    public boolean sync(AccountAPIInfo data) 
    {	
		AccountId = data.AccountId;
		Cost = data.Cost;
		Category = data.Category;
		Brand = data.Brand;
		Position = data.Position;
		Comments = data.Comments;
		CreateTime = data.CreateTime;
		UpdatedTime = data.UpdatedTime;
		return true;
    	
    }
    
    public static Account findAccountByAccountId(long id)
    {
    	return new Select().from(Account.class).where("AccountId == ? ", id).executeSingle();
    }
}
