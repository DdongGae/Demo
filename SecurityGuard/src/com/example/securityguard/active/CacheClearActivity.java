package com.example.securityguard.active;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;

import com.example.securityguard.R;

import android.app.Activity;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.DropBoxManager;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CacheClearActivity extends Activity {
	protected static final int UPDATE_CACHE_APP = 100;
	protected static final int CHECK_CACHE_APP = 101;
	protected static final int CHECK_FINISH = 102;
	protected static final int CLEAR_CACHE = 103;
	private Button bt_clear;
	private ProgressBar pb_bar;
	private TextView tv_name;
	private LinearLayout ll_add_text;
	private PackageManager mPm;
	private int mIndex = 0;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UPDATE_CACHE_APP:
				// 8.在线性布局中添加有缓存应用的条目
				View view = View.inflate(getApplicationContext(),
						R.layout.linearlayout_cache_item, null);
				ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
				TextView tv_item_name = (TextView) view
						.findViewById(R.id.tv_name);
				TextView tv_memory_info = (TextView) view
						.findViewById(R.id.tv_memory_info);
				ImageView iv_delete = (ImageView) view
						.findViewById(R.id.iv_delete);

				final CacheInfo cacheInfo = (CacheInfo) msg.obj;
				iv_icon.setBackgroundDrawable(cacheInfo.icon);
				tv_item_name.setText(cacheInfo.name);
				tv_memory_info.setText(Formatter.formatFileSize(
						getApplicationContext(), cacheInfo.cacheSize));

				ll_add_text.addView(view, 0);
				
				iv_delete.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						//清除单个选中应用缓存
						try {
							Class<?> clazz = Class.forName("android.content.pm.PackageManager");
							Method method = clazz.getMethod("deleteApplicationCacheFiles", String.class,
									IPackageStatsObserver.class);
							method.invoke(mPm, cacheInfo.packageName, new IPackageDataObserver.Stub() {
								
								@Override
								public void onRemoveCompleted(String packageName, boolean succeeded)
										throws RemoteException {
									//删除此应用缓存后，调用的方法
									
								}
							});
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
				});
				break;
			case CHECK_CACHE_APP:
				tv_name.setText((String) msg.obj);
				break;
			case CHECK_FINISH:
				tv_name.setText("扫描完成");
				break;
			case CLEAR_CACHE:
				//从布局中移除所有条目
				ll_add_text.removeAllViews();
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cache_clear);

		initUI();
		initData();
	}

	/**
	 * 遍历手机所有的应用，获取有缓存的应用，用作显示
	 */
	private void initData() {
		new Thread() {

			public void run() {
				mPm = getPackageManager();
				// 2.获取安装在手机上的所有的应用
				List<PackageInfo> installedPackages = mPm
						.getInstalledPackages(0);
				// 3.给进度条设置最大值（手机中所有应用的总数）
				pb_bar.setMax(installedPackages.size());
				// 4.遍历每一个应用，获取有缓存的应用信息
				for (PackageInfo packageInfo : installedPackages) {
					// 包名作为缓存信息的条件
					String packageName = packageInfo.packageName;
					getPackageCache(packageName);
					try {
						Thread.sleep(100 + new Random().nextInt(50));
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					mIndex++;
					pb_bar.setProgress(mIndex);
					// 每循环一次就将检测应用的名称发送给主线程显示
					Message msg = Message.obtain();
					msg.what = CHECK_CACHE_APP;
					String name = null;
					try {
						name = mPm.getApplicationInfo(packageName, 0)
								.loadLabel(mPm).toString();
					} catch (NameNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					msg.obj = name;
					mHandler.sendMessage(msg);
				}
				Message msg = Message.obtain();
				msg.what = CHECK_FINISH;
				mHandler.sendMessage(msg);
			};
		}.start();
	}

	class CacheInfo {
		public String name;
		public Drawable icon;
		public String packageName;
		public long cacheSize;
	}

	/**
	 * @param packageName
	 *            应用包名
	 */
	protected void getPackageCache(String packageName) {
		// 创建了一个IPackageStatsObserver.Stub子类的对象,并且实现了onGetStatsCompleted方法
		IPackageStatsObserver.Stub mStatsObserver = new IPackageStatsObserver.Stub() {

			public void onGetStatsCompleted(PackageStats stats,
					boolean succeeded) {
				// 4.获取指定包名缓存大小
				long cacheSize = stats.cacheSize;
				// 5.判断缓存大小是否大于0
				if (cacheSize > 0) {
					CacheInfo cacheInfo = null;
					// 6.告之主线程更新UI
					Message msg = Message.obtain();
					msg.what = UPDATE_CACHE_APP;
					// 7.维护有缓存应用的javabean
					try {
						cacheInfo = new CacheInfo();
						cacheInfo.cacheSize = cacheSize;
						cacheInfo.packageName = stats.packageName;
						cacheInfo.name = mPm
								.getApplicationInfo(stats.packageName, 0)
								.loadLabel(mPm).toString();
						cacheInfo.icon = mPm.getApplicationInfo(
								stats.packageName, 0).loadIcon(mPm);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					msg.obj = cacheInfo;
					mHandler.sendMessage(msg);
				}
			}
		};
		// 1.获取指定类的字节码文件
		try {
			Class<?> clazz = Class.forName("android.content.pm.PackageManager");
			// 2.获取调用方法对象
			Method method = clazz.getMethod("getPackageSizeInfo", String.class,
					IPackageStatsObserver.class);
			// 3.获取对象调用方法
			method.invoke(mPm, packageName, mStatsObserver);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void initUI() {
		bt_clear = (Button) findViewById(R.id.bt_clear);
		pb_bar = (ProgressBar) findViewById(R.id.pb_bar);
		tv_name = (TextView) findViewById(R.id.tv_name);
		ll_add_text = (LinearLayout) findViewById(R.id.ll_add_text);

		bt_clear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 1.获取指定类的字节码文件
				try {
					Class<?> clazz = Class
							.forName("android.content.pm.PackageManager");
					// 2.获取调用方法对象
					Method method = clazz.getMethod("freeStorageAndNotify",
							long.class, IPackageDataObserver.class);
					// 3.获取对象调用方法
					method.invoke(mPm, Long.MAX_VALUE,
							new IPackageDataObserver.Stub() {

								@Override
								public void onRemoveCompleted(
										String packageName, boolean succeeded)
										throws RemoteException {

									Message msg = Message.obtain();
									msg.what = CLEAR_CACHE;
									mHandler.sendMessage(msg);
								}
							});
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
}
