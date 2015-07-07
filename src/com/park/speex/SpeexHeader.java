/**
 * 
 */
package com.park.speex;

/**
 * @author "Park_tan"
 * <h2>TODO
 * @since 2015-7-7
 * @version V2.0
 */
public class SpeexHeader {
	   /**版本*/
       public int speex_version;
       /** 采样率：8000*/
       public int speex_rate;
       /**0：窄带   1:宽带*/
       public int speex_mode;
       /**声道：2*/
       public int speex_nbChannels;
       /** 比特率：160*/
       public int speex_bitrate;
       /**帧大小 ： 每一帧源数据大小为比特率，经过编码后获取的数据大小为帧大小  */
       public int speex_frameSize;
       
}
