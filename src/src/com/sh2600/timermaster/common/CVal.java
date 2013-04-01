package com.sh2600.timermaster.common;

public interface CVal {
	
	long DayMs = 24*3600*1000;
	
	public interface Cmd {
		String cmdtype = "cmdtype";	
		int CMD_None 			= 0;
		int CMD_Quit 			= 1;
		int CMD_Play 			= 2;
		int CMD_HeadsetClick 	= 3;
		
		int CMD_StartInterval 	= 4;
		int CMD_StopInterval 	= 5;
		int CMD_IntervalInterval= 6;
		
	}
	
	public interface Action {		
		String TimeAutoQuit 		= "com.sh2600.timermaster.action.TimeAutoQuit";
		String TimeIntervalStart	= "com.sh2600.timermaster.action.TimeIntervalStart";
		String TimeIntervalStop		= "com.sh2600.timermaster.action.TimeIntervalStop";
		String TimeIntervalInterval	= "com.sh2600.timermaster.action.TimeIntervalInterval";				
	}
	
}
