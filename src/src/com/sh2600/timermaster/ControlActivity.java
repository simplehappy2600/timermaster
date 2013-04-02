package com.sh2600.timermaster;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.sh2600.timermaster.common.CVal;
import com.sh2600.timermaster.service.TimerService;

public class ControlActivity extends Activity {
	
	static final String tag = ControlActivity.class.getSimpleName();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
	       
		setContentView(R.layout.activity_control);
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();		
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
	
	
}
