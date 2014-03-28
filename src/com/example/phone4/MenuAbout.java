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
		
		textview.setText("  您好，亲。感谢您使用这款软件，希望此软件给您的手机生活带来了便利。" +
				"  本人是华航的一名学生，这款软件是我在课余时间编写的，并且是我编写的第一款手机软件，多少肯定存在一些不完善的地方。" +
				"在您使用的过程当中如果发现有什么问题，或者您还需要一些其他另外的功能，欢迎您跟我联系，我会根据您的要求一步步完善此软件。" +
				"如果您觉得此软件还不错可以介绍给您的朋友使用。方便大家的生活。如果您想感谢软件开发者，下面的广告也是不错的哦，如果您有兴趣可以浏览一下。");
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		finish();
	}

}
