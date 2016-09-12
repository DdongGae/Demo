package com.example.securityguard.receiver;

import com.example.securityguard.engine.ProcessInfoProvider;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class KillProcessReceived extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// 杀死进程
		ProcessInfoProvider.killAll(context);

	}

}
