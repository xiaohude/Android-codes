package com.example.phone4;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

public class AnswerCall {
	
	
	
	//���ǳ�����������������ˡ���������������ÿ�����绰ʱ����һ�¾ͱ�����ˡ����������ע���˾�û����������
	//�´θĽ������÷������������ӵ绰���⡣
	public synchronized void answerRingingCall(Context context) {
		// TODO Auto-generated method stub
		  
		  Log.e("answerring","000000000000000");

		     //��˵�÷���ֻ������Android2.3��2.3���ϵİ汾�ϣ���������2.2�ϲ��Կ���ʹ��
		     try  {
		    	//�����
		         Intent localIntent1 = new Intent(Intent.ACTION_HEADSET_PLUG);
		         localIntent1.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		         localIntent1.putExtra("state", 1);
		         localIntent1.putExtra("microphone", 1);
		         localIntent1.putExtra("name", "Headset");
		         context.sendOrderedBroadcast(localIntent1, "android.permission.CALL_PRIVILEGED");
		       //���¶�����ť
		         Intent localIntent2 = new Intent(Intent.ACTION_MEDIA_BUTTON);
		         KeyEvent localKeyEvent1 = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK);
		         localIntent2.putExtra("android.intent.extra.KEY_EVENT", localKeyEvent1);
		         context.sendOrderedBroadcast(localIntent2, "android.permission.CALL_PRIVILEGED");
		       //�ſ�������ť
		         Intent localIntent3 = new Intent(Intent.ACTION_MEDIA_BUTTON);
		         KeyEvent localKeyEvent2 = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK);
		         localIntent3.putExtra("android.intent.extra.KEY_EVENT", localKeyEvent2);
		         context.sendOrderedBroadcast(localIntent3, "android.permission.CALL_PRIVILEGED");
		       //�γ�����
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
