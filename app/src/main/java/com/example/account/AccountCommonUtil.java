package com.example.account;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import android.content.Context;
import android.content.Intent;
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
	
	//yyyy-MM-dd hh:mm:ss
	public static Long ConverStringToDate(String date)
	{
		Log.i(Constants.TAG, "-----------ConverDateToString---before convert ----" + date);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		long time = 0;
		try {
			time =  format.parse(date).getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Log.i(Constants.TAG, "-----------ConverDateToString---- check Date----" + ConverDateToString(time));

		return time;
	}

	//yyyy-MM-dd
	public static Long ConverStringToDateWithoutTime(String date)
	{
		Log.i(Constants.TAG, "-----------ConverDateToString---before convert ----" + date);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		long time = 0;
		try {
			time =  format.parse(date).getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Log.i(Constants.TAG, "-----------ConverDateToString---- check Date----" + ConverDateToString(time));

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

	public static boolean IsFirstEnter(Context context)
	{
		SharedPreferences pSharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);

		return pSharedPreferences.getBoolean(
				"is_first", true);
	}

	public static boolean SetNotFirstEnter(Context context)
	{
		SharedPreferences pSharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);

		pSharedPreferences.edit().putBoolean("is_first", false)
				.apply();
		return true;
	}

	public static boolean IsSupportSync(Context context)
	{
		SharedPreferences pSharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);

		return pSharedPreferences.getBoolean(
				"is_support_sync", false);
	}

	public static void setSupportSync(Context context, boolean support)
	{
		SharedPreferences pSharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);

		pSharedPreferences.edit().putBoolean("is_support_sync", support)
				.apply();
	}


	public static boolean IsOnlyWifi(Context context)
	{
		SharedPreferences pSharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);

		return pSharedPreferences.getBoolean(
				"only_wifi", true);
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

	public static String GetGudieStyle(Context context)
	{
		SharedPreferences pSharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);

		return pSharedPreferences.getString(
				"guide_style", "");
	}

	public static boolean SetGudieStyle(Context context, String style)
	{
		SharedPreferences pSharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);

		pSharedPreferences.edit().putString("guide_style", style)
				.apply();
		return true;
	}

	public static String GetGudieBudget(Context context)
	{
		SharedPreferences pSharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);

		return pSharedPreferences.getString(
				"guide_budget", "");
	}

	public static boolean SetGudieBudget(Context context, String budget)
	{
		SharedPreferences pSharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);

		pSharedPreferences.edit().putString("guide_budget", budget)
				.apply();
		return true;
	}

	public static String GetGudieArea(Context context)
	{
		SharedPreferences pSharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);

		return pSharedPreferences.getString(
				"guide_area", "");
	}

	public static boolean SetGudieArea(Context context, String area)
	{
		SharedPreferences pSharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);

		pSharedPreferences.edit().putString("guide_area", area)
				.apply();
		return true;
	}

	public static String GetCurrentCity(Context context)
	{
		SharedPreferences pSharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);

		return pSharedPreferences.getString(
				"city", "");
	}

	public static boolean SetCurrentCity(Context context, String city)
	{
		SharedPreferences pSharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);

		pSharedPreferences.edit().putString("city", city)
				.apply();
		return true;
	}

	public static void sendBroadcastForAccountDataChange(Context context) {
		Intent intent = new Intent(Constants.INTENT_NOTIFY_ACCOUNT_CHANGE);
		context.sendBroadcast(intent);
	}


}
