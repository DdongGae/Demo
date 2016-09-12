package com.example.securityguard.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewDebug.ExportedProperty;
import android.widget.TextView;

public class FocusTextView extends TextView {
	// 使用在通过java代码创建控件
	public FocusTextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	// 由系统调用（带属性+上下文环境构造方法）
	public FocusTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	// 由系统调用（带属性+上下文环境构造方法+布局文件中定义样式文件构造方法）
	public FocusTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	//重写构造方法
	@Override
	@ExportedProperty(category = "focus")
	public boolean isFocused() {
		// TODO Auto-generated method stub
		return true;
	}
	
}
