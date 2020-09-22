/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.POS.apis.CaptureController;

import com.google.zxing.ResultPoint;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import java.util.Collection;
import java.util.HashSet;

import jepower.com.t508ac_demo.R;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder
 * rectangle and partial transparency outside it, as well as the laser scanner
 * animation and result points.
 * 
 * @author dswitkin@google.com (Daniel Switkin)
 */
@SuppressLint("DrawAllocation")
public final class ViewfinderView extends View {

	@SuppressWarnings("unused")
	private static final int[] SCANNER_ALPHA = { 0, 64, 128, 192, 255, 192,
			128, 64 };
	private static final long ANIMATION_DELAY = 10L;
	private static final int OPAQUE = 0xFF;
	/**
	 * 中间那条线每次刷新移动的距离
	 */
	private static final int SPEEN_DISTANCE = 5;
	/**
	 * 中间滑动线的最顶端位置
	 */
	private int slideTop;
	/**
	 * 中间滑动线的最底端位置
	 */
	@SuppressWarnings("unused")
	private int slideBottom;
	/**
	 * 扫描框中的中间线的与扫描框左右的间隙
	 */
	@SuppressWarnings("unused")
	private static final int MIDDLE_LINE_PADDING = 5;
	/**
	 * 扫描框中的中间线的宽度
	 */
	private static final int MIDDLE_LINE_WIDTH = 6;
	/**
	 * 四个绿色边角对应的宽度
	 */
	@SuppressWarnings("unused")
	private static final int CORNER_WIDTH = 10;
	/**
	 * 四个绿色边角对应的长度
	 */
	@SuppressWarnings("unused")
	private int ScreenRate;
	/**
	 * 手机的屏幕密度
	 */
	private static float density;
	private final Paint paint;
	private Bitmap resultBitmap;
	private final int maskColor;
	private final int resultColor;
	private final int frameColor;
	@SuppressWarnings("unused")
	private final int laserColor;
	private final int resultPointColor;
	@SuppressWarnings("unused")
	private int scannerAlpha;
	private Collection<ResultPoint> possibleResultPoints;
	private Collection<ResultPoint> lastPossibleResultPoints;
	boolean isFirst;
	// This constructor is used when the class is built from an XML resource.
	public ViewfinderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		density = context.getResources().getDisplayMetrics().density;
		//将像素转换成dp
		ScreenRate = (int)(20 * density);

		// Initialize these once for performance rather than calling them every
		// time in onDraw().
		paint = new Paint();
		Resources resources = getResources();
		maskColor = resources.getColor(R.color.viewfinder_mask);
		resultColor = resources.getColor(R.color.result_view);
		frameColor = resources.getColor(R.color.viewfinder_frame);
		laserColor = resources.getColor(R.color.viewfinder_laser);
		resultPointColor = resources.getColor(R.color.possible_result_points);
		scannerAlpha = 0;
		possibleResultPoints = new HashSet<ResultPoint>(5);
	}

	@Override
	public void onDraw(Canvas canvas) {
		Rect frame = CameraManager.get().getFramingRect();
		if (frame == null) {
			return;
		}
		int width = canvas.getWidth();
		int height = canvas.getHeight();

		// 初始化中间线滑动的最上边和最下边
		if (!isFirst) {
			isFirst = true;
			slideTop = frame.top+75;
			slideBottom = frame.bottom-80;
		}

		// Draw the exterior (i.e. outside the framing rect) darkened
		paint.setColor(resultBitmap != null ? resultColor : maskColor);
		canvas.drawRect(0, 0, width, frame.top, paint);
		canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
		canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1,
				paint);
		canvas.drawRect(0, frame.bottom + 1, width, height, paint);

		if (resultBitmap != null) {
			// Draw the opaque result bitmap over the scanning rectangle
			paint.setAlpha(OPAQUE);
			canvas.drawBitmap(resultBitmap, frame.left, frame.top, paint);
		} else {

			// Draw a two pixel solid black border inside the framing rect
			paint.setColor(frameColor);
			canvas.drawRect(frame.left, frame.top, frame.right + 1,
					frame.top + 2, paint);
			canvas.drawRect(frame.left, frame.top + 2, frame.left + 2,
					frame.bottom - 1, paint);
			canvas.drawRect(frame.right - 1, frame.top, frame.right + 1,
					frame.bottom - 1, paint);
			canvas.drawRect(frame.left, frame.bottom - 1, frame.right + 1,
					frame.bottom + 1, paint);
			
			//画四个对角
			paint.setColor(Color.GREEN);
			canvas.drawRect(frame.left+183, frame.top+95, frame.left+170,
					frame.top + 60, paint);
			canvas.drawRect(frame.left+200, frame.top+75, frame.left + 180, frame.top
					+ 60, paint);
			canvas.drawRect(frame.right - 200, frame.top+75, frame.right-180,
					frame.top + 60, paint);
			canvas.drawRect(frame.right - 183, frame.top+95, frame.right-170, frame.top
					+ 60, paint);
			canvas.drawRect(frame.left+203, frame.bottom -75, frame.left
					+ 180, frame.bottom-60, paint);
			canvas.drawRect(frame.left+183, frame.bottom - 95,
					frame.left + 170, frame.bottom-60, paint);
			canvas.drawRect(frame.right - 183, frame.bottom - 95,
					frame.right-170, frame.bottom-60, paint);
			canvas.drawRect(frame.right - 203, frame.bottom - 75,
					frame.right-180, frame.bottom-60, paint);

			// Draw a red "laser scanner" line through the middle to show
			// decoding is active
//			paint.setColor(laserColor);
//			paint.setAlpha(SCANNER_ALPHA[scannerAlpha]);
//			scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.length;
			slideTop += SPEEN_DISTANCE;
			if (slideTop >= frame.bottom-80) {
				slideTop = frame.top+75;
			}
			canvas.drawRect(frame.left + 190, slideTop - MIDDLE_LINE_WIDTH/2, frame.right - 190,slideTop + MIDDLE_LINE_WIDTH/20, paint);

			Collection<ResultPoint> currentPossible = possibleResultPoints;
			Collection<ResultPoint> currentLast = lastPossibleResultPoints;
			if (currentPossible.isEmpty()) {
				lastPossibleResultPoints = null;
			} else {
				possibleResultPoints = new HashSet<ResultPoint>(5);
				lastPossibleResultPoints = currentPossible;
				paint.setAlpha(OPAQUE);
				paint.setColor(resultPointColor);
				for (ResultPoint point : currentPossible) {
					canvas.drawCircle(frame.left + point.getX(), frame.top
							+ point.getY(), 6.0f, paint);
				}
			}
			if (currentLast != null) {
				paint.setAlpha(OPAQUE / 2);
				paint.setColor(resultPointColor);
				for (ResultPoint point : currentLast) {
					canvas.drawCircle(frame.left + point.getX(), frame.top
							+ point.getY(), 3.0f, paint);
				}
			}

			// Request another update at the animation interval, but only
			// repaint the laser line,
			// not the entire viewfinder mask.
			postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top,
					frame.right, frame.bottom);
		}
	}

	public void drawViewfinder() {
		resultBitmap = null;
		invalidate();
	}

	/**
	 * Draw a bitmap with the result points highlighted instead of the live
	 * scanning display.
	 * 
	 * @param barcode
	 *            An image of the decoded barcode.
	 */
	public void drawResultBitmap(Bitmap barcode) {
		resultBitmap = barcode;
		invalidate();
	}

	public void addPossibleResultPoint(ResultPoint point) {
		possibleResultPoints.add(point);
	}

}
