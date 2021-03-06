/**
 *WeiboManager.java
 *2011-11-9 下午09:12:42
 *Touch Android
 *http://bbs.droidstouch.com
 */
package com.droidstouch.iweibo.app;

import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;

import com.droidstouch.iweibo.util.emotions.EmotionsParse;

/**
 * @author <a href="http://bbs.droidstouch.com">Touch Android</a>
 *
 */
public class WeiboManager
{

	
	private static final String START="start";
	private static final String END="end";
	private static final String PHRASE="phrase";
	
	private static final String PATTERN_URL="http://.* ";//url
	private static final String PATTERN_TOPIC="#.+?#";//话题
	private static final String PATTERN_NAMES="@([\u4e00-\u9fa5A-Za-z0-9_]*)";//人名
	private static final String PATTERN_EMOTION="\\[[\u4e00-\u9fa5A-Za-z0-9]*\\]";//表情
	
	public static final int MESSAGE_ID=1;
	public static final String EXTRA_SPANNABLE = "extra_spannable";
	public static final String EXTRA_WEIBO = "extra_weibo";
	
	private EmotionsParse emotionsParse = new EmotionsParse();
	
	private BlockingQueue<String> weiboQueue = new ArrayBlockingQueue<String>(50);
	private ParseThread parseThread = new ParseThread();
	private CallbackManager callbackManager = new CallbackManager();
	
	
	
	Handler handler = new Handler()
	{
		
		public void handleMessage(android.os.Message msg) 
		{
			
			switch (msg.what)
			{
			case MESSAGE_ID:
				
				final Bundle bundle = msg.getData();
				String weibo = bundle.getString(EXTRA_WEIBO);
				SpannableStringBuilder spannable=(SpannableStringBuilder) bundle.getCharSequence(EXTRA_SPANNABLE);
				callbackManager.calll(weibo, spannable);
				break;

			default:
				break;
			}
		};
	};
	
	
	
	
	public SpannableStringBuilder parseWeibo(String wb,WeiboParseCallBack callBack)
	{
	
		SpannableStringBuilder builder = new  SpannableStringBuilder(wb);
		
		callbackManager.put(wb, callBack);
		startPaserThread(wb);
		
		return builder;
	}
	
	
	
	
	public SpannableStringBuilder parseWeibo(String weibo)
	{
		
		SpannableStringBuilder spannable = new SpannableStringBuilder(weibo);
		
		replace(weibo,PATTERN_URL,spannable,false);
		replace(weibo,PATTERN_TOPIC,spannable,false);
		replace(weibo,PATTERN_NAMES,spannable,false);
		replace(weibo,PATTERN_EMOTION,spannable,true);
		
		return spannable;
	}
	
	

	private void startPaserThread(String weibo)
	{
		
		if(null !=weibo)
			putWeiboToQueue(weibo);
		
		State state =parseThread.getState();
		//尚未启动的线程的状态
		if(Thread.State.NEW == state)
			parseThread.start();
		//已终止线程的线程状态。线程已经结束执行。重新New一个线程
		else if (Thread.State.TERMINATED == state)
		{
			parseThread = new ParseThread();
			parseThread.start();
		}
	}
	
	
	

	private void putWeiboToQueue(String weibo)
	{
		
		try
		{
		 if(!weiboQueue.contains(weibo))
			 weiboQueue.put(weibo);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	/***
	 *解析微博
	 * @param weibo
	 * @param patternStr
	 * @param spannable
	 * @param emotion 是否解析表情
	 */
	public void replace(String weibo,String patternStr, SpannableStringBuilder spannable,boolean emotion)
	{
		
		
		Pattern pattern = Pattern.compile(patternStr);
		
		List<HashMap<String, String>>  lst= this.getStartAndEnd(pattern, weibo);
		
		if(null != lst)
		{
			
			for (HashMap<String, String> hashMap : lst)
			{
				int start = Integer.parseInt(hashMap.get(START));
				int end = Integer.parseInt(hashMap.get(END));
				if(emotion) // 解析表情
				{
					
					String phrase = hashMap.get(PHRASE);
					Drawable drawable = emotionsParse.getEmotionByName(phrase);
					if(drawable != null)
					{
						drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
						ImageSpan imgSpan = new ImageSpan(drawable);
						spannable.setSpan(imgSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					}
				}
				else
				{
					ForegroundColorSpan span = new ForegroundColorSpan(Color.BLUE);
					spannable.setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
			}
			
		}
		
	}
	
	
	
	
	
	
	public List<HashMap<String, String>>  getStartAndEnd(Pattern pattern,String weibo)
	{
		
		List<HashMap<String, String>> lst = new ArrayList<HashMap<String,String>>();
		
		Matcher matcher = pattern.matcher(weibo);
		
		while(matcher.find())
		{
			
			HashMap<String, String> map = new HashMap<String, String>();
			
			map.put(PHRASE, matcher.group());
			map.put(START, matcher.start()+"");
			map.put(END, matcher.end()+"");
			
			lst.add(map);
		}
		
		return lst;
		
	}
	
	


	/**
	 * 回调
	 * @author Administrator
	 *
	 */
	public static class CallbackManager
	{
		private ConcurrentHashMap<String, List<WeiboParseCallBack>> callbackMap;
		
		private CallbackManager()
		{
			callbackMap = new ConcurrentHashMap<String, List<WeiboParseCallBack>>();
		}
		
		
		public void put(String weibo,WeiboParseCallBack callback)
		{
			if(!callbackMap.contains(weibo))
			{
				callbackMap.put(weibo, new ArrayList<WeiboParseCallBack>());
			}
			callbackMap.get(weibo).add(callback);
		}
		
		
		
		public void calll(String weibo,SpannableStringBuilder spannable)
		{
			List<WeiboParseCallBack> callbacks = callbackMap.get(weibo);
			if(null != callbacks)
			{
				for (WeiboParseCallBack callback : callbacks)
				{
					if(null != callback)
						callback.refresh(weibo, spannable);
				}
				callbacks.clear();
			}
			callbackMap.remove(weibo);
		}
		
	}
	
	/**
	 * 解析下载线程
	 * @author Administrator
	 *
	 */
	private class ParseThread extends Thread
	{
		private boolean isRun=true;
		public void shotDown()
		{
			isRun = false;
		}
		
		public void run()
		{
			try
			{
			   while(isRun)
				{
					String weibo=weiboQueue.poll();
					if(weibo==null)
						return;
					
					SpannableStringBuilder spannable = parseWeibo(weibo);
					
					Message message =handler.obtainMessage(MESSAGE_ID);
					Bundle bundle =message.getData();
					bundle.putString(EXTRA_WEIBO, weibo);
					bundle.putCharSequence(EXTRA_SPANNABLE, spannable);
					
					handler.sendMessage(message);
				}
			}
			catch (Exception e)
			{
			 e.printStackTrace();
			}
			finally
			{
				 shotDown();
			}
			
		}
	}
	
	
	
}
