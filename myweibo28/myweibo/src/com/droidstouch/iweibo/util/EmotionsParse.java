/**
 *EmotionsParse.java
 *2011-11-1 下午09:13:25
 *Touch Android
 *http://bbs.droidstouch.com
 */
package com.droidstouch.iweibo.util;

import java.util.HashMap;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.droidstouch.iweibo.R;

/**
 * @author <a href="http://bbs.droidstouch.com">Touch Android</a>
 *
 */
public class EmotionsParse
{

	
	
	private Context context;
	private static HashMap<String, Integer> emotionMap;
	private static String[] phrases;
	private static int[] emotions_id=
	{
		R.drawable.love,
		R.drawable.hx,
		R.drawable.crazy
	};
	
	public EmotionsParse(Context context)
	{
		this.context= context;
		
		phrases = context.getResources().getStringArray(R.array.defualt_emotions);
		
		if(emotions_id.length != phrases.length)
			throw new RuntimeException("Error !!!");
		
		
		int length = emotions_id.length;
		emotionMap = new HashMap<String, Integer>(length);
		
		for (int i=0 ; i < length; i++)
		{
			emotionMap.put(phrases[i], emotions_id[i]);
		}
		
	}
	
	
	
	
	
	public Drawable getDrawableByphrase(String phrase)
	{
		
		 int id=emotionMap.get(phrase);
		 
		 return context.getResources().getDrawable(id);
		
	}
	
	
	
	
	
}
