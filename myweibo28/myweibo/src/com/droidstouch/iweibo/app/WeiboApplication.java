/**
 *WeiboApplication.java
 *2011-10-15 下午04:45:56
 *Touch Android
 *http://bbs.droidstouch.com
 */
package com.droidstouch.iweibo.app;

import com.droidstouch.iweibo.imgCache.LazyImageLoader;

import android.app.Application;
import android.content.Context;

/**
 * @author <a href="http://bbs.droidstouch.com">Touch Android</a>
 *
 */
public class WeiboApplication extends Application
{

	
	
	public static LazyImageLoader lazyImageLoader;
	public static Context context;
	
	
	
	public void onCreate()
	{
		super.onCreate();
		
		context = this.getApplicationContext();
		lazyImageLoader = new LazyImageLoader();
	}
	
	
}
