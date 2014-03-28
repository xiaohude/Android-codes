package com.example.phone4;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;



/**
 * ��BroadcastReceiverע����Manifest�ϵ�Ч���ǣ�
 * ��ʹӦ�ó��򱻹ر��ˡ�����㲥�����������Խ�����Ӧ�Ĺ㲥��
 * �˵绰ϵͳҪ�ľ�������Ч��������Ӧ��ע����Manifest�ϡ�
 * */
public class tp extends BroadcastReceiver  {

//	Context mcontext;
//	static WindowManager wm;
//	WindowManager.LayoutParams wmParams;
//	static View view1;
	

	
 /*public  tp(){
	 
	 System.out.println("TP receive�Ĺ��캯��");

	
 }*/
 

public void onReceive(Context context, Intent intent) {

	
	 System.out.println("Broadcast��onReceive���������ã�");
		  
	 

  TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
  
 
  
  switch (tm.getCallState()) {

  case TelephonyManager.CALL_STATE_RINGING:// ��������

	   try {
	
	    String phoneNumber = intent.getStringExtra("incoming_number");	   
		 //  int number= Integer.parseInt(phoneNumber);//�绰����ת����int���ݴ���ȥ�������У�int�����ʮ������
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

   break;// ����

  case TelephonyManager.CALL_STATE_OFFHOOK: // �����ͨ ȥ�粦��
	  
	  System.out.println("�����ͨ��ȥ�㲦����");

	  try {
		   intent.setClass(context,MyService.class);
		   int a[]={2,0};
		   intent.putExtra("num",a);
		   context.startService(intent);
		
	  	} catch (Exception e) {
	  		// TODO: handle exception
	  	}
	  
   break;// ժ��

  case TelephonyManager.CALL_STATE_IDLE: // ��ȥ��绰�Ҷ�
	  
	  try {
		  intent.setClass(context,MyService.class);
		  int a[]={3,0};
		  intent.putExtra("num",a);
		  context.startService(intent); 

	  	} catch (Exception e) {
	  		// TODO: handle exception
	  	}	    

   break;// �һ�

  }

 }	

	
}



