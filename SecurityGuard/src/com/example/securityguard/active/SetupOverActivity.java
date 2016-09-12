package com.example.securityguard.active;

import com.example.securityguard.R;
import com.example.securityguard.MoilbSetactive.Setup1Activity;
import com.example.securityguard.utils.ConstantValue;
import com.example.securityguard.utils.SpUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class SetupOverActivity extends Activity {
	private TextView tv_safe_number;
	private TextView tv_reset_setup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		boolean setup_over=SpUtil.getBoolean(this, ConstantValue.SETUP_OVER, false);
		if(setup_over){
			//密码输入成功，四个导航界面设置完成
			setContentView(R.layout.activity_setup_over);
			
			initUI();
		}else{
			//密码输入成功，四个导航界面没有设置完成
			Intent intent=new Intent(this,Setup1Activity.class);
			startActivity(intent);
			finish();
		}
	}

	private void initUI() {
		tv_safe_number = (TextView) findViewById(R.id.tv_safe_number);
		String phone=SpUtil.getString(this, ConstantValue.CONTACT_PHONE, "");
		tv_safe_number.setText(phone);
		
		tv_reset_setup = (TextView) findViewById(R.id.tv_reset_setup);
		tv_reset_setup.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(getApplicationContext(),Setup1Activity.class);
				startActivity(intent);
				finish();
			}
		});
	}
}
