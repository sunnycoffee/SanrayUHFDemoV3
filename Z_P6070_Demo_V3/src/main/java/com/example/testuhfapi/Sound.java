package com.example.testuhfapi;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.testuhfapi.R;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Vibrator;


public class Sound 
{

	public int max;
	public int current;
	MediaPlayer player_fail;
	MediaPlayer player_success;
	Vibrator vibrator;

	public Sound(Context context) 
	{
		super();
		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE); // 播放提示音
		max = audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION);
		current = audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
		player_fail = MediaPlayer.create(context, R.raw.beep);
		player_fail.setVolume((float) current / (float) max, (float) current / (float) max); // 设置提示音量
		player_success = MediaPlayer.create(context, R.raw.duka3);
		player_success.setVolume((float) current / (float) max, (float) current / (float) max); // 设置提示音量
		vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE); // 振动100毫秒
		
	}
	
	/**
	 * 
	 * @param isok 成功还是失败变量
	 * @param ms 振动时间变量，单位ms
	 */

	public  void callAlarm(Boolean isok,int ms) 
	{
		//vibrator.vibrate(ms); // 振动100毫秒
		if(isok)
		{
			if(!player_success.isPlaying())
			{
				player_success.start();	
			}			
		}
		else
		{
			if(!player_fail.isPlaying())
			{
				player_fail.start();
			}
		}
		
	}
}
