package com.sh2600.timermaster.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.sh2600.timermaster.common.CVal;
import com.sh2600.timermaster.common.Utils;

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
	
	public Calendar getAlarmTimeByDay(String time){
		Calendar t = getTime(time);
		if (t != null && t.before(Calendar.getInstance())){			
			t.add(Calendar.DATE, 1);			
		}
		return t;
	}
	
	public Calendar getTime(String time){
		
		if (time == null){
			return null;
		}
		
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
	
	public void start(){
		enable = true;
		
		configStartTime();
		configStopTime();
		
		Calendar now = Calendar.getInstance();
		Calendar begin = getTime(startTime);
		Calendar stop = getTime(stopTime);
		if (now.after(begin) && now.before(stop)){
			configInterval();
		}
	}
	
	public void stop(){
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
	
	public void configStartTime(){
		Calendar startTime = getAlarmTimeByDay(this.startTime);
		if (startTime == null){
			return;
		}
		Log.i(tag, "start startTime alarm " + Utils.getDateString(startTime.getTime()));
		Intent intent = Utils.buildCmdIntent(this.context, TimerService.class, CVal.Action.TimeIntervalStart, CVal.Cmd.CMD_StartInterval);		
	    PendingIntent pendingIntent = PendingIntent.getService(this.context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
	    this.alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startTime.getTimeInMillis(), CVal.DayMs, pendingIntent);		
	}
	
	public void cancelStartTime(){
		Log.i(tag, "cancel startTime alarm");
		Intent intent = Utils.buildIntent(this.context, TimerService.class, CVal.Action.TimeIntervalStart);
	    PendingIntent pendingIntent = PendingIntent.getService(this.context, 0, intent, 0);
	    this.alarmManager.cancel(pendingIntent);		
	}
	
	public void configStopTime(){
		Calendar time = getAlarmTimeByDay(this.stopTime);
		if (time == null){
			return;
		}
		Log.i(tag, "set stopTime alarm " + Utils.getDateString(time.getTime()));
		Intent intent = Utils.buildCmdIntent(this.context, TimerService.class, CVal.Action.TimeIntervalStop, CVal.Cmd.CMD_StopInterval);		
	    PendingIntent pendingIntent = PendingIntent.getService(this.context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
	    this.alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), CVal.DayMs, pendingIntent);	
	}
	
	public void cancelStopTime(){
		Log.i(tag, "cancel stopTime alarm");
		Intent intent = Utils.buildIntent(this.context, TimerService.class, CVal.Action.TimeIntervalStop);
	    PendingIntent pendingIntent = PendingIntent.getService(this.context, 0, intent, 0);
	    this.alarmManager.cancel(pendingIntent);		
	}	
	
	public void configInterval(){
		if (this.started){
			return;
		}
		
		this.started = true;
		
		Calendar time = Calendar.getInstance();
		time.set(Calendar.MILLISECOND, 0);
		time.set(Calendar.SECOND, 0);
			
		int m = time.get(Calendar.MINUTE);		
		
		time.set(Calendar.MINUTE,  + m + this.interval - m%this.interval);
		
		Log.i(tag, "set voiceTimer alarm " + Utils.getDateString(time.getTime()));
		
		Intent intent = Utils.buildCmdIntent(this.context, TimerService.class, 
				CVal.Action.TimeIntervalInterval, CVal.Cmd.CMD_IntervalInterval
		);		
	    PendingIntent pendingIntent = PendingIntent.getService(this.context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
	    this.alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), this.interval*60*1000, pendingIntent);		
	}
	
	public void cancelInterval(){
		
		Log.i(tag, "cancel voiceTimer alarm");
		
		Intent intent = Utils.buildIntent(this.context, TimerService.class, CVal.Action.TimeIntervalInterval);
	    PendingIntent pendingIntent = PendingIntent.getService(this.context, 0, intent, 0);
	    this.alarmManager.cancel(pendingIntent);	
	    
	    this.started = false;
	}
	
}
