package com.park.util;

/**
 * byte转换工具
 * 
 * @author huangzp
 * @date 2015-5-20
 */
public class ByteUtil {

	/**
	 * 将iSource转为长度为iArrayLen的byte数组，字节数组的低位是整型的低字节位
	 * @param iSource
	 * @param iArrayLen
	 * @return
	 */
	public static byte[] toByteArray(int iSource, int iArrayLen) {
		byte[] bLocalArr = new byte[iArrayLen];
		for (int i = 0; (i < 4) && (i < iArrayLen); i++) {
			bLocalArr[i] = (byte) (iSource >> 8 * i & 0xFF);

		}
		return bLocalArr;
	}

	/**
	 * 将byte数组bRefArr转为�?��整数,字节数组的低位是整型的低字节�?
	 * @param bRefArr
	 * @return
	 */
	public static int toInt(byte[] bRefArr) {
		int iOutcome = 0;
		byte bLoop;

		for (int i = 0; i < 4; i++) {
			bLoop = bRefArr[i];
			iOutcome += (bLoop & 0xFF) << (8 * i);
		}
		return iOutcome;
	}
	
	
	
}
