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
	Boolean button1=false;//�����ж�¼����ť�Ѿ������¡�����ֹͣ��ť��δ����.ture�����Ѿ�������
	Boolean button2=true;
	Boolean isServe;
	
	
	private Button button02;
	public MediaPlayer myPlayer1;
	CheckBox checkBox;
	EditText timeEdit;
	Button button03;
	Button refresh;//����ˢ���б�İ�ť
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
		
		
		
		//��״̬�����Ǹ���ʾɾ����
		NotificationManager m_NotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		m_NotificationManager.cancel(0);
		
		//��sd�����½�һ��XiaoHuPhone���ļ��У���������¼��������
		xiaohuDir = new File(Environment.getExternalStorageDirectory()+"/XiaoHuPhone");//�ļ�Ŀ¼

        if (!xiaohuDir.exists()){//�ж�Ŀ¼�Ƿ���ڣ������ڴ���
            xiaohuDir.mkdir();//����Ŀ¼
        }
        
		
		//����ʵ��������Թ����������ڼ��̳�����ʱ�򣩡�ͬʱ����Ӱ��listview�Ĺ�����Ҳ���Ǹ�listview�Ӽ����������ӦonTouchʱ���ж�scrollview����Ӧ��
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
		
		
		//����Ͷ�ȡ�û����ã�
		SharedPreferences pref;
		pref=getSharedPreferences("xiaohu", 0);
		final SharedPreferences.Editor editor = pref.edit();
		isServe=pref.getBoolean("isServe", true);//�ڶ��������൱�ڶ�isServe�ĳ�ʼ����
		time=pref.getInt("time", 30);//�ڶ��������൱�ڶ�time��ʼ��Ϊ30��
		
		timeEdit=(EditText)findViewById(R.id.timeEdit);
		
		timeEdit.setText(String.valueOf(time));
		
		button03=(Button)findViewById(R.id.button3);
		button03.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				System.out.println("���水ť�����£�����");
				
				editor.putBoolean("isServe", isServe);
				time= Integer.parseInt(timeEdit.getText().toString());
				editor.putInt("time",time);
				
				editor.commit();
				
				myTextView1.setText("���óɹ���");
				
				
				Intent intent=new Intent(MainActivity.this,MyService.class);
				MainActivity.this.stopService(intent);
				
			}
		});
		
		
		////��������ý�������Ĺ�����
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
					System.out.println("�Զ�����������������");
					isServe=true;
					
				//	MyService.isServe=true;
					
				}
				else
				{
					System.out.println("�Զ���������رգ�����");
					isServe=false;
					
				//	MyService.isServe=false;
				}
				
			}
		});
		 
		//��������Ǻ���ע�͵���
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
	    
	    Button button0=null;//////�������Եİ�ť�����ڿ���ɾȥ��
	    button0=(Button)findViewById(R.id.button);
	    button0.setOnClickListener(new ButtonListener());
	    
	    
	    button02=(Button)findViewById(R.id.button2);
	    button02.setOnClickListener(new ButtonListener2());
	    
	    refresh=(Button)findViewById(R.id.refreshbutton);
	    refresh.setOnClickListener(new refreshButtonListener());
	    

	    /* �ж�SD Card�Ƿ���� */
	    sdCardExit = Environment.getExternalStorageState().equals(
	        android.os.Environment.MEDIA_MOUNTED);
	    /* ȡ��SD Card·����Ϊ¼�����ļ�λ�� */
	    if (sdCardExit)
	      //myRecAudioDir = Environment.getExternalStorageDirectory();
	    	myRecAudioDir = xiaohuDir;
	    /* ȡ��SD CardĿ¼�������.amr�ļ� */
	    getRecordFiles();

	    adapter = new ArrayAdapter<String>(this,
	        R.layout.my_simple_list_item, recordFiles);
	    /* ��ArrayAdapter����ListView������ */
	    myListView1.setAdapter(adapter);

	    /* ¼�� */
	    myButton1.setOnClickListener(new ImageButton.OnClickListener()
	    {

	      @Override
	      public void onClick(View arg0)
	      {
	    	  System.out.println("¼�������������");
	    	  
	    	  
	    	  if (button1)
	    		  {
	    		  	return;//������ֹ¼����ť����¼����ʱ���ֵ����¼����ť��
	    		  }
	    	  button1=true;
	    	  button2=false;
	    	  
	        try
	        {
	          if (!sdCardExit)
	          {
	            Toast.makeText(MainActivity.this, "�����SD Card",
	                Toast.LENGTH_LONG).show();
	            return;
	          }
	          
	          /* ����¼���� */
	          myRecAudioFile = File.createTempFile(strTempFile, ".amr",
	              myRecAudioDir);

	          mMediaRecorder01 = new MediaRecorder();
	          /* �趨¼����ԴΪ��˷� */
	          mMediaRecorder01
	              .setAudioSource(MediaRecorder.AudioSource.MIC);//���������ó��˵绰�Ļ�Ͳ����Ͳ��һ�ĳ�������е�ʱ��ͻ�����⣬¼����ȥ�����ϲ��ƺ����������ã� Ӧ�����б�����
	          //����������ó�MediaRecorder.AudioSource.VOICE_DOWNLINK�Ϳ���¼�����緿������������������VOICE_UPLINK�ǶԷ��������������Լ����Լ��ֻ�ʵ���ʱ��ȷʵ��VOICE_DOWNLINKʱ����¼�¶Է���������
	          
	          mMediaRecorder01
	              .setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);//����¼����ʽΪĬ�ϸ�ʽ��.amr
	          mMediaRecorder01
	              .setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);//������Ƶ���뷽ʽΪĬ��

	          mMediaRecorder01.setOutputFile(myRecAudioFile
	              .getAbsolutePath());//�������λ��ΪSD��������

	          mMediaRecorder01.prepare();

	          mMediaRecorder01.start();

	          myTextView1.setText("¼����");

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
	    /* ֹͣ */
	    myButton2.setOnClickListener(new ImageButton.OnClickListener()
	    {
	    	

	      @Override
	      public void onClick(View arg0)
	      {
	    	  
	    	  
	    	  if (button2)
    		  {
    		  	return;//������ֹ¼����ť����¼����ʱ���ֵ����¼����ť��
    		  }
	    	  button2=true;
	    	  button1=false;
	    	  
	    	  System.out.println("myButton2�������");
	    	  
	        // TODO Auto-generated method stub
	        if (myRecAudioFile != null)
	        {
	          /* ֹͣ¼�� */
	          mMediaRecorder01.stop();
	          /* ��¼���ļ�����Adapter */
	          adapter.add(myRecAudioFile.getName());
	          mMediaRecorder01.release();
	          mMediaRecorder01 = null;
	          myTextView1.setText("ֹͣ��" + myRecAudioFile.getName());

	          myButton2.setEnabled(false);

	          isStopRecord = true;
	        }
	      }
	    });
	    /* ���� */
	    myButton3.setOnClickListener(new ImageButton.OnClickListener()
	    {

	      @Override
	      public void onClick(View arg0)
	      {
	        // TODO Auto-generated method stub
	    	  
	    	  
	    	  
	      if (myPlayFile != null && myPlayFile.exists())
	        {
	          // �������ŵĳ��� 
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
			          �� MediaPlayer��ʼ����
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
		        	  //myPlayer1.release();//��Ҫ������Activity�رպ����ͷ�Player��Ϊû�ر�֮ǰ���п��ܵ�����š�
		        	  myTextView1.setText("������ϣ�");
		          }
		        });
	        */
	        
	        }
	        else
	        	myTextView1.setText(myPlayFile.getPath()+"�ļ��д���");

	      }
	    });
	    /* ɾ�� */
	    myButton4.setOnClickListener(new ImageButton.OnClickListener()
	    {

	      @Override
	      public void onClick(View arg0)
	      {
	        // TODO Auto-generated method stub
	        if (myPlayFile != null)
	        {
	          /* ��Adapter�Ƴ��ļ��� */
	          adapter.remove(myPlayFile.getName());
	          /* ɾ���ļ� */
	          if (myPlayFile.exists())
	            myPlayFile.delete();
	          myTextView1.setText("���ɾ��");
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
	            /* ���е�ѡ�ļ���ʱ��ɾ�������Ű�ťEnable */
	            myButton3.setEnabled(true);
	            myButton4.setEnabled(true);

	            myPlayFile = new File(myRecAudioDir.getAbsolutePath()
	                + File.separator
	                + ((CheckedTextView) arg1).getText());
	            myTextView1.setText("��ѡ���ǣ�"
	                + ((CheckedTextView) arg1).getText());
	          }
	        });
	    
	    
	    
    //�����������ӹ��Ĵ��룺
	   
	
	    // ���� LinearLayout���������ѻ��
	    // ���� android:id="@+id/mainLayout"
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
	    	  System.out.println("������ʾ�������������");
	    	  
	    	  
	    	  if (button1)
	    		  {
	    		  	return;//������ֹ¼����ť����¼����ʱ���ֵ����¼����ť��
	    		  }
	    	  button1=true;
	    	  button2=false;
	    	  
	        try
	        {
	          if (!sdCardExit)
	          {
	            Toast.makeText(MainActivity.this, "�����SD Card",
	                Toast.LENGTH_LONG).show();
	            return;
	          }

	          /* ����¼���� */
	         // myRecAudioFile = File.createTempFile("xiaohu", ".amr",myRecAudioDir);
	          File xiaohu = new File(xiaohuDir+"/XiaoHu");//�ļ�Ŀ¼

	          if (!xiaohu.exists()){//�ж�Ŀ¼�Ƿ���ڣ������ڴ���
	              xiaohu.mkdir();//����Ŀ¼
	          }
	          myRecAudioFile=new File(xiaohu+"/xiaohu.amr");
	          mMediaRecorder01 = new MediaRecorder();
	          /* �趨¼����ԴΪ��˷� */
	          mMediaRecorder01
	              .setAudioSource(MediaRecorder.AudioSource.MIC);//���������ó��˵绰�Ļ�Ͳ����Ͳ��һ�ĳ�������е�ʱ��ͻ�����⣬¼����ȥ�����ϲ��ƺ����������ã� Ӧ�����б�����
	          //����������ó�MediaRecorder.AudioSource.VOICE_DOWNLINK�Ϳ���¼�����緿������������������VOICE_UPLINK�ǶԷ��������������Լ����Լ��ֻ�ʵ���ʱ��ȷʵ��VOICE_DOWNLINKʱ����¼�¶Է���������
	          
	          mMediaRecorder01
	              .setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);//����¼����ʽΪĬ�ϸ�ʽ��.amr
	          mMediaRecorder01
	              .setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);//������Ƶ���뷽ʽΪĬ��

	          mMediaRecorder01.setOutputFile(myRecAudioFile
	              .getAbsolutePath());//�������λ��ΪSD��������

	          mMediaRecorder01.prepare();

	          mMediaRecorder01.start();

	          myTextView1.setText("¼����");

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
			
			System.out.println("����ʾ��������");
			
			File f=new File(xiaohuDir+"/XiaoHu/xiaohu.amr");
			
			if (f != null && f.exists())
	        {
	          /* �������ŵĳ��� */
	          //openFile(f);
				
				myPlayer1.reset();//ǿ�����á������Ų������
		          
				try {
					myPlayer1.setDataSource(f.toString());
					myPlayer1.prepare();
					myPlayer1.start();
			          /*�� MediaPlayer��ʼ����*/
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
				
				Toast.makeText(MainActivity.this, "��δ������ʾ����", Toast.LENGTH_LONG).show();
				try {
					//myPlayer1.setDataSource(R.raw.xiaohu);
					
					System.out.println("reset()!!!");
					//myPlayer1.create(MainActivity.this, R.raw.xh);
					System.out.println("set����������");
					//myPlayer1.prepare();
					System.out.println("prepare����������");
					myPlayer1.start();
					System.out.println("Ĭ����ʾ��������");
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
			System.out.println("ˢ�°�ť���������");
			/* ȡ��SD CardĿ¼�������.amr�ļ� */
			getRecordFiles();
			adapter = new ArrayAdapter<String>(MainActivity.this,R.layout.my_simple_list_item, recordFiles);
			/* ��ArrayAdapter����ListView������ */
			myListView1.setAdapter(adapter);
		}
		
	}

	
	 @Override
	  protected void onStop()
	  {
	    if (mMediaRecorder01 != null && !isStopRecord)
	    {
	      /* ֹͣ¼�� */
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
	            /* ��ȡ.amr�ļ� */
	            String fileS = files[i].getName().substring(
	                files[i].getName().indexOf("."));
	            if (fileS.toLowerCase().equals(".amr"))
	              recordFiles.add(files[i].getName());

	          }
	        }
	      }
	    }
	  }

	  /* ��������¼���ļ��ĳ��� */
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
	  
	  
	  
	  //���ڿ����ֻ�ý��������С�Ľ�������
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
		//�϶���
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
		//	System.out.println("��ǰֵ:"+progress);
			//this.textView1.setText("��ǰֵ:"+progress);
			 if (fromUser) {
				 int SeekPosition=seekBar.getProgress();
				 audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, SeekPosition, 0);
			 }

		}
		//��ʼ�϶�
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			System.out.println("�϶���...");
			//this.textView2.setText("�϶���...");

		}
		//�����϶�
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			System.out.println("�϶����");
			//this.textView2.setText("�϶����");

		}
		  
	}
	  
	  
	  
	  
	@Override
	//��������������ʵ�ְ���menu��������ʾ�Ĳ˵����Լ��˵���ť�Ĺ��ܡ�
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
