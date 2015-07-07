/**
 * 
 */
package com.park.recorder;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.park.speex.Speex;

/**
 * @author "Park_tan"
 * <h2>TODO
 * @since 2015-7-7
 * @version V2.0
 */
public class SpeexEncoder implements Runnable
{
	private final Object mutex = new Object();
	private Speex speex = new Speex();
	public static int encoder_packagesize = 1024;
	/**保存每一帧编码后的数据*/
	private byte[] processedData = new byte[encoder_packagesize];
	
	List<ReadData> list = null;
	private volatile boolean isRecording;
	private String fileName;
	
	/**
	 * 
	 */
	public SpeexEncoder(String fileName) {
		speex.init();
		this.fileName = fileName;
		list=new ArrayList<SpeexEncoder.ReadData>();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		// 启动writer线程写speex文件。
				SpeexWriter spWriter = new SpeexWriter(fileName);
				android.os.Process.
				setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
				
				int frameSize = 0;
				
				while (this.isRecording()) {
					
					if (list.size() == 0) {//没有数据
//						log.debug("no data need to do encode");
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						continue;
					}
					
					if (list.size() > 0) {
						synchronized (mutex) {
							ReadData rawdata = list.remove(0);
							frameSize = speex.encode(rawdata.ready, 0, processedData, rawdata.size);
							Log.v(Speex.SPEEX_LOG, "原数据长度---="+rawdata.ready.length);
							Log.v(Speex.SPEEX_LOG, "编码过后的有效数据长度---="+frameSize);
							Log.v(Speex.SPEEX_LOG, "processedData---="+processedData.length);
						}
						if (frameSize > 0) {
							spWriter.putData(processedData, frameSize);
							processedData = new byte[encoder_packagesize];
						}
					}
					
				}
				spWriter.stop();
				
	}
	
	/**
	 * 供Recorder方待处理的数据
	 * @param data
	 * @param size
	 */
	public void putData(short[] data, int size) {
		ReadData rd = new ReadData();
		synchronized (mutex) {
			rd.size = size;
			System.arraycopy(data, 0, rd.ready, 0, size);
			list.add(rd);
		}
	}
	
	public void setRecording(boolean isRecording) {
		synchronized (mutex) {
			this.isRecording = isRecording;
		}
	}

	public boolean isRecording() {
		synchronized (mutex) {
			return isRecording;
		}
	}
	
	class ReadData {
		private int size;
		private short[] ready = new short[encoder_packagesize];
	}
}
