package com.example.securityguard.BlackNumberActivity;

import java.util.List;

import com.example.securityguard.R;
import com.example.securityguard.db.dao.BlackNumberDao;
import com.example.securityguard.db.domain.BlackNumberInfo;
import com.example.securityguard.utils.ToastUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class BlackNumberActivity extends Activity {
	private Button bt_add;
	private ListView lv_blacknumber;
	private BlackNumberDao mDao;
	private MyAdapter mAdapter;
	private int mCount;
	private int mode = 1;
	private boolean mIsLoad = false;
	private List<BlackNumberInfo> mBlackNumberList;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (mAdapter == null) {
				mAdapter = new MyAdapter();
				lv_blacknumber.setAdapter(mAdapter);
			} else {
				mAdapter.notifyDataSetChanged();
			}
		};
	};

	class MyAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return mBlackNumberList.size();
		}

		@Override
		public Object getItem(int position) {
			return mBlackNumberList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			// 1.复用convertView
			// 复用ViewHolder步骤一
			ViewHolder holder = null;
			if (convertView == null) {

				convertView = View.inflate(getApplicationContext(),
						R.layout.listview_blacknumber_item, null);
				// 2.减少findViewById()次数
				// 复用viewHolder步骤三
				holder = new ViewHolder();
				// 复用viewHolder步骤四
				holder.tv_phone = (TextView) convertView
						.findViewById(R.id.tv_phone);
				holder.tv_mode = (TextView) convertView
						.findViewById(R.id.tv_mode);
				holder.iv_delete = (ImageView) convertView
						.findViewById(R.id.iv_delete);
				// 复用viewHolder步骤五
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.iv_delete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 1.数据库的删除
					mDao.delete(mBlackNumberList.get(position).phone);
					// 2.集合中的删除，通知数据适配器刷新
					mBlackNumberList.remove(position);
					// 3.通知数据适配器刷新
					if (mAdapter != null) {
						mAdapter.notifyDataSetChanged();
					}
				}
			});
			holder.tv_phone.setText(mBlackNumberList.get(position).phone);
			int mode = Integer.parseInt(mBlackNumberList.get(position).mode);
			switch (mode) {
			case 1:
				holder.tv_mode.setText("拦截短信");
				break;

			case 2:
				holder.tv_mode.setText("拦截电话");
				break;

			case 3:
				holder.tv_mode.setText("拦截所有");
				break;
			}
			return convertView;
		}

	}

	// 复用viewHolder步骤二
	static class ViewHolder {
		TextView tv_phone;
		TextView tv_mode;
		ImageView iv_delete;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blacknumber);

		initUI();
		initData();
	}

	private void initData() {
		// 获取数据库中所有电话号码
		new Thread() {

			public void run() {
				// 1.获取操作黑名单数据库的对象
				mDao = BlackNumberDao.getInstance(getApplicationContext());
				// 2.查询部分数据
				mBlackNumberList = mDao.find(0);
				mCount = mDao.getCount();
				// 3.通过消息机制告知主线程可以去使用包含数据的集合
				mHandler.sendEmptyMessage(0);
			}
		}.start();
	}

	private void initUI() {
		bt_add = (Button) findViewById(R.id.bt_add);
		lv_blacknumber = (ListView) findViewById(R.id.lv_blacknumber);
		bt_add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog();
			}
		});

		// 监听其滚动状态
		lv_blacknumber.setOnScrollListener(new OnScrollListener() {

			// 滚动过程中，状态发生改变调用方法
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (mBlackNumberList != null) {
					// 条件一:滚动到停止状态
					// 条件二:最后一个条目可见
					if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
							&& lv_blacknumber.getLastVisiblePosition() >= mBlackNumberList
									.size() - 1 && !mIsLoad) {
						// mIsLoad防止重复加载的变量
						if (mCount > mBlackNumberList.size()) {
							// 加载下一页数据
							new Thread() {
								public void run() {
									// 1.获取操作黑名单数据库的对象
									mDao = BlackNumberDao
											.getInstance(getApplicationContext());
									// 2.查询部分数据(20)
									List<BlackNumberInfo> moreData = mDao
											.find(mBlackNumberList.size());
									// 3.添加下一页数据的过程
									mBlackNumberList.addAll(moreData);
									// 4.通知数据适配器刷新
									mHandler.sendEmptyMessage(0);
								}
							}.start();
						}
					}
				}
			}

			// 滚动过程中调用方法
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

			}
		});
	}

	protected void showDialog() {
		Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
		View view = View.inflate(getApplicationContext(),
				R.layout.dialog_add_blacknumber, null);
		dialog.setView(view, 0, 0, 0, 0);
		final EditText et_phone = (EditText) findViewById(R.id.et_phone);
		RadioGroup rg_group = (RadioGroup) view.findViewById(R.id.rg_group);
		Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
		Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);
		// 监听其选中条目的切换过程
		rg_group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				switch (checkedId) {
				case R.id.rb_sms:
					// 拦截短信
					mode = 1;
					break;

				case R.id.rb_phone:
					// 拦截电话
					mode = 2;
					break;
				case R.id.rb_all:
					// 拦截所有
					mode = 3;
					break;
				}
			}
		});
		bt_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 1.获取输入框中的电话号码
				String phone = et_phone.getText().toString();
				if (!TextUtils.isEmpty(phone)) {
					// 2.数据库插入当前输入的拦截电话号码
					mDao.insert(phone, mode + "");
					// 3.让数据库和集合保持同步
					BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
					blackNumberInfo.phone = phone;
					blackNumberInfo.mode = mode + "";
					// 4.将对象插入到集合的最顶部
					mBlackNumberList.add(0, blackNumberInfo);
					// 5.通过数据适配器刷新(数据适配器的数据有改变了)
					if (mAdapter != null) {
						mAdapter.notifyDataSetChanged();
					}
					// 6.销毁对话框
					dialog.dismiss();
				} else {
					ToastUtil.show(getApplicationContext(), "请输入拦截号码");
				}
			}
		});
		bt_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.show();
	}
}
