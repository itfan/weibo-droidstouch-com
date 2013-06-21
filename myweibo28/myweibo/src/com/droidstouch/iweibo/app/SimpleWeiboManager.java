package com.droidstouch.iweibo.app;

import android.text.SpannableStringBuilder;
import android.widget.TextView;

public class SimpleWeiboManager
{

	
	
	private static WeiboManager manager = new WeiboManager();
	
	public static void display(final TextView textView, String weibo) 
	{
		textView.setText(weibo);
		textView.setText(manager.parseWeibo(weibo, createCallback(textView,weibo)));
	}
	
	public static WeiboParseCallBack createCallback(final TextView textView,final String weibo)
	{
		
		return new WeiboParseCallBack()
		{
			public void refresh(String weibo, SpannableStringBuilder spannable)
			{
				textView.setText(spannable);
			}
		};
		
	}
	
	
	
}
