package com.example.phone4;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MenuAbout extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu_about);
		TextView textview = new TextView(this);
		
		textview.setText("  ���ã��ס���л��ʹ����������ϣ��������������ֻ���������˱�����" +
				"  �����ǻ�����һ��ѧ���������������ڿ���ʱ���д�ģ��������ұ�д�ĵ�һ���ֻ���������ٿ϶�����һЩ�����Ƶĵط���" +
				"����ʹ�õĹ��̵������������ʲô���⣬����������ҪһЩ��������Ĺ��ܣ���ӭ��������ϵ���һ��������Ҫ��һ�������ƴ������" +
				"��������ô������������Խ��ܸ���������ʹ�á������ҵ������������л��������ߣ�����Ĺ��Ҳ�ǲ����Ŷ�����������Ȥ�������һ�¡�");
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		finish();
	}

}
