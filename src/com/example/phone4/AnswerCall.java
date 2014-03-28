package com.example.phone4;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

public class AnswerCall {
	
	
	
	//就是出在这个函数这问题了。如果有这个函数，每次来电话时刚响一下就变成震动了。把这个函数注销了就没有这个情况了
	//下次改进试着用反射调用来解决接电话问题。
	public synchronized void answerRingingCall(Context context) {
		// TODO Auto-generated method stub
		  
		  Log.e("answerring","000000000000000");

		     //据说该方法只能用于Android2.3及2.3以上的版本上，但本人在2.2上测试可以使用
		     try  {
		    	//插耳机
		         Intent localIntent1 = new Intent(Intent.ACTION_HEADSET_PLUG);
		         localIntent1.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		         localIntent1.putExtra("state", 1);
		         localIntent1.putExtra("microphone", 1);
		         localIntent1.putExtra("name", "Headset");
		         context.sendOrderedBroadcast(localIntent1, "android.permission.CALL_PRIVILEGED");
		       //按下耳机按钮
		         Intent localIntent2 = new Intent(Intent.ACTION_MEDIA_BUTTON);
		         KeyEvent localKeyEvent1 = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK);
		         localIntent2.putExtra("android.intent.extra.KEY_EVENT", localKeyEvent1);
		         context.sendOrderedBroadcast(localIntent2, "android.permission.CALL_PRIVILEGED");
		       //放开耳机按钮
		         Intent localIntent3 = new Intent(Intent.ACTION_MEDIA_BUTTON);
		         KeyEvent localKeyEvent2 = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK);
		         localIntent3.putExtra("android.intent.extra.KEY_EVENT", localKeyEvent2);
		         context.sendOrderedBroadcast(localIntent3, "android.permission.CALL_PRIVILEGED");
		       //拔出耳机
		         Intent localIntent4 = new Intent(Intent.ACTION_HEADSET_PLUG);
		         localIntent4.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		         localIntent4.putExtra("state", 0);
		         localIntent4.putExtra("microphone", 1);
		         localIntent4.putExtra("name", "Headset");
		         context.sendOrderedBroadcast(localIntent4, "android.permission.CALL_PRIVILEGED");
		         
		   }  catch (Exception e)   {
		         e.printStackTrace();
		   }
		     Log.e("answerring","1111111111111111");
		     
		}



}
