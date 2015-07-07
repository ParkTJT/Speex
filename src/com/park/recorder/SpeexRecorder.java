/**
 * 
 */
package com.park.recorder;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import com.park.speex.Speex;

/**
 * @author "Park_tan"
 * <h2>TODO
 * @since 2015-7-7
 * @version V2.0
 */
public class SpeexRecorder implements Runnable{
	private volatile boolean isRecording;
	private final Object mutex = new Object();
	private static final int frequency = 8000;
	private static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
	private String fileName = null;
	public static int bitrate = 160;
	public SpeexRecorder(String fileName) {
		super();
		this.fileName = fileName;
	}
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		       // 启动编码线程
				SpeexEncoder encoder = new SpeexEncoder(this.fileName);
				Thread encodeThread = new Thread(encoder);
				encoder.setRecording(true);
				encodeThread.start();
				
				synchronized (mutex) {
					while (!this.isRecording) {
						try {
							mutex.wait();
						} catch (InterruptedException e) {
							throw new IllegalStateException("Wait() interrupted!", e);
						}
					}
				}
				android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

				//缓冲大小
				int bufferSize = AudioRecord.getMinBufferSize(frequency, AudioFormat.CHANNEL_IN_MONO, audioEncoding);
				short[] tempBuffer = new short[bufferSize];
				
				Log.d(Speex.SPEEX_LOG, "最小一帧的缓存带大小="+bufferSize);
				
				AudioRecord recordInstance = new AudioRecord(MediaRecorder.AudioSource.MIC, 
						frequency, 
						AudioFormat.CHANNEL_IN_MONO, 
						audioEncoding,bufferSize);
				recordInstance.startRecording();
				
				int bufferRead = 0;
				//编码每一帧
				while (this.isRecording) {
					
					bufferRead = recordInstance.read(tempBuffer, 0, bitrate);
					
					if (bufferRead == AudioRecord.ERROR_INVALID_OPERATION) {
						throw new IllegalStateException("read() returned AudioRecord.ERROR_INVALID_OPERATION");
					} else if (bufferRead == AudioRecord.ERROR_BAD_VALUE) {
						throw new IllegalStateException("read() returned AudioRecord.ERROR_BAD_VALUE");
					} else if (bufferRead == AudioRecord.ERROR_INVALID_OPERATION) {
						throw new IllegalStateException("read() returned AudioRecord.ERROR_INVALID_OPERATION");
					}
					
					encoder.putData(tempBuffer, bufferRead);
					
				}
				
				recordInstance.stop();
				recordInstance.release();
				//tell encoder to stop.
				encoder.setRecording(false);
				
	}
  
	
	public void setRecording(boolean isRecording) {
		synchronized (mutex) {
			this.isRecording = isRecording;
			if (this.isRecording) {
				mutex.notify();
			}
		}
	}

	public boolean isRecording() {
		synchronized (mutex) {
			return isRecording;
		}
	}
}
