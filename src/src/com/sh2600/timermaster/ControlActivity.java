package com.sh2600.timermaster;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.sh2600.timermaster.common.CVal;
import com.sh2600.timermaster.service.TimerService;

public class ControlActivity extends Activity implements SharedPreferences.OnSharedPreferenceChangeListener{
	
	static final String tag = ControlActivity.class.getSimpleName();
	
	private SharedPreferences sharedPreferences;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
	       
		setContentView(R.layout.activity_control);

		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		sharedPreferences.registerOnSharedPreferenceChangeListener(this);
		
		if (true){
			Intent i1 = new Intent(this, TimerService.class);
			i1.setAction("a1");
			
			Intent i2 = new Intent(this, TimerService.class);
			i2.setAction("a1");
			
			Log.d(tag, "filterEquals: " + i1.filterEquals(i2));
		}

	}
	
//	public class TestReceiver extends BroadcastReceiver {
//
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			Log.d(tag, "hello timer");
//		}
//		
//	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
	}
	
	public void btnClick(View v){
		switch (v.getId()) {
		case R.id.btnStart:
			startService(new Intent(this, TimerService.class));
			Toast.makeText(this, "启动报时服务", Toast.LENGTH_SHORT).show();
			break;
		case R.id.btnStop:
			boolean r = stopService(new Intent(this, TimerService.class));
			Log.v(tag, "stopService return " + r);
			Toast.makeText(this, "停止报时服务", Toast.LENGTH_SHORT).show();
			break;	
		case R.id.btnPlay:
			Intent intent = new Intent(this, TimerService.class);
			intent.putExtra(CVal.Cmd.cmdtype, CVal.Cmd.CMD_Play);			
			startService(intent);	
			break;
		case R.id.btnSetting:
			startActivity(new Intent(this, SettingsActivity.class));
			break;
		case R.id.btnCancel:
			this.finish();
			break;			
		default:
			break;
		}
		
	}
	
	@Override
	public boolean onTrackballEvent(MotionEvent event) {
		
//		if (event.getAction() == MotionEvent.ACTION_UP){
//			Intent intent = new Intent(this, TimerService.class);
//			intent.putExtra(CVal.Cmd.cmdtype, CVal.Cmd.CMD_PLAY);
//			
//			startService(intent);
//			return true;
//		}

		return super.onTrackballEvent(event);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// TODO Auto-generated method stub
		Log.v(tag, "onSharedPreferenceChanged");
	}

	
	
}
