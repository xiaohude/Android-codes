package com.example.phone4;


import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class MyService extends android.app.Service{
	
	
	private WindowManager wm;
	private WindowManager.LayoutParams wmParams;
	private Button button;
	private Button button2;
	private Spinner mySpinner;//�����б�ť
	private ArrayAdapter<String> adapter; //�����б�ť��һ�����ò���
	private List<String> list = new ArrayList<String>();  //�����б������
	
	private File myRecAudioFile;
	private File myRecAudioDir;
	private File myPlayFile;
	private MediaRecorder mMediaRecorder01;
	private boolean sdCardExit;
	private boolean isOFFHOOK;
	private boolean isView;
	private boolean isStart;
	private boolean isRINGING=false;
	public static boolean isServe;
	private int time;
	AudioManager audioManager;
	public String telNumber=null;
	
	public MediaPlayer myPlayer1;
	public MediaPlayer myPlayer2;
	
	/*������״̬����ʾ��ʾ���õ��ı���*/
	 //����֪ͨ����Ϣ��������
    NotificationManager m_NotificationManager;
    Intent m_Intent;
    PendingIntent m_PendingIntent;
    //����Notification����
    Notification  m_Notification;
	
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		System.out.println("����service��onCreat������");
   /*״̬������ʾ���ܣ�*/
        //��ʼ��NotificationManager����
		m_NotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);				
		//���֪ͨʱת������
        m_Intent = new Intent(this, MainActivity.class);
        //��Ҫ�����õ��֪ͨʱ��ʾ���ݵ���
        m_PendingIntent = PendingIntent.getActivity(this, 0, m_Intent, 0);
        //����Notification����
        m_Notification = new Notification();
    /*״̬������ʾ���ܣ�*/
		
		
		//������ȡ�û�����
		SharedPreferences pref;
		pref=getSharedPreferences("xiaohu", 0);
		//final SharedPreferences.Editor editor = pref.edit();
		isServe=pref.getBoolean("isServe", true);//�ڶ��������൱�ڶ�isServe�ĳ�ʼ����
		time=pref.getInt("time", 30);
		
		/*��������Ϊ����� ������᲻���������ã�Ҫ���ᵼ�µ绰������ͱ������*/
//		audioManager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
//		audioManager.setMode(AudioManager.MODE_IN_CALL);
		
		
		//��¼����Ŀ¼�ļ���XiaoHuPhone
		File xiaohuDir=new File(Environment.getExternalStorageDirectory()+"/XiaoHuPhone");
		if(!xiaohuDir.exists())
		{
			Toast.makeText(this, "XiaoHuPhone�ļ��в����ڣ�", Toast.LENGTH_LONG).show();			
		}
		File xiaohu=new File(xiaohuDir+"/XiaoHu/xiaohu.amr");
		if(!xiaohu.exists())
		{
			Toast.makeText(this, "��δ������ʾ����", Toast.LENGTH_LONG).show();
		}
		
		new MediaPlayer();
		myPlayer1 = MediaPlayer.create(this, R.raw.xh);
		//myPlayer2 = MediaPlayer.create(this, R.raw.empty);
		myPlayer2 = MediaPlayer.create(this, R.raw.close);
		 /*�趨 MediaPlayer��ȡSDcard�ĵ���*/
		if(xiaohu.exists())
		{
	        try {
	        	
	              /*�� MediaPlayer����*/
	            myPlayer1.reset();
	            
				myPlayer1.setDataSource(xiaohu.toString());
			
				myPlayer1.prepare();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
        
        
        isOFFHOOK=true;/////��ֹ�Է���δ��֮ǰ�Ҷ��ֵ���timer������
		
		 button=new Button(this);
		 button.setOnClickListener(new ButtonListener());
		 button.setText("������");
		 
		 button2=new Button(this);
		 button2.setOnClickListener(new ButtonListener2());
		 button2.setText("�ѹػ�");
		 
		 /*�����б��̿�ʼ*/
		 
		 mySpinner = new Spinner(this);
		//��һ�������һ�������б����list��������ӵ�����������б�Ĳ˵��� 
		 list.add("�ػ�");     
	     list.add("�պ�");     
	     list.add("��æ"); 
	     //�ڶ�����Ϊ�����б���һ����������������õ���ǰ�涨���list��     
	     adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list);
	     //��������Ϊ���������������б�����ʱ�Ĳ˵���ʽ��     
	     adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);     
	     //���Ĳ�������������ӵ������б���     
	     mySpinner.setAdapter(adapter);     
	     //���岽��Ϊ�����б����ø����¼�����Ӧ���������Ӧ�˵���ѡ��
	     mySpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){     
	          //  @SuppressWarnings("unchecked")  
	            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {     
	                // TODO Auto-generated method stub     
	                /* ����ѡmySpinner ��ֵ����myTextView ��*/    
	                System.out.println("��ѡ����ǣ�"+ adapter.getItem(arg2));     
	                /* ��mySpinner ��ʾ*/    
	                arg0.setVisibility(View.VISIBLE);     
	            }     
	           // @SuppressWarnings("unchecked")  
	            public void onNothingSelected(AdapterView<?> arg0) {     
	                // TODO Auto-generated method stub     
	                     
	                arg0.setVisibility(View.VISIBLE);     
	            }     
	        });  
	     /*�����˵�����������ѡ����¼�����*/    
	        mySpinner.setOnTouchListener(new Spinner.OnTouchListener(){     
	            
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					/* ��mySpinner ���أ�������Ҳ���ԣ����Լ�����*/    
	              //  v.setVisibility(View.INVISIBLE);
					System.out.println("�����б����������������");
					return false;
				}     
	        });     
	        /*�����˵�����������ѡ���ı��¼�����*/    
	        mySpinner.setOnFocusChangeListener(new Spinner.OnFocusChangeListener(){     
	        public void onFocusChange(View v, boolean hasFocus) {     
	        // TODO Auto-generated method stub     
	            v.setVisibility(View.VISIBLE);     
	        }     
	        }); 
	        
	    /*�����б��̽���*/
	     
		
		 wm=(WindowManager)this.getSystemService("window");
		 wmParams=new WindowManager.LayoutParams();
		  /**
		   *���¶���WindowManager.LayoutParams���������
		   * ������;��ο�SDK�ĵ�
		   */
		 wmParams.type=2002;   //�����ǹؼ�����Ҳ��������2003
		 wmParams.format=1;
		   /**
		   *�����flagsҲ�ܹؼ�
		   *����ʵ����wmParams.flags |= FLAG_NOT_FOCUSABLE;
		   *40��������wmParams��Ĭ�����ԣ�32��+ FLAG_NOT_FOCUSABLE��8��
		   */
		 // wmParams.flags= WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; ;     //��������ĳ�32�ڵȴ��绰������ʱ��ͻ����������ʾ���������ڡ�
		  wmParams.flags=WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
		  wmParams.width=110;
		  wmParams.height=60;
		  
		  wmParams.gravity = Gravity.RIGHT | Gravity.CENTER;  // �����������������Ͻǣ����ڵ������� 
		
		
		/* �ж�SD Card�Ƿ���� */
	    sdCardExit = Environment.getExternalStorageState().equals(
	        android.os.Environment.MEDIA_MOUNTED);
	    /* ȡ��SD Card·����Ϊ¼�����ļ�λ�� */
	    if (sdCardExit)
	      myRecAudioDir = xiaohuDir;
		
	    
	    mMediaRecorder01 = new MediaRecorder();
	    
        mMediaRecorder01
            .setAudioSource(MediaRecorder.AudioSource.VOICE_DOWNLINK);//���������ó��˵绰�Ļ�Ͳ����Ͳ��һ�ĳ�������е�ʱ��ͻ�����⣬¼����ȥ�����ϲ��ƺ����������ã� Ӧ�����б�����
        //����������ó�MediaRecorder.AudioSource.VOICE_DOWNLINK�Ϳ���¼�����緿������������������VOICE_UPLINK�ǶԷ��������������Լ����Լ��ֻ�ʵ���ʱ��ȷʵ��VOICE_DOWNLINKʱ����¼�¶Է���������        
        mMediaRecorder01
            .setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);//����¼����ʽΪĬ�ϸ�ʽ��.amr
        mMediaRecorder01
            .setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);//������Ƶ���뷽ʽΪĬ��

       
		
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		System.out.println("����service��onStartCommand������");

		
		int a[];
		a=intent.getIntArrayExtra("num");
		
		System.out.println("����num��ֵΪ"+a[0]);
		
		
		
		switch (a[0]) {
		
		case 1://��������ʱ
			
			telNumber=intent.getStringExtra("telNumber");
			System.out.println("�绰����Ϊ��"+telNumber);
			
			isRINGING=true;
			wmParams.gravity = Gravity.RIGHT | Gravity.CENTER;
			wm.addView(button,wmParams);
			wmParams.gravity = Gravity.LEFT | Gravity.CENTER;
			wm.addView(button2,wmParams);
			//wm.addView(mySpinner,wmParams);
			
			
			isView=true;
			System.out.println(" wm.addView����������");
			isOFFHOOK=false;
			
			
			if(isServe)
			{
				Timer timer=new Timer();/////////�����ȴ�30��û�н���ʱ�Զ�������¼��
				  TimerTask task=new TimerTask() {	
					public void run() {
						System.out.println("��ʱ���Զ���ʼ���Թ���???????");
						if(!isOFFHOOK)
							{
							System.out.println("��ʱ���Զ���ʼ���Թ���!!!!!!!");
								//button.performClick();///�������������ʱ�����
							AnswerCall answerCall=new AnswerCall();
							answerCall.answerRingingCall(MyService.this);
							myStart();
							wm.removeView(button);
							isView=false;
							}
						}
				  };
				  timer.schedule(task, 1000*time);
			}
			else;
			  
			break;
		case 2://�����ͨ
			if(isView)
			{
			wm.removeView(button);
			isView=false;
			}
			isOFFHOOK=true;
					
			//wm.addView(button2,wmParams);
			
			break;
		case 3://�绰�Ҷ�
			
			//wm.removeView(button2);
			
			if(isRINGING)
			{
				if(isView)
				{
				wm.removeView(button);
				isView=false;
				}
				myStop();
				isOFFHOOK=true;
				
				wm.removeView(button2);
			}
			myPlayer1.stop();
			myPlayer2.stop();
			stopSelf();
			break;
		}

		return super.onStartCommand(intent, flags, startId);
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		System.out.println("����service��onDestroy������");
		super.onDestroy();
	}
	
	public void myStart()
	{
		System.out.println("myStart����������");
		
		/*��������Ϊ����� ������᲻����oncreat�����У�Ҫ���ᵼ�µ绰������ͱ������*/
		audioManager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
		audioManager.setMode(AudioManager.MODE_IN_CALL);
		
		
		audioManager.setSpeakerphoneOn(true);
		
		
		int MaxSound=audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);//��ȡϵͳ�����õ��������	
		int currentSount=audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);//��ȡ��ǰ������С�������Ժ������û�����
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, MaxSound-1, 0);//����ý������Ϊ���
		
        if (!sdCardExit)
        {
		Toast.makeText(this, "�����SD Card",
		    Toast.LENGTH_LONG).show();
		return;
        }
        
        /*��ʼ������ʾ��*/
        myPlayer1.start();
        
        
        /* ����¼���� */
	      String time=getTime();
	      System.out.println("start������ĵ绰����Ϊ��"+telNumber);
	      myRecAudioFile =new File(myRecAudioDir, time+"~"+telNumber+".amr");//telNumberΪ�������ȫ�ֱ���

	      mMediaRecorder01.setOutputFile(myRecAudioFile.getAbsolutePath());//�������λ��ΪSD��������
	  	   
	      try {
	    	  
			mMediaRecorder01.prepare();
			
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	      /*��ʼ¼��*/
	      mMediaRecorder01.start();
       
        
        
        /* �����ֲ����ִ�е�Listener */  
        myPlayer1.setOnCompletionListener(new OnCompletionListener() 
        { 
		// @Override 
		public void onCompletion(MediaPlayer arg0) 
		{  
			
			audioManager.setSpeakerphoneOn(false);
			
			//myPlayer1.release();
			System.out.println("��ʾ��������ϣ���ʼ¼����");

		      isStart=true;
		      
		     
		      
		      
		      //����֪ͨ��״̬����ʾ��ͼ��
	            m_Notification.icon = R.drawable.x1;
	            //�����ǵ��֪ͨʱ��ʾ������
	            m_Notification.tickerText = "�����ԣ�";
	            //֪ͨʱ����Ĭ�ϵ�����
	            m_Notification.defaults = Notification.DEFAULT_SOUND;
	            //����֪ͨ��ʾ�Ĳ���
	            m_Notification.setLatestEventInfo(MyService.this, "С������", "�����µĵ绰���ԣ�", m_PendingIntent);
	            //�������Ϊִ�����֪ͨ
	            m_NotificationManager.notify(0, m_Notification);
		      
		}
        });

      }
	public void myStop()
	{
		if(isStart)
			if (myRecAudioFile != null)
	        {
	          /* ֹͣ¼�� */
	          mMediaRecorder01.stop();
	          /* ��¼���ļ�����Adapter */
	          mMediaRecorder01.release();
	          mMediaRecorder01 = null;    
	
	        }
	}
	private String getTime(){  
        SimpleDateFormat   formatter   =   new   SimpleDateFormat   ("yy��MM��dd��HH��mm");  //��������Ϊ     ("yyyy��MM��dd��HH��mm��ss")  
        Date  curDate=new  Date(System.currentTimeMillis());//��ȡ��ǰʱ��        
        String   time   =   formatter.format(curDate);         
        return time;  
        }  
	

	class ButtonListener implements android.view.View.OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			System.out.println("������ť�������������");
			//answerRingingCall();	
			AnswerCall answerCall=new AnswerCall();
			answerCall.answerRingingCall(MyService.this);
			myStart();
			wm.removeView(button);
			isView=false;
			
		}

	}
	
	class ButtonListener2 implements android.view.View.OnClickListener {

		@Override
		public void onClick(View v) {
			
			//answerRingingCall();
			System.out.println("���԰�ť������????????");
//			AudioManager audioManager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
//			audioManager.setMode(AudioManager.MODE_IN_CALL);
//			audioManager.setSpeakerphoneOn(true);
//			
			
//			 //����֪ͨ��״̬����ʾ��ͼ��
//            m_Notification.icon = R.drawable.ic_launcher;
//            //�����ǵ��֪ͨʱ��ʾ������
//            m_Notification.tickerText = "Button1֪ͨ����...........";
//            //֪ͨʱ����Ĭ�ϵ�����
//            m_Notification.defaults = Notification.DEFAULT_SOUND;
//            //����֪ͨ��ʾ�Ĳ���
//            m_Notification.setLatestEventInfo(MyService.this, "����", "���԰�ť����", m_PendingIntent);
//            //�������Ϊִ�����֪ͨ
//            m_NotificationManager.notify(0, m_Notification);
			
			//answerRingingCall();
			AnswerCall answerCall=new AnswerCall();
			answerCall.answerRingingCall(MyService.this);
			
			/*��������Ϊ����� ������᲻����oncreat�����У�Ҫ���ᵼ�µ绰������ͱ������*/
			audioManager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
			audioManager.setMode(AudioManager.MODE_IN_CALL);
			audioManager.setSpeakerphoneOn(true);
			

			myPlayer2.start();
			myPlayer2.setOnCompletionListener(new OnCompletionListener() 
	        { 
			// @Override 
			public void onCompletion(MediaPlayer arg0) 
			{  
				audioManager.setSpeakerphoneOn(false);	
				
				
			}
	        });

			
			
			  System.out.println("���԰�ť������!!!!!!!!!!");
		}

	}
	
/*	
	 public synchronized void answerRingingCall() {
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
			         this.sendOrderedBroadcast(localIntent1, "android.permission.CALL_PRIVILEGED");
			       //���¶�����ť
			         Intent localIntent2 = new Intent(Intent.ACTION_MEDIA_BUTTON);
			         KeyEvent localKeyEvent1 = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK);
			         localIntent2.putExtra("android.intent.extra.KEY_EVENT", localKeyEvent1);
			         this.sendOrderedBroadcast(localIntent2, "android.permission.CALL_PRIVILEGED");
			       //�ſ�������ť
			         Intent localIntent3 = new Intent(Intent.ACTION_MEDIA_BUTTON);
			         KeyEvent localKeyEvent2 = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK);
			         localIntent3.putExtra("android.intent.extra.KEY_EVENT", localKeyEvent2);
			         this.sendOrderedBroadcast(localIntent3, "android.permission.CALL_PRIVILEGED");
			       //�γ�����
			         Intent localIntent4 = new Intent(Intent.ACTION_HEADSET_PLUG);
			         localIntent4.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			         localIntent4.putExtra("state", 0);
			         localIntent4.putExtra("microphone", 1);
			         localIntent4.putExtra("name", "Headset");
			         this.sendOrderedBroadcast(localIntent4, "android.permission.CALL_PRIVILEGED");
			         
			   }  catch (Exception e)   {
			         e.printStackTrace();
			   }
			     Log.e("answerring","1111111111111111");
			     
			}*/
	
	
}

