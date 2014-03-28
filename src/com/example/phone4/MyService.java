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
	private Spinner mySpinner;//下拉列表按钮
	private ArrayAdapter<String> adapter; //下拉列表按钮的一个设置参数
	private List<String> list = new ArrayList<String>();  //下拉列表的内容
	
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
	
	/*用来在状态栏显示提示所用到的变量*/
	 //声明通知（消息）管理器
    NotificationManager m_NotificationManager;
    Intent m_Intent;
    PendingIntent m_PendingIntent;
    //声明Notification对象
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
		System.out.println("调用service的onCreat方法。");
   /*状态栏的提示功能：*/
        //初始化NotificationManager对象
		m_NotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);				
		//点击通知时转移内容
        m_Intent = new Intent(this, MainActivity.class);
        //主要是设置点击通知时显示内容的类
        m_PendingIntent = PendingIntent.getActivity(this, 0, m_Intent, 0);
        //构造Notification对象
        m_Notification = new Notification();
    /*状态栏的提示功能：*/
		
		
		//用来获取用户设置
		SharedPreferences pref;
		pref=getSharedPreferences("xiaohu", 0);
		//final SharedPreferences.Editor editor = pref.edit();
		isServe=pref.getBoolean("isServe", true);//第二个参数相当于对isServe的初始化。
		time=pref.getInt("time", 30);
		
		/*用来设置为免提的 这个免提不能在这设置，要不会导致电话刚响铃就变成震动了*/
//		audioManager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
//		audioManager.setMode(AudioManager.MODE_IN_CALL);
		
		
		//打开录音的目录文件：XiaoHuPhone
		File xiaohuDir=new File(Environment.getExternalStorageDirectory()+"/XiaoHuPhone");
		if(!xiaohuDir.exists())
		{
			Toast.makeText(this, "XiaoHuPhone文件夹不存在！", Toast.LENGTH_LONG).show();			
		}
		File xiaohu=new File(xiaohuDir+"/XiaoHu/xiaohu.amr");
		if(!xiaohu.exists())
		{
			Toast.makeText(this, "还未设置提示音！", Toast.LENGTH_LONG).show();
		}
		
		new MediaPlayer();
		myPlayer1 = MediaPlayer.create(this, R.raw.xh);
		//myPlayer2 = MediaPlayer.create(this, R.raw.empty);
		myPlayer2 = MediaPlayer.create(this, R.raw.close);
		 /*设定 MediaPlayer读取SDcard的档案*/
		if(xiaohu.exists())
		{
	        try {
	        	
	              /*把 MediaPlayer重设*/
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
        
        
        isOFFHOOK=true;/////防止对方在未接之前挂断又调用timer函数。
		
		 button=new Button(this);
		 button.setOnClickListener(new ButtonListener());
		 button.setText("请留言");
		 
		 button2=new Button(this);
		 button2.setOnClickListener(new ButtonListener2());
		 button2.setText("已关机");
		 
		 /*下拉列表编程开始*/
		 
		 mySpinner = new Spinner(this);
		//第一步：添加一个下拉列表项的list，这里添加的项就是下拉列表的菜单项 
		 list.add("关机");     
	     list.add("空号");     
	     list.add("繁忙"); 
	     //第二步：为下拉列表定义一个适配器，这里就用到里前面定义的list。     
	     adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list);
	     //第三步：为适配器设置下拉列表下拉时的菜单样式。     
	     adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);     
	     //第四步：将适配器添加到下拉列表上     
	     mySpinner.setAdapter(adapter);     
	     //第五步：为下拉列表设置各种事件的响应，这个事响应菜单被选中
	     mySpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){     
	          //  @SuppressWarnings("unchecked")  
	            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {     
	                // TODO Auto-generated method stub     
	                /* 将所选mySpinner 的值带入myTextView 中*/    
	                System.out.println("您选择的是："+ adapter.getItem(arg2));     
	                /* 将mySpinner 显示*/    
	                arg0.setVisibility(View.VISIBLE);     
	            }     
	           // @SuppressWarnings("unchecked")  
	            public void onNothingSelected(AdapterView<?> arg0) {     
	                // TODO Auto-generated method stub     
	                     
	                arg0.setVisibility(View.VISIBLE);     
	            }     
	        });  
	     /*下拉菜单弹出的内容选项触屏事件处理*/    
	        mySpinner.setOnTouchListener(new Spinner.OnTouchListener(){     
	            
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					/* 将mySpinner 隐藏，不隐藏也可以，看自己爱好*/    
	              //  v.setVisibility(View.INVISIBLE);
					System.out.println("下拉列表被点击······");
					return false;
				}     
	        });     
	        /*下拉菜单弹出的内容选项焦点改变事件处理*/    
	        mySpinner.setOnFocusChangeListener(new Spinner.OnFocusChangeListener(){     
	        public void onFocusChange(View v, boolean hasFocus) {     
	        // TODO Auto-generated method stub     
	            v.setVisibility(View.VISIBLE);     
	        }     
	        }); 
	        
	    /*下拉列表编程结束*/
	     
		
		 wm=(WindowManager)this.getSystemService("window");
		 wmParams=new WindowManager.LayoutParams();
		  /**
		   *以下都是WindowManager.LayoutParams的相关属性
		   * 具体用途请参考SDK文档
		   */
		 wmParams.type=2002;   //这里是关键，你也可以试试2003
		 wmParams.format=1;
		   /**
		   *这里的flags也很关键
		   *代码实际是wmParams.flags |= FLAG_NOT_FOCUSABLE;
		   *40的由来是wmParams的默认属性（32）+ FLAG_NOT_FOCUSABLE（8）
		   */
		 // wmParams.flags= WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; ;     //这里如果改成32在等待电话接听的时候就会出错。不会显示出悬浮窗口。
		  wmParams.flags=WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
		  wmParams.width=110;
		  wmParams.height=60;
		  
		  wmParams.gravity = Gravity.RIGHT | Gravity.CENTER;  // 调整悬浮窗口至左上角，便于调整坐标 
		
		
		/* 判断SD Card是否插入 */
	    sdCardExit = Environment.getExternalStorageState().equals(
	        android.os.Environment.MEDIA_MOUNTED);
	    /* 取得SD Card路径做为录音的文件位置 */
	    if (sdCardExit)
	      myRecAudioDir = xiaohuDir;
		
	    
	    mMediaRecorder01 = new MediaRecorder();
	    
        mMediaRecorder01
            .setAudioSource(MediaRecorder.AudioSource.VOICE_DOWNLINK);//我这里设置成了电话的话筒和听筒。一改成这个运行的时候就会出问题，录不进去。网上查似乎都不可以用， 应该是有保护。
        //这里后来设置成MediaRecorder.AudioSource.VOICE_DOWNLINK就可以录下来电房的语音啦！！！！！VOICE_UPLINK是对方的声音。但是自己用自己手机实验的时候确实是VOICE_DOWNLINK时可以录下对方的声音。        
        mMediaRecorder01
            .setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);//设置录音格式为默认格式，.amr
        mMediaRecorder01
            .setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);//设置音频编码方式为默认

       
		
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		System.out.println("调用service的onStartCommand方法。");

		
		int a[];
		a=intent.getIntArrayExtra("num");
		
		System.out.println("参数num的值为"+a[0]);
		
		
		
		switch (a[0]) {
		
		case 1://来电响铃时
			
			telNumber=intent.getStringExtra("telNumber");
			System.out.println("电话号码为："+telNumber);
			
			isRINGING=true;
			wmParams.gravity = Gravity.RIGHT | Gravity.CENTER;
			wm.addView(button,wmParams);
			wmParams.gravity = Gravity.LEFT | Gravity.CENTER;
			wm.addView(button2,wmParams);
			//wm.addView(mySpinner,wmParams);
			
			
			isView=true;
			System.out.println(" wm.addView！！！！！");
			isOFFHOOK=false;
			
			
			if(isServe)
			{
				Timer timer=new Timer();/////////用来等待30秒没有接听时自动接听并录音
				  TimerTask task=new TimerTask() {	
					public void run() {
						System.out.println("延时后自动开始留言功能???????");
						if(!isOFFHOOK)
							{
							System.out.println("延时后自动开始留言功能!!!!!!!");
								//button.performClick();///用这个函数运行时会出错
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
		case 2://来电接通
			if(isView)
			{
			wm.removeView(button);
			isView=false;
			}
			isOFFHOOK=true;
					
			//wm.addView(button2,wmParams);
			
			break;
		case 3://电话挂断
			
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
		System.out.println("调用service的onDestroy方法。");
		super.onDestroy();
	}
	
	public void myStart()
	{
		System.out.println("myStart函数！！！");
		
		/*用来设置为免提的 这个免提不能在oncreat函数中，要不会导致电话刚响铃就变成震动了*/
		audioManager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
		audioManager.setMode(AudioManager.MODE_IN_CALL);
		
		
		audioManager.setSpeakerphoneOn(true);
		
		
		int MaxSound=audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);//获取系统可设置的最大音量	
		int currentSount=audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);//获取当前音量大小，方便以后再设置回来。
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, MaxSound-1, 0);//设置媒体音量为最大
		
        if (!sdCardExit)
        {
		Toast.makeText(this, "请插入SD Card",
		    Toast.LENGTH_LONG).show();
		return;
        }
        
        /*开始播放提示音*/
        myPlayer1.start();
        
        
        /* 建立录音档 */
	      String time=getTime();
	      System.out.println("start函数里的电话号码为："+telNumber);
	      myRecAudioFile =new File(myRecAudioDir, time+"~"+telNumber+".amr");//telNumber为整个类的全局变量

	      mMediaRecorder01.setOutputFile(myRecAudioFile.getAbsolutePath());//设置输出位置为SD卡···
	  	   
	      try {
	    	  
			mMediaRecorder01.prepare();
			
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	      /*开始录音*/
	      mMediaRecorder01.start();
       
        
        
        /* 当音乐播完会执行的Listener */  
        myPlayer1.setOnCompletionListener(new OnCompletionListener() 
        { 
		// @Override 
		public void onCompletion(MediaPlayer arg0) 
		{  
			
			audioManager.setSpeakerphoneOn(false);
			
			//myPlayer1.release();
			System.out.println("提示音播放完毕，开始录音！");

		      isStart=true;
		      
		     
		      
		      
		      //设置通知在状态栏显示的图标
	            m_Notification.icon = R.drawable.x1;
	            //当我们点击通知时显示的内容
	            m_Notification.tickerText = "新留言！";
	            //通知时发出默认的声音
	            m_Notification.defaults = Notification.DEFAULT_SOUND;
	            //设置通知显示的参数
	            m_Notification.setLatestEventInfo(MyService.this, "小虎精灵", "您有新的电话留言！", m_PendingIntent);
	            //可以理解为执行这个通知
	            m_NotificationManager.notify(0, m_Notification);
		      
		}
        });

      }
	public void myStop()
	{
		if(isStart)
			if (myRecAudioFile != null)
	        {
	          /* 停止录音 */
	          mMediaRecorder01.stop();
	          /* 将录音文件名给Adapter */
	          mMediaRecorder01.release();
	          mMediaRecorder01 = null;    
	
	        }
	}
	private String getTime(){  
        SimpleDateFormat   formatter   =   new   SimpleDateFormat   ("yy年MM月dd日HH：mm");  //可以设置为     ("yyyy年MM月dd日HH：mm：ss")  
        Date  curDate=new  Date(System.currentTimeMillis());//获取当前时间        
        String   time   =   formatter.format(curDate);         
        return time;  
        }  
	

	class ButtonListener implements android.view.View.OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			System.out.println("悬浮按钮被点击！！！！");
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
			System.out.println("测试按钮被按下????????");
//			AudioManager audioManager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
//			audioManager.setMode(AudioManager.MODE_IN_CALL);
//			audioManager.setSpeakerphoneOn(true);
//			
			
//			 //设置通知在状态栏显示的图标
//            m_Notification.icon = R.drawable.ic_launcher;
//            //当我们点击通知时显示的内容
//            m_Notification.tickerText = "Button1通知内容...........";
//            //通知时发出默认的声音
//            m_Notification.defaults = Notification.DEFAULT_SOUND;
//            //设置通知显示的参数
//            m_Notification.setLatestEventInfo(MyService.this, "测试", "测试按钮按下", m_PendingIntent);
//            //可以理解为执行这个通知
//            m_NotificationManager.notify(0, m_Notification);
			
			//answerRingingCall();
			AnswerCall answerCall=new AnswerCall();
			answerCall.answerRingingCall(MyService.this);
			
			/*用来设置为免提的 这个免提不能在oncreat函数中，要不会导致电话刚响铃就变成震动了*/
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

			
			
			  System.out.println("测试按钮被按下!!!!!!!!!!");
		}

	}
	
/*	
	 public synchronized void answerRingingCall() {
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
			         this.sendOrderedBroadcast(localIntent1, "android.permission.CALL_PRIVILEGED");
			       //按下耳机按钮
			         Intent localIntent2 = new Intent(Intent.ACTION_MEDIA_BUTTON);
			         KeyEvent localKeyEvent1 = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK);
			         localIntent2.putExtra("android.intent.extra.KEY_EVENT", localKeyEvent1);
			         this.sendOrderedBroadcast(localIntent2, "android.permission.CALL_PRIVILEGED");
			       //放开耳机按钮
			         Intent localIntent3 = new Intent(Intent.ACTION_MEDIA_BUTTON);
			         KeyEvent localKeyEvent2 = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK);
			         localIntent3.putExtra("android.intent.extra.KEY_EVENT", localKeyEvent2);
			         this.sendOrderedBroadcast(localIntent3, "android.permission.CALL_PRIVILEGED");
			       //拔出耳机
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

