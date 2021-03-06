package com.example.securityguard.active;

import com.example.securityguard.R;
import com.example.securityguard.BlackNumberActivity.BlackNumberActivity;
import com.example.securityguard.advancedtools.AToolActivity;
import com.example.securityguard.utils.ConstantValue;
import com.example.securityguard.utils.Md5Util;
import com.example.securityguard.utils.SpUtil;
import com.example.securityguard.utils.ToastUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeActivity extends Activity {
	private GridView gv_home;
	private String[] mTitleStrs;
	private int[] mDrawableIds;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		// 初始化控件
		initUI();
		// 初始化数据的方法
		initData();
	}

	private void initData() {
		// 准备数据
		mTitleStrs = new String[] { "手机防盗", "通信卫士", "软件管理", "进程管理", "流量统计",
				"手机杀毒", "缓存清理", "高级工具", "设置中心" };
		mDrawableIds = new int[] { R.drawable.home_safe,
				R.drawable.home_callmsgsafe, R.drawable.home_apps,
				R.drawable.home_taskmanager, R.drawable.home_netmanager,
				R.drawable.home_trojan, R.drawable.home_sysoptimize,
				R.drawable.home_tools, R.drawable.home_settings };
		// 九宫格控件设置数据适配器
		gv_home.setAdapter(new MyAdapter());
		// 注册九宫格单个条目点击事件
		gv_home.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				switch (position) {
				case 0:
					// 开启对话框
					showDialog();
					break;
				case 1:
					//跳转到通信卫士界面
					startActivity(new Intent(getApplicationContext(),BlackNumberActivity.class));
					break;
				case 2:
					//跳转到软件管理界面
					startActivity(new Intent(getApplicationContext(),AppManagerActivity.class));
					break;
				case 3:
					//跳转到进程管理界面
					startActivity(new Intent(getApplicationContext(),ProcessManagerActivity.class));
					break;
				case 5:
					//跳转到手机杀毒界面
					startActivity(new Intent(getApplicationContext(),AnitVirusActivity.class));
					break;
				case 6:
					//跳转到缓存清理界面
					startActivity(new Intent(getApplicationContext(),CacheClearActivity.class));
					break;
				case 7:
					//跳转到高级工具功能列表界面
					startActivity(new Intent(getApplicationContext(),AToolActivity.class));
					break;
				case 8:
					Intent intent = new Intent(getApplicationContext(),
							SettingActivity.class);
					startActivity(intent);
					break;

				default:
					break;
				}
			}
		});
	}

	protected void showDialog() {
		String psd = SpUtil.getString(this, ConstantValue.MOBILE_SAFE_PSD, "");
		if (TextUtils.isEmpty(psd)) {
			// 1.初始设置密码对话框
			showSetPsdDialog();
		} else {
			// 2.确认密码对话框
			showConfirmPsdDialog();
		}
	}

	/**
	 * 确认密码的对话框
	 */
	private void showConfirmPsdDialog() {
		Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();

		final View view = View.inflate(this, R.layout.dialog_confirm_psd, null);
		// 让对话框显示自定义的界面
		//dialog.setView(view);
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();

		Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
		Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);

		bt_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				EditText et_confirm_psd = (EditText) view
						.findViewById(R.id.et_confirm_psd);

				String confirmPsd = et_confirm_psd.getText().toString();

				if (!TextUtils.isEmpty(confirmPsd)) {
					//将存储在sp中32位的密码，获取出来，然后将输入的密码同样进行md5，然后与sp中存储密码比对
					String psd = SpUtil.getString(getApplicationContext(),
							ConstantValue.MOBILE_SAFE_PSD, "");
					if (psd.equals(Md5Util.encoded(confirmPsd))) {
						// 进入手机应用
						Intent intent = new Intent(getApplicationContext(),
								SetupOverActivity.class);
						startActivity(intent);
						// 跳转到新的界面以后需要去隐藏对话框
						dialog.dismiss();
						// 存储密码
					} else {
						ToastUtil.show(getApplicationContext(), "确认密码错误");
					}
				} else {
					ToastUtil.show(getApplicationContext(), "请输入密码");
				}
			}
		});

		bt_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});

	}

	/**
	 * 设置密码对话框
	 */
	private void showSetPsdDialog() {
		Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();

		final View view = View.inflate(this, R.layout.dialog_set_psd, null);
		// 让对话框显示自定义的界面
		//dialog.setView(view);
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();

		Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
		Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);

		bt_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				EditText et_set_psd = (EditText) view
						.findViewById(R.id.et_set_psd);
				EditText et_confirm_psd = (EditText) view
						.findViewById(R.id.et_confirm_psd);

				String psd = et_set_psd.getText().toString();
				String confirmPsd = et_confirm_psd.getText().toString();

				if (!TextUtils.isEmpty(psd) && !TextUtils.isEmpty(confirmPsd)) {
					if (psd.equals(confirmPsd)) {
						// 进入手机应用
						Intent intent = new Intent(getApplicationContext(),
								SetupOverActivity.class);
						startActivity(intent);
						// 跳转到新的界面以后需要去隐藏对话框
						dialog.dismiss();
						// 存储密码
						SpUtil.putString(getApplicationContext(),
								ConstantValue.MOBILE_SAFE_PSD,
								Md5Util.encoded(confirmPsd));
					} else {
						ToastUtil.show(getApplicationContext(), "确认密码错误");
					}
				} else {
					ToastUtil.show(getApplicationContext(), "请输入密码");
				}
			}
		});

		bt_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});

	}

	private void initUI() {
		gv_home = (GridView) findViewById(R.id.gv_home);
	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// 条目总数
			return mTitleStrs.length;
		}

		@Override
		public Object getItem(int position) {
			return mTitleStrs[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(getApplicationContext(),
					R.layout.gridview_item, null);
			ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
			TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
			iv_icon.setBackgroundResource(mDrawableIds[position]);
			tv_title.setText(mTitleStrs[position]);
			return view;
		}

	}
}
