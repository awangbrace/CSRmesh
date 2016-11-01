/**
 * File Name                   : ����¼�����
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/8
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.view.activity;

import java.io.File;

import com.axalent.R;
import com.axalent.application.BaseActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

public class PlayVideoActivity extends BaseActivity {
	
	private VideoView videoView;
	private MediaController mediaController;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_play_video);
		// ��ȡ�������
		videoView = (VideoView) findViewById(R.id.atyPlayVideoView);
		// ��ȡMediaController���󣬿���ý�岥��
		mediaController = new MediaController(this);
		videoView.setMediaController(mediaController);
		String path = getIntent().getStringExtra("path");
		File videoFile = new File(path);
		if (videoFile.exists()) {
			videoView.setVideoPath(videoFile.getAbsolutePath());
			videoView.setMediaController(mediaController);
			mediaController.setMediaPlayer(videoView);
			videoView.requestFocus();
			videoView.start();
		}
	}
	
	
//	private void playVideo(int msec) {
//		videoView.start();
//		videoView.seekTo(msec);
//	}

}
