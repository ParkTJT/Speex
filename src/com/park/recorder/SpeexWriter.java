/**
 * 
 */
package com.park.recorder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.util.Log;

import com.park.speex.Speex;
import com.park.util.ByteUtil;

/**
 * @author "Park_tan"
 * <h2>TODO
 * @since 2015-7-7
 * @version V2.0
 */
public class SpeexWriter {

	private volatile boolean isRecording;
	private ProcessedData pData;
	public static int write_packageSize = 1024;
	
	/** The OutputStream */
	private OutputStream out;
	/** Data buffer */
	private byte[] dataBuffer;
	/** Pointer within the Data buffer */
	private int dataBufferPtr;
	
	public SpeexWriter(String fileName) {
		super();
		dataBuffer = new byte[65565];
		dataBufferPtr = 0;
		File file = new File(fileName);
		try {
			out = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	class ProcessedData {
		//private long ts;
		private int size;
		private byte[] processed = new byte[write_packageSize];
	}


	/**
	 * 
	 */
	public void stop() {
		try {
			flush();
            out.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}



	/**
	 * 输出文件
	 */
	private void flush()  throws IOException{
		Log.d(Speex.SPEEX_LOG, "总的数据长度="+dataBuffer.length);
		Log.d(Speex.SPEEX_LOG, "总的数据指针dataBufferPtr="+dataBufferPtr);
		/**以下为模拟数据*/
		 int version = 13;
		 byte[] versionbyte = ByteUtil.toByteArray(version, 4);
		 int rate = 8000;
		 byte[] ratebyte = ByteUtil.toByteArray(rate, 4);
		 int mode = 1 ;
		 byte[] modebyte = ByteUtil.toByteArray(mode, 4);
		 int bitrate = 160;
		 byte[] bitratebyte = ByteUtil.toByteArray(bitrate, 4);
		 int framesize = 20;
		 byte[] framesizebyte = ByteUtil.toByteArray(framesize, 4);
		 out.write(versionbyte);//0
		 out.write(ratebyte);//1
		 out.write(modebyte);//2
		 out.write(bitratebyte);//3
		 out.write(framesizebyte);//4
		/**spx真实有效数据 dataBuffer*/
		out.write(dataBuffer,0, dataBufferPtr);
	}



	/**
	 * 保存spx数据
	 * @param processedData 每一帧编码过后的spx数据
	 * @param frameSize 有效数据大小
	 */
	public void putData(byte[] processedData, int frameSize) {
		if (frameSize <= 0) { // nothing to write
			return;
		}
		System.arraycopy(processedData, 0, dataBuffer, dataBufferPtr, frameSize);
		dataBufferPtr += frameSize;
		
	}
}
