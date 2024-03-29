package com.sh2600.timermaster.service;

import com.sh2600.timermaster.common.CVal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;

public class MediaButtonReceiver extends BroadcastReceiver {
	
	static final String tag = MediaButtonReceiver.class.getSimpleName();

	private Handler handler;

	public MediaButtonReceiver(Handler handler) {
		this.handler = handler;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(tag, "some received");
		
		boolean isActionMediaButton = Intent.ACTION_MEDIA_BUTTON.equals(intent
				.getAction());
		if (!isActionMediaButton) {
			return;
		}
		KeyEvent event = (KeyEvent) intent
				.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
		if (event == null) {
			return;
		}

		boolean isActionUp = (event.getAction() == KeyEvent.ACTION_UP);
		if (!isActionUp) {
			return;
		}

		int keyCode = event.getKeyCode();

		long eventTime = event.getEventTime() - event.getDownTime();// 按键按下到松开的时长

		Message msg = Message.obtain();
		msg.what = CVal.Cmd.CMD_HeadsetClick;
		Bundle data = new Bundle();
		data.putInt("key_code", keyCode);
		data.putLong("event_time", eventTime);
		msg.setData(data);
		handler.sendMessage(msg);

		// 终止广播(不让别的程序收到此广播，免受干扰)
		//abortBroadcast();
	}
	
	private void registerMediaButtonReceiver(){
//		Log.d(tag, "registerReceiver media button");
//		IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MEDIA_BUTTON);  
//		intentFilter.setPriority(100);
//		//intentFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY - 1);
//		registerReceiver(this, intentFilter);		
	}
	
	private void unregisterMediaButtonReceiver(){
//		Log.d(tag, "unregisterReceiver media button");
//		unregisterReceiver(this);
	}	
}
