package com.example.securityguard.service;

import android.app.Service;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;

public class LocationService extends Service {
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		// 获取手机的经纬度坐标
		// 1.获取位置管理者对象
		LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		// 2.以最优的方式获取经纬度坐标（）
		Criteria criteria = new Criteria();
		// 允许花费
		criteria.setCostAllowed(true);
		criteria.setAccuracy(Criteria.ACCURACY_FINE);// 指定获取经纬度的精确度
		String bestProvider = lm.getBestProvider(criteria, true);
		// 3.在一定时间间隔，移动一定距离以后获取经纬度坐标
		MyLocationListener mylocationlistener = new MyLocationListener();
		lm.requestLocationUpdates(bestProvider, 0, 0, mylocationlistener);
	}

	class MyLocationListener implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {
			// 经度
			double longitude = location.getLongitude();
			// 纬度
			double altitude = location.getAltitude();
			// 4.发送短信
			SmsManager sms = SmsManager.getDefault();
			sms.sendTextMessage("5556", "经度：" + longitude, "纬度:" + altitude,
					null, null);
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
		}

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
