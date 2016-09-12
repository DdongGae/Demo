package com.example.securityguard.view;

import com.example.securityguard.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingItemView extends RelativeLayout {

	private static final String NAMESPACE = "http://schemas.android.com/apk/res/com.example.securityguard";
	private CheckBox cb_box;
	private TextView tv_des;
	private String mDestitle;
	private String mDesoff;
	private String mDeson;
	public SettingItemView(Context context) {
		this(context,null);
	}

	public SettingItemView(Context context, AttributeSet attrs) {
		this(context, attrs,0);
		// TODO Auto-generated constructor stub
	}

	public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
		View.inflate(context, R.layout.setting_item_view, this);
		//自定义组件控件中的标题描述
		TextView tv_title=(TextView) findViewById(R.id.tv_title);
		tv_des = (TextView) findViewById(R.id.tv_des);
		cb_box = (CheckBox) findViewById(R.id.cb_box);
		
		//获取自定义以及原生属性的操作,写在此处,AttributeSet attrs对象中获取
		initAttrs(attrs);
		
		tv_title.setText(mDestitle);
	}
	/**
	 * @param attrs 构造方法中维护好的属性集合
	 * 返回属性集合中自定义属性属性值
	 */
	private void initAttrs(AttributeSet attrs) {
		mDestitle = attrs.getAttributeValue(NAMESPACE, "destitle");
		mDesoff = attrs.getAttributeValue(NAMESPACE, "desoff");
		mDeson = attrs.getAttributeValue(NAMESPACE, "deson");
	}

	/**
	 * 判断是否开启的方法
	 */
	public boolean isCheck(){
		return cb_box.isChecked();
	}
	/**
	 * @param isCheck 作为是否开启的变量，由点击过程中去做传递
	 */
	public void setCheck(boolean isCheck) {
		// TODO Auto-generated method stub
		cb_box.setChecked(isCheck);
		if(isCheck){
			tv_des.setText(mDeson);
		}else{
			tv_des.setText(mDesoff);
		}
	}

}
