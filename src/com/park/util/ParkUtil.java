/**
 * 
 */
package com.park.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.os.Environment;
import android.text.format.Time;

/**
 * @author "Park_tan"
 * <h2>TODO
 * @since 2015-7-7
 * @version V2.0
 */
public class ParkUtil {
	
	/****************************************************************
	 * 
	 * 判断SD卡是否存在
	 * @return
	 */
	public static boolean isHaveSDcard(){
		//判断SD卡是否存在 存在返回true 不存在返回false
		return Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	}
	/*****************************************************************
	 *  
	 * 获得SD卡的路径
	 */
	public static String getSDPath(){
		String sdDir=null;
		if(isHaveSDcard()){
			sdDir = Environment.getExternalStorageDirectory().toString();//获得根目录
		}
		return sdDir;
	}
	
	
	/** *******************************************
	 * 得到系统时间 
	 */
	public static String now()
	  {
	    Time localTime = new Time();
	    localTime.setToNow();
	    return localTime.format("%Y%m%d%H%M%S");
	  }
	
	/** ************************************************************
	 * 
	 * 获取随机字符串
	 * @param len 字符串的长度
	 * @return
	 */
	public static String getRandomString(int len) {
		String returnStr = "";
		char[] ch = new char[len];
		Random rd = new Random();
		for (int i = 0; i < len; i++) {
			ch[i] = (char) (rd.nextInt(9)+97);
		}
		returnStr = new String(ch);
		return returnStr;
	}
	
	
	
	/** *************************************************************************
	 * 
	 * 从SD卡的dir目录下得到type类型的文件
	 * @param path
	 * @param type
	 * @return
	 */
	public static List<String> getFileFormSDcard(File dir,String type){
		List<String> listFilesName = new ArrayList<String>();
		if(isHaveSDcard()){
			File[] files = dir.listFiles();
			if(files !=null){
				for(int i=0; i<files.length; i++){
					if(files[i].getName().indexOf(".")>=0){
						// 只取Type类型的文件
						String filesResult = files[i].getName()
						.substring(files[i].getName().indexOf("."));
						if(filesResult.toLowerCase().equals(type.toLowerCase())){
							listFilesName.add(files[i].getName());
						}
						
					}
				}
			}
		}
		return listFilesName;
	}
	
}
