
package com.park.speex;


public class Speex  {

	/* quality
	 * 1 : 4kbps (very noticeable artifacts, usually intelligible)
	 * 2 : 6kbps (very noticeable artifacts, good intelligibility)
	 * 4 : 8kbps (noticeable artifacts sometimes)
	 * 6 : 11kpbs (artifacts usually only noticeable with headphones)
	 * 8 : 15kbps (artifacts not usually noticeable)   
	 * 
	 * 8K 就夠了 根據奈奎斯特
	 * 
	 * 人的聲音頻率為300~3000赫茲左右  聽力認清的頻率為發出頻率的2倍就能夠人讓人清晰地獲取語音信息  
	 * 
	 * 人的聽力採樣會是20~30赫茲  所以需要高度44.1k 赫茲的採樣
	 */
	private static final int DEFAULT_COMPRESSION = 4;
	public static final String SPEEX_LOG = "Speex_log";
	public Speex() {
		
	}

	public void init() {
		load();	
		open(DEFAULT_COMPRESSION);
	}
	
	private void load() {
		try {
			System.loadLibrary("speex");
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public native int open(int compression);
	public native int getFrameSize();
	public native int decode(byte encoded[], short lin[], int size);
	public native int encode(short lin[], int offset, byte encoded[], int size);
	public native void close();
	
}
