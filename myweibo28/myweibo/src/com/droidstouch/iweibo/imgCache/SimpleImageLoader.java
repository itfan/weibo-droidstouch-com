/**
 *SimpleImageLoader.java
 *2011-10-15 下午04:50:52
 *Touch Android
 *http://bbs.droidstouch.com
 */
package com.droidstouch.iweibo.imgCache;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.droidstouch.iweibo.app.WeiboApplication;

/**
 * @author <a href="http://bbs.droidstouch.com">Touch Android</a>
 *
 */
public class SimpleImageLoader
{

	
	public static void showImg(ImageView view,String url)
	{
		
		view.setTag(url);
		
		view.setImageBitmap(WeiboApplication.lazyImageLoader.get(url, getCallback(url,view)));
		
	}
	
	
	private static ImageLoaderCallback getCallback(final String url,final ImageView view)
	{
		
		
		return new ImageLoaderCallback()
		{
			
			@Override
			public void refresh(String url, Bitmap bitmap)
			{
				if(url.equals(view.getTag().toString()))
				{
					view.setImageBitmap(bitmap);
				}
			}
		};
		
	}
	
	
	public static void dispalyForDlg(ImageView imageView, String url,ProgressBar pb,Button btnBig)
	{
		
		imageView.setTag(url);
		imageView.setImageBitmap(WeiboApplication.lazyImageLoader.get(url,createCallback(url, imageView,pb,btnBig)));
	}
	
	
	private static ImageLoaderCallback createCallback(final String url,final ImageView imageView,final ProgressBar pb,final Button btnBig)
	{
		
		return new ImageLoaderCallback()
		{
			
			public void refresh(String url, Bitmap bitmap)
			{
				pb.setVisibility(View.GONE);
				imageView.setVisibility(View.VISIBLE);
				btnBig.setVisibility(View.VISIBLE);
				if (url.equals(imageView.getTag())) 
				{
					imageView.setImageBitmap(bitmap);
				}
			}
		};
	}
	
	
	
	
}
