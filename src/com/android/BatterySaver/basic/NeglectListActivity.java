package com.android.BatterySaver.basic;

import java.util.ArrayList;
import java.util.List;

import com.android.BatterySaver.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class NeglectListActivity extends Activity implements OnClickListener {
	public static final String TAG = "NeglectListActivity";
	private ArrayList<AppInfo> mData;
	private ListView mListView;

	private void finishActivityByResult(int requstCode) {
		Log.d("NeglectListActivity", "finishActivityByResult, resultCode: "
				+ requstCode);
		setResult(requstCode, new Intent());
		finish();
	}

	private ArrayList<AppInfo> getInsalledAppInfo() {
		ArrayList appintoArrayList = new ArrayList();
		PackageManager packageManager = getPackageManager();
		Intent intent = new Intent("android.intent.action.MAIN", null);
		intent.addCategory("android.intent.category.LAUNCHER");
		List list = getPackageManager().queryIntentActivities(intent,
				0);


		for (int i = 0; i < list.size(); i++) {
			AppInfo appInfo = new AppInfo(this);
			ResolveInfo resolveInfo = (ResolveInfo) list.get(i);
			appInfo.appIcon = resolveInfo.loadIcon(packageManager);
			appInfo.appName = resolveInfo.loadLabel(packageManager).toString();
			appInfo.packageName = resolveInfo.activityInfo.packageName;
			appInfo.className = resolveInfo.activityInfo.name;
			if ((appInfo.packageName.equals("com.android.deskclock"))
					|| (appInfo.packageName.equals("com.mediatek.schpwronoff"))
					|| (appInfo.packageName.equals("com.android.launcher"))
					|| (appInfo.packageName.equals("com.android.keyguard"))
					|| (appInfo.packageName.equals("com.android.BatterySaver"))) {

				continue;
			}

			appintoArrayList.add(appInfo);

		}
		return appintoArrayList;
	}

	private void initLayout() {
		TextView settings_title = (TextView) findViewById(R.id.settings_title);

		settings_title.setText(R.string.app_neglect_list_name);
		mData = getInsalledAppInfo();
		mListView = ((ListView) findViewById(R.id.selectapp_listview));
		MyAdapter myAdapter = new MyAdapter(this);
		mListView.setAdapter(myAdapter);
	}

	private void nextStep(boolean isNextStep) {
		int requesCodeMe = (isNextStep) ? (20) : (21);
		finishActivityByResult(requesCodeMe);

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

	}

	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		Log.d("NeglectListActivity", "OnCreate()");
		setContentView(R.layout.neglect_list_layout);
		initLayout();
	}

	public void onDestroy() {
		Log.v("NeglectListActivity", "NeglectListActivity onDestroy");
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finishActivityByResult(21);
		}
		return super.onKeyDown(keyCode, event);
	}

	public void onPause() {
		super.onPause();
	}

	public void onResume() {
		super.onResume();
	}

	public class MyAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public MyAdapter(Context context) {

			mInflater = LayoutInflater.from(context);
		}

		public int getCount() {
			return mData.size();
		}

		public Object getItem(int item) {
			return mData.get(item);
		}

		public long getItemId(int itemId) {
			return itemId;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			final ViewHolder holder;
			// 观察convertView随ListView滚动情况
			Log.v("MyListViewBase", "getView " + position + " " + convertView);
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.select_app_item, null);
				holder = new ViewHolder();

				convertView.setTag((Object) (holder));
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			/* 得到各个控件的对象 */
			holder.appIcon = ((ImageView) convertView.findViewById(R.id.img));
			holder.appIcon
					.setImageDrawable(((AppInfo) mData.get(position)).appIcon);
			holder.appName = ((TextView) convertView.findViewById(R.id.title));
			holder.appName.setText(((AppInfo) mData.get(position)).appName);
			holder.selectItem = ((ImageView) convertView
					.findViewById(R.id.select_item));
			if (((AppInfo) mData.get(position))
					.getSelectItemFlag("NeglectListActivity"
							+ ((AppInfo) mData.get(position)).appName)) {
				holder.selectItem.setImageResource(R.drawable.selectitem);
			} else {

				holder.selectItem.setImageResource(R.drawable.noselectitem);
			}

			holder.selectItem.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Log.v("MyListViewBase", "你点击了按钮" + position);
		
					if (((AppInfo) mData.get(position))
							.getSelectItemFlag("NeglectListActivity"
									+ ((AppInfo) mData.get(position)).appName)) {
						((AppInfo) mData.get(position)).setSelectItemFlag(
								"NeglectListActivity"
										+ ((AppInfo) mData.get(position)).appName,
								false);
						holder.selectItem
								.setImageResource(R.drawable.noselectitem);
						AppInfo.setSelectItemCount(-1
								+ AppInfo.getSelectItemCount());

					} else {
						((AppInfo) mData.get(position)).setSelectItemFlag(
								"NeglectListActivity"
										+ ((AppInfo) mData.get(position)).appName,
								true);
						holder.selectItem
								.setImageResource(R.drawable.selectitem);
						AppInfo.setSelectItemCount(1 + AppInfo
								.getSelectItemCount());
					}
				}

			});
			return convertView;
		}

		private class ViewHolder {
			ImageView appIcon;
			TextView appName;
			ImageView selectItem;

		}

	}

}
