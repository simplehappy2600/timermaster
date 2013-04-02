package com.sh2600.timermaster.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.util.Log;

/**
 * 播放时间5188ms
 * 
 * @author 
 *
 */
public class PlayTask extends AsyncTask<Void, Void, Void> implements MediaPlayer.OnCompletionListener {
	
	private final String tag = PlayTask.class.getSimpleName();
	
	private MediaPlayer mp = null; 
	private List<String> sounds = new ArrayList<String>();
	private int idx = 0;
	
	private AssetManager assetManager;
	//private Context context;	
	
	public PlayTask(Context context){		
		this.assetManager = context.getAssets();				
	}

	@Override
	protected Void doInBackground(Void... params) {		
		Log.v(tag, "start play " + System.currentTimeMillis());        		
		
		sounds.add("sound/nowtime.mp3");		
		//play sound		
		Calendar c = Calendar.getInstance();
	
		addSound(c.get(Calendar.HOUR_OF_DAY), false);
		addSound(c.get(Calendar.MINUTE), true);		
		
		mp = new MediaPlayer(); 
		mp.setOnCompletionListener(this);			
				
		play(sounds.get(idx++));
				
		return null;
	}
	
	private void addSound(int num, boolean isMinute){
		
		if (isMinute && num == 0){
			sounds.add("sound/z.mp3");
			return;
		}
		
		try{
			if (num  < 10){
				if (isMinute){
					sounds.add("sound/0.mp3");					
				}
				sounds.add("sound/" + num + ".mp3");
				return;
			}
			
			int n1 = num/10, n2 = num%10;
			
			if (n1 > 1){
				sounds.add("sound/" + n1 + ".mp3");	
			}
			sounds.add("sound/10.mp3");
			if (n2 > 0){
				sounds.add("sound/" + n2 + ".mp3");
			}			
		}
		finally{
			if (isMinute){
				sounds.add("sound/m.mp3");		
			}
			else{
				sounds.add("sound/h.mp3");	
			}
		}
		
	}
	
	public void onCompletion(MediaPlayer mp){
		
		if (idx < sounds.size()){
			play(sounds.get(idx++));	
		}
		else{
			Log.v(tag, "start done " + System.currentTimeMillis());
			mp.release();
			mp = null;
		}
	}
	
	private void play(String file){
		mp.reset();
		try {
			AssetFileDescriptor afd = this.assetManager.openFd(file);
			mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
			mp.prepare();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		mp.start();	
	}

}
