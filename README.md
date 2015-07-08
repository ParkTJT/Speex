

以下代码参考于：
             ios : http://www.cocoachina.com/bbs/read.php?tid=114755
    Android：https://code.google.com/p/android-recorder/downloads/list
      
       上面android是一个托管项目，分为6个部分，可以拿最后的部分来看。

根据上一篇博客《Android 与 iOS 下 Speex的使用》中提到，ios中会添加38这个帧节数添加到头信息中，而以上的android项目是把源pcm音频数据保存为flv文件，对于两者需要互用，明显不合适，所以需要向这两个项目分别进行改造。
    
根据上一遍博客中提到，需要进行方案二进行对音频编码，则ios和android需要拥有相同规则的头文件，这些头文件分别可以在ios和android系统下面解析。由于ios项目中已经添加了一个头文件（添加头文件的代码位置，下图所示位置），因此只需Android项目从新的整理便可。

ios， EncodePCMToRawSpeex方法中添加头文件speexHeader：

![这里写图片描述](http://img.blog.csdn.net/20150707091423385)


由此可以知道，speex的C++库中，其实已经定义了speexheader，即speex文件的头信息，打开speex_header.h头文件可以看到：
![这里写图片描述](http://img.blog.csdn.net/20150707092240960)
speex_header.h声明了一些变量，列如speex的版本，头信息的长度，比特率，声道，帧大小等等。

由于speex_header.h定义的头信息和头信息协议与我们目前im项目的协议并不是那么符合，因此我也对speex_header.h的头信息进行了从新的定义。speex_header.h定义的头信息字段被我大幅删减了，仅仅保留了speex_version,speex_rate,speex_mode,speex_nbChannels,speex_bitrate,speex_frameSize

所以java中的这个类变为了：

```
`package com.park.speex;

/**
 * @author "Park_tan"
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
       
}`

```

定义协议.
根据socket传输协议，加入头信息，如下：
![这里写图片描述](http://img.blog.csdn.net/20150707114151964)

由图可知，一个编码后的文件包括两个部分，一个是头信息，包含了录音时音频指数，一个是spx的编码数据。关于头信息的定义如下：

![这里写图片描述](http://img.blog.csdn.net/20150707114451036)

由上图可知，每个字段的字节长度为4.关于如何把整型int转为长度为4的byte字节，大家可以自行上网找找了，下面demo工程也有，这里就不贴代码了。

定义头信息的协议之后，在保存spx文件之前，需要加入头信息：

```
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
		 out.write(versionbyte);
		 out.write(ratebyte);
		 out.write(modebyte);
		 out.write(bitratebyte);
		 out.write(framesizebyte);
		/**spx真实有效数据 dataBuffer*/
		out.write(dataBuffer,0, dataBufferPtr);
```

同样地，在解码这个文件时，先获取头信息，在头信息中获取音频相关指数，然后利用相关指数初始化音频播放器，再对spx编码文件进行解码，并播放。

```
/**示例 获取协议头信息*/
				for(int i= 0 ; i<5;i++){
					byte[] 	headerinfo = new byte[4];
					int headersizeinfo=dis.read(headerinfo, 0, 4);
					if(headersizeinfo==-1){
						//读取头信息长度失败
						Log.d(Speex.SPEEX_LOG, "读取头信息失败");
					}else{
						int a = ByteUtil.toInt(headerinfo);
						Log.d(Speex.SPEEX_LOG, "Speex header info = "+i+", = "+a);
					}
				}
```

