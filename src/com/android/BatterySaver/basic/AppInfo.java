package com.android.BatterySaver.basic;

import java.util.ArrayList;
import java.util.Comparator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;



public class AppInfo implements Comparator<AppInfo> {

	private static final String SELECT_ALL_ITEM_FLAG = "SelectAllItemFlag";
	private static final String SELECT_ITEM_COUNT = "SelectItemCount";
	private static final String SELECT_ITEM_FLAG = "SelectItemFlag";
	private static boolean SelectAllItemFlag = false;
	private static SharedPreferences mSharedPreferences;
	private boolean SelectItemFlag = false;
	public Drawable appIcon = null;
	public String appName = "";
	public String className;

	public Intent intent;
	public ArrayList<AppInfo> mData;
	public String packageName = "";
	public int uid;

	public AppInfo(Context context) {
		mSharedPreferences = context.getSharedPreferences(SELECT_ITEM_FLAG, 0);
	}

	public AppInfo(String packagenm) {
		packageName = packagenm;
		className = null;
		appIcon = null;
		appName = null;
		intent = null;
		uid = 0;
	}

	public static boolean getSelectAllItemFlag(String packageName) {
		SelectAllItemFlag = mSharedPreferences.getBoolean(SELECT_ALL_ITEM_FLAG
				+ packageName, false);
		return SelectAllItemFlag;
	}

	public static int getSelectItemCount() {
		return mSharedPreferences.getInt("SelectItemCount", 0);
	}

	public static boolean setSelectAllItemFlag(String activityName,
			boolean allselect) {
		SharedPreferences.Editor mEditor = mSharedPreferences.edit();
		mEditor.putBoolean(SELECT_ALL_ITEM_FLAG + activityName, allselect);
		return mEditor.commit();
	}

	public static boolean setSelectItemCount(int count) {
		SharedPreferences.Editor mEditor = mSharedPreferences.edit();
		mEditor.putInt("SelectItemCount", count);
		return mEditor.commit();
	}

	@Override
	public int compare(AppInfo appInfo1, AppInfo appInfo2) {
		try {
			int i = appInfo1.appName.compareTo(appInfo2.appName);
			return i;
		} catch (Exception e) {
		}
		return 0;
	}

	public boolean getSelectItemFlag(String activityName) {
		SelectItemFlag = mSharedPreferences.getBoolean(SELECT_ITEM_FLAG
				+ activityName, false);
		return SelectItemFlag;
	}

	public boolean setSelectItemFlag(String activityName, boolean isSelect) {
		SharedPreferences.Editor mEditor = mSharedPreferences.edit();
		mEditor.putBoolean(SELECT_ITEM_FLAG + activityName, isSelect);
		return mEditor.commit();
	}

}
