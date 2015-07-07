/**
 * 
 */
package com.park.activity;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.parkspeex.R;
import com.park.recorder.SpeexTool;
import com.park.speex.Speex;
import com.park.util.ParkUtil;

/**
 * @author "Park_tan"
 * <h2>TODO
 * @since 2015-7-7
 * @version V2.0
 */
public class ParkSpeexActivity extends Activity implements OnClickListener,OnItemClickListener,
OnItemLongClickListener{
	private Button btn_recorder;
	private Button btn_stop;
	private ListView lsv_spx_fil;
	private ArrayAdapter<String> adaperListAudio; // 列表
	private String fileAudioName; // 保存的音频文件的名字
	private String filePath;// 音频保存的文件路径
	
      /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.main);
    	setupView();
    	initData();
    }
	/**
	 * 
	 */
	private void initData() {
		if (!ParkUtil.isHaveSDcard()) {
			Toast.makeText(this, "请插入SD卡以便存储录音", Toast.LENGTH_LONG).show();
			return;
		}

		// 要保存的文件的路径
		filePath = ParkUtil.getSDPath() + "/" + "myAudio";
		// 实例化文件夹
		File dir = new File(filePath);
		if (!dir.exists()) {
			// 如果文件夹不存在 则创建文件夹
			dir.mkdir();
		}
		Log.i("test", "要保存的录音的文件名为" + fileAudioName + "路径为" + filePath);
		List<String> listAudioFileName = ParkUtil.getFileFormSDcard(dir, ".spx");
		adaperListAudio = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listAudioFileName);
		lsv_spx_fil.setAdapter(adaperListAudio);
	}
	/**
	 * 
	 */
	private void setupView() {
		btn_recorder = (Button)findViewById(R.id.luyin);
		btn_stop =  (Button)findViewById(R.id.stop);
		lsv_spx_fil =  (ListView)findViewById(R.id.list);
		
		btn_recorder.setOnClickListener(this);
		btn_stop.setOnClickListener(this);
		lsv_spx_fil.setOnItemClickListener(this);
		lsv_spx_fil.setOnItemLongClickListener(this);
		
	}
	/* (non-Javadoc)
	 * @see android.widget.AdapterView.OnItemLongClickListener#onItemLongClick(android.widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		// TODO Auto-generated method stub
		return false;
	}
	/* (non-Javadoc)
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		
	}
	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.luyin:
			startSpeex();
			break;
		case R.id.stop:
			stopSpeex();
			break;
		}
	}
	/**
	 * 
	 */
	private void stopSpeex() {
		if (null != SpeexTool.recorderInstance) {
			// 停止录音
			SpeexTool.stop();
			btn_recorder.setText("录音");
			Toast.makeText(this, "录音停止", 1000).show();

			// 开始键能够按下
			btn_recorder.setEnabled(true);
			btn_stop.setEnabled(false);
			// 删除键能按下
			adaperListAudio.add(fileAudioName);
			adaperListAudio.notifyDataSetChanged();
		}
	}
	/**
	 * 开始录音
	 */
	private void startSpeex() {
		// 创建录音频文件
				// 这种创建方式生成的文件名是随机的
				fileAudioName = "audio" + ParkUtil.now() +
						ParkUtil.getRandomString(2) + ".spx";
				String name = filePath + "/" + fileAudioName;
				try {
					Log.d(Speex.SPEEX_LOG, "file name = "+name);
					SpeexTool.start(name);
					btn_recorder.setText("录音中。。。");
					btn_recorder.setEnabled(false);
					btn_stop.setEnabled(true);
				} catch (IllegalStateException e) {
					e.printStackTrace();
				}
	}
}
