/*
 * Copyright (c) 2012-2013 NetEase, Inc. and other contributors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package com.performance.test.mcn.activity;

import java.io.DataOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.performance.test.mcn.R;
import com.performance.test.mcn.utils.Settings;
import com.performance.test.mcn.utils.WakeLockHelper;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import static android.content.ContentValues.TAG;

/**
 * Setting Page of MCN
 *
 */
public class SettingsActivity extends Activity {

	private static final String LOG_TAG = "MCN-" + SettingsActivity.class.getSimpleName();

	private CheckBox chkFloat;
//	private CheckBox chkRoot;
//	private CheckBox chkAutoStop;
//	private CheckBox chkWakeLock;
	private TextView tvTime;
//	private LinearLayout about;
//	private LinearLayout mailSettings;
//	private LinearLayout testReport;

	private SharedPreferences preferences;
	private WakeLockHelper wakeLockHelper;
	private EditText mTimeText;
	private EditText mEmailText;

	//移植邮件配置
	private static final String BLANK_STRING = "";
	private String recipients;
	private String[] receivers;



	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(LOG_TAG, "onCreate");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.settings);

		wakeLockHelper = Settings.getDefaultWakeLock(this);
		
		// init views
		chkFloat = (CheckBox) findViewById(R.id.floating);
//		chkRoot = (CheckBox) findViewById(R.id.is_root);
//		chkAutoStop = (CheckBox) findViewById(R.id.auto_stop);
//		chkWakeLock = (CheckBox) findViewById(R.id.wake_lock);
		tvTime = (TextView) findViewById(R.id.time);
//		testReport = (LinearLayout) findViewById(R.id.test_report);
//		about = (LinearLayout) findViewById(R.id.about);
		mTimeText = (EditText) findViewById(R.id.set_time);
		mEmailText = (EditText) findViewById(R.id.recipients);
//		mailSettings = (LinearLayout) findViewById(R.id.mail_settings);
//		SeekBar timeBar = (SeekBar) findViewById(R.id.timeline);
		ImageView btnSave = (ImageView) findViewById(R.id.btn_set);
		RelativeLayout floatingItem = (RelativeLayout) findViewById(R.id.floating_item);
//		RelativeLayout autoStopItem = (RelativeLayout) findViewById(R.id.auto_stop_item);
//		RelativeLayout wakeLockItem = (RelativeLayout) findViewById(R.id.wake_lock_item);
		LinearLayout layGoBack = (LinearLayout) findViewById(R.id.lay_go_back);
//		LinearLayout layHeapItem = (LinearLayout) findViewById(R.id.heap_item);

		btnSave.setVisibility(ImageView.INVISIBLE);
		
		preferences = Settings.getDefaultSharedPreferences(getApplicationContext());
		int interval = preferences.getInt(Settings.KEY_INTERVAL, 1);
		boolean isfloat = preferences.getBoolean(Settings.KEY_ISFLOAT, true);
		recipients = preferences.getString(Settings.KEY_RECIPIENTS, BLANK_STRING);
		checkMailReceiver();
//		boolean isRoot = preferences.getBoolean(Settings.KEY_ROOT, false);
//		boolean autoStop = preferences.getBoolean(Settings.KEY_AUTO_STOP, true);
//		boolean wakeLock = preferences.getBoolean(Settings.KEY_WACK_LOCK, false);
		
		tvTime.setText(String.valueOf(interval));
		chkFloat.setChecked(isfloat);
//		chkRoot.setChecked(isRoot);
//		chkAutoStop.setChecked(autoStop);
//		chkWakeLock.setChecked(wakeLock);
		
		// start activity listener
		layGoBack.setOnClickListener(startActivityListener(MainPageActivity.class));
//		testReport.setOnClickListener(startActivityListener(TestListActivity.class));
//		mailSettings.setOnClickListener(startActivityListener(MailSettingsActivity.class));
//		about.setOnClickListener(startActivityListener(AboutActivity.class));
		
		/*timeBar.setProgress(interval);
		timeBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				tvTime.setText(Integer.toString(arg1 + 1));
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// when tracking stoped, update preferences
				int interval = arg0.getProgress() + 1;
				preferences.edit().putInt(Settings.KEY_INTERVAL, interval).commit();
			}
		});*/

		mTimeText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
//				String mString = mTimeText.getText().toString();//这个mString就是我想取得的值
				int interval= Integer.valueOf(mTimeText.getText().toString());
				preferences.edit().putInt(Settings.KEY_INTERVAL, interval).commit();
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {
				// TODO Auto-generated method stub

			}
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

		mEmailText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {
				// TODO Auto-generated method stub
			}
			@Override
			public void afterTextChanged(Editable s) {
				checkMailReceiver();
			}
		});

		floatingItem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				boolean isChecked = chkFloat.isChecked();
				chkFloat.setChecked(!isChecked);
				preferences.edit().putBoolean(Settings.KEY_ISFLOAT, !isChecked).commit();
			}
		});
		
//		autoStopItem.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				boolean isChecked = chkAutoStop.isChecked();
//				chkAutoStop.setChecked(!isChecked);
//				preferences.edit().putBoolean(Settings.KEY_AUTO_STOP, !isChecked).commit();
//			}
//		});
		
		/*wakeLockItem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				boolean isChecked = chkWakeLock.isChecked();
				chkWakeLock.setChecked(!isChecked);
				preferences.edit().putBoolean(Settings.KEY_WACK_LOCK, !isChecked).commit();
				if (chkWakeLock.isChecked()) {
					wakeLockHelper.acquireFullWakeLock();
				} else {
					wakeLockHelper.releaseWakeLock();
				}
			}
		});*/
		
		/*// get root permission
		layHeapItem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// if root checkbox is checked, change status to
				// opposite;otherwise, try to upgrade app to root
				boolean isChecked = chkRoot.isChecked();
				if (isChecked) {
					chkRoot.setChecked(!isChecked);
					preferences.edit().putBoolean(Settings.KEY_ROOT, !isChecked).commit();
				} else {
					boolean root = upgradeRootPermission(getPackageCodePath());
					if (root) {
						Log.d(LOG_TAG, "root succeed");
						chkRoot.setChecked(!isChecked);
						preferences.edit().putBoolean(Settings.KEY_ROOT, !isChecked).commit();
					} else {
						// if root failed, tell user to check if phone is rooted
						Toast.makeText(getBaseContext(), getString(R.string.root_failed_notification), Toast.LENGTH_LONG).show();
					}
				}

			}
		});*/
	}

	private OnClickListener startActivityListener(final Class<?> specClass) {
		return new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(SettingsActivity.this, specClass);
				startActivityForResult(intent, Activity.RESULT_FIRST_USER);
			}
		};
	} 
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/**
	 * check mail format
	 *
	 * @return true: valid email address
	 */
	private boolean checkMailFormat(String mail) {
		String strPattern = "^[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*" + "[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
		Pattern p = Pattern.compile(strPattern);
		Matcher m = p.matcher(mail);
		return m.matches();
	}

	private void checkMailReceiver() {
		recipients = mEmailText.getText().toString().trim();
		receivers = recipients.split("\\s+");
		for (int i = 0; i < receivers.length; i++) {
			if (!BLANK_STRING.equals(receivers[i]) && !checkMailFormat(receivers[i])) {
				Toast.makeText(SettingsActivity.this,
						getString(R.string.receiver_mail_toast) + "[" + receivers[i] + "]" + getString(R.string.format_incorrect_format),
						Toast.LENGTH_LONG).show();
				return;
			}
		}
		SharedPreferences preferences = Settings.getDefaultSharedPreferences(getApplicationContext());
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(Settings.KEY_RECIPIENTS, recipients);
		editor.commit();
	}
	/**
	 * upgrade app to get root permission
	 * 
	 * @return is root successfully
	 */
	public static boolean upgradeRootPermission(String pkgCodePath) {
		Process process = null;
		DataOutputStream os = null;
		try {
			String cmd = "chmod 777 " + pkgCodePath;
			process = Runtime.getRuntime().exec("su"); // 切换到root帐号
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes(cmd + "\n");
			os.writeBytes("exit\n");
			os.flush();
			int existValue = process.waitFor();
			if (existValue == 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			Log.w(LOG_TAG, "upgradeRootPermission exception=" + e.getMessage());
			return false;
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				process.destroy();
			} catch (Exception e) {
			}
		}
	}
}
