package com.example.phone4;

import java.io.File;
import java.io.IOException;

import android.R.string;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class MyMediaPlayer extends Activity{
	
	public MediaPlayer myPlayer1;
	private SeekBar ProceseekBar;
	private Button replayButton;
	private Button pauseButton;
	private Button continueButton;
	private Button stopButton;
	private File file;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_media_player);
		
		
		Intent intent=getIntent();
		String filePath=intent.getStringExtra("filePath");
		file=new File(filePath);
		
		ProceseekBar=(SeekBar)findViewById(R.id.SeekBar);
		replayButton=(Button)findViewById(R.id.button1);
		pauseButton=(Button)findViewById(R.id.button2);		
		continueButton=(Button)findViewById(R.id.button3);
		stopButton=(Button)findViewById(R.id.button4);
		
		
		
		
		ProceseekBar.setOnSeekBarChangeListener(new ProcessBarListener());
		replayButton.setOnClickListener(new replayListener());
		pauseButton.setOnClickListener(new pauseListener());		
		continueButton.setOnClickListener(new continueListener());
		stopButton.setOnClickListener(new stopListener());
		
		
		myPlayer1=new MediaPlayer();
		
		Play(file);
	}
	
	
	
	
	
	public void Play(File file) {
		// TODO Auto-generated constructor stub
		
		
		
		 if (file != null && file.exists())
	        {
	          // 开启播放的程序 
	         // openFile(myPlayFile);

		
		        myPlayer1.reset();
		          
				try {
					myPlayer1.setDataSource( file.getPath());
					myPlayer1.prepare();
					myPlayer1.start();
			   // MediaPlayer开始播放
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
				
				
			final Handler handler=new Handler();
				  			    
			Runnable r=new Runnable() {
				                
			 @Override
				public void run() {
				      // TODO Auto-generated method stub
				       int CurrentPosition=myPlayer1.getCurrentPosition();	                       
				       int mMax=myPlayer1.getDuration();
				       ProceseekBar.setMax(mMax);
				       ProceseekBar.setProgress(CurrentPosition);
				       handler.postDelayed(this, 100);
				 }
			};
			handler.post(r);
				
				
				myPlayer1.setOnCompletionListener(new OnCompletionListener() 
		        { 
		          // @Override 
		          public void onCompletion(MediaPlayer arg0) 
		          {  
		        	  //myPlayer1.release();//需要在整个Activity关闭后再释放Player因为没关闭之前还有可能点击播放。
		        	  System.out.println("MyPlayer类播放完毕！");
		        	  finish();
		          }
		        });
	        }
	        
	}
	
	
	 class ProcessBarListener implements OnSeekBarChangeListener{

         public void onProgressChanged(SeekBar seekBar, int progress,
                         boolean fromUser) {
                 // TODO Auto-generated method stub
                 if (fromUser==true) {
                         myPlayer1.seekTo(progress);                       
                 }               
         }
         @Override
         public void onStartTrackingTouch(SeekBar seekBar) {
                 // TODO Auto-generated method stub                
         }

         @Override
         public void onStopTrackingTouch(SeekBar seekBar) {
                 // TODO Auto-generated method stub               
         }
     
	 }
	 class replayListener implements OnClickListener
	 {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Play(file);
			
		}
		 
	 }
	 class pauseListener implements OnClickListener
	 {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			myPlayer1.pause();
		}
		 
	 }
	 class continueListener implements OnClickListener
	 {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			myPlayer1.start();
		}	 
	 }
	 class stopListener implements OnClickListener
	 {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			myPlayer1.stop();
			finish();
		}
		 
	 }
	
	 
	 @Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		myPlayer1.stop();
	}
	 

}
