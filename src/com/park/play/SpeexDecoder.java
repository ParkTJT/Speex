/**
 * 
 */
package com.park.play;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import com.park.speex.Speex;
import com.park.util.ByteUtil;

/**
 * @author "Park_tan"
 * <h2>TODO
 * @since 2015-7-8
 * @version V2.0
 */
public class SpeexDecoder {
	protected Speex speexDecoder;
	private File srcPath;
	private AudioTrack track;
	private boolean paused = false;
	
	public SpeexDecoder(File srcPath) throws Exception {
		this.srcPath = srcPath;
	}
	
	private void initializeAndroidAudio(int sampleRate) throws Exception {
		if (track != null) {
			return;
		}
		Log.d(Speex.SPEEX_LOG, "player's sampleRate = "+sampleRate);
		
		int minBufferSize = AudioTrack.getMinBufferSize(sampleRate, 
				AudioFormat.CHANNEL_OUT_MONO, 
				AudioFormat.ENCODING_PCM_16BIT);

		Log.d(Speex.SPEEX_LOG, "player's minBufferSize = "+minBufferSize);
		
		
		if (minBufferSize < 0) {
			throw new Exception("Failed to get minimum buffer size: " + Integer.toString(minBufferSize));
		}
		track = new AudioTrack(AudioManager.STREAM_MUSIC, 
				sampleRate, 
				AudioFormat.CHANNEL_OUT_MONO,
				AudioFormat.ENCODING_PCM_16BIT,
				minBufferSize,
				AudioTrack.MODE_STREAM);
	}
	
	public void decode() throws Exception {
		speexDecoder = new Speex();
		speexDecoder.init();
		Log.d(Speex.SPEEX_LOG, "srcPath = "+srcPath);
		long spxdatalength = srcPath.length();
		Log.d(Speex.SPEEX_LOG, "spxdatalength = "+spxdatalength);
		 InputStream is = new FileInputStream(srcPath);
	     BufferedInputStream bis = new BufferedInputStream(is);
	     DataInputStream dis = new DataInputStream(bis);
		 initializeAndroidAudio(8000);//应该先获取头信息 再去初始化播放器
		 try {
			 
			 while (true) {
				 
				 if (Thread.interrupted()) {
						dis.close();
						track.stop();
						track.release();
						return;
					}
				 
				 while (this.isPaused()) {
						track.stop();
						track.release();
						// Thread.sleep(100);
					}
				 
				 
				    int mDatapro = 0 ;//数据指针
				    /**
				     * 每一帧编码后大小的数值
				     * 列如： 比特率为160，经过编码压缩之后，160个字节压缩为20个字节，那么这个frameSize = 20
				     * 解码还原时，再把20个字节解码变为160个源数据
				     */
					int frameSize = 0;//
				 
				 
				 /**示例 获取协议头信息*/
					for(int i= 0 ; i<5;i++){
						byte[] 	headerinfo = new byte[4];
						int headersizeinfo=dis.read(headerinfo, 0, 4);
						
						if(headersizeinfo==-1){
							//读取头信息长度失败
							Log.d(Speex.SPEEX_LOG, "读取头信息失败");
						}else{
							int a = ByteUtil.toInt(headerinfo);
							if(i==4){
								frameSize = a;
							}
							Log.d(Speex.SPEEX_LOG, "Speex header info = "+i+", = "+a);
						}
					}
				 
					byte[] endecode = new byte[65536];
					for(int packagecount = 0 ;mDatapro<=(int)spxdatalength;packagecount++){
						
						dis.readFully(endecode,0, frameSize);
						Log.d(Speex.SPEEX_LOG, "mDatapro="+mDatapro);
						mDatapro+=frameSize;
						
						int decsize;
						short[] decoded = new short[160];
						if((decsize=speexDecoder.decode(endecode, decoded, 160))>0){
							Log.d(Speex.SPEEX_LOG, "decsize size="+decsize);
							Log.d(Speex.SPEEX_LOG, "decoded size="+decoded.length);
							Log.d(Speex.SPEEX_LOG, "decoded ="+decoded);
							track.write(decoded, 0, decsize);
							track.play();
						}
					}
				 
			 }
			 
		 }catch(Exception e){
			 e.printStackTrace();
		 } finally {
				try {
					if (null != track && track.getPlayState() != AudioTrack.PLAYSTATE_STOPPED) {
						track.stop();
						track.release();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			dis.close();
	}
	
	
	
	public synchronized boolean isPaused() {
		return paused;
	}

	public synchronized void setPaused(boolean paused) {
		this.paused = paused;
	}
}
