/**
 *ImageLoaderCallback.java
 *2011-10-13 下午10:04:31
 *Touch Android
 *http://bbs.droidstouch.com
 */
package com.droidstouch.iweibo.imgCache;

import android.graphics.Bitmap;

/**
 * @author <a href="http://bbs.droidstouch.com">Touch Android</a>
 *
 */
public interface ImageLoaderCallback
{

	void refresh(String url,Bitmap bitmap);
	
}
