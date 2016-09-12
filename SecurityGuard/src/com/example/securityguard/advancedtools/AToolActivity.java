package com.example.securityguard.advancedtools;

import java.io.File;

import com.example.securityguard.R;
import com.example.securityguard.engine.SmsBackUp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class AToolActivity extends Activity {
	private TextView tv_query_phone_address;
	private TextView tv_sms_backup;
	private TextView tv_commonnumber_query;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atool);

		// 电话归属地查询方法
		initPhoneAddress();
		// 短信备份
		initSmsBackup();
		// 常用号码查询
		initCommonNumberQuery();
	}

	private void initCommonNumberQuery() {
		tv_commonnumber_query = (TextView) findViewById(R.id.tv_commonnumber_query);
		tv_commonnumber_query.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(getApplicationContext(),
						CommonNumberQueryActivity.class));
			}
		});
	}

	private void initSmsBackup() {
		tv_sms_backup = (TextView) findViewById(R.id.tv_sms_backup);
		tv_sms_backup.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showSmsBackupDialog();
			}
		});

	}

	protected void showSmsBackupDialog() {
		// 1.创建一个带进度条的对话框
		final ProgressDialog progressDialog = new ProgressDialog(this);
		progressDialog.setIcon(R.drawable.ic_launcher);
		progressDialog.setTitle("短信备份");
		// 2.指定进度条的样式水平
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		// 3.展示进度条
		progressDialog.show();
		// 4.直接调用备份短信方法即可
		new Thread() {
			public void run() {
				String path = Environment.getExternalStorageDirectory()
						.getAbsolutePath() + File.separator + "sms74.xml";
				SmsBackUp.backup(getApplicationContext(), path, progressDialog);
				progressDialog.dismiss();
			};
		}.start();
	}

	private void initPhoneAddress() {
		tv_query_phone_address = (TextView) findViewById(R.id.tv_query_phone_address);
		tv_query_phone_address.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(getApplicationContext(),
						QueryAddressActivity.class));
			}
		});
	}
}
