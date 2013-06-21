/**
 *HomeActivity.java
 *2011-9-18 下午10:00:06
 *Touch Android
 *http://bbs.droidstouch.com
 */
package com.droidstouch.iweibo.ui;

import java.util.HashMap;
import java.util.List;

import weibo4android.Status;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.droidstouch.iweibo.R;
import com.droidstouch.iweibo.adapter.WeiboAdapter;
import com.droidstouch.iweibo.app.Preferences;
import com.droidstouch.iweibo.bean.Task;
import com.droidstouch.iweibo.logic.MainService;

/**
 * @author <a href="http://bbs.droidstouch.com">Touch Android</a>
 *
 */
public class HomeActivity extends Activity implements IWeiboActivity
{


	private static final String TAG="HomeActivity";
	
	
	//加载View
	private View progresView;
	private View titleView;
	private ListView weiboListView;
	
	private View loadMoreView;
	private LinearLayout llytLoading;
	private TextView txtMore;
	
	private long   maxId=0;
	private WeiboAdapter adapter;
	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.home);
		
		
	
	  init();
	  
	  loadMoreView.setOnClickListener(new LoadMoreOnClickListener());
	
		
	}


	/**
	 * 添加获取微博信息任务
	 */
	private void newTask()
	{
		
		
		HashMap<String, Object> params = new HashMap<String, Object>(1);
		
		params.put("maxId", maxId);
		
		Task task = new Task(Task.WEIBO_FRIEDNS_TIMELINE,params);
		
		MainService.newTask(task);
		
		
		
		
		
	}


	public void init()
	{
		
		weiboListView = (ListView) this.findViewById(R.id.lv_weibos);
		progresView = this.findViewById(R.id.layout_progress);
		titleView = this.findViewById(R.id.layout_title_bar);
		loadMoreView = View.inflate(this, R.layout.more_layout, null);
		llytLoading = (LinearLayout) loadMoreView.findViewById(R.id.llyt_loading);
		txtMore = (TextView) loadMoreView.findViewById(R.id.txt_more);
		
		
	  ((TextView)titleView.findViewById(R.id.txt_wb_title)).setText(MainService.nowUser.getUserName());
		
	  
		weiboListView.addFooterView(loadMoreView, null, true);
		MainService.addActivity(this);
		
		
		newTask();
		
	}


	@SuppressWarnings("unchecked")
	public void refresh(Object... params)
	{
		
		
		List<Status> status = (List<Status>) params[0];
		
		if(status != null && status.size() >0)
		{
			maxId = Long.parseLong(status.get(status.size() -1).getMid())-1;
			
			if(adapter == null)
			{
				progresView.setVisibility(View.GONE);
				WeiboAdapter adapter = new WeiboAdapter(this, status);
				weiboListView.setAdapter(adapter);
			}
			else
			{
				adapter.refrensh(status);
				showLoadingControl(false);
				
				weiboListView.setSelection(adapter.getCount() - Preferences.PAGE_SIZE);
				
			}
			
		}
		
	
		
		
		
		
		
		
	
	}
	

	/**
	 * 加载更多
	 */
	
	public void loadMore()
	{
		newTask();
	}
	

	
	private void showLoadingControl(boolean isShow)
	{
		
		txtMore.setVisibility(isShow?View.GONE:View.VISIBLE);
		llytLoading.setVisibility(isShow?View.VISIBLE:View.GONE);
		
	}
	
	
	final class LoadMoreOnClickListener implements OnClickListener
	{
		
		public void onClick(View v)
		{
			showLoadingControl(true);
			loadMore();
		}
	}


	
	
}
