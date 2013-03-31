package com.sh2600.timermaster.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class TimerTask {
	
	final String tag = TimerTask.class.getSimpleName();
	
	private Context context;
	private AlarmManager alarmManager;
	
	private boolean enable = false;
	private boolean started = false;
	
	private String startTime;
	private String stopTime;
	private int interval;
	
	public TimerTask(Context context){
		this.context = context;
		this.alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
	}
	
	public void initParam(String startTime, String stopTime, int interval){
		this.startTime = startTime;
		this.stopTime = stopTime;
		this.interval = interval;		
	}
	
	private Calendar getTime(String time){		
		try {
			Date d = new SimpleDateFormat("hh:mm").parse(time);
			Calendar c = Calendar.getInstance();
			c.set(Calendar.HOUR_OF_DAY, d.getHours());
			c.set(Calendar.MINUTE, d.getMinutes());			
			return c;
		} catch (ParseException e) {
			Log.e(tag, "时间解析错误time=" + time, e);
			return null;
		}
	}
	
	private void start(){		
		enable = true;
		
		configStartTime();
		configStopTime();
		
		Calendar now = Calendar.getInstance();
		Calendar begin = getTime(startTime);
		if (now.after(begin)){
			configInterval();
		}
	}
	
	private void stop(){
		enable = false;
		cancelInterval();
		cancelStartTime();
		cancelStopTime();
	}
	
	public void onEnableChange(boolean enable){
		if (this.enable && !enable){
			stop();
		}
		if (!this.enable && enable){
			start();
		}		
	}
	
	public void onStartTimeChange(String startTime){
		this.startTime = startTime;
		if (this.enable){
			cancelStartTime();			
			configStartTime();
		}
	}
	
	public void onStopTimeChange(String stopTime){
		this.stopTime = stopTime;
		if (this.enable){
			cancelStopTime();
			configStopTime();
		}
	}
	
	public void onIntervalChange(String interval){		
		int i = Integer.parseInt(interval);
		if (this.interval == i){
			return;
		}
		this.interval = i;
		if (this.started){
			cancelInterval();
			configInterval();
		}
	}
	
	private void configStartTime(){
		Calendar startTime = getTime(this.startTime);
		Intent intent = new Intent(this.context, StartTimeReceiver.class);		
	    PendingIntent pendingIntent = PendingIntent.getBroadcast(this.context, 0, intent, 0);
	    this.alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startTime.getTimeInMillis(), 24*3600*1000, pendingIntent);		
	}
	private void cancelStartTime(){
		Intent intent = new Intent(this.context, StartTimeReceiver.class);		
	    PendingIntent pendingIntent = PendingIntent.getBroadcast(this.context, 0, intent, 0);
	    this.alarmManager.cancel(pendingIntent);		
	}
	private void configStopTime(){
		Calendar stopTime = getTime(this.stopTime);
		Intent intent = new Intent(this.context, StopTimeReceiver.class);		
	    PendingIntent pendingIntent = PendingIntent.getBroadcast(this.context, 0, intent, 0);
	    this.alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, stopTime.getTimeInMillis(), 24*3600*1000, pendingIntent);	
	}
	private void cancelStopTime(){
		Intent intent = new Intent(this.context, StopTimeReceiver.class);		
	    PendingIntent pendingIntent = PendingIntent.getBroadcast(this.context, 0, intent, 0);
	    this.alarmManager.cancel(pendingIntent);		
	}	
	private void configInterval(){
		if (this.started){
			return;
		}
		
		this.started = true;
		
		Calendar startTime = getTime(this.startTime);
		startTime.set(Calendar.MILLISECOND, 0);
		startTime.set(Calendar.SECOND, 0);
		startTime.set(Calendar.MINUTE, startTime.get(Calendar.MINUTE) + this.interval);
		
		int m = this.interval%60;		
		startTime.set(Calendar.MINUTE, startTime.get(Calendar.MINUTE)/m*m);		
		
		Intent intent = new Intent(this.context, IntervalReceiver.class);		
	    PendingIntent pendingIntent = PendingIntent.getBroadcast(this.context, 0, intent, 0);
	    this.alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startTime.getTimeInMillis(), this.interval*60*1000, pendingIntent);		
	}
	private void cancelInterval(){
		this.started = false;
		
		Intent intent = new Intent(this.context, IntervalReceiver.class); 
	    PendingIntent pendingIntent = PendingIntent.getBroadcast(this.context, 0, intent, 0);
	    this.alarmManager.cancel(pendingIntent);		
	}
	
	public class StartTimeReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			configInterval();
		}
		
	}
 
	public class StopTimeReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			cancelInterval();
		}
		
	}
	
	public class IntervalReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Calendar now = Calendar.getInstance();
			Calendar startTime = getTime(TimerTask.this.startTime);
			Calendar stopTime = getTime(TimerTask.this.stopTime);
			
			boolean ok = false;
			if (startTime.before(stopTime)){
				ok = startTime.before(now) && now.before(stopTime);
			}
			else{
				ok = now.before(stopTime) || now.after(startTime);
			}
			
			if (ok){
				//play
				Log.d(tag, "timer task do" + now);
			}
			
		}
		
	}	
}
