package com.sh2600.timermaster;

import com.sh2600.timermaster.service.TimerService;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class ControlActivity extends Activity implements View.OnClickListener{
	
	static final String tag = ControlActivity.class.getSimpleName();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
	       
		setContentView(R.layout.activity_control);
		
		findViewById(R.id.btnStart).setOnClickListener(this);
		findViewById(R.id.btnStop).setOnClickListener(this);
		findViewById(R.id.btnSetting).setOnClickListener(this);
		findViewById(R.id.btnCancel).setOnClickListener(this);
		
	}
	
	@Override
	public void onClick(View v){
		switch (v.getId()) {
		case R.id.btnStart:
			startService(new Intent(this, TimerService.class));
			//this.finish();
			break;
		case R.id.btnStop:
			boolean r = stopService(new Intent(this, TimerService.class));
			Log.v(tag, "stopService return " + r);
			//this.finish();
			break;			
		case R.id.btnSetting:
			startActivity(new Intent(this, SettingsActivity.class));
			this.finish();
			break;
		case R.id.btnCancel:
			this.finish();
			break;			
		default:
			break;
		}
		
	}
	
	
	
}
