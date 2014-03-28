package com.example.phone4;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;



/**
 * 把BroadcastReceiver注册在Manifest上的效果是：
 * 即使应用程序被关闭了。这个广播接收器还可以接受相应的广播。
 * 此电话系统要的就是这种效果。所以应该注册在Manifest上。
 * */
public class tp extends BroadcastReceiver  {

//	Context mcontext;
//	static WindowManager wm;
//	WindowManager.LayoutParams wmParams;
//	static View view1;
	

	
 /*public  tp(){
	 
	 System.out.println("TP receive的构造函数");

	
 }*/
 

public void onReceive(Context context, Intent intent) {

	
	 System.out.println("Broadcast的onReceive函数被调用！");
		  
	 

  TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
  
 
  
  switch (tm.getCallState()) {

  case TelephonyManager.CALL_STATE_RINGING:// 来电响铃

	   try {
	
	    String phoneNumber = intent.getStringExtra("incoming_number");	   
		 //  int number= Integer.parseInt(phoneNumber);//电话号码转换成int数据传出去。。不行，int类型最长十个数字
		   System.out.println("startService(intent)???");
		  
		   intent.setClass(context,MyService.class);
		   int a[]={1,0};
		   intent.putExtra("num",a);
		   intent.putExtra("telNumber", phoneNumber);
		   context.startService(intent);
		   		   
		   System.out.println("startService(intent)!!!!");
		   	
	   } catch (Exception e) {
	    e.printStackTrace();
	   }

   break;// 响铃

  case TelephonyManager.CALL_STATE_OFFHOOK: // 来电接通 去电拨出
	  
	  System.out.println("来电接通。去点拨出。");

	  try {
		   intent.setClass(context,MyService.class);
		   int a[]={2,0};
		   intent.putExtra("num",a);
		   context.startService(intent);
		
	  	} catch (Exception e) {
	  		// TODO: handle exception
	  	}
	  
   break;// 摘机

  case TelephonyManager.CALL_STATE_IDLE: // 来去电电话挂断
	  
	  try {
		  intent.setClass(context,MyService.class);
		  int a[]={3,0};
		  intent.putExtra("num",a);
		  context.startService(intent); 

	  	} catch (Exception e) {
	  		// TODO: handle exception
	  	}	    

   break;// 挂机

  }

 }	

	
}



