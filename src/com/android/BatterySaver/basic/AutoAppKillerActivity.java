package com.android.BatterySaver.basic;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.android.BatterySaver.R;
import com.android.BatterySaver.utils.CircleProgress;
import com.android.BatterySaver.utils.RotatView;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AutoAppKillerActivity extends Activity {
	ProgressDialog mProgressDialog;
	private long mLastAvailMemory;
	private RotatView mProgressBar;
	private boolean mReadDataDone;
	private CircleProgress mCircleProgressBar;

	private AppInfo mAppInfo;
	private ArrayList<AppInfo> mData;
	private TextView mUsedMemory;
	private TextView mFreeMemory;
	private Thread newThread;
	private static final int MEM_CLEARED = 0;
	public static final String TAG = "AutoAppKillerActivity";

	Handler mHandler = new Handler() {
		public void handleMessage(Message message) {
			super.handleMessage(message);
			switch (message.what) {
			case MEM_CLEARED:
				if (mProgressDialog != null) {
					mProgressDialog.dismiss();
				}
				long availMemory = getAvailMemory(AutoAppKillerActivity.this);
				Object[] arrayOfObject = new Object[2];
				arrayOfObject[0] = Long.valueOf(Math.max(0L, availMemory
						- mLastAvailMemory));
				arrayOfObject[1] = Long.valueOf(availMemory);
				Toast.makeText(
						AutoAppKillerActivity.this,
						AutoAppKillerActivity.this.getString(
								R.string.mem_cleared, arrayOfObject), 0).show();

				long useMemory = getTotalMemory(AutoAppKillerActivity.this)
						- availMemory;
				String usedMemory = getString(R.string.app_used_memory)
						+ useMemory + "MB";
				mUsedMemory.setText(usedMemory);
				float scale = (float) availMemory
						/ (float) getTotalMemory(AutoAppKillerActivity.this);
				Log.v("AutoAppKillerActivity", " MEM_CLEARED mUsedRatio = "
						+ scale);
				mProgressBar.removeMessagesRotate();
				mProgressBar.setVisibility(View.GONE);
				mCircleProgressBar
						.startCartoom((int) (100.0F - 100.0F * scale));
				// mCircleProgressBar.setMainProgress((int) (100.0F - scale *
				// 100.0F));
				break;

			default:
				break;
			}

		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.auto_app_killer_layout);
	}

	@Override
	protected void onResume() {

		super.onResume();
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
		}
		initLayout();
	}

	private ArrayList<AppInfo> getInsalledAppInfo() {
		/*
		 * ( Log.v("AutoAppKillerActivity", " mData.size()= " + mData.size() +
		 * " AppInfo.getSelectItemCount() = " + AppInfo.getSelectItemCount());
		 */
		ArrayList appinfoArrayList = new ArrayList();
		PackageManager packageManager = getPackageManager();
		Intent intent = new Intent("android.intent.action.MAIN", null);
		intent.addCategory("android.intent.category.LAUNCHER");
		List list = getPackageManager().queryIntentActivities(intent, 0);
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

			appinfoArrayList.add(appInfo);

		}

		return appinfoArrayList;
	}

	private void initLayout() {
		TextView tiletextView = (TextView) findViewById(R.id.settings_title);
		tiletextView.setText(R.string.app_auto_app_killer);
		mAppInfo = new AppInfo(this);

		mProgressBar = (RotatView) findViewById(R.id.progress_Bar);
		mProgressBar.setRotatDrawableResource(R.drawable.spinner_white_76);
		mProgressBar.setVisibility(View.GONE);
		newThread = new Thread(new Runnable() {

			@Override
			public void run() {
				mReadDataDone = true;
				mData = getInsalledAppInfo();
				mReadDataDone = false;

			}

		});
		newThread.start();
		Button speButton = (Button) findViewById(R.id.speed_up_memory);
		speButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				memClear();

			}
		});
		mUsedMemory = (TextView) findViewById(R.id.used_memory);
		long yiyong = getTotalMemory(this) - getAvailMemory(this);
		String mString = getString(R.string.app_used_memory) + yiyong + "MB";
		mUsedMemory.setText(mString);
		mFreeMemory = (TextView) findViewById(R.id.free_memory);
		String str2 = getString(R.string.app_free_memory)
				+ getTotalMemory(this) + "MB";
		mFreeMemory.setText(str2);

		Button whiteButton = (Button) findViewById(R.id.neglect_list_button);
		whiteButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AutoAppKillerActivity.this,
						NeglectListActivity.class);
				startActivity(intent);
			}

		});
		mCircleProgressBar = (CircleProgress) findViewById(R.id.circle_Bar);
		float scale = (float) getAvailMemory(this)
				/ (float) getTotalMemory(this);
		Log.v(TAG, "mUsedRadio=" + scale);
		mCircleProgressBar.setMainProgress((int) (100.0F - scale * 100.0F));

	}

	@Override
	protected void onDestroy() {
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
		}
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		mProgressBar.setVisibility(View.GONE);
		mProgressBar.removeMessagesRotate();
		mHandler.removeMessages(MEM_CLEARED);
		if (mCircleProgressBar != null) {
			mCircleProgressBar.stopCartoom();

		}
		super.onPause();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		return super.onKeyDown(keyCode, event);
	}

	private long getAvailMemory(Context context) {
		ActivityManager mActivityManagera = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo mMemoryInfo = new MemoryInfo();
		mActivityManagera.getMemoryInfo(mMemoryInfo);
		return mMemoryInfo.availMem / 1048576L;
	}

	private long getTotalMemory(Context context) {
		long totalMemory = 0L;
		try {
			BufferedReader localBufferedReader = new BufferedReader(
					new FileReader("/proc/meminfo"), 8192);
			totalMemory = Integer.valueOf(
					localBufferedReader.readLine().split("\\s+")[1]).intValue();
			localBufferedReader.close();
			return totalMemory / 1024L;
		} catch (IOException e) {
		}
		return totalMemory;
	}

	private void memClear() {
		mLastAvailMemory = getAvailMemory(this);
		Log.v("chunlei",
				" mData.size()= " + this.mData.size()
						+ " AppInfo.getSelectItemCount() = "
						+ AppInfo.getSelectItemCount());
		// mProgressBar.startMessagesRotate();
		if (mData.size() == AppInfo.getSelectItemCount()) {
			long availMemory = getAvailMemory(this);
			Object[] arrayOfObject = new Object[2];
			arrayOfObject[0] = Integer.valueOf(Math.max(0, 0));
			arrayOfObject[1] = Long.valueOf(availMemory);
			Toast.makeText(this,
					getString(R.string.mem_cleared, arrayOfObject), 0).show();
			return;
		}
		mCircleProgressBar.setMainProgress(0);
		mProgressBar.setVisibility(View.VISIBLE);
		mProgressBar.startMessagesRotate();
		mLastAvailMemory = getAvailMemory(this);
		ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		// 通过调用ActivityManager的getRunningAppProcesses()方法获得系统里所有正在运行的进程
		List appProcessList = mActivityManager.getRunningAppProcesses();
		Iterator myIterator = appProcessList.iterator();
		while (myIterator.hasNext()) {
			ActivityManager.RunningAppProcessInfo runningAppProcessInfo = (ActivityManager.RunningAppProcessInfo) myIterator
					.next();
			if (runningAppProcessInfo.importance <= 300) {
				continue;
			}
			if ((runningAppProcessInfo.processName
					.equals("com.android.deskclock"))
					|| (runningAppProcessInfo.processName
							.equals("com.mediatek.schpwronoff"))
					|| (runningAppProcessInfo.processName
							.equals("com.android.launcher"))
					|| (runningAppProcessInfo.processName
							.equals("com.android.keyguard"))
					|| (runningAppProcessInfo.processName
							.equals("com.android.BatterySaver"))) {
				continue;
			}
			// og.i("chunfengou", "packageName notkill"+
			// runningAppProcessInfo.processName);
			for (int i = 0; i < mData.size(); i++) {
				Log.i("chunfengou", "data pckage"
						+ (((AppInfo) mData.get(i)).packageName));

				// if ((((AppInfo) mData.get(i))
				// .getSelectItemFlag("NeglectListActivity"
				// + ((AppInfo) mData.get(i)).appName) == true)
				// &&

				if (!runningAppProcessInfo.processName.equals(((AppInfo) mData
						.get(i)).packageName)) {
					continue;
				} else {
					if (((AppInfo) mData.get(i))
							.getSelectItemFlag("NeglectListActivity"
									+ ((AppInfo) mData.get(i)).appName)==true) {

					}else {
						mActivityManager.forceStopPackage(((AppInfo)mData.get(i)).packageName);
						Log.i("chunleiwo", "packageName kill"
								+ runningAppProcessInfo.processName);
					}
			                    break;
				}
			}
		}
		      System.gc();
		    Runtime.getRuntime().runFinalization();
		    this.mHandler.removeMessages(0);
		    this.mHandler.sendEmptyMessageDelayed(0, 2000L);

	}
}
