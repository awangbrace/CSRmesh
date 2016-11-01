/**
 * File Name                   : 照片画廊
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/8
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.view.widget;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.Gallery;

public class PhotoGallery extends Gallery {
	
	private Context context;
	private PhotoImage photoImage;
	private boolean isScroll = false;
	private boolean isScrollFirst = false;
	private GestureDetector gestureScanner; 
	private int screenWidth;
	private int screenHeight;
	
	@SuppressWarnings("deprecation")
	public PhotoGallery(Context context) {
		super(context);
		this.context = context;
	}

	@SuppressWarnings("deprecation")
	public PhotoGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
	}

	@SuppressWarnings("deprecation")
	public PhotoGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;

		gestureScanner = new GestureDetector(new MySimpleGesture());
		this.setOnTouchListener(new OnTouchListener() {

			float baseValue;
			float originalScale;

			//閲嶅啓onTouch鏂规硶瀹炵幇缂╂斁
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				View view = PhotoGallery.this.getSelectedView();
				if (view instanceof PhotoImage) {
					photoImage = (PhotoImage) view;

					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						baseValue = 0;
						originalScale = photoImage.getScale();
					}
					if (event.getAction() == MotionEvent.ACTION_MOVE) {
						if (event.getPointerCount() == 2) {
							isScroll = false;
							float x = event.getX(0) - event.getX(1);
							float y = event.getY(0) - event.getY(1);
							float value = (float) Math.sqrt(x * x + y * y);// 璁＄畻涓ょ偣鐨勮窛绂�
							if (baseValue == 0) {
								baseValue = value;
							} else {
								float scale = value / baseValue;// 褰撳墠涓ょ偣闂寸殑璺濈闄や互鎵嬫寚钀戒笅鏃朵袱鐐归棿鐨勮窛绂诲氨鏄渶瑕佺缉鏀剧殑姣斾緥銆�
								photoImage.zoomTo(originalScale * scale);								
							}
						}
					}
					if(event.getAction() == MotionEvent.ACTION_UP){
						float ScaleRate = photoImage.getScaleRate();
						if(photoImage.getScale() < ScaleRate){ //缂╂斁鍥炴斁
							photoImage.zoomTo(ScaleRate, event.getX(0), event.getY(0),500);								
							photoImage.layoutToCenter();
						}
						isScroll = true;
					}
				}
				return false;
			}

		});
	}
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		
		View view = PhotoGallery.this.getSelectedView();
		if (view instanceof PhotoImage) {
			photoImage = (PhotoImage) view;

			float v[] = new float[9];
			Matrix m = photoImage.getImageMatrix();
			m.getValues(v);
			// 鍥剧墖瀹炴椂鐨勪笂涓嬪乏鍙冲潗鏍�
			float left = 0, right = 0;
			// 鍥剧墖鐨勫疄鏃跺锛岄珮
			float width, height;
			width = photoImage.getScale() * photoImage.getImageWidth();
			height = photoImage.getScale() * photoImage.getImageHeight();
			if ((int) width <= screenWidth && (int) height <= screenHeight)
			{
				if(isScroll)
					super.onScroll(e1, e2, distanceX, distanceY);
			} else {
				left = v[Matrix.MTRANS_X];
				right = left + width;	
				
				if (distanceX > 0)// 鎵嬪娍鍚戝乏婊戝姩锛屼笅涓�寮犲浘
				{
					if (right <= screenWidth) {
						super.onScroll(e1, e2, distanceX, distanceY);
					}else {
						photoImage.postTranslate(-distanceX, -distanceY);
					}
				} else if (distanceX < 0)// 鎵嬪娍鍚戝彸婊戝姩锛屼笂涓�寮犲浘
				{
					if (left >= 0) {
						super.onScroll(e1, e2, distanceX, distanceY);
					} else {
						photoImage.postTranslate(-distanceX, -distanceY);
					}
				}
			}

			if (!isScrollFirst){
				super.onScroll(e1, e2, distanceX, distanceY);
				isScrollFirst = true;
			}
			
		} else {
			super.onScroll(e1, e2, distanceX, distanceY);
		}
		
		return true;
	}
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		if (e2.getX() > e1.getX()) {
			super.onKeyDown(KeyEvent.KEYCODE_DPAD_LEFT, null);
		} else {
			super.onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);
		}
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		gestureScanner.onTouchEvent(event);
		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			// 鍒ゆ柇涓婁笅杈圭晫鏄惁瓒婄晫
			View view = PhotoGallery.this.getSelectedView();
			if (view instanceof PhotoImage) {
				photoImage = (PhotoImage) view;
				float width = photoImage.getScale() * photoImage.getImageWidth();
				float height = photoImage.getScale() * photoImage.getImageHeight();
				if ((int) width <= screenWidth && (int) height <= screenHeight)
				{
					break;
				}
				float v[] = new float[9];
				Matrix m = photoImage.getImageMatrix();
				m.getValues(v);
				float top = v[Matrix.MTRANS_Y];
				float bottom = top + height;
				if (top > 0) {
					photoImage.postTranslateDur(-top, 200f);
				}
				if (bottom < screenHeight/*MianActivity.screenHeight*/) {
					photoImage.postTranslateDur(screenHeight - bottom, 200f);
				}
			}
			break;
		}
		return super.onTouchEvent(event);
	}

	private class MySimpleGesture extends SimpleOnGestureListener {
		// 鎸変袱涓嬬殑绗簩涓婽ouch down鏃惰Е鍙�
		public boolean onDoubleTap(MotionEvent e) {
			View view = PhotoGallery.this.getSelectedView();
			if (view instanceof PhotoImage) {
				photoImage = (PhotoImage) view;
				if (photoImage.getScale() > photoImage.getScaleRate()) {
					photoImage.zoomTo(photoImage.getScaleRate(), screenWidth / 2, screenHeight / 2, 200f);
					// photoImage.layoutToCenter();
				} else {
					photoImage.zoomTo(1.0f, screenWidth / 2, screenHeight / 2, 200f);
				}
			} else {
			}
			return true;
		}
	}

	public int getScreenWidth() {
		return screenWidth;
	}

	public void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}

	public int getScreenHeight() {
		return screenHeight;
	}

	public void setScreenHeight(int screenHeight) {
		this.screenHeight = screenHeight;
	}
	
	

}
