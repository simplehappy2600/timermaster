package com.sh2600.timermaster.service;

import java.util.Calendar;

import android.speech.tts.TextToSpeech;

/**
 * 播放时间5188ms
 * 
 * It's two o'clock. 现在两点。
 * It is half past two.两点半。
 * It's ten past eight.八点十分了。
 * 
 * http://zhidao.baidu.com/question/89930778.html
 * 
 * @author 
 *
 */
public class PlayEnTask {
	
	private TextToSpeech mTts;
	
	public PlayEnTask(TextToSpeech tts){
		this.mTts = tts;
	}
	
	final String[] numbers = {
			"o",
			"one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten",
			"eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eighteen", "nineteen", "twenty",			
	};
	
	final String[] decades = {
			"", "",
			"twenty", "thirty", "forty", "fifty", "sixty"
	};
	
	public StringBuffer getWord(int n){
		if (n < numbers.length){
			return new StringBuffer(numbers[n]);
		}
		
		return new StringBuffer(decades[n/10]).append(" ").append(numbers[n%10]);
	}
	
	/**
	 * 1. 直接表达法
	 * 	A. 用基数词 + o'clock来表示整点，注意o'clock须用单数，可以省略。如：
	 * 		eight o'clock 八点钟，ten (o'clock) 十点钟
	 * 	B. 用基数词按钟点 + 分钟的顺序直接写出时间。如：
	 * 		eleven-o-five 十一点过五分， six forty六点四十
	 * 
	 * 	half past nine
	 */
	public void play(){
		
		StringBuffer sb = new StringBuffer("It's");
				
		Calendar now = Calendar.getInstance();
		
		int hour = now.get(Calendar.HOUR_OF_DAY);
		int minute = now.get(Calendar.MINUTE);
		
		if (minute == 0){
			if (hour == 0){
				sb.append(" zero");	
			}
			else{
				sb.append(" ").append(getWord(hour));
			}
			sb.append(" o’clock");
		}			
		else{
			sb.append(" ").append(getWord(hour));
			if (minute < 10){
				sb.append(" ").append("o");	
			}
			sb.append(" ").append(getWord(minute));
		}
		
		//mTts.speak("It's " + Calendar.getInstance().getTime().toString(), TextToSpeech.QUEUE_ADD, null);
		//String sound = "It's eleven o five";
		String sound = sb.toString();
		
		mTts.speak(sound, TextToSpeech.QUEUE_ADD, null);
		
	}
	
}
