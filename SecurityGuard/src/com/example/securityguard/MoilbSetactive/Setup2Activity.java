package com.example.securityguard.MoilbSetactive;

import com.example.securityguard.R;
import com.example.securityguard.utils.ConstantValue;
import com.example.securityguard.utils.SpUtil;
import com.example.securityguard.utils.ToastUtil;
import com.example.securityguard.view.SettingItemView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

public class Setup2Activity extends Activity {
	private SettingItemView siv_sim_bound;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup2);

		initUI();
	}

	private void initUI() {
		siv_sim_bound = (SettingItemView) findViewById(R.id.siv_sim_bound);
		// 1.回显（读取已有的绑定状态，用作显示，sp中是否存储了sim卡的序列号）
		String sim_number = SpUtil
				.getString(this, ConstantValue.SIM_NUMBER, "");
		// 2.判断是否序列卡号为""
		if (TextUtils.isEmpty(sim_number)) {
			siv_sim_bound.setCheck(false);
		} else {
			siv_sim_bound.setCheck(true);
		}
		siv_sim_bound.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 3.获取原有状态()
				boolean isCheck = siv_sim_bound.isCheck();
				// 4.将原有的状态取反
				// 5.设置给当前条目，存储(序列头号)
				siv_sim_bound.setCheck(!isCheck);
				if (!isCheck) {
					// 6.存储(序列卡号)
					// 获取sim卡序列卡号TelephonyManagerw
					TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
					// 获取sim卡的序列卡号
					String simSerialNumber = manager.getSimSerialNumber();
					// 存储
					SpUtil.putString(getApplicationContext(),
							ConstantValue.SIM_NUMBER, simSerialNumber);
				} else {
					// 7.将存储序列卡号的节点，从sp中删除掉
					SpUtil.remove(getApplicationContext(),
							ConstantValue.SIM_NUMBER);
				}
			}
		});
	}

	public void nextPage(View view) {
		String serialNumber = SpUtil.getString(this, ConstantValue.SIM_NUMBER,
				"");
		if (!TextUtils.isEmpty(serialNumber)) {
			Intent intent = new Intent(getApplicationContext(),
					Setup3Activity.class);
			startActivity(intent);
			finish();

			overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
		} else {
			ToastUtil.show(this, "请绑定信用卡");
		}
	}

	public void prePage(View view) {
		Intent intent = new Intent(getApplicationContext(),
				Setup1Activity.class);
		startActivity(intent);
		finish();
		
		overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);
	}

}
