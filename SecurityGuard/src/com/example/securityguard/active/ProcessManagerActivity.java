package com.example.securityguard.active;

import java.util.ArrayList;
import java.util.List;

import com.example.securityguard.R;
import com.example.securityguard.db.domain.AppInfo;
import com.example.securityguard.db.domain.ProcessInfo;
import com.example.securityguard.engine.AppInfoProvider;
import com.example.securityguard.engine.ProcessInfoProvider;
import com.example.securityguard.utils.ConstantValue;
import com.example.securityguard.utils.SpUtil;
import com.example.securityguard.utils.ToastUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

public class ProcessManagerActivity extends Activity implements OnClickListener {
	private TextView tv_process_count, tv_memory_info, tv_des;
	private ListView lv_process_list;
	private Button bt_select_all, bt_select_reverse, bt_clear, bt_setting;
	private int mProcessCount;
	private List<ProcessInfo> mProcessInfoList;
	private ArrayList<ProcessInfo> mSystemList;
	private ArrayList<ProcessInfo> mCustomerList;
	private MyAdater mAdater;
	private ProcessInfo mProcessInfo;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			mAdater = new MyAdater();
			lv_process_list.setAdapter(mAdater);

			if (tv_des != null && mCustomerList != null) {
				tv_des.setText("用户进程(" + mCustomerList.size() + ")");
			}
		};
	};
	private long mAvailSpace;
	private String mStrTotalSpace;

	class MyAdater extends BaseAdapter {

		// 获取数据适配器中条目类型的总数
		@Override
		public int getViewTypeCount() {
			return super.getViewTypeCount() + 1;
		}

		// 指定索引指向的条目类型
		@Override
		public int getItemViewType(int position) {
			if (position == 0 || position == mCustomerList.size() + 1) {
				// 返回0,代表纯文字条目的状态码
				return 0;
			} else {
				// 返回1,代表图片+文字条目的状态码
				return 1;
			}
		}

		// listview中添加两个描述条目
		@Override
		public int getCount() {
			if (SpUtil.getBoolean(getApplicationContext(),
					ConstantValue.SHOW_SYSTEM, false)) {
				return mCustomerList.size() + mSystemList.size() + 2;
			} else {
				return mCustomerList.size() + 1;
			}
		}

		@Override
		public ProcessInfo getItem(int position) {
			if (position == 0 || position == mCustomerList.size() + 1) {
				return null;
			} else {
				if (position < mCustomerList.size() + 1) {
					return mCustomerList.get(position - 1);
				} else {
					// 返回系统进程对应条目的对象
					return mSystemList.get(position - mCustomerList.size() - 2);
				}
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			int type = getItemViewType(position);
			if (type == 0) {
				// 展示灰色条目
				ViewTitleHolder holder = null;
				if (convertView == null) {
					convertView = View.inflate(getApplicationContext(),
							R.layout.listview_app_item_title, null);
					holder = new ViewTitleHolder();
					holder.tv_title = (TextView) convertView
							.findViewById(R.id.tv_title);
					convertView.setTag(holder);
				} else {
					holder = (ViewTitleHolder) convertView.getTag();
				}
				if (position == 0) {
					holder.tv_title.setText("用户进程(" + mCustomerList.size()
							+ ")");
				} else {
					holder.tv_title.setText("系统进程(" + mSystemList.size() + ")");
				}
				return convertView;
			} else {
				// 展示图片+文字条目
				ViewHolder holder = null;
				if (convertView == null) {
					convertView = View.inflate(getApplicationContext(),
							R.layout.listview_process_item, null);
					holder = new ViewHolder();
					holder.iv_icon = (ImageView) convertView
							.findViewById(R.id.iv_icon);
					holder.tv_name = (TextView) convertView
							.findViewById(R.id.tv_name);
					holder.tv_memory_info = (TextView) convertView
							.findViewById(R.id.tv_memory_info);
					holder.cb_box = (CheckBox) convertView
							.findViewById(R.id.cb_box);
					convertView.setTag(holder);
				} else {
					holder = (ViewHolder) convertView.getTag();
				}
				holder.iv_icon.setBackgroundDrawable(getItem(position).icon);
				holder.tv_name.setText(getItem(position).name);
				String strSize = Formatter.formatFileSize(
						getApplicationContext(), getItem(position).memSize);
				holder.tv_memory_info.setText(strSize);

				// 本进程不能被选中，所以先将checkbox隐藏掉
				if (getItem(position).packageName.equals(getPackageName())) {
					holder.cb_box.setVisibility(View.GONE);
				} else {
					holder.cb_box.setVisibility(View.VISIBLE);
				}
				holder.cb_box.setChecked(getItem(position).isCheck);
				return convertView;
			}

		}

	}

	static class ViewHolder {
		ImageView iv_icon;
		TextView tv_name;
		TextView tv_memory_info;
		CheckBox cb_box;
	}

	static class ViewTitleHolder {
		TextView tv_title;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_process_manager);

		initUI();
		initTitleData();
		initListData();
	}

	private void initListData() {
		getData();
	}

	private void getData() {
		new Thread() {
			public void run() {
				mProcessInfoList = ProcessInfoProvider
						.getProcessInfo(getApplicationContext());
				mSystemList = new ArrayList<ProcessInfo>();
				mCustomerList = new ArrayList<ProcessInfo>();
				for (ProcessInfo info : mProcessInfoList) {
					if (info.isSystem) {
						// 系统进程
						mSystemList.add(info);
					} else {
						// 用户进程
						mCustomerList.add(info);
					}
				}
				mHandler.sendEmptyMessage(0);
			};
		}.start();
	}

	private void initTitleData() {
		mProcessCount = ProcessInfoProvider.getProcessCount(this);
		tv_process_count.setText("进程总数：" + mProcessCount);

		mAvailSpace = ProcessInfoProvider.getAvailSpace(this);
		String strAvailSpace = Formatter.formatFileSize(this, mAvailSpace);

		// 总运行内存大小,并且格式化
		long totalSpace = ProcessInfoProvider.getTotalSpace(this);
		mStrTotalSpace = Formatter.formatFileSize(this, totalSpace);

		tv_memory_info.setText("剩余/总共:" + strAvailSpace + "/" + mStrTotalSpace);
	}

	private void initUI() {
		tv_process_count = (TextView) findViewById(R.id.tv_process_count);
		tv_memory_info = (TextView) findViewById(R.id.tv_memory_info);

		tv_des = (TextView) findViewById(R.id.tv_des);

		lv_process_list = (ListView) findViewById(R.id.lv_process_list);

		bt_select_all = (Button) findViewById(R.id.bt_select_all);
		bt_select_reverse = (Button) findViewById(R.id.bt_select_reverse);
		bt_clear = (Button) findViewById(R.id.bt_clear);
		bt_setting = (Button) findViewById(R.id.bt_setting);

		bt_select_all.setOnClickListener(this);
		bt_select_reverse.setOnClickListener(this);
		bt_clear.setOnClickListener(this);
		bt_setting.setOnClickListener(this);

		lv_process_list.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (mCustomerList != null && mSystemList != null) {
					if (firstVisibleItem >= mCustomerList.size() + 1) {
						tv_des.setText("系统进程(" + mSystemList.size() + ")");
					} else {
						tv_des.setText("用户进程(" + mCustomerList.size() + ")");
					}
				}
			}
		});

		lv_process_list.setOnItemClickListener(new OnItemClickListener() {
			// view选中条目指向的view对象
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0 || position == mCustomerList.size() + 1) {
					return;
				} else {
					if (position < mCustomerList.size() + 1) {
						mProcessInfo = mCustomerList.get(position - 1);
					} else {
						// 返回系统应用对应条目的对象
						mProcessInfo = mSystemList.get(position
								- mCustomerList.size() - 2);
					}
					if (mProcessInfo != null) {
						if (!mProcessInfo.packageName.equals(getPackageName())) {
							// 选中条目指向的对象和本应用的包名不一致，才需要去状态取反和设置单选框状态
							// 状态取反
							mProcessInfo.isCheck = !mProcessInfo.isCheck;
							// checkbox显示状态切换
							// 通过选中条目的view对象，findViewById找到此条目指向的cb_box，然后切换其状态
							CheckBox cb_box = (CheckBox) view
									.findViewById(R.id.cb_box);
							cb_box.setChecked(mProcessInfo.isCheck);
						}
					}
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_select_all:
			selectAll();
			break;
		case R.id.bt_select_reverse:
			selectReverse();
			break;
		case R.id.bt_clear:
			clearAll();
			break;
		case R.id.bt_setting:
			setting();
			break;
		}
	}

	private void setting() {
		Intent intent = new Intent(this, ProcessSettingActivity.class);
		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//通知适配器刷新
		if(mAdater!=null){
			mAdater.notifyDataSetChanged();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 清理选中进程
	 */
	private void clearAll() {
		// 1.获取选中进程
		// 2.创建一个记录需要杀死的进程的集合
		List<ProcessInfo> killProcessList = new ArrayList<ProcessInfo>();
		for (ProcessInfo processInfo : mCustomerList) {
			if (processInfo.getPackageName().equals(getPackageManager())) {
				continue;
			}
			if (processInfo.isCheck) {
				// 3.记录需要杀死的用户进程
				killProcessList.add(processInfo);
			}
		}
		for (ProcessInfo processInfo : mSystemList) {
			if (processInfo.isCheck) {
				// 4.记录需要杀死的用户进程
				killProcessList.add(processInfo);
			}
		}
		// 5.循环遍历killProcessList，然后移除mCustomerList和mSystemList中的对象
		long totalReleaseSpace = 0;
		for (ProcessInfo processInfo : killProcessList) {
			// 6.判断当前进程在哪个集合中，从所在集合中移除
			if (mCustomerList.contains(processInfo)) {
				mCustomerList.remove(processInfo);
			}
			if (mSystemList.contains(processInfo)) {
				mSystemList.remove(processInfo);
			}
			// 7.杀死记录在killProcessList中的进程
			ProcessInfoProvider.killProcess(this, processInfo);

			// 记录释放空间的总大小
			totalReleaseSpace += processInfo.memSize;
		}
		// 8.在集合改变后需要通知数据适配器刷新
		if (mAdater != null) {
			mAdater.notifyDataSetChanged();
		}
		// 9.更新进程总数
		mProcessCount -= killProcessList.size();
		// 10.更新可用剩余空间
		mAvailSpace += totalReleaseSpace;
		// 11.更新进程总数和剩余空间
		tv_process_count.setText("进程总数:" + mProcessCount);
		tv_memory_info.setText("剩余/总共"
				+ Formatter.formatFileSize(this, mAvailSpace) + "/"
				+ mStrTotalSpace);
		// 12.通过吐司弹出杀死的进程个数和释放的空间
		String totalRelease = Formatter.formatFileSize(this, totalReleaseSpace);
		ToastUtil.show(getApplicationContext(), String.format(
				"杀死了%d进程，释放了%s空间", killProcessList.size(), totalRelease));
	}

	private void selectReverse() {
		// 1.将所有集合中的对象上isCheck字段设置为true，代表全选，排除当前应用
		for (ProcessInfo processInfo : mCustomerList) {
			if (processInfo.getPackageName().equals(getPackageManager())) {
				continue;
			}
			processInfo.isCheck = !processInfo.isCheck;
		}
		for (ProcessInfo processInfo : mSystemList) {
			processInfo.isCheck = !processInfo.isCheck;
		}
		// 2.通知数据适配器进行刷新
		if (mAdater != null) {
			mAdater.notifyDataSetChanged();
		}
	}

	private void selectAll() {
		// 1.将所有集合中的对象上isCheck字段设置为true，代表全选，排除当前应用
		for (ProcessInfo processInfo : mCustomerList) {
			if (processInfo.getPackageName().equals(getPackageManager())) {
				continue;
			}
			processInfo.isCheck = true;
		}
		for (ProcessInfo processInfo : mSystemList) {
			processInfo.isCheck = true;
		}
		// 2.通知数据适配器进行刷新
		if (mAdater != null) {
			mAdater.notifyDataSetChanged();
		}
	}
}