/**
 *ImgDisplayActivity.java
 *2011-11-20 上午11:10:04
 *Touch Android
 *http://bbs.droidstouch.com
 */
package com.droidstouch.iweibo.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.droidstouch.iweibo.R;
import com.droidstouch.iweibo.app.WeiboApplication;
import com.droidstouch.iweibo.imgCache.ImageLoaderCallback;

/**
 * @author <a href="http://bbs.droidstouch.com">Touch Android</a>
 *
 */
public class ImgDisplayActivity extends Activity
{

	
	
	private ImageView imgDisplay;
	private Button btnZoomin;
	private Button btnZoomout;
  private FrameLayout fLayoutDisplay;
  private LinearLayout lLayoutDisplay;
  private Bitmap bitmap;
  
  private int imgId=0;
  
  private double scale_in=0.8;// 缩小的比例
  private double scale_out=1.25;// 放大的比例
  
  private float scaleWidth=1;
  private float scaleHeight=1;
	
  
  
  private final static int NONE=0;
  private final static int DRAG=1;
  private final static int ZOOM=2;
  private int mode=NONE;
  
  private Matrix matrix;
  private Matrix currMatrix;
  
  private PointF starPoint;
  private PointF midPointF;
  
  private float startDistance;
  
  
  
	protected void onCreate(Bundle savedInstanceState)
	{
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.display_img);
		
		
		lLayoutDisplay = (LinearLayout) this.findViewById(R.id.llayout_img_display);
		fLayoutDisplay = (FrameLayout) this.findViewById(R.id.flayout_img_display);
		imgDisplay = (ImageView) this.findViewById(R.id.img_display);
		btnZoomin = (Button) this.findViewById(R.id.btn_zoomin);
		btnZoomout = (Button) this.findViewById(R.id.btn_zoomout);
		
		
		matrix = new Matrix(); // 保存拖拽变化
		currMatrix = new Matrix(); // 保存当前的
		
		starPoint = new PointF(); // 开始点
		
		
		btnZoomout.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				zoomOut();				
			}
		});
		
		btnZoomin.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				zoomin();				
			}
		});
		
		
		imgDisplay.setOnTouchListener(new ImageViewOnTouchListener());
		
		
		
		String imgUrl = this.getIntent().getStringExtra("imgUrl");
		
		WeiboApplication.lazyImageLoader.get(imgUrl, new ImageLoaderCallback()
		{
			public void refresh(String url, Bitmap bmap)
			{
				bitmap = bmap;
				imgDisplay.setImageBitmap(bmap);
			}
		});
		
		
	}
	
	
	
	private void zoomin()
	{
		
		reSizeBmp(scale_in);
	}
	
	/**
	 * 
	 */
			
	private void zoomOut()
	{
		
		
		reSizeBmp(scale_out);
		
		btnZoomin.setEnabled(true);
		//int bmpWidth = bitmap.getWidth();
		//int bmtHeight = bitmap.getHeight();
			
		
	
		/*Bitmap reSizeBmp=Bitmap.createBitmap(bitmap, 0, 0, bmpWidth, bmtHeight, matrix, true);
		
		
		
		//把旧的ImageView移掉
		if(imgId==0)
		{
			lLayoutDisplay.removeView(imgDisplay);
		}
		else {
			lLayoutDisplay.removeView(findViewById(imgId));
		}
		
		imgId ++;
		ImageView imageView = new ImageView(this);
		imageView.setImageBitmap(reSizeBmp);
		imageView.setId(imgId);
		
		lLayoutDisplay.addView(imageView);
		
		setContentView(fLayoutDisplay);
		*/
		
	}
	
	
	
	
	private void reSizeBmp(double scale)
	{
		
		scaleWidth = (float) (scaleWidth * scale);
		scaleHeight = (float) (scaleHeight * scale);
	
		// 重新生成一个放大/缩小 后图片
		
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		
		imgDisplay.setImageMatrix(matrix);
	}
	
	
	
	
	final class ImageViewOnTouchListener implements OnTouchListener
	{

		public boolean onTouch(View v, MotionEvent event)
		{
			
			
			
			switch (event.getAction() & MotionEvent.ACTION_MASK)
			{
				case MotionEvent.ACTION_DOWN:  // 单只手指按下
				
					currMatrix.set(matrix);
					starPoint.set(event.getX(), event.getY());
				
					mode = DRAG;
				break;
				
				case MotionEvent.ACTION_POINTER_DOWN: // 如果有一只手指按下了屏幕，后续在右手指按下屏幕的时候，就会触发这个事件
				{
					startDistance =distance(event);
					
					if(startDistance > 5f) // 2手指之间的距离像素如果大于5，那我则认为是多点
					{
						mode = ZOOM;
						currMatrix.set(matrix);
						
						midPointF = getMidPoint(event);
						
					}
					
					break;
				}
				
				case MotionEvent.ACTION_MOVE:
				{
					
					if(mode == DRAG) // 拖拽模式
					{
						
						float dx = event.getX() -  starPoint.x;
						float dy = event.getY() - starPoint.y;
						matrix.set(currMatrix);
						
						matrix.postTranslate(dx, dy);// 移动到指定点
						
					}
					
					else if(mode == ZOOM)
					{
						
						float distance = distance(event);
						
						if(distance > 5f)
						{
							matrix.set(currMatrix);
							float  cale =  distance / startDistance;
							
							matrix.preScale(cale, cale, midPointF.x, midPointF.y);
						}
						
					}
					break;
				}
				
				
				case MotionEvent.ACTION_UP: // 最后一只手机离开屏幕后触发此事件
				case MotionEvent.ACTION_POINTER_UP: // 一只手指离开屏幕，但还有一只手指在屏幕上，会触发此事件
					mode = NONE;
				break;
				
			default:
				break;
			}
			
			
	   	imgDisplay.setImageMatrix(matrix);
			
			return true;
		}

		/**
		 * @param event
		 * @return
		 */
		private PointF getMidPoint(MotionEvent event)
		{
			
			float x = (event.getX(1) - event.getX(0)) / 2;
			float y = (event.getY(1) - event.getY(0)) / 2;
			
			return new PointF(x, y);
		}
		
		
	}
	
	
	
	private float distance(MotionEvent event)
	{
		
		float eX = event.getX(1) - event.getX(0);
		float eY = event.getY(1) - event.getY(0);
		
		return FloatMath.sqrt(eX * eX + eY * eY);
		
	}
	
	
	
	
	
}
