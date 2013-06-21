/**
 *WeiboAdapter.java
 *2011-9-29 下午08:18:26
 *Touch Android
 *http://bbs.droidstouch.com
 */
package com.droidstouch.iweibo.adapter;

import java.util.List;

import weibo4android.Status;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.droidstouch.iweibo.R;
import com.droidstouch.iweibo.app.SimpleWeiboManager;
import com.droidstouch.iweibo.imgCache.SimpleImageLoader;
import com.droidstouch.iweibo.ui.ImgDisplayActivity;
import com.droidstouch.iweibo.util.StringUtil;

/**
 * @author <a href="http://bbs.droidstouch.com">Touch Android</a>
 *
 */
public class WeiboAdapter extends BaseAdapter
{

	
	private List<Status> status;
	private LayoutInflater mInflater;
	private Context context ;
	
	
	private Dialog dialog;
	private ImageView imgView;
	private ProgressBar pb;
	private Button btnBig;
	
	
	public WeiboAdapter(Context context,List<Status> status) 
	{
		
		this.context = context;
		this.status = status;
		mInflater = LayoutInflater.from(context);
		
		dialog = new Dialog(context,R.style.simple_dialog);
		View view = mInflater.inflate(R.layout.weibo_img_dlg, null);
		imgView = (ImageView) view.findViewById(R.id.img_weibo_img);
		pb = (ProgressBar) view.findViewById(R.id.progress_wbimg_load);
		btnBig = (Button) view.findViewById(R.id.btn_big);
		
		dialog.setContentView(view);
		
	}
	
	
	public int getCount()
	{
		return status==null?0:status.size();
	}

	public Object getItem(int position)
	{
		return  status==null?null:status.get(position);
	}

	public long getItemId(int position)
	{
		return status.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		
		return createViewFromResource(position,convertView);
	}

	
	 private View createViewFromResource(int position, View convertView) 
	 {
	   View v;
	   if (convertView == null) {
	       v = mInflater.inflate(R.layout.wb_item_template, null);
	   } else {
	       v = convertView;
	   }
	
	   bindView(position, v);
	
	   return v;
	 }
	
	
	private void bindView(int position,View view)
	{
		
		Status s = this.status.get(position);

		WeiboHolder holder = new WeiboHolder();
		
		holder.txt_wb_item_uname = (TextView) view.findViewById(R.id.txt_wb_item_uname);
		holder.txt_wb_item_content= (TextView) view.findViewById(R.id.txt_wb_item_content);
		holder.txt_wb_item_from= (TextView) view.findViewById(R.id.txt_wb_item_from);
		holder.img_wb_item_head = (ImageView) view.findViewById(R.id.img_wb_item_head);
		holder.img_wb_item_V = (ImageView) view.findViewById(R.id.img_wb_item_V);
		holder.img_wb_item_content_pic = (ImageView) view.findViewById(R.id.img_wb_item_content_pic);
		holder.lyt_wb_item_sublayout = (LinearLayout) view.findViewById(R.id.lyt_wb_item_sublayout);
		holder.txt_wb_item_subcontent = (TextView) view.findViewById(R.id.txt_wb_item_subcontent);
		holder.img_wb_item_content_subpic = (ImageView) view.findViewById(R.id.img_wb_item_content_subpic);
		
		
		holder.txt_wb_item_uname.setText(s.getUser().getName());
		
		SimpleWeiboManager.display(holder.txt_wb_item_content, s.getText());
		
		
	
		
		
		SimpleImageLoader.showImg(holder.img_wb_item_head, s.getUser().getProfileImageURL().toString());
		holder.txt_wb_item_from.setText("来着:"+Html.fromHtml(s.getSource()));
		
		
		
		//判断是否通过认证
		if(s.getUser().isVerified())
		{
			holder.img_wb_item_V.setVisibility(View.VISIBLE);
		}
		else
			holder.img_wb_item_V.setVisibility(View.GONE);
		
		//判断微博中是否含有图片
		if(!StringUtil.isEmpty(s.getThumbnail_pic()))
		{
			holder.img_wb_item_content_pic.setVisibility(View.VISIBLE);
			SimpleImageLoader.showImg(holder.img_wb_item_content_pic, s.getThumbnail_pic());
			holder.img_wb_item_content_pic.setOnClickListener(new PicOnClickListener(s.getBmiddle_pic(),s.getOriginal_pic()));
		}
		else
			holder.img_wb_item_content_pic.setVisibility(View.GONE);
		// 判断使用又转发
		if(s.getRetweeted_status()!=null)
		{
			holder.lyt_wb_item_sublayout.setVisibility(View.VISIBLE);
			//holder.txt_wb_item_subcontent.setText(s.getRetweeted_status().getText());
			
			SimpleWeiboManager.display(holder.txt_wb_item_subcontent, s.getRetweeted_status().getText());
			
			//判断微博中是否含有图片
			if(!StringUtil.isEmpty(s.getRetweeted_status().getThumbnail_pic()))
			{
				holder.img_wb_item_content_subpic.setVisibility(View.VISIBLE);
				SimpleImageLoader.showImg(holder.img_wb_item_content_subpic, s.getRetweeted_status().getThumbnail_pic());
			
				holder.img_wb_item_content_subpic.setOnClickListener(new PicOnClickListener(s.getRetweeted_status().getBmiddle_pic(),s.getRetweeted_status().getOriginal_pic()));
			}
			else
				holder.img_wb_item_content_subpic.setVisibility(View.VISIBLE);
		}
		else
			holder.lyt_wb_item_sublayout.setVisibility(View.GONE);
	}
	 
	
	
	public void refrensh(List<Status> newStatus)
	{
		
		status.addAll(newStatus);
		this.notifyDataSetChanged();
	}
	
	
	
	
	
	private static class WeiboHolder
	{
		
		ImageView img_wb_item_head;
		
		TextView txt_wb_item_uname;
		
		ImageView img_wb_item_V;
		
		//TextView txt_wb_item_time;
		
		TextView txt_wb_item_content;
		
		ImageView img_wb_item_content_pic;
		
		LinearLayout lyt_wb_item_sublayout;
		
		TextView txt_wb_item_subcontent;
		
		ImageView img_wb_item_content_subpic;
		
		TextView txt_wb_item_from;
		
		//TextView txt_wb_item_redirect;
		
		//TextView txt_wb_item_comment;
		
	}
	
	
	

	class PicOnClickListener implements OnClickListener
	{
		private String bmiddlePicUrl;
		private String originalPicUrl;
		
		public PicOnClickListener (String bmiddlePicUrl,String originalPicUrl)
		{
			this.bmiddlePicUrl = bmiddlePicUrl;
			this.originalPicUrl = originalPicUrl;
		}
		public void onClick(View v)
		{
			imgView.setVisibility(View.GONE);
			pb.setVisibility(View.VISIBLE);
			btnBig.setVisibility(View.GONE);
			btnBig.setOnClickListener(new BigOnClickListner(originalPicUrl));
			
			dialog.show();
			
			SimpleImageLoader.dispalyForDlg(imgView, bmiddlePicUrl, pb,btnBig);
		}
		
	}
	
	
	class BigOnClickListner implements OnClickListener
	{
		
		
		private String originalPicUrl;
		public BigOnClickListner (String originalPicUrl)
		{
			this.originalPicUrl = originalPicUrl;
		}
		public void onClick(View v)
		{
			
			Intent intent = new Intent(context, ImgDisplayActivity.class);
			intent.putExtra("imgUrl", originalPicUrl);
			
			context.startActivity(intent);
		}
	}
	
	
	
}
