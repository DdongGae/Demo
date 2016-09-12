package com.example.securityguard.receiver;

import com.example.securityguard.utils.ConstantValue;
import com.example.securityguard.utils.SpUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		// 1.获取本地存储的sim卡序列号
		String spSimSerialNumber = SpUtil.getString(context,
				ConstantValue.SIM_NUMBER, "");
		// 2.获取当前插入手机的sim卡序列号
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(context.TELECOM_SERVICE);
		String simSerialNumber = tm.getSimSerialNumber();
		// 3.两个sim卡序列号比对
		if (!spSimSerialNumber.equals(simSerialNumber)) {
			// 4.如果序列号不一致，则给指定联系人发送短信
			SmsManager sm = SmsManager.getDefault();
			String phone = SpUtil.getString(context,
					ConstantValue.CONTACT_PHONE, "");
			sm.sendTextMessage(phone, null, "sim change!!", null, null);
		}
	}
}
