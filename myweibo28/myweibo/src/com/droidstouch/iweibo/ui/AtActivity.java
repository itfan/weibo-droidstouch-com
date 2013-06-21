/**
 *HomeActivity.java
 *2011-9-18 下午10:00:06
 *Touch Android
 *http://bbs.droidstouch.com
 */
package com.droidstouch.iweibo.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import weibo4android.Emotion;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.widget.TextView;

import com.droidstouch.iweibo.R;
import com.droidstouch.iweibo.util.EmotionManager;
import com.droidstouch.iweibo.util.EmotionsParse;

/**
 * @author <a href="http://bbs.droidstouch.com">Touch Android</a>
 *
 */
public class AtActivity extends Activity implements IWeiboActivity
{

	
	EmotionsParse emotionsParse;
	
	private static final String START="start";
	private static final String END="end";
	private static final String PHRASE="phrase";
	
	private static final String TOPIC="#.+?#";
	private static final String NAME="@([\u4e00-\u9fa5A-Za-z0-9_]*)";
	private static final String URL="http://.*";
	private static final String EMOTION="\\[[\u4E00-\u9FA5a-zA-Z0-9]*\\]";
	private TextView textView;
	

	private String str="#《爱够了没》第二季开播了# 麻辣的话题看够没？[爱你][花心][抓狂]强大的美女观察团看够了没？@陈汉典 回归，@HOLD住姊LINLIN 爆笑加盟，《#爱够了没#》第二季播出了！五位帅哥一一登台亮相，Hold住姐现场征婚，不知道Hold住姐青睐哪一个呢？ http://t.cn/ShSRRN ";
	
	private SpannableString spannableString = new SpannableString(str);
	
	private ProgressDialog dialog;
	
	
	
	
	
	
	
	
	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.at);
		
		emotionsParse = new EmotionsParse(this);
		
		textView = (TextView) this.findViewById(R.id.txt_test);
		
		
		//高亮话题
		hightLight(Pattern.compile(TOPIC));
		
		hightLight(Pattern.compile(NAME));
		hightLight(Pattern.compile(URL));
	
		phrase(Pattern.compile(EMOTION));
		
		
		textView.setText(spannableString);
		
		
		/*MainService.addActivity(this);
		
		Task task = new Task(Task.WEIBO_EMOTIONS, null);
		MainService.newTask(task);
		
		dialog.show(this, null, "正在获取表情");*/
		
	}
	
	
	
	
	public void phrase(Pattern pattern)
	{
		List<HashMap<String, String>> lst  = this.getStartAndEnd(pattern);
		
		
		for (HashMap<String, String> map : lst)
		{
			Drawable drawable = emotionsParse.getDrawableByphrase(map.get(PHRASE));
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
			ImageSpan imgSpan = new ImageSpan(drawable);
			
			spannableString.setSpan(imgSpan, Integer.parseInt(map.get(START)), Integer.parseInt(map.get(END)), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		
	}
	
	
	
	public void hightLight(Pattern pattern)
	{
		
		List<HashMap<String, String>> lst  = this.getStartAndEnd(pattern);
		
		
		for (HashMap<String, String> map : lst)
		{
			ForegroundColorSpan span = new ForegroundColorSpan(Color.BLUE);
			spannableString.setSpan(span, Integer.parseInt(map.get(START)), Integer.parseInt(map.get(END)), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		
		
	}
	
	
	
	
	
	public List<HashMap<String, String>>  getStartAndEnd(Pattern pattern)
	{
		
		List<HashMap<String, String>> lst = new ArrayList<HashMap<String,String>>();
		
		Matcher matcher = pattern.matcher(str);
		
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



	public void init()
	{
		
	}




	public void refresh(Object... params)
	{
		
		if(dialog != null)
			dialog.dismiss();
		
		List<Emotion> emotions =(List<Emotion>) params[0];
		
		EmotionManager manager = new EmotionManager(emotions.size());
		
		for (Emotion emotion : emotions)
		{
			manager.putURL(emotion.getUrl());
			
			Log.i("AtActivity", emotion.getPhrase());
			Log.i("AtActivity", emotion.getUrl());
		}
		
		manager.startThread();
	}
	
	
	
	
	
	
	
	
}
