/**
 * 
 */
package com.park.recorder;

import java.io.File;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioManager;

import com.park.play.OnSpeexCompletionListener;
import com.park.play.SpeexDecoder;
import com.park.play.SpeexPlayer;

/**
 * @author "Park_tan"
 * <h2>TODO
 * @since 2015-7-7
 * @version V2.0
 */
public class SpeexTool {

	//录音
	public static SpeexRecorder recorderInstance = null;
	// Speex语音播放
	public static SpeexPlayer mSpeexPlayer = null;
	/**
	 * 开始录音
	 * 
	 * @param name 音频存放路径
	 */
	public static void start(String name) {
		// speex录音
		if (recorderInstance != null) {
			recorderInstance.setRecording(false);
			recorderInstance = null;
		}
		if (recorderInstance == null) {
			recorderInstance = new SpeexRecorder(name);
			Thread th = new Thread(recorderInstance);
			th.start();
		}

		recorderInstance.setRecording(true);
	}

	/**
	 * 停止录音，并是否删除文件
	 * 
	 * @param del
	 * @param filename
	 */
	public static void stop(boolean del, final String filename) {
		// speex录音
		stop();
		if (del) {
			new Thread((new Runnable() {
				@Override
				public void run() {
					if (filename != null && filename.length() > 0) {
						File file = new File(filename);
						if (file.exists()) {
							file.delete();
						}
					}
				}
			})).start();
		}
	}

	/**
	 * 停止录音
	 * 
	 */
	public static void stop() {
		// speex录音
		if (recorderInstance != null) {
			recorderInstance.setRecording(false);
			recorderInstance = null;
		}
	}

	/**
	 * 语音播放
	 * 
	 * @param name
	 * @param chatmsgid
	 */
	public static void playMusic(Context context, String name) {
		try {
			// 如果是speex录音
			if (name != null && name.endsWith(".spx")) {
				if (mSpeexPlayer != null && mSpeexPlayer.isPlay) {
					stopMusic(context);
				} else {
					muteAudioFocus(context, true);
					mSpeexPlayer = new SpeexPlayer(name, new OnSpeexCompletionListener() {
						@Override
						public void onError(Exception ex) {
							System.out.println("播放错误");
						}

						@Override
						public void onCompletion(SpeexDecoder speexdecoder) {
							System.out.println("播放完成");
						}
					});
					mSpeexPlayer.startPlay();
				}
			} else {
				System.out.println("音频文件格式不正确");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 停止播放语音
	 */
	public static void stopMusic(Context context) {
		// 停止播放录音
		if (mSpeexPlayer != null && mSpeexPlayer.isPlay) {
			mSpeexPlayer.stopPlay();
			mSpeexPlayer = null;
			muteAudioFocus(context, false);
		}
	}

	@TargetApi(8)
	public static boolean muteAudioFocus(Context context, boolean bMute) {
		if (context == null) {
			return false;
		}
		if (!isBeforeFroyo()) {
			// 2.1以下的版本不支持下面的API：requestAudioFocus和abandonAudioFocus

			System.out.println("ANDROID_LAB Android 2.1 and below can not stop music");
			return false;
		}
		boolean bool = false;
		AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		if (bMute) {
			int result = am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
			bool = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
		} else {
			int result = am.abandonAudioFocus(null);
			bool = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
		}
		System.out.println("ANDROID_LAB pauseMusic bMute=" + bMute + " result=" + bool);
		return bool;
	}

	public static boolean isBeforeFroyo() {
		int sdkVersion = 0;
		try {
			sdkVersion = Integer.valueOf(android.os.Build.VERSION.SDK);
		} catch (NumberFormatException e) {
			sdkVersion = 0;
		}
		if (sdkVersion <= 8) {
			return true;
		}
		return true;
	}

}
