package com.example.account;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;

public class AccountCommonUtil {

	static String ConverDateToString(long date)
	{
		SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");
        String sDate = sdformat.format(date); 
		Log.i(Constants.TAG, "-----------ConverDateToString----sDate----" + sDate);
        return sDate;
	}
	
	static String ConverWholeDateToString(long date)
	{
		SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String sDate = sdformat.format(date); 
		Log.i(Constants.TAG, "-----------ConverDateToString----whole sDate----" + sDate);
        return sDate;
	}
	
	
	public static Long ConverStringToDate(String date)
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		long time = 0;
		try {
			time =  format.parse(date).getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return time;
	}

	/**
	 * dpתpx
	 *
	 * @param context
	 * @param val
	 * @return
	 */
	public static int dp2px(Context context, float dpVal)
	{
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				dpVal, context.getResources().getDisplayMetrics());
	}

	public static boolean IsLogin(Context context)
	{
        SharedPreferences pSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

		return pSharedPreferences.getBoolean(
                "is_login", false);
	}

	public static String GetToken(Context context)
	{
		SharedPreferences pSharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);

		return pSharedPreferences.getString(
				"token", "");
	}

	public static boolean SetToken(Context context, String token)
	{
		SharedPreferences pSharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);

		pSharedPreferences.edit().putString("token", token)
				.apply();
		return true;
	}

}
