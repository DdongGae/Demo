package com.example.securityguard.active;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.example.securityguard.R;
import com.example.securityguard.R.id;
import com.example.securityguard.utils.ConstantValue;
import com.example.securityguard.utils.SpUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressWarnings("deprecation")
@SuppressLint("NewApi")
public class SplashActivity extends Activity {

	private TextView tv_version_name;
	private int mLocalVersionCode;

	protected static final int ENTER_HOME = 101;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case ENTER_HOME:
				enterHome();
				break;

			default:
				break;
			}
		};
	};
	private RelativeLayout rl_root;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);
		// 初始化UI
		initUI();
		// 初始化数据
		initData();
		// 页面跳转
		pageJump();
		// 初始动画
		initAnimation();
		// 初始化数据库
		initDB();
		if (!SpUtil.getBoolean(this, ConstantValue.HAS_SHORTCUT, false)) {
			// 生成快捷方式
			initShortCut();
		}
	}

	/**
	 * 生成快捷方式
	 */
	private void initShortCut() {
		// 1.给intent维护图标，名称
		Intent intent = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");
		// 维护图标
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory
				.decodeResource(getResources(), R.drawable.ic_launcher));
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "安全卫士");
		// 2.点击快捷方式后跳转到activity
		// 维护开启的意图对象
		Intent shortCutIntent = new Intent("android.intent.action.HOME");
		shortCutIntent.addCategory("android.intent.category.DEFAULT");
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortCutIntent);

		// 3.发送广播
		sendBroadcast(intent);
		// 4.告之sp已经生成了快捷方式
		SpUtil.getBoolean(this, ConstantValue.HAS_SHORTCUT, true);
	}

	private void initDB() {
		// 1.归属地数据拷贝过程
		initAddressDB("address.db");
		// 2.常用号码数据库的拷贝过程
		initAddressDB("commonnum.db");
		// 3.拷贝病毒数据库
		initAddressDB("antivirus.db");
	}

	/**
	 * 拷贝数据库至files文件夹下
	 * 
	 * @param dbName
	 *            数据库名称
	 */
	private void initAddressDB(String dbName) {
		// 1.在files文件夹下创建同名数据库文件过程
		File files = getFilesDir();
		File file = new File(files, dbName);
		if (file.exists()) {
			return;
		}
		InputStream stream = null;
		FileOutputStream fos = null;
		// 2.输入流读取第三方资产目录下的文件
		try {
			stream = getAssets().open(dbName);
			// 3.将读取的内容写入到指定文件夹的文件中去
			fos = new FileOutputStream(file);
			// 4.每次的读取内容大小
			byte[] bs = new byte[1024];
			int temp = -1;
			while ((temp = stream.read(bs)) != -1) {
				fos.write(bs, 0, temp);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (stream != null && fos != null) {
				try {
					stream.close();
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 淡出动画设置
	 */
	private void initAnimation() {
		// TODO Auto-generated method stub
		AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
		alphaAnimation.setDuration(3000);
		rl_root.startAnimation(alphaAnimation);
	}

	/**
	 * 进入应用程序主界面
	 */
	protected void enterHome() {
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		// 在开启新的界面后，将导航界面关闭(导航界面只可见一次)
		finish();
	}

	private void pageJump() {
		// TODO Auto-generated method stub
		new Thread() {
			@Override
			public void run() {
				Message msg = Message.obtain();
				// 进入应用程序主界面
				msg.what = ENTER_HOME;
				mHandler.sendMessageDelayed(msg, 2000);
			}
		}.start();
	}

	/*
	 * 获取数据方法
	 */
	private void initData() {
		// TODO Auto-generated method stub
		// 1.应用版本名称
		tv_version_name.setText("版本名称：" + getVersionName());
		// 2.检测是否有更新
		mLocalVersionCode = getVersionCode();
		// 3.获取服务器版本号（客户端发请求，服务端给响应（json，xml））
	}

	private int getVersionCode() {
		// 1.包管理者对象packageManager
		PackageManager pm = getPackageManager();
		// 2.从包的管理者对象中，获取指定包名的基本信息（版本名称,版本号）
		try {
			PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
			// 3.获取版本名称
			return packageInfo.versionCode;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 获取版本名称：清单文件中
	 * 
	 * @return 应用版本名称 返回null代表异常
	 */
	private String getVersionName() {
		// 1.包管理者对象packageManager
		PackageManager pm = getPackageManager();
		// 2.从包的管理者对象中，获取指定包名的基本信息（版本名称,版本号）
		try {
			PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
			// 3.获取版本名称
			return packageInfo.versionName;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	private void initUI() {
		tv_version_name = (TextView) findViewById(id.tv_version_name);
		rl_root = (RelativeLayout) findViewById(id.rl_root);
	}
}
