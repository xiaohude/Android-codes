package com.example.phone4;






import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.baidu.mobads.AdSize;
import com.baidu.mobads.AdView;
import com.kyview.AdViewInterface;
import com.kyview.AdViewLayout;



import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("DefaultLocale")
public class MainActivity extends Activity implements AdViewInterface{
	
	Context mcontext;
	//File file;
	Boolean button1=false;//用来判断录音按钮已经被按下。并且停止按钮还未按下.ture代表已经被按下
	Boolean button2=true;
	Boolean isServe;
	
	
	private Button button02;
	public MediaPlayer myPlayer1;
	CheckBox checkBox;
	EditText timeEdit;
	Button button03;
	Button refresh;//用来刷新列表的按钮
	int time;
	
	  private ImageButton myButton1;
	  private ImageButton myButton2;
	  private ImageButton myButton3;
	  private ImageButton myButton4;
	  private ListView myListView1;
	  private String strTempFile = "ex07_11_";
	  private File myRecAudioFile;
	  private File myRecAudioDir;
	  private File myPlayFile;
	  private MediaRecorder mMediaRecorder01;

	  private ArrayList<String> recordFiles;
	  private ArrayAdapter<String> adapter;
	  private TextView myTextView1;
	  private boolean sdCardExit;
	  private boolean isStopRecord;
	  
	  private ScrollView scrollView;
	  
	  public File xiaohuDir;

	@SuppressLint("CommitPrefEdits")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		
		//将状态栏的那个提示删除掉
		NotificationManager m_NotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		m_NotificationManager.cancel(0);
		
		//在sd卡下新建一个XiaoHuPhone的文件夹，用来保存录音等数据
		xiaohuDir = new File(Environment.getExternalStorageDirectory()+"/XiaoHuPhone");//文件目录

        if (!xiaohuDir.exists()){//判断目录是否存在，不存在创建
            xiaohuDir.mkdir();//创建目录
        }
        
		
		//用来实现整体可以滚屏（方便在键盘出来的时候）。同时还不影响listview的滚屏。也就是给listview加监听，如果响应onTouch时就中断scrollview的响应。
		scrollView=(ScrollView)findViewById(R.id.srcollview);
		ListView listview=(ListView)findViewById(R.id.ListView01);
		 listview.setOnTouchListener(new OnTouchListener() {
			 
			 public boolean onTouch(View v, MotionEvent event) {
			 if (event.getAction() == MotionEvent.ACTION_MOVE) { 
				 scrollView.requestDisallowInterceptTouchEvent(true);
			   }
			   return false;
			 }
			});	
		
		
		//保存和读取用户设置；
		SharedPreferences pref;
		pref=getSharedPreferences("xiaohu", 0);
		final SharedPreferences.Editor editor = pref.edit();
		isServe=pref.getBoolean("isServe", true);//第二个参数相当于对isServe的初始化。
		time=pref.getInt("time", 30);//第二个参数相当于对time初始化为30。
		
		timeEdit=(EditText)findViewById(R.id.timeEdit);
		
		timeEdit.setText(String.valueOf(time));
		
		button03=(Button)findViewById(R.id.button3);
		button03.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				System.out.println("保存按钮被按下！！！");
				
				editor.putBoolean("isServe", isServe);
				time= Integer.parseInt(timeEdit.getText().toString());
				editor.putInt("time",time);
				
				editor.commit();
				
				myTextView1.setText("设置成功！");
				
				
				Intent intent=new Intent(MainActivity.this,MyService.class);
				MainActivity.this.stopService(intent);
				
			}
		});
		
		
		////用来设置媒体音量的滚动条
		SoundSeekBar soundSeekBar=new SoundSeekBar();
		
		
		myPlayer1 = new MediaPlayer().create(MainActivity.this, R.raw.xh);
		
		
		checkBox=(CheckBox)findViewById(R.id.checkBox);		
	    checkBox.setChecked(isServe);		
		checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked)
				{
					System.out.println("自动接听服务开启！！！");
					isServe=true;
					
				//	MyService.isServe=true;
					
				}
				else
				{
					System.out.println("自动接听服务关闭！！！");
					isServe=false;
					
				//	MyService.isServe=false;
				}
				
			}
		});
		 
		//下面代码是后来注释掉的
//		IntentFilter filter = new IntentFilter();
//		filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
		//registerReceiver(mReceiver,filter);
		
		myButton1 = (ImageButton) findViewById(R.id.ImageButton01);
	    myButton2 = (ImageButton) findViewById(R.id.ImageButton02);
	    myButton3 = (ImageButton) findViewById(R.id.ImageButton03);
	    myButton4 = (ImageButton) findViewById(R.id.ImageButton04);
	    myListView1 = (ListView) findViewById(R.id.ListView01);
	    myTextView1 = (TextView) findViewById(R.id.TextView01);
	    myButton2.setEnabled(false);
	    myButton3.setEnabled(false);
	    myButton4.setEnabled(false);
	    
	    Button button0=null;//////用来测试的按钮，后期可以删去。
	    button0=(Button)findViewById(R.id.button);
	    button0.setOnClickListener(new ButtonListener());
	    
	    
	    button02=(Button)findViewById(R.id.button2);
	    button02.setOnClickListener(new ButtonListener2());
	    
	    refresh=(Button)findViewById(R.id.refreshbutton);
	    refresh.setOnClickListener(new refreshButtonListener());
	    

	    /* 判断SD Card是否插入 */
	    sdCardExit = Environment.getExternalStorageState().equals(
	        android.os.Environment.MEDIA_MOUNTED);
	    /* 取得SD Card路径做为录音的文件位置 */
	    if (sdCardExit)
	      //myRecAudioDir = Environment.getExternalStorageDirectory();
	    	myRecAudioDir = xiaohuDir;
	    /* 取得SD Card目录里的所有.amr文件 */
	    getRecordFiles();

	    adapter = new ArrayAdapter<String>(this,
	        R.layout.my_simple_list_item, recordFiles);
	    /* 将ArrayAdapter存入ListView对象中 */
	    myListView1.setAdapter(adapter);

	    /* 录音 */
	    myButton1.setOnClickListener(new ImageButton.OnClickListener()
	    {

	      @Override
	      public void onClick(View arg0)
	      {
	    	  System.out.println("录音按键被点击！");
	    	  
	    	  
	    	  if (button1)
	    		  {
	    		  	return;//用来防止录音按钮正在录音的时候又点击了录音按钮。
	    		  }
	    	  button1=true;
	    	  button2=false;
	    	  
	        try
	        {
	          if (!sdCardExit)
	          {
	            Toast.makeText(MainActivity.this, "请插入SD Card",
	                Toast.LENGTH_LONG).show();
	            return;
	          }
	          
	          /* 建立录音档 */
	          myRecAudioFile = File.createTempFile(strTempFile, ".amr",
	              myRecAudioDir);

	          mMediaRecorder01 = new MediaRecorder();
	          /* 设定录音来源为麦克风 */
	          mMediaRecorder01
	              .setAudioSource(MediaRecorder.AudioSource.MIC);//我这里设置成了电话的话筒和听筒。一改成这个运行的时候就会出问题，录不进去。网上查似乎都不可以用， 应该是有保护。
	          //这里后来设置成MediaRecorder.AudioSource.VOICE_DOWNLINK就可以录下来电房的语音啦！！！！！VOICE_UPLINK是对方的声音。但是自己用自己手机实验的时候确实是VOICE_DOWNLINK时可以录下对方的声音。
	          
	          mMediaRecorder01
	              .setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);//设置录音格式为默认格式，.amr
	          mMediaRecorder01
	              .setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);//设置音频编码方式为默认

	          mMediaRecorder01.setOutputFile(myRecAudioFile
	              .getAbsolutePath());//设置输出位置为SD卡···

	          mMediaRecorder01.prepare();

	          mMediaRecorder01.start();

	          myTextView1.setText("录音中");

	          myButton2.setEnabled(true);
	          myButton3.setEnabled(false);
	          myButton4.setEnabled(false);

	          isStopRecord = false;

	        } catch (IOException e)
	        {
	          // TODO Auto-generated catch block
	          e.printStackTrace();
	        }

	      }
	    });
	    /* 停止 */
	    myButton2.setOnClickListener(new ImageButton.OnClickListener()
	    {
	    	

	      @Override
	      public void onClick(View arg0)
	      {
	    	  
	    	  
	    	  if (button2)
    		  {
    		  	return;//用来防止录音按钮正在录音的时候又点击了录音按钮。
    		  }
	    	  button2=true;
	    	  button1=false;
	    	  
	    	  System.out.println("myButton2被点击！");
	    	  
	        // TODO Auto-generated method stub
	        if (myRecAudioFile != null)
	        {
	          /* 停止录音 */
	          mMediaRecorder01.stop();
	          /* 将录音文件名给Adapter */
	          adapter.add(myRecAudioFile.getName());
	          mMediaRecorder01.release();
	          mMediaRecorder01 = null;
	          myTextView1.setText("停止：" + myRecAudioFile.getName());

	          myButton2.setEnabled(false);

	          isStopRecord = true;
	        }
	      }
	    });
	    /* 播放 */
	    myButton3.setOnClickListener(new ImageButton.OnClickListener()
	    {

	      @Override
	      public void onClick(View arg0)
	      {
	        // TODO Auto-generated method stub
	    	  
	    	  
	    	  
	      if (myPlayFile != null && myPlayFile.exists())
	        {
	          // 开启播放的程序 
	         // openFile(myPlayFile);
	    	  Intent intent=new Intent();
	    	  intent.setClass(MainActivity.this, MyMediaPlayer.class);
	    	  intent.putExtra("filePath", myPlayFile.getPath());
	    	  startActivity(intent);
	    	 // MyMediaPlayer myPlayer=new MyMediaPlayer(myPlayFile);
	  /*        	
		            myPlayer1.reset();
		          
				try {
					myPlayer1.setDataSource( myPlayFile.getPath());
					myPlayer1.prepare();
					myPlayer1.start();
			          把 MediaPlayer开始播放
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				myPlayer1.setOnCompletionListener(new OnCompletionListener() 
		        { 
		          // @Override 
		          public void onCompletion(MediaPlayer arg0) 
		          {  
		        	  //myPlayer1.release();//需要在整个Activity关闭后再释放Player因为没关闭之前还有可能点击播放。
		        	  myTextView1.setText("播放完毕！");
		          }
		        });
	        */
	        
	        }
	        else
	        	myTextView1.setText(myPlayFile.getPath()+"文件有错误！");

	      }
	    });
	    /* 删除 */
	    myButton4.setOnClickListener(new ImageButton.OnClickListener()
	    {

	      @Override
	      public void onClick(View arg0)
	      {
	        // TODO Auto-generated method stub
	        if (myPlayFile != null)
	        {
	          /* 因将Adapter移除文件名 */
	          adapter.remove(myPlayFile.getName());
	          /* 删除文件 */
	          if (myPlayFile.exists())
	            myPlayFile.delete();
	          myTextView1.setText("完成删除");
	        }

	      }
	    });

	    myListView1
	        .setOnItemClickListener(new AdapterView.OnItemClickListener()
	        {
	          @Override
	          public void onItemClick(AdapterView<?> arg0, View arg1,
	              int arg2, long arg3)
	          {
	            /* 当有点选文件名时将删除及播放按钮Enable */
	            myButton3.setEnabled(true);
	            myButton4.setEnabled(true);

	            myPlayFile = new File(myRecAudioDir.getAbsolutePath()
	                + File.separator
	                + ((CheckedTextView) arg1).getText());
	            myTextView1.setText("你选的是："
	                + ((CheckedTextView) arg1).getText());
	          }
	        });
	    
	    
	    
    //下面代码是添加广告的代码：
	   
	
	    // 查找 LinearLayout，假设其已获得
	    // 属性 android:id="@+id/mainLayout"
	    LinearLayout layout = (LinearLayout)findViewById(R.id.mainLayout);
	    
	    
	    AdViewLayout adViewLayout = new AdViewLayout(this, "SDK20132019080428s3zlkwj7nh2yik0");
	    //AdViewLayout adViewLayout = new AdViewLayout(this, "SDK20111812070129bb9oj4n571faaka");
        adViewLayout.setAdViewInterface(this);
        layout.addView(adViewLayout);
        layout.invalidate(); 
        
//        AdView adView = new AdView(this,AdSize.Banner, "d6cd31aa");
//        layout.addView(adView);
      //  adView.loadAd(new AdRequest());
       // layout.invalidate(); 

	    
	    
	}
	
	
	class ButtonListener  implements android.view.View.OnClickListener {


		
		 @Override
	      public void onClick(View arg0)
	      {
	    	  System.out.println("设置提示音按键被点击！");
	    	  
	    	  
	    	  if (button1)
	    		  {
	    		  	return;//用来防止录音按钮正在录音的时候又点击了录音按钮。
	    		  }
	    	  button1=true;
	    	  button2=false;
	    	  
	        try
	        {
	          if (!sdCardExit)
	          {
	            Toast.makeText(MainActivity.this, "请插入SD Card",
	                Toast.LENGTH_LONG).show();
	            return;
	          }

	          /* 建立录音档 */
	         // myRecAudioFile = File.createTempFile("xiaohu", ".amr",myRecAudioDir);
	          File xiaohu = new File(xiaohuDir+"/XiaoHu");//文件目录

	          if (!xiaohu.exists()){//判断目录是否存在，不存在创建
	              xiaohu.mkdir();//创建目录
	          }
	          myRecAudioFile=new File(xiaohu+"/xiaohu.amr");
	          mMediaRecorder01 = new MediaRecorder();
	          /* 设定录音来源为麦克风 */
	          mMediaRecorder01
	              .setAudioSource(MediaRecorder.AudioSource.MIC);//我这里设置成了电话的话筒和听筒。一改成这个运行的时候就会出问题，录不进去。网上查似乎都不可以用， 应该是有保护。
	          //这里后来设置成MediaRecorder.AudioSource.VOICE_DOWNLINK就可以录下来电房的语音啦！！！！！VOICE_UPLINK是对方的声音。但是自己用自己手机实验的时候确实是VOICE_DOWNLINK时可以录下对方的声音。
	          
	          mMediaRecorder01
	              .setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);//设置录音格式为默认格式，.amr
	          mMediaRecorder01
	              .setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);//设置音频编码方式为默认

	          mMediaRecorder01.setOutputFile(myRecAudioFile
	              .getAbsolutePath());//设置输出位置为SD卡···

	          mMediaRecorder01.prepare();

	          mMediaRecorder01.start();

	          myTextView1.setText("录音中");

	          myButton2.setEnabled(true);
	          myButton3.setEnabled(false);
	          myButton4.setEnabled(false);

	          isStopRecord = false;

	        } catch (IOException e)
	        {
	          // TODO Auto-generated catch block
	          e.printStackTrace();
	        }

	      }
	} 
	
	class ButtonListener2  implements android.view.View.OnClickListener {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			
			System.out.println("打开提示音！！！");
			
			File f=new File(xiaohuDir+"/XiaoHu/xiaohu.amr");
			
			if (f != null && f.exists())
	        {
	          /* 开启播放的程序 */
	          //openFile(f);
				
				myPlayer1.reset();//强制重置。这样才不会出错
		          
				try {
					myPlayer1.setDataSource(f.toString());
					myPlayer1.prepare();
					myPlayer1.start();
			          /*把 MediaPlayer开始播放*/
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				myPlayer1.setOnCompletionListener(new OnCompletionListener() 
		        { 
		          // @Override 
		          public void onCompletion(MediaPlayer arg0) 
		          {  
		        	 // myPlayer1.release();
		          }
		        });
         				
	        }
			else
			{
				
				Toast.makeText(MainActivity.this, "还未设置提示音！", Toast.LENGTH_LONG).show();
				try {
					//myPlayer1.setDataSource(R.raw.xiaohu);
					
					System.out.println("reset()!!!");
					//myPlayer1.create(MainActivity.this, R.raw.xh);
					System.out.println("set函数！！！");
					//myPlayer1.prepare();
					System.out.println("prepare函数！！！");
					myPlayer1.start();
					System.out.println("默认提示音！！！");
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
								
			}			
			
		}
		
	}
	
	class refreshButtonListener  implements android.view.View.OnClickListener {

		@Override
		public void onClick(View arg0) {
			System.out.println("刷新按钮被点击！！");
			/* 取得SD Card目录里的所有.amr文件 */
			getRecordFiles();
			adapter = new ArrayAdapter<String>(MainActivity.this,R.layout.my_simple_list_item, recordFiles);
			/* 将ArrayAdapter存入ListView对象中 */
			myListView1.setAdapter(adapter);
		}
		
	}

	
	 @Override
	  protected void onStop()
	  {
	    if (mMediaRecorder01 != null && !isStopRecord)
	    {
	      /* 停止录音 */
	      mMediaRecorder01.stop();
	      mMediaRecorder01.release();
	      mMediaRecorder01 = null;
	    }
	    super.onStop();
	  }

	  private void getRecordFiles()
	  {
	    recordFiles = new ArrayList<String>();
	    if (sdCardExit)
	    {
	      File files[] = myRecAudioDir.listFiles();
	      if (files != null)
	      {

	        for (int i = 0; i < files.length; i++)
	        {
	          if (files[i].getName().indexOf(".") >= 0)
	          {
	            /* 读取.amr文件 */
	            String fileS = files[i].getName().substring(
	                files[i].getName().indexOf("."));
	            if (fileS.toLowerCase().equals(".amr"))
	              recordFiles.add(files[i].getName());

	          }
	        }
	      }
	    }
	  }

	  /* 开启播放录音文件的程序 */
	  @SuppressLint("DefaultLocale")
	@SuppressWarnings("unused")
	private void openFile(File f)
	  {
	    Intent intent = new Intent();
	    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    intent.setAction(android.content.Intent.ACTION_VIEW);

	    String type = getMIMEType(f);
	    intent.setDataAndType(Uri.fromFile(f), type);
	    startActivity(intent);
	  }

	  @SuppressLint("DefaultLocale")
	private String getMIMEType(File f)
	  {
	    String end = f.getName().substring(
	        f.getName().lastIndexOf(".") + 1, f.getName().length())
	        .toLowerCase();
	    String type = "";
	    if (end.equals("mp3") || end.equals("aac") || end.equals("aac")
	        || end.equals("amr") || end.equals("mpeg")
	        || end.equals("mp4"))
	    {
	      type = "audio";
	    } else if (end.equals("jpg") || end.equals("gif")
	        || end.equals("png") || end.equals("jpeg"))
	    {
	      type = "image";
	    } else
	    {
	      type = "*";
	    }
	    type += "/*";
	    return type;
	  }
	  
	  
	  
	  //用于控制手机媒体音量大小的进度条：
	class SoundSeekBar implements SeekBar.OnSeekBarChangeListener
	{

		private SeekBar seekBar;
		//private TextView textView1,textView2;
		private AudioManager audioManager;
		SoundSeekBar()
		{
			seekBar = (SeekBar) MainActivity.this.findViewById(R.id.SeekBar01);
			seekBar.setOnSeekBarChangeListener(this);
			
			audioManager=audioManager=(AudioManager)getSystemService(AUDIO_SERVICE);
			 int MaxSound=audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);		   
		     seekBar.setMax(MaxSound);
		     int currentSount=audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		     seekBar.setProgress(currentSount);
		}
		//拖动中
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
		//	System.out.println("当前值:"+progress);
			//this.textView1.setText("当前值:"+progress);
			 if (fromUser) {
				 int SeekPosition=seekBar.getProgress();
				 audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, SeekPosition, 0);
			 }

		}
		//开始拖动
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			System.out.println("拖动中...");
			//this.textView2.setText("拖动中...");

		}
		//结束拖动
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			System.out.println("拖动完毕");
			//this.textView2.setText("拖动完毕");

		}
		  
	}
	  
	  
	  
	  
	@Override
	//下面两个类用来实现按下menu键后所显示的菜单，以及菜单按钮的功能。
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);		
		return true;
	}
	
	//AlertDialog menuDialog;
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if(item.getItemId()==R.id.menu_settings){
			finish();
		}
		else if(item.getItemId()==R.id.menu_about){
			//getMenuInflater().inflate(R.menu.activity_main);
			Intent intent=new Intent(MainActivity.this,MenuAbout.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}
	
	
@Override
protected void onResume() {
	// TODO Auto-generated method stub
	super.onResume();
}

@Override
public void onClickAd() {
	// TODO Auto-generated method stub
	
}

@Override
public void onDisplayAd() {
	// TODO Auto-generated method stub
	
}



}
